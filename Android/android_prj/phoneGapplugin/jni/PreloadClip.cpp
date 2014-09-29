////////////////////////////////////////////////////////////////////////////////
// Pre-loading clips. This class will spawn a thread to do tasks.
//
// This class is for HLS to pre-load clips during playback. 
//
// Author: zackchiu@realtek.com
//
// Created: 11/22/2010
////////////////////////////////////////////////////////////////////////////////

#include "PreloadClip.h"
#include "M3ULineTokenParser.h"

////////////////////////////////////////////////////////////////////////////////
// Name space declaration
//


////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// Macro definition
//
//#define DUMP_DEBUG_MESSAGE
#ifdef DUMP_DEBUG_MESSAGE
    #define zDebug printf 
#else
    static void zDebug(...) { };
#endif

#define zPrint printf

// The max counts of pre-loaded items in our list.
// When exceeding this number, the top item in the list will be dropped.
// 
#define MAX_PRELOADED_ITEM_COUNTS 3

////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// Memory tracker for debug
//
#ifdef DUMP_DEBUG_MESSAGE

static std::vector<HLSClip*> MemoryTracker;
void StartMonitor(HLSClip* pClip)
{
    MemoryTracker.push_back(pClip);
}

void StopMonitor(HLSClip* pClip)
{
    std::vector<HLSClip*>::iterator itor, itorEnd = MemoryTracker.end();
    for (itor = MemoryTracker.begin(); itor != itorEnd; ++itor)
    {
        if (*itor == pClip) 
        {
            MemoryTracker.erase(itor);
            break;
        }
    }
}

void DumpMemoryLeak()
{
    zDebug("DumpMemoryLeak(): %d\n", MemoryTracker.size());
    std::vector<HLSClip*>::iterator itor, itorEnd = MemoryTracker.end();
    for (itor = MemoryTracker.begin(); itor != itorEnd; ++itor)
    {
        zDebug("DumpMemoryLeak(): Leakage--->(0x%x)\n", (int)(*itor));
    }
    MemoryTracker.clear();
}
#endif

////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// PreloadClip Implementation
//
PreloadClip::PreloadClip(IOPLUGIN& IoPlugin, const char* pUserAgent)
{
    m_pPreloadCmdProcessor = new PreloadCmdProcessor(IoPlugin, pUserAgent);
    m_pPreloadCmdList = new CommandQueue<PlayListItem*>(m_pPreloadCmdProcessor);
}

PreloadClip::~PreloadClip()
{
    if (m_pPreloadCmdList) 
    { 
        delete m_pPreloadCmdList; 
        m_pPreloadCmdList = NULL; 
    }
    
    if (m_pPreloadCmdProcessor) 
    {
        ClearPreloadedItem();
        delete m_pPreloadCmdProcessor;
        m_pPreloadCmdProcessor = NULL;
    }
    zDebug("PreloadClip::~PreloadClip(): ......\n");
#ifdef DUMP_DEBUG_MESSAGE    
    DumpMemoryLeak();
#endif
}

bool PreloadClip::Preload(PlayListItem* pItem)
{
    if (!pItem) return false;
    m_pPreloadCmdList->AddCommand(pItem);
    return true;
}

HLSClip* PreloadClip::GetPreloadItem(PlayListItem* pItem, bool bRemoveFromList)
{
    return pItem ? m_pPreloadCmdProcessor->Find(pItem, bRemoveFromList) : NULL;
}

void PreloadClip::ClearPreloadedItem()
{
    m_pPreloadCmdProcessor->ClearAllCommands();
}

bool PreloadClip::IsPreLoading(PlayListItem* pItem)
{
    if (!pItem) return false;
    return m_pPreloadCmdList->IsExistCommand(pItem);
}

bool PreloadClip::WaitForPreload(PlayListItem* pItem)
{
    if (!pItem) return false;
    CEvent syncEvent;
    return m_pPreloadCmdProcessor->SetSyncComamnd(pItem, &syncEvent)
                ? syncEvent.Wait(TIME_INFINITY)
                : false;
}

////////////////////////////////////////////////////////////////////////////////
// HLSClip Implementation
//
HLSClip::HLSClip(IOPLUGIN& IoPlugin, 
                 PlayListItem* pItem,
                 const char* pUserAgent): m_IoPlugin(IoPlugin),
                                          m_pFileHandle(NULL),
                                          m_pItem(pItem),
                                          m_iRefCounter(1)
{
    HLSPlayList* pPlayList = (HLSPlayList*)pItem->pOwner;
    if (pItem->iTag == TAG_FILE || pItem->iTag == TAG_EXT_INF)
        m_URL = pPlayList->GetFileURL(pItem);
    else if (pItem->iTag == TAG_EXT_KEY_INF) // This is a key file
        m_URL = ((M3UExtKeyInf*)(pItem->pItemInfo))->pKeyURI;

    if (pUserAgent && !m_URL.empty())
    {
        // Add extra command to HTTP IO Plugin
        m_URL.append(" extraHeader='User-Agent: ");
        m_URL.append(pUserAgent);
        m_URL.append("'");
    }
    zDebug("HLSClip::HLSClip(): Load %s(0x%x)(%s)\n", 
           (pItem->iTag == TAG_EXT_KEY_INF ? "Key" : ""), (int)this, m_URL.c_str());
    m_pFileHandle = m_IoPlugin.open(const_cast<char*>(m_URL.c_str()), 0); 
    zDebug("HLSClip::HLSClip(): Load (0x%x-0x%x)(%s) Done.\n", 
           (int)this, (int)m_pFileHandle, m_URL.c_str());
#ifdef DUMP_DEBUG_MESSAGE 
    StartMonitor(this);
#endif
}

