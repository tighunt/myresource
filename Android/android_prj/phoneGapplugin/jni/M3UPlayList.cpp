////////////////////////////////////////////////////////////////////////////////
// General M3U Play List 
//
// Author: zackchiu@realtek.com
//
// Created: 10/20/2010
////////////////////////////////////////////////////////////////////////////////

#include "M3UPlayList.h"
#include "M3ULineTokenParser.h"
//#include "StrLib.h"
#include "auto_xxx_ptr.h"
#ifdef WIN32
    #include "LogTools.h" // For DumpWinDbgMsgA
#endif

////////////////////////////////////////////////////////////////////////////////
// Name space declaration
//
/*using namespace rtk;
using namespace parser;
using namespace m3u;
using namespace str;
*/
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// Macro definition
//
//#define DUMP_DEBUG_MESSAGE
#ifdef DUMP_DEBUG_MESSAGE
    #ifdef WIN32
        #define zDebug DumpWinDbgMsgA 
    #else
        #define zDebug printf
    #endif
#else
    #ifndef WIN32
        static void zDebug(...) { };
    #endif
#endif

#ifdef WIN32
    #define zPrint DumpWinDbgMsgA
#else
    #define zPrint printf
#endif

#define MAX_CACHED_PLAY_LIST    10


char* ExtractFilePath(const char* pFullPath)
{
    if (!pFullPath) return NULL;
    
    size_t i = 0, j = 0;
    while(pFullPath[i++])
    {
        // Find the last occurred '/' in pFullPath
        if (pFullPath[i] == FILE_PATH_SLASH_CHAR) j = i;
    }
    if (!j) return NULL;
    
    char* pBuf = new char[j + 1];
    memcpy(pBuf, pFullPath, sizeof(char) * j);
    pBuf[j] = 0;
    return pBuf;
}


////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// M3UPlayListParser Implmentation
//
M3UPlayListParser::M3UPlayListParser(): PlayListParser(LineTokenPaserSet(&GetLineTokenParserCounts, 
                                                                         &GetLineTokenParserInfo))
{
}

M3UPlayListParser::~M3UPlayListParser()
{
}

bool M3UPlayListParser::IsPlayableFile(const char* pLine)
{
    return !IsM3UExtTag(pLine);
}

////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// M3UPlayList Implmentation
//
M3UPlayList::M3UPlayList(): m_iCurIndex(-1),
                            m_pM3UParser(NULL),
                            m_pCurItem(NULL),
                            m_pEmbeddedPlayList(NULL)

{
    zDebug("M3UPlayList::M3UPlayList(): 0x%x.....\n", (int)this);
    m_pCurPlayList = this;
}

M3UPlayList::~M3UPlayList()
{
    zDebug("M3UPlayList::~M3UPlayList(): (0x%x)%s.....\n", 
           (int)this, m_PlayListURL.c_str());
    Reset();
}

PlayListItem* M3UPlayList::GetNextFile()
{
    PlayListItem* pItem = NULL;
    // Get next item and check if it is playable for user.
    while ((pItem = GetNextItem()) && !CheckIfValidItem(pItem)) { ; }
    if (!pItem) return pItem;
    
    if (m_pCurItem == pItem) 
        pItem = GetNextFile();
    else
        m_pCurItem = pItem;
    return pItem;
}

PlayListItem* M3UPlayList::GetPrevFile()
{
    PlayListItem* pItem = NULL;
    // Get previous item and check if it is playable for user.
    while ((pItem = GetPrevItem()) && !CheckIfValidItem(pItem)) { ; }
    if (!pItem) return pItem;
    
    if (m_pCurItem == pItem)
        pItem = GetPrevFile();
    else
        m_pCurItem = pItem;
    return pItem;
}

void M3UPlayList::JumpToHead()
{
    zDebug("M3UPlayList::JumpToHead(): %s.....\n", m_PlayListURL.c_str());
    if (IsPlayingEmbeddedPlayList()) 
        LeaveEmbeddedPlayList(m_pEmbeddedPlayList);
    m_iCurIndex = -1;
    m_pCurItem = NULL;
    m_pCurPlayList = this;
    
    std::map<PlayListItem*, M3UPlayList*>::iterator itor, itorEnd = m_PlayListMap.end();
    for(itor = m_PlayListMap.begin(); itor != itorEnd; ++itor)
        if (itor->second) itor->second->JumpToHead();
}

