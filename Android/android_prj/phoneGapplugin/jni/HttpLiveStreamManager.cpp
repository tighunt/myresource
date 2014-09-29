////////////////////////////////////////////////////////////////////////////////
// HTTP Live Streaming Manager
// 
// This class uses class HLSPlayList to parse and get playable files.
//
// Author: zackchiu@realtek.com
//
// Created: 10/18/2010
////////////////////////////////////////////////////////////////////////////////

#include "HttpLiveStreamManager.h"
#include <stdio.h>
#include "PreloadClip.h"
#include "M3ULineTokenParser.h"

#include <stdlib.h>

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

#define MIN_STREAM_RATE_SAMPLE_COUNTS       6
#define STREAM_RATE_LIST_SIZE               6
#define STREAM_RATE_FACTOR                  6
const int g_StreamRateWeightTable[STREAM_RATE_LIST_SIZE] = 
{
    1, 2, 3, 4, 5, 10
};

////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// Assistant Functions
//
unsigned int GetTotalWeight()
{
    static unsigned int iResult = 0;
    if (iResult > 0) return iResult;
    
    for (int i = 0; i < STREAM_RATE_LIST_SIZE; i++)
        iResult += g_StreamRateWeightTable[i];
    return iResult;
}

unsigned int GetContentLengthFromURL(const char* pURL)
{
    /*if (!pURL || !(*pURL)) return -1;
    auto_array_ptr<char> strURL(ExtractFileName(pURL));
    if (!strURL || !strlwr(strURL)) return -1;
    
    const static char TOKEN[] = "contentlength=";
    char* pToken = strstr(pURL, TOKEN);
    if (!pToken) return -1;
    
    char* pToken2 = strtok(pToken + strlen(TOKEN), "&=");   
    return pToken2 ? atoi(pToken2) : -1;*/
    return 0;
}

////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// extern functions defined in HttpIOPlugin.c & HttpIOPlugin_asy.c
//
extern "C" 
{
    extern unsigned int http_IOPlugin_Get_Stream_Rate(void* pInstance);
}

////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// HLSManager Implementation
//
HLSManager::HLSManager(IOPLUGIN& IoPlugin): m_IoPlugin(IoPlugin),
                                            m_pClip(NULL),
                                            m_pPreloadClip(NULL),
                                            m_iStreamRate(0),
                                            m_bNeedPrevCipherText(false),
                                            m_iCachedDataSize(0),
                                            m_iReadedDuration(0),
                                            m_pCachedPreloadedItem(NULL),
                                            m_bUseCachedPreloadedItem(false)
{
    zDebug("HLSManager::HLSManager(): ......\n");
    memset(&m_SubtitleInfo, 0, sizeof(NAVSUBTITLEINFO));
    memset(&m_ThumbnailInfo, 0, sizeof(NAVTHUMBNAILINFO));
    //MCP_CIPHER_CTX_init(&m_CipherContext);
}

HLSManager::~HLSManager()
{
    Close();
    zDebug("HLSManager::~HLSManager(): ......\n");
}

bool HLSManager::SetPlayList(const char* pContentBuffer, 
                             UINT iBufSize, 
                             char* pBaseURL,
                             const char* pUserAgent)
{
    zDebug("HLSManager::SetPlayList(): pBaseURL=%s, UserAgent=%s\n", 
           pBaseURL, pUserAgent);
    if (!pContentBuffer || !iBufSize || !pBaseURL) return false; 
    Close();
    m_HLSPlayList.SetObserver(this);
    
    if (!m_pPreloadClip)
        m_pPreloadClip = new PreloadClip(m_IoPlugin, pUserAgent);

    if (!m_HLSPlayList.SetContent(pContentBuffer, iBufSize)) return false;
    m_HLSPlayList.SetBaseURL(pBaseURL);

    zDebug("HLSManager::SetPlayList(): Done.\n");
    return LoadNextItem();
}

