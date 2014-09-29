////////////////////////////////////////////////////////////////////////////////
// HLS(HTTP Live Streaming) Play List. HLS uses M3U extension.
//
// Author: zackchiu@realtek.com
//
// Created: 10/22/2010
////////////////////////////////////////////////////////////////////////////////

#include "HLSPlayList.h"
#include "M3ULineTokenParser.h"
#include <map>
#include <unistd.h>

#ifdef WIN32
    #include "LogTools.h" // For DumpWinDbgMsgA
#endif
//#include "OSAL.h"

////////////////////////////////////////////////////////////////////////////////
// Name space declaration
//

/*
using namespace rtk;
using namespace parser;
using namespace m3u;
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

#define INVALID_BAND_WIDTH              0xFFFFFFFF
#define RELOAD_PLAYLIST_WAITING_TIME    3000 // in milli-seconds.
#define RELOAD_PLAYLIST_RETRY_COUNTS    20

////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// Helper Functions
//
inline M3UExtStreamInf* GetStreamInfo(PlayListItem* pItem)
{
    return (M3UExtStreamInf*)pItem->pItemInfo;
}

inline UINT GetStreamBandWidth(PlayListItem* pItem)
{
    return pItem ? GetStreamInfo(pItem)->iBandWidth : 0;
}

inline UINT GetStreamId(PlayListItem* pItem)
{
    return GetStreamInfo(pItem)->iProgId;
}

inline bool IsEqualStream(PlayListItem* pItem1, PlayListItem* pItem2)
{
    return GetStreamId(pItem1) == GetStreamId(pItem2);
}

////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// M3UPlayListParser Implmentation
//
HLSPlayListParser::HLSPlayListParser()
{
}

HLSPlayListParser::~HLSPlayListParser()
{
}

////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// HLSPlayList Implmentation
//
HLSPlayList::HLSPlayList(): m_iBandWidth(INVALID_BAND_WIDTH),
                            m_iCurStreamId(INVALID_STREAM_ID),
                            m_iCurDuration(0),
                            m_iTotalDuration(0),
                            m_bCheckBandWidth(false),
                            m_pObserver(NULL),
                            m_pCurKeyItem(NULL),
                            m_iSequenceID(INVALID_SEQUENCE_ID),
                            m_pReloadItem(NULL)
{
    zDebug("HLSPlayList::HLSPlayList(): .....\n");
}

HLSPlayList::~HLSPlayList()
{
    zDebug("HLSPlayList::~HLSPlayList(): .....\n");
}

const char* HLSPlayList::GetFileURL(PlayListItem* pItem)
{
    if (!pItem) return NULL;
    if (pItem->iTag != TAG_EXT_STREAM_INF)
        return M3UPlayList::GetFileURL(pItem);

    HLSPlayList* pItemOwner = (HLSPlayList*)pItem->pOwner;
    if (pItemOwner != this) return pItemOwner->GetFileURL(pItem);

    return MakeFullPath(GetStreamInfo(pItem)->pURI);
}

bool HLSPlayList::SetContent(const char* pURL)
{
    return M3UPlayList::SetContent(pURL) &&
           m_pM3UParser &&
           (*m_pM3UParser)[0] &&
           (*m_pM3UParser)[0]->iTag == TAG_EXT_M3U;
}

bool HLSPlayList::SetContent(const char* pContentBuffer, UINT iBufSize)
{
    return M3UPlayList::SetContent(pContentBuffer, iBufSize) &&
           m_pM3UParser &&
           (*m_pM3UParser)[0] &&
           (*m_pM3UParser)[0]->iTag == TAG_EXT_M3U;
}

void HLSPlayList::Reset()
{
    m_iBandWidth = INVALID_BAND_WIDTH;
    m_iCurStreamId = INVALID_STREAM_ID;
    m_iCurDuration = 0;
    m_iTotalDuration = 0;
    m_bCheckBandWidth = false;
    m_pCurKeyItem = NULL;
    m_iSequenceID = INVALID_SEQUENCE_ID;
    m_HLSItemInfo.clear();
    m_pReloadItem = NULL;
    M3UPlayList::Reset();
}

PlayListItem* HLSPlayList::GetNextFile()
{
    int iDuration = GetFileDuration(GetCurrentFile());    
    UINT iSeqID = GetSequenceID(GetCurrentFile());
    bool bChangeStream = CheckBandWidthAndReloadPlayList();
    PlayListItem* pItem = M3UPlayList::GetNextFile();
    if (iDuration > 0) 
    { 
        m_iCurDuration += (UINT)iDuration; 
        if (m_iTotalDuration > 0 && m_iCurDuration > m_iTotalDuration) 
            m_iCurDuration = m_iTotalDuration;
    }
    if (pItem && pItem->pOwner == this) 
    {
        // Every play list will maintain its own sequence number and key.
        // So we update these infomation when we are handling ourself, 
        // not handling an embedded play list.
        //
        UpdateSequenceID(pItem);
        UpdateItemKey(pItem);
    }

    // If this is a live stream, we need to reload play list.
    // The exception is when we are changing stream.
    //
    static UINT iRecursiveCounter = 0;
    if (!pItem && m_pReloadItem && !bChangeStream) 
    {
        zDebug("HLSPlayList::GetNextFile(): Reload(%s)(%d)(%d)\n", 
               GetFileURL(m_pReloadItem), iSeqID, iRecursiveCounter);
        LoadPlayList(m_pReloadItem, true);
        m_pReloadItem = NULL;
        if (iRecursiveCounter > 0)
        {
            zPrint("HLSPlayList::GetNextFile(): The playlist's content dose not change."\
                   " Sleep for the next reload. Retry counter=%d\n", iRecursiveCounter);
            if (iRecursiveCounter == RELOAD_PLAYLIST_RETRY_COUNTS) return NULL;
            usleep(RELOAD_PLAYLIST_WAITING_TIME);
        }
        iRecursiveCounter++;
        
        while ((pItem = GetNextFile()))
        {
            UINT iNextSeqID = GetSequenceID(pItem);
            zDebug("HLSPlayList::GetNextFile(): Serach(%s)(%d/%d)\n", 
                   GetFileURL(pItem), iSeqID, iNextSeqID);
            if (iSeqID == iNextSeqID)
            {
                pItem = GetNextFile();
                break;
            }
            else if (iNextSeqID > iSeqID) // The sequence ID of live stream is continuous
                break;
        }
    }
    else m_pReloadItem = NULL;
    iRecursiveCounter = 0;

    return pItem;
}

PlayListItem* HLSPlayList::GetPrevFile()
{
    CheckBandWidthAndReloadPlayList();
    PlayListItem* pItem = M3UPlayList::GetPrevFile();
    int iDuration = GetFileDuration(pItem);
    if (iDuration > 0) 
    {
        if (m_iCurDuration >= (UINT)iDuration)
            m_iCurDuration -= (UINT)iDuration; 
        else 
            m_iCurDuration = 0;
    }
    return pItem;
}

void HLSPlayList::JumpToHead()
{
    M3UPlayList::JumpToHead();
    m_iCurDuration = 0;
    m_pCurKeyItem = NULL;
    m_iSequenceID = INVALID_SEQUENCE_ID;
}

void HLSPlayList::JumpToTail()
{
    // We need to do this in HLS play list.
    // Since We need to go over each item for updating sequence ID and key.
    //
    while (GetNextFile()) { ; };
    
    M3UPlayList::JumpToTail();
    m_iCurDuration = GetTotalDuration();
    m_pCurKeyItem = NULL;
    m_iSequenceID = INVALID_SEQUENCE_ID;
}

void HLSPlayList::PushContext()
{
    M3UPlayList::PushContext();
    m_ContextStack.push_back((long)m_iCurDuration);
    m_ContextStack.push_back((long)m_pCurKeyItem);
    m_ContextStack.push_back((long)m_iSequenceID);
    m_ContextStack.push_back((long)m_iCurStreamId);
}

void HLSPlayList::PopContext()
{
    m_iCurStreamId = (UINT) m_ContextStack.back();
    m_ContextStack.pop_back();
    m_iSequenceID = (UINT) m_ContextStack.back();
    m_ContextStack.pop_back();
    m_pCurKeyItem = (PlayListItem*) m_ContextStack.back();
    m_ContextStack.pop_back();
    m_iCurDuration = (UINT) m_ContextStack.back();
    m_ContextStack.pop_back();
    M3UPlayList::PopContext();
}

PlayListItem* HLSPlayList::SeekByTime(UINT iTimeOffset)
{
    if (iTimeOffset > GetTotalDuration()) return NULL;
    if (IsLiveStream())
    {
        zDebug("HLSPlayList::SeekByTime(): Skip since this is live stream!\n");
        return NULL;
    }

    PlayListItem* pSelectedItem = GetCurrentFile();
    bool bGoNext = iTimeOffset >= m_iCurDuration;
    int iDuration = 0;
    do
    { 
        iDuration = GetFileDuration(pSelectedItem); 
        if (iDuration > 0 && 
            m_iCurDuration <= iTimeOffset && 
            (m_iCurDuration + (UINT)iDuration) >= iTimeOffset)
            break;
        pSelectedItem = bGoNext ? GetNextFile() : GetPrevFile();
    }while (pSelectedItem);
    return pSelectedItem;
}

PlayListItem* HLSPlayList::SeekBySequenceID(UINT iSequenceID)
{
    zDebug("HLSPlayList::SeekBySequenceID(): %d\n", iSequenceID);
    PlayListItem* pItem = GetCurrentFile();
    do
    {
        UINT iNextSeqID = GetSequenceID(pItem);
        zDebug("HLSPlayList::SeekBySequenceID(): %s(%d)\n", 
               GetFileURL(pItem), iNextSeqID);
               
        // The sequence ID of live stream is continuous
        if (iSequenceID <= iNextSeqID) break;
        pItem = GetNextFile();
    }while (pItem);
    zDebug("HLSPlayList::SeekBySequenceID(): Done. %s(%d)\n", 
           GetFileURL(pItem), GetSequenceID(pItem));
    return pItem;
}

UINT HLSPlayList::GetSequenceID(PlayListItem* pItem)
{
    if (!pItem) return 0;
    HLSPlayList* pItemOwner = (HLSPlayList*)pItem->pOwner;
    return (pItemOwner == this)
                ? m_HLSItemInfo[pItem].iSequenceID
                : pItemOwner->GetSequenceID(pItem);
}

UINT HLSPlayList::GetIV(PlayListItem* pItem, BYTE* pBuf, UINT iBufSize)
{
    if (!pItem || !pBuf || !iBufSize) return 0;
    
    HLSPlayList* pItemOwner = (HLSPlayList*)pItem->pOwner;
    if (pItemOwner != this) return pItemOwner->GetIV(pItem, pBuf, iBufSize);
    if (!m_HLSItemInfo[pItem].pKeyItem) return 0;
    
    M3UExtKeyInf* pKeyInfo = ((M3UExtKeyInf*)(m_HLSItemInfo[pItem].pKeyItem->pItemInfo));
    if (!pKeyInfo || pKeyInfo->iMethod == M3U_EXT_ENCRYPT_METHOD_NONE ||
        (pKeyInfo->iMethod == M3U_EXT_ENCRYPT_METHOD_AES_128 && 
         iBufSize < AES_128_IV_SIZE)) 
        return 0;
    
    if (pKeyInfo->bValidIV)
    {
        memcpy(pBuf, pKeyInfo->IV, sizeof(pKeyInfo->IV));
        return sizeof(pKeyInfo->IV);
    }
    
    // According to HLS spec.
    // If no IV existed, we use the sequence number as IV.
    // Put the sequence number in big-endian order. 
    // The other bytes are padding as zero.
    //
    memset(pBuf, 0, iBufSize);
    UINT iID = GetSequenceID(pItem);
    pBuf[15] = iID & 0xFF;
    pBuf[14] = (iID >> 8) & 0xFF;
    pBuf[13] = (iID >> 16) & 0xFF;
    pBuf[12] = (iID >> 24) & 0xFF;
    return AES_128_IV_SIZE;
}

const char* HLSPlayList::GetKeyURI(PlayListItem* pItem)
{
    if (!pItem) return NULL;
    
    HLSPlayList* pItemOwner = (HLSPlayList*)pItem->pOwner;
    if (pItemOwner != this) return pItemOwner->GetKeyURI(pItem);
    if (!m_HLSItemInfo[pItem].pKeyItem) return NULL;
    
    M3UExtKeyInf* pKeyInfo = ((M3UExtKeyInf*)(m_HLSItemInfo[pItem].pKeyItem->pItemInfo));
    return pKeyInfo->pKeyURI;
}

int HLSPlayList::GetEncryptMethod(PlayListItem* pItem)
{
    if (!pItem) return M3U_EXT_ENCRYPT_METHOD_NONE;
    
    HLSPlayList* pItemOwner = (HLSPlayList*)pItem->pOwner;
    if (pItemOwner != this) return pItemOwner->GetEncryptMethod(pItem);
    if (!m_HLSItemInfo[pItem].pKeyItem) return M3U_EXT_ENCRYPT_METHOD_NONE;
    
    M3UExtKeyInf* pKeyInfo = ((M3UExtKeyInf*)(m_HLSItemInfo[pItem].pKeyItem->pItemInfo));
    return pKeyInfo->iMethod;
}

PlayListItem* HLSPlayList::GetKeyItem(PlayListItem* pItem)
{
    if (!pItem) return NULL;
    HLSPlayList* pItemOwner = (HLSPlayList*)pItem->pOwner;
    if (pItemOwner != this) return pItemOwner->GetKeyItem(pItem);
    return m_HLSItemInfo[pItem].pKeyItem;
}

UINT HLSPlayList::GetSrtCounts()
{
    PlayListItem* pSrtItem = m_pM3UParser->Find(TAG_EXT_SRT_INF);
    if (!pSrtItem) return 0;
    
    UINT iCounts = 0;
    while(pSrtItem)
    {
        iCounts++;
        pSrtItem = m_pM3UParser->FindNext(pSrtItem);
    }
    
    return iCounts;
}

bool HLSPlayList::GetSrtInfo(SrtInfo& srtInfo, UINT iIndex)
{
    PlayListItem* pSrtItem = m_pM3UParser->Find(TAG_EXT_SRT_INF);
    if (!pSrtItem) return false;
    
    UINT i = 0;
    while(pSrtItem)
    {
        if (i == iIndex)
        {
            M3UExtSrtInf* pSrtInf = (M3UExtSrtInf*)pSrtItem->pItemInfo;
            if (!pSrtInf || !pSrtInf->pSrtLanguage || !pSrtInf->pSrtURL)
                return false;
            srtInfo.pLanguage = pSrtInf->pSrtLanguage;
            srtInfo.pURL = pSrtInf->pSrtURL;
            return true;
        }
        
        i++;
        pSrtItem = m_pM3UParser->FindNext(pSrtItem);
    }
    
    return false;
}

bool HLSPlayList::GetThumbnailInfo(ThumbnailInfo& thumbnailInfo)
{
    PlayListItem* pThumbnailItem = m_pM3UParser->Find(TAG_EXT_THUMBNAIL_INF);
    if (!pThumbnailItem) return false;
    
    M3UExtThumbnailInf* pThumbnailInf = (M3UExtThumbnailInf*)pThumbnailItem->pItemInfo;
    if (!pThumbnailInf->iCounts || !pThumbnailInf->iInterval || !pThumbnailInf->pThumbnailURL)
        return false;

    thumbnailInfo.iCounts = pThumbnailInf->iCounts;
    thumbnailInfo.iInterval = pThumbnailInf->iInterval;
    thumbnailInfo.pURL = pThumbnailInf->pThumbnailURL;
    return true;
}

bool HLSPlayList::IsLiveStream()
{
    return m_pCurPlayList == this 
                ? !m_pM3UParser->Find(TAG_EXT_END_LIST) && !m_pM3UParser->Find(TAG_EXT_STREAM_INF)
                : ((HLSPlayList*)m_pCurPlayList)->IsLiveStream();
}

UINT HLSPlayList::GetTotalDuration()
{
    if (m_iTotalDuration > 0) return m_iTotalDuration;
    if (IsLiveStream()) { m_iTotalDuration = 0xFFFFFFFF; return m_iTotalDuration; }
    
    PushContext();
    JumpToHead();
    PlayListItem* pItem = NULL;
    UINT iTotalDuration = 0;
    int iDuration = 0;
    while((pItem = GetNextFile()))
    {
        iDuration = GetFileDuration(pItem);
        if (iDuration > 0) iTotalDuration += (UINT)iDuration;
    }
    PopContext();
    
    m_iTotalDuration = iTotalDuration;
    return iTotalDuration;
}

bool HLSPlayList::LoadPlayList(PlayListItem* pItem, bool bReloadIfExist)
{
    if (!pItem) return false;
    if (pItem->iTag != TAG_EXT_STREAM_INF) return M3UPlayList::LoadPlayList(pItem, bReloadIfExist);
    if (m_iCurStreamId == GetStreamId(pItem)) return false;

    PlayListItem* pSelectedItem = FindPlayListByBandWidth(GetStreamId(pItem), m_iBandWidth);
    zDebug("HLSPlayList::LoadPlayList(): %d/%d\n", 
           GetStreamBandWidth(pSelectedItem ? pSelectedItem : pItem),
           m_iBandWidth);
    return M3UPlayList::LoadPlayList(pSelectedItem ? pSelectedItem : pItem, bReloadIfExist);
}

bool HLSPlayList::IsPlayList(PlayListItem* pItem)
{
    return pItem->iTag == TAG_EXT_STREAM_INF
                        ? true
                        : M3UPlayList::IsPlayList(pItem);
}

void HLSPlayList::EnterEmbeddedPlayList(PlayListItem* pItem)
{
    M3UPlayList::EnterEmbeddedPlayList(pItem);
    if (!pItem || pItem->iTag != TAG_EXT_STREAM_INF) return;
    m_iCurStreamId = GetStreamId(pItem);
    zDebug("HLSPlayList::EnterEmbeddedPlayList(): %d\n", m_iCurStreamId);
}

void HLSPlayList::LeaveEmbeddedPlayList(PlayListItem* pItem)
{
    M3UPlayList::LeaveEmbeddedPlayList(pItem);
    if (!pItem || pItem->iTag != TAG_EXT_STREAM_INF) return;
    m_iCurStreamId = INVALID_STREAM_ID;
    m_pReloadItem = IsLiveStream() ? pItem : NULL;
    zDebug("HLSPlayList::LeaveEmbeddedPlayList(): %d, 0x%x\n", 
           m_iCurStreamId, (int)m_pReloadItem);
}

bool HLSPlayList::CheckIfValidItem(PlayListItem* pItem)
{
    if (!pItem || pItem->iTag != TAG_EXT_KEY_INF) 
        return M3UPlayList::CheckIfValidItem(pItem);
    m_pCurKeyItem = pItem;
    return false;
}

PlayListItem* HLSPlayList::FindPlayListByBandWidth(UINT iStreamId, UINT iActualBandWidth)
{
    // Get the first matched item.
    PlayListItem* pSelectedItem = m_pM3UParser->Find(TAG_EXT_STREAM_INF);
    if (!pSelectedItem) return NULL;

    // Find a matched bandwidth item.
    //
    PlayListItem* pNextStreamInfItem = pSelectedItem;
    std::map<UINT, PlayListItem*, std::less<UINT> > bandWidthMap; // Sort item by bandwidth.
    UINT iDefaultAvgBandWidth = 0;
    while(pNextStreamInfItem)
    {
        if (iStreamId == GetStreamId(pNextStreamInfItem))
        {
            bandWidthMap[GetStreamBandWidth(pNextStreamInfItem)] = pNextStreamInfItem;
            iDefaultAvgBandWidth += GetStreamBandWidth(pNextStreamInfItem);
        }
        pNextStreamInfItem = m_pM3UParser->FindNext(pNextStreamInfItem);
    }
 
    // When no correct bandwidth is assigned, we use the average value.
    if (iActualBandWidth == INVALID_BAND_WIDTH && 
        bandWidthMap.size() > 1 && 
        iDefaultAvgBandWidth > 0)
        iActualBandWidth = iDefaultAvgBandWidth / bandWidthMap.size();
    if (bandWidthMap[iActualBandWidth]) return bandWidthMap[iActualBandWidth];
  
    std::map<UINT, PlayListItem*, std::less<UINT> >::iterator itor = bandWidthMap.find(iActualBandWidth);
    std::map<UINT, PlayListItem*, std::less<UINT> >::iterator itorPrev = itor;
    --itorPrev;
    return (itorPrev == bandWidthMap.end()) ? (++itor)->second : itorPrev->second;
}

bool HLSPlayList::CheckBandWidthAndReloadPlayList()
{
    if (!m_bCheckBandWidth || 
        !IsPlayingEmbeddedPlayList() || 
        m_pCurPlayList == this || 
        m_iCurStreamId != GetStreamId(m_pEmbeddedPlayList)) 
    {
        m_bCheckBandWidth = false;
        return false;
    }

    UINT iCurDuration = ((HLSPlayList*)m_pCurPlayList)->GetDuration();
    if (iCurDuration < 0) return false; 
    UINT iSeqID = GetSequenceID(GetCurrentFile());

    PlayListItem* pItem = FindPlayListByBandWidth(m_iCurStreamId, m_iBandWidth);
    zDebug("HLSPlayList::CheckBandWidthAndReloadPlayList(): %s\n", GetFileURL(pItem));
    m_bCheckBandWidth = false;
    if (pItem && pItem != m_pEmbeddedPlayList && M3UPlayList::LoadPlayList(pItem, IsLiveStream()))
    {
        if (IsLiveStream())
            m_pCurItem = ((HLSPlayList*)m_pCurPlayList)->SeekBySequenceID(iSeqID);
        else
            m_pCurItem = ((HLSPlayList*)m_pCurPlayList)->SeekByTime(iCurDuration);
        m_iCurDuration = ((HLSPlayList*)m_pCurPlayList)->GetDuration();
        if (m_pObserver) m_pObserver->ChangeBandWidth(m_iBandWidth);
        return true;
    }
    return false;
}

void HLSPlayList::UpdateSequenceID(PlayListItem* pItem)
{
    if (!pItem || pItem->pOwner != this) return;
    
    if (m_iSequenceID == INVALID_SEQUENCE_ID)
    {
        PlayListItem* pItem = m_pM3UParser->Find(TAG_EXT_MEDIA_SEQUENCE);
        if (!pItem) m_iSequenceID = 0;
        else m_iSequenceID = *((UINT*)(pItem->pItemInfo));
    }
    else if (m_HLSItemInfo[pItem].iSequenceID != INVALID_SEQUENCE_ID)
    {
        // If it is already assigned a value, but going to this item again, it means user using seek operation.
        // In this condition, we need to adjust our m_iSequenceID, since m_iSequenceID always plus one
        // when calling GetNextFile().
        m_iSequenceID = m_HLSItemInfo[pItem].iSequenceID;
    }
    else 
    { 
        m_iSequenceID++; 
    }

    m_HLSItemInfo[pItem].iSequenceID = m_iSequenceID;
}

void HLSPlayList::UpdateItemKey(PlayListItem* pItem)
{
    if (!pItem || pItem->pOwner != this || !m_pCurKeyItem) return;
    if (!m_HLSItemInfo[pItem].pKeyItem) m_HLSItemInfo[pItem].pKeyItem = m_pCurKeyItem;
    else m_pCurKeyItem = m_HLSItemInfo[pItem].pKeyItem; // Adjust current key item.
}

////////////////////////////////////////////////////////////////////////////////