void M3UPlayList::JumpToTail()
{
    zDebug("M3UPlayList::JumpToTail(): %s.....\n", m_PlayListURL.c_str());
    if (IsPlayingEmbeddedPlayList()) 
        LeaveEmbeddedPlayList(m_pEmbeddedPlayList);
    m_iCurIndex = m_pM3UParser->GetItemCounts();
    m_pCurItem = NULL;
    m_pCurPlayList = this;
    
    std::map<PlayListItem*, M3UPlayList*>::iterator itor, itorEnd = m_PlayListMap.end();
    for(itor = m_PlayListMap.begin(); itor != itorEnd; ++itor)
        if (itor->second) itor->second->JumpToTail();
}

void M3UPlayList::PushContext()
{
    zDebug("M3UPlayList::PushContext(): %s.....\n", m_PlayListURL.c_str());
    m_ContextStack.push_back((long)m_iCurIndex);
    m_ContextStack.push_back((long)m_pCurItem);
    m_ContextStack.push_back((long)m_pEmbeddedPlayList);
    m_ContextStack.push_back((long)m_pCurPlayList);
    
    std::map<PlayListItem*, M3UPlayList*>::iterator itor, itorEnd = m_PlayListMap.end();
    for(itor = m_PlayListMap.begin(); itor != itorEnd; ++itor)
        if (itor->second) itor->second->PushContext();
}

void M3UPlayList::PopContext()
{
    zDebug("M3UPlayList::PopContext(): %s.....\n", m_PlayListURL.c_str());
    m_pCurPlayList = (M3UPlayList*) m_ContextStack.back();
    m_ContextStack.pop_back();
    m_pEmbeddedPlayList = (PlayListItem*) m_ContextStack.back();
    m_ContextStack.pop_back();
    m_pCurItem = (PlayListItem*) m_ContextStack.back();
    m_ContextStack.pop_back();
    m_iCurIndex = m_ContextStack.back();
    m_ContextStack.pop_back();
    
    std::map<PlayListItem*, M3UPlayList*>::iterator itor, itorEnd = m_PlayListMap.end();
    for(itor = m_PlayListMap.begin(); itor != itorEnd; ++itor)
        if (itor->second) itor->second->PopContext();
}

const char* M3UPlayList::GetFileURL(PlayListItem* pItem)
{
    if (!pItem) return NULL;
    M3UPlayList* pItemOwner = (M3UPlayList*)pItem->pOwner;
    if (pItemOwner != this) return pItemOwner->GetFileURL(pItem);
    
    switch(pItem->iTag)
    {
        case TAG_FILE: 
            return MakeFullPath((const char*)pItem->pItemInfo);
            
        case TAG_EXT_INF: 
            return MakeFullPath(((M3UExtInf*)pItem->pItemInfo)->pURI);
    }
    return NULL;
}

int M3UPlayList::GetFileDuration(PlayListItem* pItem)
{
    return pItem && pItem->iTag == TAG_EXT_INF
                    ? ((M3UExtInf*)pItem->pItemInfo)->iDuration
                    : -1;
}

const char* M3UPlayList::GetFileDescription(PlayListItem* pItem)
{
    return pItem && pItem->iTag == TAG_EXT_INF
                    ? ((M3UExtInf*)pItem->pItemInfo)->pTitle
                    : NULL;
}

void M3UPlayList::SetBaseURL(const char* pURL)
{
    if (!pURL) return;
    m_szBaseURL.assign(pURL);
    if (m_szBaseURL[m_szBaseURL.length() - 1] != FILE_PATH_SLASH_CHAR)
        m_szBaseURL.append(FILE_PATH_SLASH);
}

bool M3UPlayList::SetContent(const char* pURL)
{
    m_PlayListURL = pURL;
    Reset();
    m_pM3UParser = CreateParser();
    if (!m_pM3UParser || 
        !m_pM3UParser->SetContent(pURL) ||
        !SetItemOwner()) return false;
#ifdef WIN32
    char* pPath = ExtractFilePath(pURL, '/');
    if (!pPath) pPath = ExtractFilePath(pURL, '\\');
    auto_array_ptr<char> pBaseURL(pPath);
#else
    auto_array_ptr<char> pBaseURL(ExtractFilePath(pURL));
#endif
    if (!pBaseURL) { Reset(); return false; }
    
    SetBaseURL(pBaseURL);
    return true;
}