NAVSUBTITLEINFO* HLSManager::GetSubtitleInfo()
{
    ReleaseSubtitleInfo();

    UINT iCounts = m_HLSPlayList.GetSrtCounts();
    if (!iCounts) return &m_SubtitleInfo;
    
    NAVSUBTITLE* pSubtitle = new NAVSUBTITLE[iCounts];
    int i = 0;
    SrtInfo srtInfo;
    while(m_HLSPlayList.GetSrtInfo(srtInfo, i))
    {
        pSubtitle[i].pLanguage = srtInfo.pLanguage;
        pSubtitle[i].pURL = srtInfo.pURL;
        i++;
    }
    m_SubtitleInfo.iCounts = iCounts;
    m_SubtitleInfo.pSubtitle = pSubtitle;
    return &m_SubtitleInfo;
}

NAVTHUMBNAILINFO* HLSManager::GetThumbnailInfo()
{
    ThumbnailInfo thumbnailInfo;
    if (!m_HLSPlayList.GetThumbnailInfo(thumbnailInfo)) return NULL;
    m_ThumbnailInfo.iCounts = thumbnailInfo.iCounts;
    m_ThumbnailInfo.iInterval = thumbnailInfo.iInterval;
    m_ThumbnailInfo.pURL = thumbnailInfo.pURL;
    return &m_ThumbnailInfo;
}

int GetAES128PaddingLength(BYTE* pData, int iDataSize)
{
    // If AES-128 has padding, its padding value is the padding bytes.
    // Ex: Padding 4 bytes, you will see 0x04 0x04 0x04 0x04 at the end of the file.

    if (!pData || iDataSize <= 0) return 0;
    int iPaddingValue = pData[iDataSize - 1];
    if (iPaddingValue <= 0 || iPaddingValue >= iDataSize) return 0;
    
    int iPaddingLength = iPaddingValue;
    for(int i = iDataSize - 1; iPaddingValue > 1; i--, iPaddingValue--)
        if (pData[i] != pData[i - 1]) return 0;
    return iPaddingLength;
}

int HLSManager::Read(BYTE* pBuffer, int iSize, NAVBUF* pEventReceived)
{
    //zDebug("HLSManager::Read(): (0x%x, %d, 0x%x)\n", pBuffer, iSize, pEventReceived); 
    if (!pBuffer || iSize <= 0) return IOPLUGIN_ERROR_GENERIC;
    if ((!m_pClip || !m_pClip->GetHandle()) && !LoadNextItem()) return IOPLUGIN_ERROR_EOF;

    int iRet = 0;
    if (IsEncrypted(m_pClip->GetItem()))   
        iRet = ReadEncryptedData(pBuffer, iSize, pEventReceived);
    else
        iRet = m_IoPlugin.read(m_pClip->GetHandle(), pBuffer, iSize, pEventReceived);

    if (iRet < 0)
    {
        CalculateStreamRate(http_IOPlugin_Get_Stream_Rate(m_pClip->GetHandle()) * STREAM_RATE_FACTOR);
        
        // Reset file pointer to begin of the file.
        // Since this file handle may be cached in PreloadClip, we need to do this.
        m_pClip->SeekToBegin();

        m_iReadedDuration += m_HLSPlayList.GetFileDuration(m_pClip->GetItem());
        
        // Close file to force the next Read() to open the next playable file.
        CloseItem();
        return Read(pBuffer, iSize, pEventReceived);         
    }
    zDebug("HLSManager::Read(): DONE(%s)(%d, %d, (%x,%x,%x,%x,%x,%x,%x,%x))\n", 
           m_HLSPlayList.GetFileURL(m_pClip->GetItem()), iSize, iRet, 
           pBuffer[0], pBuffer[1], pBuffer[2], pBuffer[3], pBuffer[4], 
           pBuffer[5], pBuffer[6], pBuffer[7]); 
    return iRet;
}

