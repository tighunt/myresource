////////////////////////////////////////////////////////////////////////////////
// M3U Line Token Parser
//
// Author: zackchiu@realtek.com
//
// Created: 10/19/2010
////////////////////////////////////////////////////////////////////////////////

#ifdef WIN32
    #include <Windows.h>
#endif
#include "M3ULineTokenParser.h"
#include "LineTokenParser.h"
#include "ParserErrCode.h"
#include <stdlib.h>

////////////////////////////////////////////////////////////////////////////////
// Name space declaration
//
/*using namespace rtk;
using namespace parser;
using namespace m3u;
 */
////////////////////////////////////////////////////////////////////////////////
// M3Uline parser pre-declaration
//
int ParseExtInf(char** ppLine, UINT iLineCounts, M3UExtInf* pParseResult);
int ParseTargetDuration(char** ppLine, UINT iLineCounts, int* pParseResult);
int ParseMediaSequence(char** ppLine, UINT iLineCounts, int* pParseResult);
int ParseStreamInf(char** ppLine, UINT iLineCounts, M3UExtStreamInf* pParseResult);
int ParseKeyInf(char** ppLine, UINT iLineCounts, M3UExtKeyInf* pParseResult);
int ParseSrtInf(char** ppLine, UINT iLineCounts, M3UExtSrtInf* pParseResult);
int ParseThumbnailInf(char** ppLine, UINT iLineCounts, M3UExtThumbnailInf* pParseResult);

////////////////////////////////////////////////////////////////////////////////
//
// M3U Line Parser Table
//
static LineTokenParserInfo g_M3ULineTokenParsers[] = 
{
    { TAG_EXT_M3U,              "#EXTM3U",                  NULL,                                                          1 },
    { TAG_EXT_END_LIST,         "#EXT-X-ENDLIST",           NULL,                                                          1 },
    { TAG_EXT_INF,              "#EXTINF",                  ParseLineTokenTemplate<M3UExtInf, ParseExtInf>,                2 },
    { TAG_EXT_TARGET_DURATION,  "#EXT-X-TARGETDURATION",    ParseLineTokenTemplate<int, ParseTargetDuration>,              1 },
    { TAG_EXT_MEDIA_SEQUENCE,   "#EXT-X-MEDIA-SEQUENCE",    ParseLineTokenTemplate<int, ParseMediaSequence>,               1 },
    { TAG_EXT_STREAM_INF,       "#EXT-X-STREAM-INF",        ParseLineTokenTemplate<M3UExtStreamInf, ParseStreamInf>,       2 },
    { TAG_EXT_KEY_INF,          "#EXT-X-KEY",               ParseLineTokenTemplate<M3UExtKeyInf, ParseKeyInf>,             1 },
    { TAG_EXT_SRT_INF,          "#EXT-X-SRT",               ParseLineTokenTemplate<M3UExtSrtInf, ParseSrtInf>,             2 },
    { TAG_EXT_THUMBNAIL_INF,    "#EXT-X-THUMBNAIL",         ParseLineTokenTemplate<M3UExtThumbnailInf, ParseThumbnailInf>, 2 },
};

////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// Helper Functions
//
inline UINT GetParserCounts()
{
    return sizeof(g_M3ULineTokenParsers) / sizeof(g_M3ULineTokenParsers[0]);
}

UINT GetLineTokenParserCounts()
{
    return GetParserCounts();
}

LineTokenParserInfo* GetLineTokenParserInfo(UINT iIndex)
{
    return iIndex < GetParserCounts() 
                ? &g_M3ULineTokenParsers[iIndex] 
                : NULL;
}

////////////////////////////////////////////////////////////////////////////////
// M3Uline parser Implementation
//-----------------------------------------------------------------------------
// Parse #EXTINF
//
int ParseExtInf(char** ppLine, UINT iLineCounts, M3UExtInf* pParseResult)
{
    if (!ppLine || iLineCounts != 2 || !pParseResult) 
        return PARSE_INVALID_ARGUMENT;
    if (IsM3UExtTag(ppLine[1])) 
        return PARSE_INVALID_FORMAT;
    
    char* pToken = strtok(ppLine[0], " ,:\t");
    if (!pToken) return PARSE_INVALID_FORMAT;
    pParseResult->iDuration = atoi(pToken);
    pToken = strtok(NULL, "");
    pParseResult->pTitle = pToken;
    pParseResult->pURI = ppLine[1];
    
    return PARSE_OK;
}

//-----------------------------------------------------------------------------
// Parse #EXT-X-TARGETDURATION
//
int ParseTargetDuration(char** ppLine, UINT iLineCounts, int* pParseResult)
{
    if (!ppLine || iLineCounts != 1 || !pParseResult) 
        return PARSE_INVALID_ARGUMENT;
        
    char* pToken = strtok(ppLine[0], " :\t");
    if (!pToken) return PARSE_INVALID_FORMAT;
    *pParseResult = atoi(pToken);
    return PARSE_OK;
}

//-----------------------------------------------------------------------------
// Parse #EXT-X-MEDIA-SEQUENCE
//
int ParseMediaSequence(char** ppLine, UINT iLineCounts, int* pParseResult)
{
    // #EXT-X-TARGETDURATION and #EXT-X-MEDIA-SEQUENCE have the same format.
    // Just use ParseTargetDuration() to parse #EXT-X-MEDIA-SEQUENCE.
    //
    return ParseTargetDuration(ppLine, iLineCounts, pParseResult);
}

