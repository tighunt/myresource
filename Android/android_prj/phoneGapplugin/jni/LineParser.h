////////////////////////////////////////////////////////////////////////////////
// Line parser
//
// Separate text into lines by token "\r\n" or "\n"
//
// Author: zackchiu@realtek.com
//
// Created: 10/19/2010
////////////////////////////////////////////////////////////////////////////////

#ifndef __LINE_PARSER__
#define __LINE_PARSER__

#include <vector>
#include <string>
#include "CrossPlatformDecl.h"

int getFileSize(FILE* fp) ;

//using namespace std;
/*
namespace rtk    {
namespace parser {
*/
class LineParser
{
    public:
        LineParser();
        ~LineParser();
        
        bool SetContent(const char* pURL);
        bool SetContent(const char* pTextBuffer, UINT iBufSize);
        UINT GetLineCounts() const { return m_LineList.size(); };
        const char* operator[](UINT iIndex);
        
        void Reset() { ReleaseBuffer(); };
        
    private:
        char* m_pBuffer;
        UINT m_iBufSize;
        std::vector<char*> m_LineList;
        std::string m_StrURL;

    private:
        bool Parse(char* pTextBuffer, UINT iBufSize);
        void ReleaseBuffer();
};
/*
}; // namespace parser
}; // namespace rtk
*/
#endif