void HLSManager::Close()
{
    zDebug("HLSManager::Close(): ........\n");
    if (m_pPreloadClip) 
    { 
        delete m_pPreloadClip; 
        m_pPreloadClip = NULL; 
    }
    CloseItem();
    m_HLSPlayList.Reset();
    m_iStreamRate = 0;
    m_bUseCachedPreloadedItem = false;
    m_StreamRateList.clear();
    m_pCachedPreloadedItem = NULL;
    ReleaseCachedKey();
    ReleaseSubtitleInfo();
}

void HLSManager::GetIoInfo(NAVIOINFO* pNavIoInfo)
{
    if (!pNavIoInfo || !m_pClip || !m_pClip->GetHandle()) return;
    m_IoPlugin.getIOInfo(m_pClip->GetHandle(), pNavIoInfo);
    pNavIoInfo->totalBytes = -1;
    pNavIoInfo->totalSeconds = m_HLSPlayList.GetTotalDuration();
    pNavIoInfo->bSeekByTime = true;
    pNavIoInfo->ioType = MEDIATYPE_HLS;
    zDebug("HLSManager::GetIoInfo(): totalSeconds(%d)........\n", pNavIoInfo->totalSeconds);
}   

int HLSManager::Dispose()
{
    zDebug("HLSManager::Dispose(): ........\n");
    return !m_pClip || !m_pClip->GetHandle() 
                ? 0 
                : m_IoPlugin.dispose(m_pClip->GetHandle());
}

int HLSManager::GetBufferFullness(int* pFullness, int* pFlag)
{
    zDebug("HLSManager::GetBufferFullness(): ........\n");
    return !m_pClip || !m_pClip->GetHandle() 
                ? 0 
                : m_IoPlugin.getBufferFullness(m_pClip->GetHandle(), pFullness, pFlag);
}

long long HLSManager::Seek(long long llOffset, int iOrigin)
{
    zDebug("HLSManager::Seek(): (0x%x, 0x%x, %d)....\n", 
           (m_pClip ? (int)m_pClip->GetHandle() : 0), (int)llOffset, iOrigin);

    bool bOffsetForAlignWithAES128 = false;
    if (m_pClip && IsEncrypted(m_pClip->GetItem()))
    {  
        llOffset &= AES_128_ALIGNMENT_MASK; // Align with 16 for AES-128
        if (iOrigin == SEEK_SET && llOffset == 0 && 
            (!FreeCipher() || !InitCipher(m_pClip->GetItem()))) 
        {
            zPrint("HLSManager::Seek(): ???????? Reset cipher failed!!\n");
            return IOPLUGIN_ERROR_GENERIC;
        }

        if (llOffset > 0)
        {            
            llOffset -= AES_128_BLOCK_SIZE;
            if (llOffset < 0) llOffset = 0;
            bOffsetForAlignWithAES128 = true;
        }
    }

    // We don't clear the cached data if user seeks from current position and offset is zero.
    if (iOrigin != SEEK_CUR || llOffset != 0) m_iCachedDataSize = 0;
    
    long long llSeekPos = !m_pClip || !m_pClip->GetHandle() 
                                ? IOPLUGIN_ERROR_GENERIC 
                                : m_IoPlugin.seek(m_pClip->GetHandle(), llOffset, iOrigin);
    if (bOffsetForAlignWithAES128) m_bNeedPrevCipherText = llSeekPos > 0;
    return llSeekPos;
}

