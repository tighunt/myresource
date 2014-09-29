////////////////////////////////////////////////////////////////////////////////
// Line parser
//
// Separate text into lines by token "\r\n" or "\n"
//
// Author: zackchiu@realtek.com
//
// Created: 10/19/2010
////////////////////////////////////////////////////////////////////////////////

#include "LineParser.h"
//#include "CFile.h"
#ifdef WIN32
    #include <Windows.h>
#endif
#include <string.h>
#include <stdio.h>
#include <sys/stat.h>   
#include <unistd.h>   


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

////////////////////////////////////////////////////////////////////////////////
// Name space declaration
//
/*using namespace rtk;
using namespace io;
using namespace parser;
*/
////////////////////////////////////////////////////////////////////////////////
// LineParser Implementation
//

int getFileSize(FILE* fp)   
{   
    fseek(fp, 0L, SEEK_END);  
    int size = ftell(fp);   
    return size;  
} 


LineParser::LineParser(): m_pBuffer(NULL),
                          m_iBufSize(0)
{

}

LineParser::~LineParser()
{
    ReleaseBuffer();
}

bool LineParser::SetContent(const char* pURL)
{
    if (!pURL || !(*pURL)) return false;
    ReleaseBuffer();
    m_StrURL = pURL;
    
    // NOTICE!!!
    //
    // The CFile cannot be released immediately when downloading files by http downloader.
    // By testing, if releasing it immediately, it will cause memory trash.
    // And then system may be crashed.
    // It seems that http downloader exists bugs!!!! 
    // (branch_src_sharedMemory_integration\Platform_Lib\http_file\http_download.cpp)
    //
    FILE* file;
	file = fopen(m_StrURL.c_str(),"rb");
	if(file == NULL)
		return false;
    //if (!file.Open(m_StrURL.c_str())) return false;
    
    int iFileSize = getFileSize(file);
    m_iBufSize = iFileSize + 1;
    m_pBuffer = new char[m_iBufSize];
    zDebug("LineParser::SetContent(): iFileSize=%d\n", iFileSize);
    
    BYTE* pBuf = (BYTE*)m_pBuffer;
    int iAvailableBufSize = iFileSize, iRead = 0;
    while (iAvailableBufSize > 0)
    {
        iRead = fread(pBuf, sizeof(char),iAvailableBufSize,file);
        if (iRead <= 0) break;
        pBuf += iRead;
        iAvailableBufSize -= iRead;
    } 
    m_pBuffer[iFileSize] = 0;
    zDebug("LineParser::SetContent(): %s(%s)\n", m_StrURL.c_str(), (char*)m_pBuffer);
    return iFileSize > 0 && iAvailableBufSize == 0 && Parse(m_pBuffer, m_iBufSize);
}

bool LineParser::SetContent(const char* pTextBuffer, UINT iBufSize)
{
    if (!pTextBuffer || !iBufSize) return false;
    
    ReleaseBuffer();
    m_iBufSize = iBufSize + 1;
    m_pBuffer = new char[m_iBufSize];
    strncpy(m_pBuffer, pTextBuffer, iBufSize);
    m_pBuffer[iBufSize] = 0;

    return Parse(m_pBuffer, m_iBufSize);
}

const char* LineParser::operator[](UINT iIndex)
{
    return iIndex < m_LineList.size() ? m_LineList[iIndex] : NULL;
}

bool LineParser::Parse(char* pTextBuffer, UINT iBufSize)
{
    if (!pTextBuffer || !iBufSize) return false;

    char* pLineStart = pTextBuffer;
    char* pNextLine = pLineStart;
    while ((pNextLine = strstr(pNextLine, "\n")))
    {
        // Make a c-string. We set "\n" or "\r\n" to NULL
        //
        char* pTmp = pNextLine;
        *pTmp = 0;
        if (pTmp > pLineStart && (*(--pTmp) == '\r')) 
            *pTmp = 0;
        
        if (pTmp > pLineStart) // Don't keep a null string.
            m_LineList.push_back(pLineStart);
        
        pNextLine++;
        pLineStart = pNextLine;
    }
    
    if (pLineStart && !pNextLine && strlen(pLineStart)) 
        m_LineList.push_back(pLineStart);
 
    return m_LineList.size() > 0;
}

void LineParser::ReleaseBuffer()
{
    if (m_pBuffer) delete[] m_pBuffer;
    m_pBuffer = NULL;
    m_iBufSize = 0;
    m_LineList.clear();
}

////////////////////////////////////////////////////////////////////////////////
