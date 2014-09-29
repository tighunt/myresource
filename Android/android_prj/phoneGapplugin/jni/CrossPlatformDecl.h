////////////////////////////////////////////////////////////////////////////////
// Type and Macro Declaration
//
// These declarations are for cross-platform programming.
// Using these definition, you can compile your code between Linux and Windows.
//
// Author: zackchiu@realtek.com
//
// Created: 10/19/2010
////////////////////////////////////////////////////////////////////////////////

#ifndef __CROSS_PLATFORM_TYPE__
#define __CROSS_PLATFORM_TYPE__

#ifndef NULL
    #define NULL            0
#endif

#ifndef BYTE
    typedef unsigned char   BYTE;
    typedef unsigned char*  PBYTE;
#endif

#ifndef UINT
    typedef unsigned int    UINT;
    typedef unsigned int*   PUINT;
#endif

#ifdef WIN32
    #define strcasecmp _stricmp
#endif

#ifndef INVALID_FILE_SIZE
    #define INVALID_FILE_SIZE -1
#endif

#endif