long long HLSManager::SeekByTime(long long timeOffset)
{
    zDebug("HLSManager::SeekByTime(): (%d-%d)....\n", (int)timeOffset, (timeOffset + 999) / 1000);
    PlayListItem* pItem = m_HLSPlayList.SeekByTime((timeOffset + 999) / 1000);
    if (!pItem) return IOPLUGIN_ERROR_GENERIC;
    
    // Reset file pointer to begin of the file.
    // Since this file handle may be cached in PreloadClip, we need to do this.
    if (m_pClip) m_pClip->SeekToBegin();
    CloseItem();

    // We don't pre-load for seeking operation, since
    //    1. We cannot predict the next seeking is forward or backward.
    //       It is decided by user's operation.
    //    2. In trick play mode, it maybe drop the loaded clip immediately,
    //       and go to the next/previous clip in a short time.
    //     
    zDebug("HLSManager::SeekByTime(): Done (%d)....\n", 
           (m_pClip && m_pClip->GetHandle() ? m_HLSPlayList.GetDuration() * 1000 : 0));
    
    if (LoadNextItem(pItem, false))
    {
        m_iReadedDuration = m_HLSPlayList.GetDuration();
        return m_HLSPlayList.GetDuration() * 1000;
    }
    return IOPLUGIN_ERROR_GENERIC;
}

bool HLSManager::GetClipInfo(int iTimeOffset, NAVIOCLIPINFO* pInfo)
{
    zDebug("HLSManager::GetClipInfo(): 1(%d-%d)....\n", iTimeOffset, (iTimeOffset + 999) / 1000);

    if (iTimeOffset < 0 || !pInfo) return false;
 
    m_HLSPlayList.PushContext();
    PlayListItem* pItem = m_HLSPlayList.SeekByTime((iTimeOffset + 999) / 1000);
    m_HLSPlayList.PopContext();
    if (!pItem) return false;

    std::map<PlayListItem*, ClipInfo>::iterator itor;
    if ((itor = m_ClipInfoMap.find(pItem)) != m_ClipInfoMap.end())
    {
        pInfo->clipSize = itor->second.iSize > 0 && itor->second.iSize != -1
                            ? itor->second.iSize
                            : GetContentLengthFromURL(m_HLSPlayList.GetFileURL(pItem));
        pInfo->clipDuration = itor->second.iDuration * 1000;
        zDebug("HLSManager::GetClipInfo(): 2(%d-%d-%s)....\n", 
               (int)pInfo->clipSize, (int)pInfo->clipDuration,
               m_HLSPlayList.GetFileURL(pItem));
        return true;
    }
    
    HLSClip* pClip = m_pPreloadClip->GetPreloadItem(pItem);
    if (!pClip)
    {
        if (!m_pPreloadClip->IsPreLoading(pItem))
            m_pPreloadClip->Preload(pItem);
        m_pPreloadClip->WaitForPreload(pItem);
        pClip = m_pPreloadClip->GetPreloadItem(pItem);
        if (!pClip) return false;
    }
    
    NAVIOINFO navIoInfo = {0, };
    m_IoPlugin.getIOInfo(pClip->GetHandle(), &navIoInfo);    
    pInfo->clipSize = navIoInfo.totalBytes > 0 && navIoInfo.totalBytes != -1
                         ? navIoInfo.totalBytes
                         : GetContentLengthFromURL(m_HLSPlayList.GetFileURL(pItem));
    pInfo->clipDuration = m_HLSPlayList.GetFileDuration(pItem) * 1000;
    zDebug("HLSManager::GetClipInfo(): 3(%d-%d-%s)....\n", 
           (int)pInfo->clipSize, (int)pInfo->clipDuration,
           m_HLSPlayList.GetFileURL(pItem));
    pClip->Release();
    return true;
}

void HLSManager::SetBlockingMode(int iBlocking, int iTimeOut)
{
    zDebug("HLSManager::SetBlockingMode(): (%d, %d)....\n", iBlocking, iTimeOut);
    if (!m_pClip || !m_pClip->GetHandle()) return;
    m_IoPlugin.setBlockingMode(m_pClip->GetHandle(), iBlocking, iTimeOut);
}

