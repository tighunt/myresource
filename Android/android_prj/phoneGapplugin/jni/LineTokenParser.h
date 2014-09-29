////////////////////////////////////////////////////////////////////////////////
// Line Token Parser
//
// This header contains some definitions and templates for line token parser.
//
// Author: zackchiu@realtek.com
//
// Created: 10/21/2010
////////////////////////////////////////////////////////////////////////////////

#ifndef __LINE_TOKEN_PARSER__
#define __LINE_TOKEN_PARSER__

#include "CrossPlatformDecl.h"


////////////////////////////////////////////////////////////////////////////////
// Line Token Parser Template
//
//  Parameters:
//      1. ppLine: Lines want to be parsed.
//                 Assign NULL to query the needed buffer size.
//      2. iLineCounts: Line counts.
//      3. pParseResult: Point to a buffer. 
//                       This buffer will contain the parsed result when
//                       function returned.
//
//  Return:
//      >= 0: Needed buffer size.
//      <  0: Error code defined in ParserErrCode.h.
//
template <class T, int (*fpParseLineToken)(char**, UINT, T*)> 
int ParseLineTokenTemplate(char** ppLine, UINT iLineCounts, void* pParseResult)
{
    return ppLine 
              ? fpParseLineToken(ppLine, iLineCounts, (T*)pParseResult)
              : sizeof(T);
};

// The prototype of line token parser.
//
//  Parameters:
//      1. ppLine: Lines want to be parsed.
//                 Assign NULL to query the needed buffer size.
//      2. iLineCounts: Line counts.
//      3. pParseResult: Point to a buffer. 
//                       This buffer will contain the parsed result when
//                       function returned.
//
//  Return:
//      >= 0: Needed buffer size.
//      <  0: Error code defined in ParserErrCode.h.
//
typedef int (*fpLineTokenParser)(char** ppLine, 
                                 UINT iLineCounts, 
                                 void* pParseResult);

// This is for the information of line token parsers.
//
typedef struct _LineTokenParserInfo
{
    UINT iTag;                              // A tag to recognize your parser.
    const char* pToken;                     // A token to tell others when to use your parser.
    fpLineTokenParser LineTokenParser;      // A function pointer to your parser.
    UINT iLineCounts;                       // How many lines your parser need?
} LineTokenParserInfo;

// Get the numbers of line token parser
//
//  Return:
//      The numbers of line token parser.
//
typedef UINT (*fpGetLineParserCounts)();

// Get the line token parser information by index.
//
//  Parameters:
//      1. iIndex: The index of line token parser. Zero-based.
//
//  Return:
//      Pointer to LineTokenParserInfo. NULL for failed.
//
typedef LineTokenParserInfo* (*fpGetLineTokenParserInfo)(UINT iIndex);

// Structure for your line token parsers for play list.
//
typedef struct _LineTokenPaserSet
{
    fpGetLineParserCounts GetLineParserCounts;
    fpGetLineTokenParserInfo GetLineTokenParserInfo;
    
    _LineTokenPaserSet(): GetLineParserCounts(NULL),
                          GetLineTokenParserInfo(NULL)
    {
    };
    
    _LineTokenPaserSet(fpGetLineParserCounts pGetLineParserCounts,
                       fpGetLineTokenParserInfo pGetLineTokenParserInfo): GetLineParserCounts(pGetLineParserCounts),
                                                                          GetLineTokenParserInfo(pGetLineTokenParserInfo)
    {
    };
} LineTokenPaserSet;


#endif