bool M3UPlayList::SetContent(const char* pContentBuffer, UINT iBufSize)
{
    Reset();
    m_pM3UParser = CreateParser();
    return m_pM3UParser && 
           m_pM3UParser->SetContent(pContentBuffer, iBufSize) &&
           SetItemOwner();
}

void M3UPlayList::Reset()
{
    zDebug("M3UPlayList::Reset(): %s.....\n", m_PlayListURL.c_str());
    ReleasePlayListMap();
    m_iCurIndex = -1;
    m_szBaseURL.clear();
    m_szFullPath.clear();
    if (m_pM3UParser) 
    { 
        delete m_pM3UParser; 
        m_pM3UParser = NULL; 
    }
    m_pCurItem = NULL;
    m_pEmbeddedPlayList = NULL;
    m_pCurPlayList = this;
    RemoveCachedPlayList();
    zDebug("M3UPlayList::Reset(): Done. %s.....\n", m_PlayListURL.c_str());
}

void M3UPlayList::MoveToCachedPlayList(M3UPlayList* pPlayList)
{
    if (!pPlayList) return;
    while (m_CachedPlayList.size() >= MAX_CACHED_PLAY_LIST)
    {
        M3UPlayList* pOldPlayList = m_CachedPlayList.front();
        if (pOldPlayList) 
        {
            zDebug("M3UPlayList::MoveToCachedPlayList(): Counts(%d), delete (%s)\n", 
                   m_CachedPlayList.size(), pOldPlayList->GetURL());
            delete pOldPlayList;
        }
        m_CachedPlayList.pop_front();
    }
    m_CachedPlayList.push_back(pPlayList);
}

void M3UPlayList::RemoveCachedPlayList()
{
    zDebug("M3UPlayList::RemoveCachedPlayList(): %s.....\n", m_PlayListURL.c_str());
    std::list<M3UPlayList*>::iterator itor, itorEnd = m_CachedPlayList.end();
    for (itor = m_CachedPlayList.begin(); itor != itorEnd; ++itor)
        if (*itor) delete (*itor);
    m_CachedPlayList.clear();
    zDebug("M3UPlayList::RemoveCachedPlayList(): Done. %s.....\n", m_PlayListURL.c_str());
}

PlayListItem* M3UPlayList::GetNextItem()
{
    if (!m_pM3UParser) return NULL;
    
    if (m_pCurPlayList != this) // Is this an embedded play list?
    {
        PlayListItem* pItem = m_pCurPlayList->GetNextFile();
        if (!pItem) // No more items in this play list.
        {            
            if (m_pEmbeddedPlayList) 
            {
                m_pCurPlayList->JumpToTail();
                LeaveEmbeddedPlayList(m_pEmbeddedPlayList);
            }
            m_pCurPlayList = this; // Back to the original play list.
        }
        else return pItem;
    }
       
    return m_iCurIndex < (int)(m_pM3UParser->GetItemCounts() - 1)
                        ? (*m_pM3UParser)[++m_iCurIndex]
                        : NULL;
}

PlayListItem* M3UPlayList::GetPrevItem()
{
    if (!m_pM3UParser) return NULL;
    
    if (m_pCurPlayList != this) // Is this an embedded play list?
    {
        PlayListItem* pItem = m_pCurPlayList->GetPrevFile();
        if (!pItem) // No more items in this play list.
        {            
            if (m_pEmbeddedPlayList) 
            {
                m_pCurPlayList->JumpToHead();
                LeaveEmbeddedPlayList(m_pEmbeddedPlayList);
            }
            m_pCurPlayList = this; // Back to the original play list.
        }
        else return pItem;
    }
    
    return m_iCurIndex > 0 ? (*m_pM3UParser)[--m_iCurIndex] : NULL;
}

void M3UPlayList::ReleasePlayListMap()
{
    zDebug("M3UPlayList::ReleasePlayListMap(): %s.....\n", m_PlayListURL.c_str());
    std::map<PlayListItem*, M3UPlayList*>::iterator itor, itorEnd = m_PlayListMap.end();
    for(itor = m_PlayListMap.begin(); itor != itorEnd; ++itor)
        if (itor->second) delete itor->second;
    m_PlayListMap.clear();
    zDebug("M3UPlayList::ReleasePlayListMap(): Done. %s.....\n", m_PlayListURL.c_str());
}