void HLSManager::ChangeBandWidth(UINT iNewBandWidth)
{
    zPrint("HLSManager::ChangeBandWidth(): (New Bandwidth=%d, CurTime=%d, SeekTime=%d)....\n", 
           iNewBandWidth, m_HLSPlayList.GetDuration(), m_iReadedDuration);
    /////m_pPreloadClip->ClearPreloadedItem();
    PlayListItem* pItem = m_HLSPlayList.SeekByTime(m_iReadedDuration);
    m_bUseCachedPreloadedItem = true;
    zDebug("HLSManager::ChangeBandWidth(): Go to(%s)....\n", m_HLSPlayList.GetFileURL(pItem));
}

bool HLSManager::LoadNextItem(PlayListItem* pItem, bool bPreload)
{
    if (!m_pPreloadClip) return false;
    if (m_iStreamRate > 0) m_HLSPlayList.SetBandWidth(m_iStreamRate);
    if (!pItem) 
    {
        pItem = m_HLSPlayList.GetNextFile();
        if (!pItem) return false;
        PlayListItem* pReuseItem = ReuseCachedPreloadedItem();
        if (pReuseItem)
        {
            zDebug("HLSManager::LoadNextItem(): Using(%s) instead of (%s)....\n", 
                   m_HLSPlayList.GetFileURL(pReuseItem),
                   m_HLSPlayList.GetFileURL(pItem));
            pItem = pReuseItem;
        }
    }
    m_pCachedPreloadedItem = NULL;

    CloseItem();
    zDebug("HLSManager::LoadNextItem(): Load (%s)....\n", m_HLSPlayList.GetFileURL(pItem));
    m_pClip = m_pPreloadClip->GetPreloadItem(pItem);
    if (!m_pClip) 
    {
        if (!m_pPreloadClip->IsPreLoading(pItem))
            m_pPreloadClip->Preload(pItem);
        m_pPreloadClip->WaitForPreload(pItem);
        m_pClip = m_pPreloadClip->GetPreloadItem(pItem);
        if (!m_pClip || !m_pClip->GetHandle()) return false;
    }
    if (IsEncrypted(pItem) && (!LoadKey(pItem) || !InitCipher(pItem))) 
        return false;
    
    NAVIOINFO navIoInfo = {0, };
    m_IoPlugin.getIOInfo(m_pClip->GetHandle(), &navIoInfo);
    ClipInfo clipInfo = { (unsigned int)navIoInfo.totalBytes, m_HLSPlayList.GetFileDuration(m_pClip->GetItem()) };
    m_ClipInfoMap[m_pClip->GetItem()] = clipInfo;
    zDebug("HLSManager::LoadNextItem(): Load (%s) Done(Handle:0x%x, Size:%d bytes).\n", 
           m_HLSPlayList.GetFileURL(pItem), (int)m_pClip->GetHandle(), 
           (int)navIoInfo.totalBytes);

    if (bPreload && (pItem = m_HLSPlayList.GetNextFile())) // If exists next file, pre-load it.
    {
        zDebug("HLSManager::LoadNextItem(): Pre-load (%s)\n", m_HLSPlayList.GetFileURL(pItem));
        m_pPreloadClip->Preload(pItem);
        m_pCachedPreloadedItem = pItem;
        PlayListItem* pKeyItem = m_HLSPlayList.GetKeyItem(pItem);
        if (pKeyItem && IsEncrypted(pItem) && 
            !IsKeyAlreadyDownload(pItem)) 
            m_pPreloadClip->Preload(pKeyItem);
        m_HLSPlayList.GetPrevFile(); // Go back to the current playing file.
    }

    return true; 
}

void HLSManager::CloseItem()
{
    if (!m_pClip) return;
    m_pClip->Release();
    m_pClip = NULL;
    FreeCipher();
    m_bNeedPrevCipherText = false;
    m_iCachedDataSize = 0;
}

