////////////////////////////////////////////////////////////////////////////////
// HLS(HTTP Live Streaming) Play List. HLS uses M3U extension.
//
// Author: zackchiu@realtek.com
//
// Created: 10/22/2010
////////////////////////////////////////////////////////////////////////////////

#ifndef __HLS_PLAY_LIST__
#define __HLS_PLAY_LIST__

#include "M3UPlayList.h"
#include <map>



typedef struct _SrtInfo
{
    char* pLanguage;
    char* pURL;
} SrtInfo;

typedef struct _ThumbnailInfo
{
    int iCounts;
    int iInterval;
    char* pURL;
} ThumbnailInfo;

class HLSPlayListParser: public M3UPlayListParser
{
    public:
        HLSPlayListParser();
        ~HLSPlayListParser();
        
    private:
        //bool IsPlayableFile(const char* pLine);        
};

// HLS Observer. This is used by HLSPlayList to notify special events
//
class HLSObserver
{
    public:        
        // This function will be called when the band width is changed.
        //
        virtual void ChangeBandWidth(UINT iNewBandWidth) = 0;
};

class HLSPlayList: public M3UPlayList
{
    public:
        HLSPlayList();
        ~HLSPlayList();

        const char* GetFileURL(PlayListItem* pItem);
        bool SetContent(const char* pURL);
        bool SetContent(const char* pContentBuffer, UINT iBufSize);
        void Reset();

        PlayListItem* GetNextFile();
        PlayListItem* GetPrevFile();
        void JumpToHead();
        void JumpToTail();
        void PushContext();
        void PopContext();

        // Return the duration from the first item to current item.
        //
        UINT GetDuration() { return m_iCurDuration; };
        
        // Return the total duration.
        //
        UINT GetTotalDuration();
        
        // Set the band width.
        // The unit of iBandWidth is bits per seconds
        //
        void SetBandWidth(UINT iBandWidth) 
        { 
            if (m_iBandWidth == iBandWidth) return;
            m_iBandWidth = iBandWidth; 
            m_bCheckBandWidth = true; 
        };

        // Seek to the m3u item by time offset(time unit is seconds).
        //
        PlayListItem* SeekByTime(UINT iTimeOffset);
        
        // Seek to the m3u item by sequence ID
        //
        PlayListItem* SeekBySequenceID(UINT iSequenceID);
        
        HLSObserver* GetObserver() const { return m_pObserver; };
        void SetObserver(HLSObserver* pObserver) { m_pObserver = pObserver; };
        
        UINT GetSequenceID(PlayListItem* pItem);
               
        // Get IV 
        //  
        //  Parameters:
        //      1. pItem: Play list item.
        //      2. pBuf: The buffer will contain the IV when function returned.
        //      3. iBufSize: The size of buffer (pBuf)
        //
        //  Return:
        //      The size of IV (in bytes). 0 means failed.
        //
        UINT GetIV(PlayListItem* pItem, BYTE* pBuf, UINT iBufSize);
        
        const char* GetKeyURI(PlayListItem* pItem);        
        int GetEncryptMethod(PlayListItem* pItem);
        PlayListItem* GetKeyItem(PlayListItem* pItem);
        
        // Get SRT counts
        //
        UINT GetSrtCounts();
        
        // Get SRT information by index.
        //
        //  Parameters:
        //      1. srtInfo: Contain the result when function returned.
        //      2. iIndex: Index(zero-based) to query SRT information.
        //
        //  Return:
        //      true --> Success.
        //      false--> Failed.
        //
        bool GetSrtInfo(SrtInfo& srtInfo, UINT iIndex);
        
        // Get thumbnail information.
        //
        //  Parameters:
        //      1. thumbnailInfo: Contain the result when function returned.
        //
        //  Return:
        //      true --> Success.
        //      false--> Failed.
        //
        bool GetThumbnailInfo(ThumbnailInfo& thumbnailInfo);
        
        // Check if the HLS stream is a live stream.
        //
        //  Return:
        //      true --> This is a live stream.
        //
        bool IsLiveStream();
        
    private:
        M3UPlayList* CreatePlayListInstance() { return new HLSPlayList(); };
        M3UPlayListParser* CreateParser() { return new HLSPlayListParser(); };
        bool LoadPlayList(PlayListItem* pItem, bool bReloadIfExist);
        bool IsPlayList(PlayListItem* pItem);
        void EnterEmbeddedPlayList(PlayListItem* pItem);
        void LeaveEmbeddedPlayList(PlayListItem* pItem);
        bool CheckIfValidItem(PlayListItem* pItem);

    private:
        PlayListItem* FindPlayListByBandWidth(UINT iStreamId, UINT iActualBandWidth);
        bool CheckBandWidthAndReloadPlayList();
        void UpdateSequenceID(PlayListItem* pItem);
        void UpdateItemKey(PlayListItem* pItem);

    private:
        enum
        {
            INVALID_SEQUENCE_ID = 0xFFFFFFFF,
            INVALID_STREAM_ID   = 0xFFFFFFFF,
            AES_128_IV_SIZE     = 16 // In bytes.
        };
    
        typedef struct _HLSItemInfo
        {
            PlayListItem* pKeyItem;
            UINT iSequenceID;
            
            _HLSItemInfo(): pKeyItem(NULL),
                            iSequenceID(INVALID_SEQUENCE_ID)
            {
            };
        } HLSItemInfo;
        
    private:
        UINT m_iBandWidth;
        UINT m_iCurStreamId;
        UINT m_iCurDuration;
        UINT m_iTotalDuration;
        bool m_bCheckBandWidth;
        HLSObserver* m_pObserver;
        PlayListItem* m_pCurKeyItem;
        UINT m_iSequenceID;
        std::map<PlayListItem*, HLSItemInfo> m_HLSItemInfo;
        PlayListItem* m_pReloadItem;
};



#endif
