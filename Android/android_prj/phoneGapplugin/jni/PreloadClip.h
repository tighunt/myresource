////////////////////////////////////////////////////////////////////////////////
// Pre-loading clips.
//
// This class is for HLS to pre-load clips during playback. 
//
// Author: zackchiu@realtek.com
//
// Created: 11/22/2010
////////////////////////////////////////////////////////////////////////////////

#ifndef __PRE_LOAD_CLIP__
#define __PRE_LOAD_CLIP__

#include "HLSPlayList.h"
#include "NavPlugins.h"
#include <map>
#include <list>
#include "CommandQueue.h"
#include "CommandProcessor.h"


class PreloadCmdProcessor;
class HLSClip;

////////////////////////////////////////////////////////////////////////////////
// This class is for pre-loading HLS clips.
//
class PreloadClip
{
    public:
        PreloadClip(IOPLUGIN& IoPlugin, const char* pUserAgent);
        ~PreloadClip();
        
        // Pre-load clip
        //
        bool Preload(PlayListItem* pItem);
        
        // Find and get pre-loaded clip
        //
        HLSClip* GetPreloadItem(PlayListItem* pItem, bool bRemoveFromList = false);
        
        // Clear all of pre-loaded clips
        //
        void ClearPreloadedItem();
        
        // Check if this item is pre-loading.
        //
        //  Return:
        //      true --> This item is pre-loading
        //
        bool IsPreLoading(PlayListItem* pItem);
        
        // Wait for the specified item pre-loaded
        //
        //  Return:
        //      true --> OK.
        //      false--> Failed and time out.
        //
        bool WaitForPreload(PlayListItem* pItem);
        
    private:
        CommandQueue<PlayListItem*>* m_pPreloadCmdList;      
        PreloadCmdProcessor* m_pPreloadCmdProcessor;
};

////////////////////////////////////////////////////////////////////////////////
//  This class presents a HLS clip.
//
class HLSClip
{
    public:
        HLSClip(IOPLUGIN& IoPlugin, 
                PlayListItem* pItem, 
                const char* pUserAgent);

        // Please call this function before using this class.
        // After calling this, it ensures that the instance must be valid.
        //
        UINT AddRef();
        
        // Please call this function to release instance.
        // Don't use delete operator.
        //
        void Release();
        
        // Get the file handle returned by IO plugin's open()
        //
        void* GetHandle();
        
        // Get the original play list item
        //
        PlayListItem* GetItem();
        
        // Set the file pointer to the begin.
        //
        void SeekToBegin();
        
        // Get the item URL.
        //
        const char* GetURL() const { return m_URL.c_str(); };
        
    private:
        // delete operator is not allowed!!
        // Please release this object by Release();
        //
        ~HLSClip();
        
    private:
        IOPLUGIN& m_IoPlugin;
        void* m_pFileHandle;
        PlayListItem* m_pItem;
        UINT m_iRefCounter;
        CCritSec m_CritSec;       
        std::string m_URL;
};

////////////////////////////////////////////////////////////////////////////////
// Pre-load commands procdessor.
//
class PreloadCmdProcessor: public CommandProcessor<PlayListItem*>
{
    public:
        PreloadCmdProcessor(IOPLUGIN& IoPlugin, const char* pUserAgent);
        ~PreloadCmdProcessor();
        
        // Find and get the pre-loaded clip
        //
        //  Parameters:
        //      1. pItem: The play list item for this clip.
        //      2. bRemoveFromList: Remove this item from list when found it.
        //
        HLSClip* Find(PlayListItem* pItem, bool bRemoveFromList);
        
        void ClearAllCommands();
        
        // Set sync command. After this item is pre-loaded, the sunc event will
        // be set.
        //
        bool SetSyncComamnd(PlayListItem* pItem, CEvent* pSyncEvent);
        
    public:
        bool Execute(PlayListItem* pItem);   
        
    private:
        void ReleaseTopItemFromList();
        
    private:   
        IOPLUGIN& m_IoPlugin;
        std::list<HLSClip*> m_PreloadList;
        std::map<PlayListItem*, CEvent*> m_CmdSyncList;
        CCritSec m_CritSec;
        std::string m_strUserAgent;
};

////////////////////////////////////////////////////////////////////////////////
#endif
