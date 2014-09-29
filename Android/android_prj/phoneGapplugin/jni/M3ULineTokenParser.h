////////////////////////////////////////////////////////////////////////////////
// M3U Line Token Parser
//
// Author: zackchiu@realtek.com
//
// Created: 10/19/2010
////////////////////////////////////////////////////////////////////////////////

#ifndef __M3U_LINE_TOKEN_PARSER__
#define __M3U_LINE_TOKEN_PARSER__

#include <string.h>
#include "LineTokenParser.h"
#include "CrossPlatformDecl.h"



////////////////////////////////////////////////////////////////////////////////
// M3U Tags
//
enum M3U_TAG
{
    TAG_EXT_M3U = 100,                  // #EXTM3U
    TAG_EXT_INF,                        // #EXTINF
    TAG_EXT_END_LIST,                   // #EXT-X-ENDLIST
    TAG_EXT_TARGET_DURATION,            // #EXT-X-TARGETDURATION
    TAG_EXT_MEDIA_SEQUENCE,             // #EXT-X-MEDIA-SEQUENCE
    TAG_EXT_STREAM_INF,                 // #EXT-X-STREAM-INF
    TAG_EXT_KEY_INF,                    // #EXT-X-KEY
    TAG_EXT_SRT_INF,                    // #EXT-X-SRT
    TAG_EXT_THUMBNAIL_INF               // #EXT-X-THUMBNAIL
};

////////////////////////////////////////////////////////////////////////////////
// Helper Functions
//
inline bool IsM3UExtTag(const char* pLine)
{
    return pLine && 
           strlen(pLine) > 4 && // 4 = strlen(#EXT)
           pLine[0] == '#' &&
           pLine[1] == 'E' &&
           pLine[2] == 'X' &&
           pLine[3] == 'T';
};

// Get the numbers of line token parser
//
//  Return:
//      The numbers of line token parser.
//
UINT GetLineTokenParserCounts();

// Get the line token parser information by index.
//
//  Parameters:
//      1. iIndex: The index of line token parser. Zero-based.
//
//  Return:
//      Pointer to LineTokenParserInfo. NULL for failed.
//
LineTokenParserInfo* GetLineTokenParserInfo(UINT iIndex);

////////////////////////////////////////////////////////////////////////////////
// M3U Item Structure
//
typedef struct _M3UExtInf
{
    int iDuration;
    char* pTitle;
    char* pURI;
} M3UExtInf;

typedef struct _M3UExtStreamInf
{
    UINT iBandWidth;
    UINT iProgId;
    char* pURI;
} M3UExtStreamInf;

typedef struct _M3UExtKeyInf
{
    int iMethod;
    char* pKeyURI;
    bool bValidIV;
    BYTE IV[16];
} M3UExtKeyInf;

typedef struct _M3UExtSrtInf
{
    char* pSrtLanguage;
    char* pSrtURL;
} M3UExtSrtInf;

typedef struct _M3UExtThumbnailInf
{
    char* pThumbnailURL;
    int iCounts;
    int iInterval;
} M3UExtThumbnailInf;

////////////////////////////////////////////////////////////////////////////////
// Constants
//
enum
{
    M3U_EXT_ENCRYPT_METHOD_NONE                   = 0,
    M3U_EXT_ENCRYPT_METHOD_AES_128                = 1,
};

////////////////////////////////////////////////////////////////////////////////



#endif