bool HLSManager::IsEncrypted(PlayListItem* pItem)
{
    if (!pItem) return false;
    
    int iMethod = m_HLSPlayList.GetEncryptMethod(pItem);
    const char* pKeyURI = m_HLSPlayList.GetKeyURI(pItem);
    return iMethod != M3U_EXT_ENCRYPT_METHOD_NONE && pKeyURI;
}

bool HLSManager::IsKeyAlreadyDownload(PlayListItem* pItem)
{
    if (!pItem) return false;
    
    const char* pKeyURI = m_HLSPlayList.GetKeyURI(pItem);
    if (!pKeyURI) return false;

    std::map<std::string, BYTE*>::iterator itor = m_keyMap.find(pKeyURI);
    return itor != m_keyMap.end();
}

bool HLSManager::LoadKey(PlayListItem* pItem)
{
    if (!pItem) return false;
    if (IsKeyAlreadyDownload(pItem)) return true;
    
    PlayListItem* pKeyItem = m_HLSPlayList.GetKeyItem(pItem);
    if (!pKeyItem) return true; // No need to load key. Return true to skip.
    
    const char* pKeyURI = m_HLSPlayList.GetKeyURI(pItem);
    if (!pKeyURI) return false;
    
    HLSClip* pKeyClip = m_pPreloadClip->GetPreloadItem(pKeyItem);
    if (!pKeyClip) 
    {
        if (!m_pPreloadClip->IsPreLoading(pKeyItem))
            m_pPreloadClip->Preload(pKeyItem);
        m_pPreloadClip->WaitForPreload(pKeyItem);
        pKeyClip = m_pPreloadClip->GetPreloadItem(pKeyItem);
        if (!pKeyClip|| !pKeyClip->GetHandle()) return false;
    }
    
    NAVIOINFO navIoInfo = {0, };
    m_IoPlugin.getIOInfo(pKeyClip->GetHandle(), &navIoInfo);
    if (navIoInfo.totalBytes <= 0 || 
        (int)navIoInfo.totalBytes != AES_128_KEY_SIZE)
    {
        zPrint("HLSManager::LoadKey(): ????????? Invalid Key Size(%d)!!!\n", 
               (int)navIoInfo.totalBytes);
        pKeyClip->Release(); 
        return false;
    }
    
    auto_array_ptr<BYTE> pKeyBuf(new BYTE[(int)navIoInfo.totalBytes]);
    int iRet = m_IoPlugin.read(pKeyClip->GetHandle(), pKeyBuf, (int)navIoInfo.totalBytes, NULL);
    if (iRet != (int)navIoInfo.totalBytes) 
    {
        zPrint("HLSManager::LoadKey(): ????????? Read Key Failed(%d)!!!\n", iRet);
        pKeyClip->Release();
        return false;
    }
    
    m_keyMap[pKeyURI] = pKeyBuf.get();
    pKeyBuf.release();    
    pKeyClip->Release();
    return true;
}

BYTE* HLSManager::GetKey(PlayListItem* pItem)
{
    if (!pItem) return false;
    
    const char* pKeyURI = m_HLSPlayList.GetKeyURI(pItem);
    if (!pKeyURI) return false;

    zDebug("HLSManager::GetKey(): Key URI=%s\n", pKeyURI);        
    std::map<std::string, BYTE*>::iterator itor = m_keyMap.find(pKeyURI);
    return itor != m_keyMap.end() ? itor->second : NULL;
}

void HLSManager::ReleaseCachedKey()
{
    std::map<std::string, BYTE*>::iterator itor, itorEnd = m_keyMap.end();
    for(itor = m_keyMap.begin(); itor != itorEnd; ++itor)
        if (itor->second) { delete[] itor->second; }
    m_keyMap.clear();
}

void HLSManager::ReleaseSubtitleInfo()
{
    if (m_SubtitleInfo.iCounts && m_SubtitleInfo.pSubtitle)
    {
        m_SubtitleInfo.iCounts = 0;
        delete[] m_SubtitleInfo.pSubtitle;
        m_SubtitleInfo.pSubtitle = NULL;
    }
}

