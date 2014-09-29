/////////////////////////////////////////////////////////////////////////////////
// Play List Parser
//
// This is a play list parser based on class LineParser.
// Its basic function is separating play list into lines.
// You need to assign a line parser to parse each line.
// Each valid parsed line is considered as an play list item descripted by 
// struct PlayListItem.
// 
// Author: zackchiu@realtek.com
//
// Created: 10/19/2010
////////////////////////////////////////////////////////////////////////////////

#ifdef WIN32
    #include <Windows.h>
#endif
#include "PlayListParser.h"
#include "auto_xxx_ptr.h"
#include <algorithm>
#include <string.h>
#include "ParserErrCode.h"

////////////////////////////////////////////////////////////////////////////////
// Name space declaration
//
/*using namespace rtk;
using namespace parser;*/
using namespace std;

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
    static void zDebug(...) { };
#endif

#ifdef WIN32
    #define zPrint DumpWinDbgMsgA
#else
    #define zPrint printf
#endif

#define MAX_CACHED_PLAY_LIST    30

////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////
// PlayListParser Implmentation
//
PlayListParser::PlayListParser(LineTokenPaserSet parserSet): m_LineTokenPaserSet(parserSet)
{
}

PlayListParser::~PlayListParser()
{
    zDebug("PlayListParser::~PlayListParser(): %s.....\n", m_PlayListURL.c_str());
    ReleaseBuffer();
}

bool PlayListParser::SetContent(const char* pURL)
{
    m_PlayListURL = pURL;
    return m_LineParser.SetContent(pURL) &&  
           Parse();
}

bool PlayListParser::SetContent(const char* pContentBuffer, UINT iBufSize)
{
    return m_LineParser.SetContent(pContentBuffer, iBufSize) && 
           Parse();
}

PlayListItem* PlayListParser::operator[](UINT iIndex)
{
    return iIndex < GetItemCounts() ? m_ItemList[iIndex] : NULL;
}

PlayListItem* PlayListParser::Find(UINT iTag)
{
    std::vector<PlayListItem*>::iterator itor = m_ItemList.begin();
    std::vector<PlayListItem*>::iterator itorEnd = m_ItemList.end();
    for(; itor != itorEnd; ++itor)
        if ((*itor)->iTag == iTag) 
            return *itor;
    return NULL;
}

PlayListItem* PlayListParser::FindNext(PlayListItem* pItem)
{
    if (!pItem) return NULL;
    
    std::vector<PlayListItem*>::iterator itorBegin = m_ItemList.begin();
    std::vector<PlayListItem*>::iterator itorEnd = m_ItemList.end();
    std::vector<PlayListItem*>::iterator pos = find(itorBegin, itorEnd, pItem);
    
    while (pos != itorEnd)
    {
        ++pos; // We start searching from the next item.
        if (pos == itorEnd) break;
        if ((*pos)->iTag == pItem->iTag) return *pos;       
    }
    
    return NULL;
}

PlayListItem* PlayListParser::FindPrev(PlayListItem* pItem)
{
    if (!pItem) return NULL;

    std::vector<PlayListItem*>::iterator itorBegin = m_ItemList.begin();
    std::vector<PlayListItem*>::iterator itorEnd = m_ItemList.end();
    std::vector<PlayListItem*>::iterator pos = find(itorBegin, itorEnd, pItem);
    if (pos == itorEnd || pos == itorBegin) return NULL;

    do
    {
        --pos; // We start searching from the previous item.
        if ((*pos)->iTag == pItem->iTag) return *pos;
    }while(pos != itorBegin);

    return NULL;
}

void PlayListParser::Reset()
{ 
    zDebug("PlayListParser::Reset(): %s.....\n", m_PlayListURL.c_str());
    m_LineParser.Reset(); 
    ReleaseBuffer(); 
}

void PlayListParser::SaveParsedResult(PlayListItem* pItem)
{
    if (!pItem) return;
    m_ItemList.push_back(pItem);
}

bool PlayListParser::Parse()
{
    ReleaseBuffer();
    
    // Parse line by line.
    for (UINT i = 0; i < m_LineParser.GetLineCounts(); i++)
    {
        bool bParseOK = false;
        auto_ptr<PlayListItem> pItem(new PlayListItem); 
        for (UINT j = 0; j < m_LineTokenPaserSet.GetLineParserCounts(); j++)
        {
            LineTokenParserInfo* pLineTokenParserInfo = m_LineTokenPaserSet.GetLineTokenParserInfo(j);
            if (strstr(m_LineParser[i], pLineTokenParserInfo->pToken) != m_LineParser[i]) continue;

            pItem->iTag = pLineTokenParserInfo->iTag;
            if (!pLineTokenParserInfo->LineTokenParser) { bParseOK = true; break; }
            
            UINT iNeededBufSize = pLineTokenParserInfo->LineTokenParser(NULL, 0, NULL);
            auto_array_ptr<BYTE> pBuf(iNeededBufSize 
                                           ? new BYTE[iNeededBufSize] 
                                           : NULL);
            const char* pLines[16] = {0, };
            UINT iLineCounts = min(sizeof(pLines) / sizeof(pLines[0]), 
                                   pLineTokenParserInfo->iLineCounts);
            for(UINT k = 0; k < iLineCounts; k++)
                pLines[k] = m_LineParser[i + k] + 
                            ((k == 0) ? strlen(pLineTokenParserInfo->pToken) : 0);

            memset(pBuf, 0, iNeededBufSize);
            if (IsParseOK(pLineTokenParserInfo->LineTokenParser((char**)pLines, 
                                                                iLineCounts,
                                                                pBuf)))
            {
                pItem->pItemInfo = !pBuf ? NULL : pBuf.release();
                i += (iLineCounts - 1);
                bParseOK = true;
            }
            break;
        }
        
        if (bParseOK) 
        { 
            SaveParsedResult(pItem.release()); 
        }
        else if (IsPlayableFile(m_LineParser[i])) // Parsing failed, but this is a playable file.
        {
            pItem->iTag = TAG_FILE;
            pItem->pItemInfo = (void*)m_LineParser[i];
            SaveParsedResult(pItem.release());
        }
    }
    return true;
}

void PlayListParser::ReleaseBuffer()
{
    zDebug("PlayListParser::ReleaseBuffer(): %s.....\n", m_PlayListURL.c_str());
    size_t iItemCounts = m_ItemList.size();
    if (iItemCounts <= 0) return;
    
    for (size_t i = 0; i < iItemCounts; i++)
    {
        if (m_ItemList[i]->iTag != TAG_FILE && 
            m_ItemList[i]->pItemInfo)
            delete[] ((BYTE*)m_ItemList[i]->pItemInfo);
        delete m_ItemList[i];
    }
    
    m_ItemList.clear();
    zDebug("PlayListParser::ReleaseBuffer(): Done. %s.....\n", m_PlayListURL.c_str());
}

////////////////////////////////////////////////////////////////////////////////
