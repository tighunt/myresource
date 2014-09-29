////////////////////////////////////////////////////////////////////////////////
// General M3U Play List. Support M3U Extension.
//
// Author: zackchiu@realtek.com
//
// Created: 10/20/2010
////////////////////////////////////////////////////////////////////////////////

#ifndef __M3U_PLAY_LIST__
#define __M3U_PLAY_LIST__

#define FILE_PATH_SLASH_CHAR	'/'
#define FILE_PATH_SLASH 		"/"

#include "PlayListParser.h"
#include <string>
#include <map>
#include <vector>
#include <list>
#include "CrossPlatformDecl.h"

class M3UPlayListParser: public PlayListParser
{
    public:
        M3UPlayListParser();
        virtual ~M3UPlayListParser();

    protected:
        virtual bool IsPlayableFile(const char* pLine);
};

class M3UPlayList
{
    public:
        M3UPlayList();
        virtual ~M3UPlayList();

        // Get the next/previous/current playable file in this play list.
        //
        //  Return:
        //      The handle of the playable item
        //
        virtual PlayListItem* GetNextFile();
        virtual PlayListItem* GetPrevFile();
        PlayListItem* GetCurrentFile() const { return m_pCurItem; };
        
        // This function will set the file pointer to the first item.
        // If next call is GetNextFile(), it will return the first playable 
        // item in the play list. 
        // If next call is GetPrevFile(), it will return NULL. 
        //
        virtual void JumpToHead();
        
        // This function will set the file pointer to the "next position" 
        // of the last item.
        // If next call is GetNextFile(), it will return NULL
        // If next call is GetPrevFile(), it will return the last playable
        // item in the play list. 
        // 
        virtual void JumpToTail();
        
        // Backup/Restore the context of this play list.
        // It provides a way to backup/restore current status.
        // If you want to do something during playback and your "do something"
        // will modify the file pointer of play list, you will need these two
        // functions.
        //
        virtual void PushContext();
        virtual void PopContext();
        
        // Get url/duration/description from this m3u item.
        //
        //  Parameters:
        //      1. pItem: The handle of the item.
        //
        //  Return:
        //      URL string/File duration(time unit is seconds)/File description string.
        //
        virtual const char* GetFileURL(PlayListItem* pItem);
        int GetFileDuration(PlayListItem* pItem);
        const char* GetFileDescription(PlayListItem* pItem);

        // Since the m3u item may be presented with a relative path.
        // We need a base URL to make a full path.
        // When you calling SetContent(const char* pURL);
        // I will generate a base URL for you, but calling
        // SetContent(const char* pContentBuffer, UINT iBufSize) will not.
        // You need to call SetBaseURL() by yourself.
        //
        void SetBaseURL(const char* pURL);
        const char* GetBaseURL() const { return m_szBaseURL.c_str(); };
        
        // Set the m3u content, this content can be an URL or from an existed memory.
        //
        virtual bool SetContent(const char* pURL);
        virtual bool SetContent(const char* pContentBuffer, UINT iBufSize);

        // Reset this m3u play list to empty.
        //
        virtual void Reset();
        
        // Return true if current playing play list is an embedded play list in this play list.
        //
        bool IsPlayingEmbeddedPlayList() { return m_pEmbeddedPlayList != NULL; };
        
        const char* GetURL() const { return m_PlayListURL.c_str(); };

    private:
        int m_iCurIndex;
        std::string m_szFullPath;
        std::string m_szBaseURL;
        std::map<PlayListItem*, M3UPlayList*> m_PlayListMap;
        std::list<M3UPlayList*> m_CachedPlayList;

    private:
        void MoveToCachedPlayList(M3UPlayList* pPlayList);
        void RemoveCachedPlayList();

    public:
        M3UPlayListParser* m_pM3UParser;
        PlayListItem* m_pCurItem;
        PlayListItem* m_pEmbeddedPlayList;
        M3UPlayList* m_pCurPlayList;        
        std::vector<long> m_ContextStack;
        std::string m_PlayListURL;
        
    protected:
        PlayListItem* GetNextItem();
        PlayListItem* GetPrevItem();
        void ReleasePlayListMap();
        const char* MakeFullPath(const char* pURI);
        bool SetItemOwner();
        
        // This is called when m3u playlist contains another play list.
        //
        virtual bool LoadPlayList(PlayListItem* pItem, bool bReloadIfExist);
        
        // Create play list instance to handle the new loaded play list.
        // This function is called by LoadPlayList()
        //
        virtual M3UPlayList* CreatePlayListInstance() { return new M3UPlayList(); };
        
        // Create parser to parse this play list.
        //
        virtual M3UPlayListParser* CreateParser() { return new M3UPlayListParser(); };
        
        // Sub-class can override this function to check m3u item.
        // Return true if this item is valid for user to play.
        //
        virtual bool CheckIfValidItem(PlayListItem* pItem);
        
        // Check if pItem is a play list.
        // Return true, if it is a play list.
        //
        virtual bool IsPlayList(PlayListItem* pItem);
        
        // A notification when entering/leaving an embedded play list.
        //
        virtual void EnterEmbeddedPlayList(PlayListItem* pItem);
        virtual void LeaveEmbeddedPlayList(PlayListItem* pItem);
};

#endif