//-----------------------------------------------------------------------------
// Parse #EXT-X-STREAM-INF
//
int ParseStreamInf(char** ppLine, UINT iLineCounts, M3UExtStreamInf* pParseResult)
{
    if (!ppLine || iLineCounts != 2 || !pParseResult) 
        return PARSE_INVALID_ARGUMENT;
    if (IsM3UExtTag(ppLine[1])) 
        return PARSE_INVALID_FORMAT;
    
    char* pStr = ppLine[0];
    char* pToken = NULL;
    while((pToken = strtok(pStr, " =:\t")))
    {
        if (strcasecmp(pToken, "BANDWIDTH") == 0)
        {
            pToken = strtok(NULL, " ,\t");
            pParseResult->iBandWidth = atoi(pToken);
        }
        else if (strcasecmp(pToken, "PROGRAM-ID") == 0)
        {
            pToken = strtok(NULL, " ,\t");
            pParseResult->iProgId = atoi(pToken);
        }
        pStr = NULL;
    }
    
    pParseResult->pURI = ppLine[1];
    return PARSE_OK;
}

//-----------------------------------------------------------------------------
// Parse #EXT-X-KEY
//
int ParseKeyInf(char** ppLine, UINT iLineCounts, M3UExtKeyInf* pParseResult)
{
    if (!ppLine || iLineCounts != 1 || !pParseResult) 
        return PARSE_INVALID_ARGUMENT;
    
    char* pStr = ppLine[0];
    char* pToken = NULL;
    pParseResult->bValidIV = false;
    while((pToken = strtok(pStr, " =:\t")))
    {
        if (strcasecmp(pToken, "METHOD") == 0)
        {
            pToken = strtok(NULL, " ,\t");
            if (pToken && strcasecmp(pToken, "AES-128") == 0)
                pParseResult->iMethod = M3U_EXT_ENCRYPT_METHOD_AES_128;
            else
                pParseResult->iMethod = M3U_EXT_ENCRYPT_METHOD_NONE;
        }
        else if (strcasecmp(pToken, "URI") == 0)
        {
            // The format must be URI="http://xxxxxx"
            //
            pToken = strtok(NULL, " ,\t\"");
            pParseResult->pKeyURI = pToken;
        }
        else if (strcasecmp(pToken, "IV") == 0)
        {
            pToken = strtok(NULL, " ,\t");

            // AES-128 IV is 128 bits = 16 bytes.
            // IV must be "IV=0xXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
            // Include "0x", strlen(IV) = 34.
            //
            if (pToken && strlen(pToken) == 34)
            {
                pToken += 2; // Skip "0x"
                int i = 0;
                while(*pToken)
                {
                    char* pStopStr = NULL;
                    char hexTmp[3] = { pToken[0], pToken[1], 0, };
                    pParseResult->IV[i] = strtoul(hexTmp, &pStopStr, 16);
                    pToken += 2;
                    i++;
                }
                pParseResult->bValidIV = (i == sizeof(pParseResult->IV));
            }
        }
        pStr = NULL;
    }

    return PARSE_OK;
}

//-----------------------------------------------------------------------------
// Parse #EXT-X-SRT
//
int ParseSrtInf(char** ppLine, UINT iLineCounts, M3UExtSrtInf* pParseResult)
{
    if (!ppLine || iLineCounts != 2 || !pParseResult) 
        return PARSE_INVALID_ARGUMENT;
    if (IsM3UExtTag(ppLine[1])) 
        return PARSE_INVALID_FORMAT;
    
    char* pStr = ppLine[0];
    char* pToken = NULL;
    while((pToken = strtok(pStr, " =:\t")))
    {
        if (strcasecmp(pToken, "LANGUAGE") == 0)
        {
            pToken = strtok(NULL, "\t");
            pParseResult->pSrtLanguage = pToken;
        }
        pStr = NULL;
    }
    
    pParseResult->pSrtURL = ppLine[1];
    return PARSE_OK;
}

//-----------------------------------------------------------------------------
// Parse #EXT-X-THUMBNAIL
//
int ParseThumbnailInf(char** ppLine, UINT iLineCounts, M3UExtThumbnailInf* pParseResult)
{
    if (!ppLine || iLineCounts != 2 || !pParseResult) 
        return PARSE_INVALID_ARGUMENT;
    if (IsM3UExtTag(ppLine[1])) 
        return PARSE_INVALID_FORMAT;
    
    char* pStr = ppLine[0];
    char* pToken = NULL;
    while((pToken = strtok(pStr, " =:\t")))
    {
        if (strcasecmp(pToken, "COUNT") == 0)
        {
            pToken = strtok(NULL, " ,\t");
            pParseResult->iCounts = atoi(pToken);
        }
        else if (strcasecmp(pToken, "INTERVAL") == 0)
        {
            pToken = strtok(NULL, " ,\t");
            pParseResult->iInterval = atoi(pToken);
        }
        pStr = NULL;
    }
    
    pParseResult->pThumbnailURL = ppLine[1];
    return PARSE_OK;
}

//-----------------------------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////