bool HLSManager::InitCipher(PlayListItem* pItem)
{
    if (!pItem) return false;
    zDebug("HLSManager::InitCipher(): (%s)\n", m_HLSPlayList.GetFileURL(pItem));
    
    BYTE* pKey = GetKey(pItem);
    if (!pKey) return false;
    
    BYTE IV[AES_128_IV_SIZE] = {0, };
    int iIVLen = m_HLSPlayList.GetIV(pItem, IV, sizeof(IV));
    if (iIVLen != AES_128_IV_SIZE) return false;
    
    zDebug("HLSManager::InitCipher(): Key(%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x)\n",
           pKey[0], pKey[1], pKey[2], pKey[3], pKey[4], pKey[5], pKey[6], pKey[7], 
           pKey[8], pKey[9], pKey[10], pKey[11], pKey[12], pKey[13], pKey[14], pKey[15]);
    zDebug("HLSManager::InitCipher(): IV(%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x,%x)\n",
           IV[0], IV[1], IV[2], IV[3], IV[4], IV[5], IV[6], IV[7], 
           IV[8], IV[9], IV[10], IV[11], IV[12], IV[13], IV[14], IV[15]);
           
    FreeCipher();
    //MCP_CIPHER_CTX_init(&m_CipherContext);
    /*return MCP_CipherInitEx(&m_CipherContext, 
                            MCP_CIPHER_AES_128_CBC, 
                            pKey, 
                            AES_128_KEY_SIZE, 
                            IV, 
                            iIVLen, 
                            0, 
                            MCP_SBH_KEEP_CLEAR) >= 0;*/


	return 0;
}

bool HLSManager::FreeCipher()
{
    //if (!m_CipherContext.ops.cleanup) return false;
   // MCP_CIPHER_CTX_cleanup(&m_CipherContext);
    return true;
}

int HLSManager::ReadEncryptedData(BYTE* pBuffer, int iSize, NAVBUF* pEventReceived)
{
    if (!m_pClip || !pBuffer || iSize < 0) return IOPLUGIN_ERROR_GENERIC;

    iSize &= AES_128_ALIGNMENT_MASK; // Align with 16 for AES-128

    if (m_bNeedPrevCipherText)
    {
        // Since CBC mode needs the previous cipher text to do XOR operation.
        // We need this step to make the cipher work correctly.
        // This happened only when user uses seek operation.
        //
        m_bNeedPrevCipherText = false;
        BYTE tmpBuf[AES_128_BLOCK_SIZE] = {0, };
        int iRead = m_IoPlugin.read(m_pClip->GetHandle(), tmpBuf, AES_128_BLOCK_SIZE, pEventReceived);
        if (iRead < 0) return iRead;
        DecryptData(tmpBuf, iRead); // This decoded data is garbage, this step is only for setting previous cipher text.
    }
    
    BYTE* pTmpBuf = pBuffer;
    UINT iTotalBufSize = 0;
    if (m_iCachedDataSize > 0)  // Restore any cached data first.
    {
        zDebug("HLSManager::ReadEncryptedData(): Restore cached data(%d bytes)\n", m_iCachedDataSize);
        memcpy(pTmpBuf, m_CachedBuffer, m_iCachedDataSize);
        pTmpBuf += m_iCachedDataSize;
        iSize -= m_iCachedDataSize;
        iTotalBufSize = m_iCachedDataSize;
        m_iCachedDataSize = 0;
    }

    // Read the needed data size.
    //
    int iRet = m_IoPlugin.read(m_pClip->GetHandle(), pTmpBuf, iSize, pEventReceived);
    if (iRet < 0) return iRet;
    iTotalBufSize += iRet;

    // Check if data is aligned with 16 bytes
    // If it is not aligned with 16 bytes, cache the non-aligned bytes.
    //
    int iAlignedSize = iTotalBufSize & AES_128_ALIGNMENT_MASK;
    if (iAlignedSize != iTotalBufSize) // Not align with 16 bytes
    {
        m_iCachedDataSize = iTotalBufSize & 0xF; // Get the non-aligned bytes.
        memcpy(m_CachedBuffer, pBuffer + iAlignedSize, m_iCachedDataSize);        
        zDebug("HLSManager::ReadEncryptedData(): Save cached data(%d bytes)\n", m_iCachedDataSize);
    }
    iRet = iAlignedSize;

    iRet = DecryptData(pBuffer, iRet);
    if (iRet < 0) return iRet;
    if (iRet != iAlignedSize) { zPrint("HLSManager::ReadEncryptedData(): ???? Invalid decoded size\n"); }

    // Check if any padding existed. We need to skip the padding.
    //
    int iPaddingLen = GetAES128PaddingLength(pBuffer, iRet);
    if (iPaddingLen > 0) iRet -= iPaddingLen; // Skip padding.

    return iRet;
}

