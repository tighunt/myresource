////////////////////////////////////////////////////////////////////////////////
// Play List Parser
//
// This is a play list parser based on class LineParser.
// Its basic function is separating play list into lines.
// You need to assign a line token parser to parse each line.
// Each valid parsed line is considered as an play list item descripted by 
// struct PlayListItem.
// 
// Author: zackchiu@realtek.com
//
// Created: 10/19/2010
////////////////////////////////////////////////////////////////////////////////

#ifndef __PLAY_LIST_PARSER__
#define __PLAY_LIST_PARSER__

#include "LineParser.h"
#include <vector>
#include <string>
#include "LineTokenParser.h"
#include "CrossPlatformDecl.h"



enum
{
    TAG_UNKNOWN,        // Unknown item
    TAG_FILE,           // A local file or a remote file.
};

typedef struct _PlayListItem
{
    UINT iTag;
    void* pItemInfo;
    void* pOwner;
    
    _PlayListItem(): iTag(TAG_UNKNOWN),
                     pItemInfo(NULL),
                     pOwner(NULL)
    {
    };
} PlayListItem;

class PlayListParser
{
    public:
        PlayListParser(LineTokenPaserSet parserSet);
        virtual ~PlayListParser();
        
        bool SetContent(const char* pURL);
        bool SetContent(const char* pContentBuffer, UINT iBufSize);
        
        UINT GetItemCounts() { return m_ItemList.size(); };
        PlayListItem* operator[](UINT iIndex);
        
        // Find the first found PlayListItem* by iTag
        //
        PlayListItem* Find(UINT iTag);
        
        // Find next/prev PlayListItem* by specified pItem.
        // This function will search the next/prev play list item with 
        // the same tag.
        // Return NULL meaning no more items.
        //
        PlayListItem* FindNext(PlayListItem* pItem);
        PlayListItem* FindPrev(PlayListItem* pItem);
        
        void Reset();

    protected:
        // Return true if this line is used to present a playable file.
        //
        virtual bool IsPlayableFile(const char* pLine) = 0;
        
        // PlayListParser will call this function to determine how to
        // handle the parsed result.
        //
        virtual void SaveParsedResult(PlayListItem* pItem);

    private:
        LineParser m_LineParser;
        std::vector<PlayListItem*> m_ItemList;
        LineTokenPaserSet m_LineTokenPaserSet;
        std::string m_PlayListURL;

    private:
        bool Parse();
        void ReleaseBuffer();
};



#endif