const char* M3UPlayList::MakeFullPath(const char* pURI)
{
    if (!pURI || !(*pURI)) return NULL;
    //zDebug("M3UPlayList::MakeFullPath(): m_szBaseURL=%s\n", m_szBaseURL.c_str());
    
    // This is a full path, just return it.
    //
    if (strstr(pURI, "://")) return pURI;
    
    m_szFullPath = m_szBaseURL;
    if (*pURI == FILE_PATH_SLASH_CHAR) pURI++;
    m_szFullPath.append(pURI);
    return m_szFullPath.c_str();
}

bool M3UPlayList::SetItemOwner()
{
    if (!m_pM3UParser) return false;
    
    UINT iItemCounts = m_pM3UParser->GetItemCounts();
    for (UINT i = 0; i < iItemCounts; i++)
        (*m_pM3UParser)[i]->pOwner = this;
    return true;
}

bool M3UPlayList::LoadPlayList(PlayListItem* pItem, bool bReloadIfExist)
{   
    const char* pURL = GetFileURL(pItem);
    if (!pURL) return false;
    zDebug("M3UPlayList::LoadPlayList(): Load (%s), Reload(%d)\n", pURL, (int)bReloadIfExist);
    
    std::map<PlayListItem*, M3UPlayList*>::iterator itor = m_PlayListMap.find(pItem);
    if (itor != m_PlayListMap.end()) 
    { 
        if (m_pEmbeddedPlayList && itor->first != m_pEmbeddedPlayList) 
            LeaveEmbeddedPlayList(m_pEmbeddedPlayList);  
        if (!bReloadIfExist)
        {          
            m_pCurPlayList = itor->second; 
            if (!m_pEmbeddedPlayList) EnterEmbeddedPlayList(itor->first);
            zDebug("M3UPlayList::LoadPlayList(): Return existed (%s)\n", pURL);
            return true; 
        }
        else
        {
            if (itor->second) 
            {
                // We need to cache the old play list, since someone maybe use it now.
                //
                MoveToCachedPlayList(itor->second);
                zDebug("M3UPlayList::LoadPlayList(): Reload existed (%s)\n", pURL);
            }
        }
    }
    
    auto_ptr<M3UPlayList> pNewPlayList(CreatePlayListInstance());
    if (!pNewPlayList->SetContent(pURL)) return false;

    if (m_pEmbeddedPlayList && m_pEmbeddedPlayList != pItem) 
        LeaveEmbeddedPlayList(m_pEmbeddedPlayList);    
    
    m_PlayListMap[pItem] = pNewPlayList.release();
    m_pCurPlayList = m_PlayListMap[pItem];
    EnterEmbeddedPlayList(pItem);
    zDebug("M3UPlayList::LoadPlayList(): Load (%s) Done.\n", pURL);
    return true;
}

bool M3UPlayList::CheckIfValidItem(PlayListItem* pItem)
{
    if (IsPlayList(pItem) && LoadPlayList(pItem, false)) return false;
    return pItem && (pItem->iTag == TAG_FILE || pItem->iTag == TAG_EXT_INF);
}

bool M3UPlayList::IsPlayList(PlayListItem* pItem)
{
    if (!pItem || pItem->iTag != TAG_FILE) return false;
    
    std::string szURL(GetFileURL(pItem));
    return szURL.rfind(".m3u") != std::string::npos ||
           szURL.rfind(".m3u8") != std::string::npos;
}

void M3UPlayList::EnterEmbeddedPlayList(PlayListItem* pItem)
{
    zDebug("M3UPlayList::EnterEmbeddedPlayList(): URL=%s......\n", GetFileURL(pItem));
    m_pEmbeddedPlayList = pItem;
}

void M3UPlayList::LeaveEmbeddedPlayList(PlayListItem* pItem)
{
    zDebug("M3UPlayList::LeaveEmbeddedPlayList(): URL=%s......\n", GetFileURL(pItem));
    m_pEmbeddedPlayList = NULL;
}

////////////////////////////////////////////////////////////////////////////////