int HLSManager::DecryptData(BYTE* pBuffer, int iSize)
{
    int iProcessedLen = 0, iLen = 0;
    /*if ((iLen = MCP_CipherUpdate(&m_CipherContext, pBuffer, iSize, pBuffer)) < 0)
    {
        zPrint("HLSManager::DecryptData(): ???????? Update cipher failed\n");        
        return IOPLUGIN_ERROR_GENERIC;
    }
    pBuffer += iLen;
    iProcessedLen += iLen;

    if ((iLen = MCP_CipherFinal(&m_CipherContext, pBuffer)) < 0)
    {
        zPrint("HLSManager::DecryptData(): ???????? Finalize cipher failed\n");        
        return IOPLUGIN_ERROR_GENERIC;
    }*/
    iProcessedLen += iLen;

    return iProcessedLen;
}

void HLSManager::CalculateStreamRate(unsigned int iCurStreamRate)
{
    zPrint("HLSManager::CalculateStreamRate(): Current Rate=%d\n", iCurStreamRate);
    while (m_StreamRateList.size() >= STREAM_RATE_LIST_SIZE)
        m_StreamRateList.pop_front();
    m_StreamRateList.push_back(iCurStreamRate);
    if (m_StreamRateList.size() < MIN_STREAM_RATE_SAMPLE_COUNTS) return;

    std::list<unsigned int>::iterator itor, itorEnd = m_StreamRateList.end();
    m_iStreamRate = 0;
    size_t i = 0;
    for (itor = m_StreamRateList.begin(); itor != itorEnd; ++itor, i++)   
        m_iStreamRate += g_StreamRateWeightTable[i] * (*itor);
    m_iStreamRate /= GetTotalWeight();
    zPrint("HLSManager::CalculateStreamRate(): Result=%d\n", m_iStreamRate);    
}

PlayListItem* HLSManager::ReuseCachedPreloadedItem()
{
    if (!m_bUseCachedPreloadedItem || !m_pCachedPreloadedItem) return NULL; 
    
    // When stream is changed, the result of m_HLSPlayList.GetNextFile()
    // will become the item in the play list corresonding to changed stream.
    // But in the preload queue, it must exist one preloaded item belonged
    // to the stream before changed. 
    // We should use it first.
    //
    m_bUseCachedPreloadedItem = false;
    PlayListItem* pItem = NULL;
    HLSClip* pClip = m_pPreloadClip->GetPreloadItem(m_pCachedPreloadedItem);
    if (pClip || m_pPreloadClip->IsPreLoading(m_pCachedPreloadedItem))
    {
        if (pClip) pClip->Release();
        pItem = m_pCachedPreloadedItem;
        m_pCachedPreloadedItem = NULL;
    }
    return pItem;
}

////////////////////////////////////////////////////////////////////////////////