HLSClip::~HLSClip()
{
#ifdef DUMP_DEBUG_MESSAGE 
    StopMonitor(this);
#endif
}

UINT HLSClip::AddRef()
{
    CAutoLock autoLock(&m_CritSec);
    if (!m_iRefCounter) return 0;
    m_iRefCounter++;                                
    zDebug("HLSClip::AddRef(): AddRef (0x%x)(%d)\n", (int)this, m_iRefCounter);
    return m_iRefCounter;
}

void HLSClip::Release()
{
    CAutoLock autoLock(&m_CritSec);
    if (!m_iRefCounter) return;
    m_iRefCounter--;
    if (m_iRefCounter > 0) return;
    if (m_pFileHandle)
    {
        m_IoPlugin.close(m_pFileHandle);
        m_pFileHandle = NULL;
    }
    m_pItem = NULL;    
    zDebug("HLSClip::Release(): Release (0x%x)\n", (int)this);
    delete this;
}

void* HLSClip::GetHandle()
{
    return m_pFileHandle;
}

PlayListItem* HLSClip::GetItem()
{
    return m_pItem;
}

void HLSClip::SeekToBegin()
{
    if (!m_pFileHandle) return;
    m_IoPlugin.seek(m_pFileHandle, 0, 0);
}

////////////////////////////////////////////////////////////////////////////////
// PreloadCmdProcessor Implementation
//
PreloadCmdProcessor::PreloadCmdProcessor(IOPLUGIN& IoPlugin, 
                                         const char* pUserAgent): m_IoPlugin(IoPlugin)
{
    if (pUserAgent) m_strUserAgent = pUserAgent;
}

PreloadCmdProcessor::~PreloadCmdProcessor() 
{ 
    ClearAllCommands(); 
}

HLSClip* PreloadCmdProcessor::Find(PlayListItem* pItem,
                                   bool bRemoveFromList)
{
    if (!pItem) return NULL;
    
    CAutoLock autoLock(&m_CritSec);
    std::list<HLSClip*>::iterator itor, itorEnd = m_PreloadList.end();
    HLSClip* pResult = NULL;
    for (itor = m_PreloadList.begin(); itor != itorEnd; ++itor)
    {
        if ((*itor)->GetItem() == pItem) 
        {
            pResult = *itor;
            break;
        }
    }

    if (bRemoveFromList && pResult) m_PreloadList.erase(itor);
    if (pResult) pResult->AddRef();
    return pResult;
}

void PreloadCmdProcessor::ClearAllCommands()
{
    zDebug("PreloadCmdProcessor::ClearAllCommands(): ....\n");        

    while (m_PreloadList.size()) ReleaseTopItemFromList();

    CAutoLock autoLock(&m_CritSec);
    std::map<PlayListItem*, CEvent*>::iterator itor, itorEnd = m_CmdSyncList.end();
    for (itor = m_CmdSyncList.begin(); itor != itorEnd; ++itor)
        if (itor->second) itor->second->Set();
    m_CmdSyncList.clear();
}

bool PreloadCmdProcessor::SetSyncComamnd(PlayListItem* pItem, 
                                         CEvent* pSyncEvent)
{
    if (!pItem || !pSyncEvent) return false;
    CAutoLock autoLock(&m_CritSec);
    m_CmdSyncList[pItem] = pSyncEvent;
    return true; 
}

bool PreloadCmdProcessor::Execute(PlayListItem* pItem)
{                      
    if (!pItem)
    {
        zDebug("PreloadCmdProcessor::Execute(): Clear all pre-loaded items!!!\n");
        ClearAllCommands();
        return true;
    }
    
    while (m_PreloadList.size() >= MAX_PRELOADED_ITEM_COUNTS) 
        ReleaseTopItemFromList();
    
    HLSClip* pClip = new HLSClip(m_IoPlugin, pItem, 
                                 m_strUserAgent.empty() 
                                        ? NULL 
                                        : m_strUserAgent.c_str());
    CAutoLock autoLock(&m_CritSec);
    if (pClip->GetHandle()) m_PreloadList.push_back(pClip);
    else pClip->Release();
    
    std::map<PlayListItem*, CEvent*>::iterator itorSync = m_CmdSyncList.find(pItem);
    if (itorSync != m_CmdSyncList.end())
    {
        if (itorSync->second) itorSync->second->Set();
        m_CmdSyncList.erase(itorSync);
    }  
    return true;
}

void PreloadCmdProcessor::ReleaseTopItemFromList()
{ 
    CAutoLock autoLock(&m_CritSec);
    HLSClip* pClip = m_PreloadList.front();
    if (pClip) pClip->Release();
    m_PreloadList.pop_front();
}

////////////////////////////////////////////////////////////////////////////////
