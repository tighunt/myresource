/**
 * Interface definition / prototypes
 */
#include <assert.h>
#include <stdio.h>
#include <unistd.h>

#include "hdcp2_hal.h"
#include <sys/socket.h>
#include <netinet/in.h>
#include "bigdigits.h"
#include "crypto.h"
#include "hdcp2_messages.h"
#include "hdcp2_interface.h"
#include <openssl/aes.h>
#include "rcp_api.h"
//#include "commands.h"
#include "hmac.h"
#include "HDCP2X_VSN.h"
#include "../include/hdcpDebug.h"

#if (HDCP_REPEATER == 1)
#include "hdmi.h"
#endif

#define LOG_TAG "HDCP2_INTERFACE"
#include <utils/Log.h>
#if 0
#define MYDEBUG(args...)
#else
#define MYDEBUG(args...) ALOGI(args)
#endif

H2_gKsvInfo gKsvInfo;
int gPairingState = PAIRING_INIT;
int g_REP_seq_num = -1;
int RXID_LIST_200MS_TIMEOUT_STATE = RXID_200MS_TIMEOUT_UNDEF;
tSerializationSecrets SerializationSecrets;

/**
 * flag to indicate that if key has protocol descriptor bit within.
 */
static char gHDCP22ProtocolDescriptorBit;
static void hdcp22_SetProtocolDescriptorBitFlag(unsigned char *cert);

static int str_to_bytes(const char* str, unsigned char* pBuff, unsigned long Len)
{    
     char*           ptr = (char*) str;    
    unsigned long   len;    
    unsigned long   nBytes;    
    int             i;
                    
    len = strlen(str);
    
    // get number of bytes
    nBytes = (len +1) >>1;       
    
    if (nBytes > Len) 
    {
        MYDEBUG("convert str to bytes failed - no enough memory (nBytes =%d, BuffLen = %d)\n",nBytes, Len);
        return -1;
    }                
    
    for (i=0; i<nBytes; i++)
    {        
        char     tmp[3] = {0,0,0};            
        char*    p;
        long int val;  
        
        tmp[0]= *ptr++;
        tmp[1]= *ptr++;                    
        
        if ((val=strtol(tmp,&p,16))<0)
        {
            MYDEBUG("convert str to bytes failed - '%s' is not in hex format\n", str);
            break;
        }
        
        pBuff[i] = (unsigned char) val;             
    }                

    return nBytes;
}

static int read_binary_file(const char* path, unsigned char* buff, unsigned int len)
{
    FILE* fd = fopen(path, "rb");     
    int ret;
    
    if (fd==NULL) {
        MYDEBUG("read_binary_file %s failed, file does not exists\n", path);
        return -1;
    }
    
    ret = fread(buff, 1, len, fd);     
    
    fclose(fd);    
    
    return ret;
}

void hdcp2_SaveStringInFile(unsigned char *filename, unsigned char* str, int len)
{
#ifndef PKT_DEBUG
	return;
#endif
	FILE *fdf=NULL;
	fdf = fopen(filename, "w+");
	if (fdf == 0)
			MYDEBUG("[LY] open file %s error\n", filename);
	fwrite(str, 1, len, fdf);
	fclose(fdf);
}

#ifdef HDCP2_KEY_PROTECT_WITH_KIMG
//#if 0
H2status hdcp2_load_from_flash(void)
{
#define HDCP2_KEY_AT_FACTORY_AREA
	#define TMP_KK "/tmp/tmp_kk.bin"
    H2Sha256Ctx_t c;
    unsigned char buff[2048];   
    unsigned char tmp[1024];
    unsigned char tmp_hdcp2[1024];
    unsigned char iv[16];
	bool bTmpKK = false;
	FILE *fdf=NULL;
	FILE *fd=NULL;
    unsigned char m[] = {
        0xf9, 0xd1, 0xbc, 0x13, 0x5d, 0x53, 0xa7, 0x70, 
        0x42, 0x2c, 0x1a, 0x17, 0x93, 0x03, 0xd8, 0x7f
    };

    memset(tmp, 0, sizeof(tmp));                                         
    memset(buff, 0, sizeof(buff));             
   
    MYDEBUG("[LY] HDCP2 use load key\n");
    
    DPRINT(("\033[0;31;31m[DHDCP] set Protocol descriptor bit to 0 (used in HDCP 2.2 mode)\033[m\n"));
    gHDCP22ProtocolDescriptorBit=0;
#ifdef HDCP2_KEY_AT_FACTORY_AREA
	//FIXME hack by baili_feng
    if (access("/tmp/factory/hdcp2.1.bin", F_OK) == 0 && read_binary_file("/tmp/factory/hdcp2.1.bin", tmp, sizeof(tmp))>0)
    {
		MYDEBUG("hdcp2_interface[%d] kelly find 2.1 key in factory hdcp2.1.bin\n",__LINE__);
    }
    else if (access("/tmp/factory/hdcp2.2.bin", F_OK) == 0 && read_binary_file("/tmp/factory/hdcp2.2.bin", tmp, sizeof(tmp))>0)
    {
		MYDEBUG("hdcp2_interface[%d] kelly find 2.2 key in factory hdcp2.1.bin\n",__LINE__);
    }
	else if (access("/tmp/factory_ro/hdcp2.1.bin", F_OK) == 0 && read_binary_file("/tmp/factory_ro/hdcp2.1.bin", tmp, sizeof(tmp))>0)
	{
		MYDEBUG("hdcp2_interface[%d]kelly find 2.1 key in factory_ro\n",__LINE__);
    }
    else if (access("/tmp/factory_ro/hdcp2.2.bin", F_OK) == 0 && read_binary_file("/tmp/factory_ro/hdcp2.2.bin", tmp, sizeof(tmp))>0)
	{
		MYDEBUG("hdcp2_interface[%d]kelly find 2.2 key in factory_ro\n",__LINE__);
    }
	else if (access("/usr/local/bin/hdcp2.1.bin", F_OK) == 0 && read_binary_file("/usr/local/bin/hdcp2.1.bin", tmp, sizeof(tmp))>0)
    {
		MYDEBUG("hdcp2_interface[%d]kelly find key 2.1 in /usr/local/bin\n",__LINE__);
    }
    else if (access("/usr/local/bin/hdcp2.2.bin", F_OK) == 0 && read_binary_file("/usr/local/bin/hdcp2.2.bin", tmp, sizeof(tmp))>0)
    {
		MYDEBUG("hdcp2_interface[%d]kelly find key 2.2 in /usr/local/bin\n",__LINE__);
    }
    else
	{
		MYDEBUG("hdcp2_interface[%d]can not find hdcp key\n",__LINE__);
        return H2_ERROR;
	}
#else
	fd = fopen("/sys/realtek_boards/_custom_param_", "r");
	fread(buff, 1, sizeof(buff), fd);
	fclose(fd);
    //if (read_binary_file("/sys/realtek_boards/_custom_param_", buff, sizeof(buff))<0)
    //    return H2_ERROR;
    str_to_bytes(buff, tmp, sizeof(tmp));				
#endif
#ifdef HDCP2_KEY_AT_FACTORY_AREA
	/*
#if !defined(IS_TV_CHIP)
	if (access(TMP_KK, F_OK) == 0)
	{
		MYDEBUG("got kk\n");
		bTmpKK = true;
	}
	if (bTmpKK)
	{
		if (read_binary_file(TMP_KK, m, sizeof(m))<0)
			return H2_ERROR;		
	}
	else
	{
    	if (read_binary_file("/sys/realtek_boards/AES_IMG_KEY", m, sizeof(m))<0)
        	return H2_ERROR;		
	}
		        
    RCP_AES_ECB_Decryption(m, tmp, tmp, sizeof(tmp)); 
#endif
*/
    RCP_AES_ECB_Decryption(NULL, tmp, tmp, 976);    //976 is the encryption data length
	memcpy(tmp_hdcp2, tmp+4, 16);
	memcpy(tmp_hdcp2+16, tmp+40, 842);
	memcpy(tmp, tmp_hdcp2, sizeof(tmp_hdcp2));
	  MYDEBUG("hdcp2 reserve [%d] kelly find key in factory_ro %d %d %d %d %d %d\n"
        , __LINE__, tmp[150], tmp[151], tmp[152], tmp[153], tmp[154], tmp[155]);

#else
    if (read_binary_file("/sys/realtek_boards/modelconfig", iv, sizeof(iv))<0)
        return H2_ERROR;
        
    RCP_AES_CBC_Decryption(m, iv, tmp, tmp, sizeof(tmp));
#endif

#if 0
	fdf = fopen("/tmp/_custom2_", "w+");
	if (fdf == 0)
			MYDEBUG("[LY] open file error\n");
	fwrite(tmp, 1, LC128_SIZE, fdf);
	fwrite(&tmp[16], 1, CERT_RX_SIZE, fdf);
	fwrite(&tmp[16+522], 1, KPRIVRX_SIZE, fdf);
	fwrite(&tmp[16+522], 1, 6, fdf); //padding 6 bytes
	fclose(fdf);
#endif
    // save data in SRAM....  
    spu_SetLc128(tmp);
        
    //spu_printBytes_with_text(&tmp[16], 522, "hdcp2_load_from_flash : CertRx :");
            
    spu_SetCertRx(&tmp[16]);   
    // save 522 bytes starts from tmp[16]         
    MYDEBUG("spu_SetCertRx %02x %02x %02x %02x\n", tmp[16], tmp[17], tmp[18], tmp[19]);
            
    spu_SetKPrivRx(&tmp[16+522]);    
    
    hdcp22_SetProtocolDescriptorBitFlag(&tmp[16]);

    // compute kh             
    crypto_sha256Init(&c);
    crypto_sha256Update(&c, &tmp[16+522], KPRIVRX_SIZE);
    crypto_sha256Final(&c, tmp);    
    spu_SetKH(&tmp[16]);

    // flush cache...    
    memset(tmp, 0, sizeof(tmp));        
    MYDEBUG("kelly return H2_OK");
    return H2_OK;
}
#else

const unsigned char gcLc128[] = 
{
   0x93, 0xce, 0x5a, 0x56, 0xa0, 0xa1, 0xf4, 0xf7, 
   0x3c, 0x65, 0x8a, 0x1b, 0xd2, 0xae, 0xf0, 0xf7
};

// R1 Cert
const unsigned char gcCert1[] = 
{
   0x74, 0x5b, 0xb8, 0xbd, 0x04, 0xbc, 0x83, 0xc7, 0x95, 0x78, 0xf9, 0x0c, 0x91, 0x4b, //14
   0x89, 0x38, 0x05, 0x5a, 0xa4, 0xac, 0x1f, 0xa8, 0x03, 0x93, 0x82, 0x79, 0x75, 0xaf, 
   0x66, 0x22, 0xde, 0x43, 0x80, 0x8d, 0xcd, 0x5d, 0x90, 0xb8, 0x3c, 0xb3, 0xd8, 0x9e, 
   0xb0, 0x0d, 0x09, 0x44, 0xf4, 0x3f, 0x5f, 0xab, 0xb9, 0xc4, 0xc9, 0x96, 0xef, 0x78, 
   0xb5, 0x8f, 0x69, 0x77, 0xb4, 0x7d, 0x08, 0x14, 0x9c, 0x81, 0xa0, 0x8f, 0x04, 0x1f, 
   0xa0, 0x88, 0xe1, 0x20, 0xc7, 0x34, 0x4a, 0x49, 0x35, 0x65, 0x99, 0xcf, 0x53, 0x19, 
   0xf0, 0xc6, 0x81, 0x76, 0x05, 0x5c, 0xb9, 0xde, 0xdd, 0xab, 0x3d, 0xb0, 0x92, 0xa1, 
   0x23, 0x4f, 0x0c, 0x71, 0x30, 0x42, 0x78, 0xf6, 0x55, 0xae, 0xbd, 0x36, 0x25, 0x8e, 
   0x25, 0x0d, 0x4e, 0x5e, 0x8e, 0x77, 0x6a, 0x60, 0xe3, 0xc1, 0xe9, 0xee, 0xcd, 0x2b, //
   0x9e, 0x18, 0x63, 0x97, 0xd4, 0xe6, 0x75, 0x01, 0x00, 0x01, 0x00, 0x00, 0x1d, 0x0a, // 0x00, 0x00, 0x1d, 0x0a, <- reserved bit
   0x61, 0xea, 0xab, 0xf8, 0xa8, 0x2b, 0x02, 0x69, 0xa1, 0x34, 0xfd, 0x91, 0xac, 0x2b, 
   0xf2, 0x8f, 0x34, 0x8b, 0xd4, 0x84, 0xfa, 0x62, 0xbc, 0x01, 0x4a, 0x4a, 0xa2, 0xb2, 
   0x14, 0xbf, 0xb5, 0xf4, 0xdf, 0xac, 0x80, 0x93, 0x0d, 0x13, 0xec, 0x9c, 0xe5, 0xd8, 
   0x34, 0x70, 0x51, 0x9a, 0x66, 0x80, 0xeb, 0xbe, 0xcc, 0x7e, 0x45, 0xf0, 0xe6, 0x39, 
   0x63, 0x84, 0xc9, 0xb9, 0x8e, 0x8c, 0xaf, 0x9c, 0xa9, 0xd4, 0x0e, 0xeb, 0x9a, 0x57, 
   0x2a, 0x17, 0x41, 0xca, 0x97, 0xf3, 0x19, 0x96, 0xb5, 0x5d, 0x0f, 0x30, 0xa3, 0x84, 
   0xe5, 0x73, 0xa2, 0xed, 0x05, 0x69, 0x7a, 0x22, 0xce, 0x84, 0x1f, 0x3e, 0x39, 0x9e, 
   0x28, 0x76, 0xc9, 0xbc, 0x89, 0x5b, 0x70, 0xb1, 0x7b, 0xf4, 0xed, 0xb6, 0x74, 0x12, 
   0xab, 0x48, 0x29, 0x64, 0xce, 0x6c, 0x60, 0x04, 0xeb, 0xa9, 0x7a, 0xa2, 0x15, 0xa6, 
   0x58, 0x9a, 0xad, 0x32, 0xc7, 0x53, 0x39, 0xe5, 0xfe, 0xf0, 0x37, 0xa7, 0xa0, 0xc5, 
   0xff, 0xec, 0xd9, 0xb0, 0x05, 0xbb, 0x25, 0x13, 0xa0, 0xa4, 0xc7, 0x0b, 0x2a, 0x5d, 
   0xc6, 0x8f, 0x51, 0x11, 0xcb, 0x36, 0xed, 0x5c, 0x17, 0x7e, 0x22, 0x20, 0xc3, 0xeb, 
   0x40, 0x8c, 0x67, 0xbb, 0x1c, 0xd2, 0x47, 0xb0, 0xe0, 0xbd, 0xe7, 0x4c, 0xcd, 0x5d, 
   0xd5, 0x23, 0x12, 0xf8, 0x3b, 0x1d, 0x91, 0x3b, 0xf3, 0xc7, 0x60, 0xea, 0x90, 0x24, 
   0x48, 0xe5, 0x92, 0x21, 0x6c, 0xf6, 0xd9, 0x5e, 0x76, 0x8d, 0x2b, 0x86, 0xa6, 0x7c, 
   0x16, 0xae, 0xa8, 0x36, 0x08, 0xa0, 0x37, 0x14, 0x1a, 0xd7, 0x03, 0xe1, 0x40, 0x31, 
   0xca, 0x6c, 0x95, 0xe0, 0x10, 0xb0, 0x43, 0xcf, 0xb7, 0xe0, 0x30, 0x05, 0xb9, 0xac, 
   0xb7, 0x08, 0x68, 0xcd, 0x7e, 0x11, 0x47, 0x2a, 0x03, 0x3b, 0xeb, 0x74, 0xc8, 0x19, 
   0x62, 0x8b, 0x2f, 0x11, 0x91, 0xb6, 0x06, 0x4f, 0xe0, 0x2a, 0x44, 0x20, 0x43, 0x29, 
   0x13, 0x1f, 0xdd, 0xd0, 0x4a, 0x11, 0x6c, 0x0e, 0x83, 0xbf, 0x22, 0x62, 0x3b, 0xeb, 
   0xec, 0xd7, 0x76, 0x28, 0xba, 0x64, 0x88, 0x42, 0xc8, 0x73, 0xa7, 0x9e, 0x4a, 0x69, 
   0x3a, 0xb2, 0x0c, 0x4b, 0x3a, 0xd9, 0x50, 0xdb, 0x7c, 0x51, 0xee, 0x15, 0xe0, 0x6b, 
   0x2c, 0x63, 0xa6, 0x91, 0x57, 0xdd, 0xbf, 0x17, 0x47, 0x23, 0xad, 0x15, 0xcb, 0xb9, 
   0x91, 0x18, 0x0b, 0x51, 0x8f, 0xf9, 0x1c, 0x51, 0x67, 0xc1, 0x0b, 0x78, 0xf5, 0xd9, 
   0x55, 0xdc, 0x48, 0xe4, 0xc0, 0x83, 0xa5, 0xdf, 0x75, 0xe2, 0xdc, 0x88, 0xd2, 0xc6, 
   0xdd, 0xdf, 0x1f, 0x37, 0x90, 0x35, 0xf6, 0xfd, 0xda, 0xe0, 0x04, 0x32, 0x69, 0xc1, 
   0xaf, 0xd9, 0xf9, 0x11, 0xc5, 0xaa, 0x74, 0x58, 0x32, 0x1c, 0x71, 0xaa, 0xa7, 0x14, 
   0xfb, 0x23, 0x17, 0x22, 
};


typedef struct 
{
   unsigned char p[64];
   unsigned char q[64];
   unsigned char dP[64];
   unsigned char dQ[64];
   unsigned char qInv[64];
} kpriv_t;

// R1 Private Key
const kpriv_t gcKpriv1 =
{
   //p[] =
   {
      0xdc, 0x1a, 0x02, 0xb8, 0x36, 0xed, 0x3a, 0xe8, 0x74, 0x74, 0xcd, 0x72, 0x28, 0x4a, 0xee, 0x31, 0x90, 0xe4, 0xd0, 
      0x6a, 0xf9, 0xf6, 0xf8, 0xd3, 0x50, 0x29, 0xc2, 0x84, 0x97, 0x98, 0x10, 0x5d, 0xea, 0x7b, 0x88, 0xfd, 0x36, 0xc5, 
      0x04, 0x99, 0xad, 0xab, 0x27, 0x0a, 0x5a, 0x2a, 0xf9, 0x18, 0x7b, 0x7d, 0xb0, 0xc3, 0xcb, 0xe3, 0x5a, 0xc2, 0x9a, 
      0x10, 0xf7, 0xc9, 0x9a, 0x18, 0x3e, 0xb5, 
   },

   //q[] = 
   {
      0xdb, 0x42, 0xe9, 0x42, 0xe3, 0x2a, 0x78, 0xc9, 0x6f, 0x2b, 0x7b, 0x74, 0xd6, 0x9b, 0xae, 0xb9, 0x3d, 0xf4, 0xe7, 
      0x35, 0x90, 0x1c, 0xc4, 0x5a, 0xb4, 0x22, 0x8c, 0x3c, 0x08, 0x9b, 0xa5, 0x29, 0x64, 0x57, 0x29, 0xb2, 0xc4, 0x80, 
      0xf9, 0xee, 0xc6, 0x94, 0x30, 0x3e, 0xd2, 0x33, 0x9f, 0xbb, 0x6a, 0x43, 0x03, 0x89, 0x14, 0x9b, 0x8f, 0x20, 0xb8, 
      0x60, 0x1f, 0x7f, 0x30, 0x3b, 0x20, 0xc1, 
   },

   //dP[] = 
   {
      0x73, 0xd1, 0xa4, 0x18, 0xb7, 0x9e, 0x81, 0xdf, 0x0c, 0x58, 0xe2, 0x3a, 0xee, 0x04, 0xef, 0xee, 0x59, 0x26, 0x6e, 
      0x9d, 0xbc, 0x47, 0x3f, 0x8c, 0x42, 0xa4, 0x96, 0xdd, 0x1a, 0xc0, 0x43, 0xec, 0x87, 0x94, 0xd5, 0xf3, 0x18, 0xbc, 
      0xf7, 0xbc, 0xbe, 0x6c, 0x4f, 0xb0, 0xdc, 0xdd, 0xbc, 0x12, 0x2b, 0xf9, 0x69, 0xe8, 0xbe, 0x03, 0x37, 0x21, 0x2b, 
      0xdd, 0x3d, 0xe6, 0x72, 0x15, 0xcb, 0xf9, 
   },

   //dQ[] = 
   {
      0x09, 0x1d, 0xe6, 0x1f, 0x0e, 0xdd, 0x04, 0x3a, 0xb3, 0xf1, 0xa5, 0xe7, 0x7c, 0xc8, 0xea, 0x61, 0xef, 0x6e, 0x90, 
      0x72, 0x8c, 0xb4, 0x75, 0x81, 0xa3, 0xfd, 0xcf, 0xc0, 0xeb, 0x46, 0xb5, 0x7e, 0x5c, 0x1a, 0xb7, 0xb4, 0x24, 0x31, 
      0x8c, 0xb2, 0xdd, 0xf4, 0xe9, 0x70, 0xa3, 0x42, 0xdc, 0x40, 0x69, 0xb1, 0xb1, 0xa2, 0xf0, 0x85, 0x6b, 0x55, 0x1b, 
      0xf5, 0x7b, 0x39, 0xc9, 0xa2, 0x9b, 0xc1,
   },

   //qInv[] =
   {
      0x89, 0x58, 0xa5, 0xa3, 0x63, 0xd9, 0xa9, 0xee, 0x0e, 0x7e, 0xa1, 0xc0, 0x56, 0x2d, 0x59, 0xfc, 0xf8, 0x66, 0x1c, 
      0x26, 0x48, 0x21, 0x9d, 0xe0, 0x61, 0xe4, 0xa8, 0x06, 0x97, 0x64, 0xc7, 0x01, 0x77, 0x47, 0x11, 0x8e, 0xa2, 0x81, 
      0xd2, 0x00, 0xdd, 0x5a, 0x1b, 0x8f, 0x7a, 0x1b, 0x2c, 0x68, 0x56, 0x39, 0xcf, 0xcd, 0xd3, 0x6a, 0xff, 0x73, 0x81, 
      0x1d, 0x91, 0x3d, 0x48, 0xb4, 0x43, 0x4c, 
   },
};


// easier management of source code.
#include "hdcp22_test_key.dat"

H2status hdcp2_load_from_flash( )
{
	H2Sha256Ctx_t c;
	unsigned char tmp[128];
	memset(tmp, 0x00, 128);
	
    MYDEBUG("[LY] HDCP2 use test key \n");
    // save data in SRAM....  
#if (HDCP_2X_VSN==22)

    DPRINT(("\033[0;31;31m[DHDCP] LOAD HDCP 2.2 TEST KEY\033[m\n"));

    spu_SetLc128(gcLc128_22);

    spu_SetCertRx(gcCert1_22);
    hdcp22_SetProtocolDescriptorBitFlag(gcCert1_22);

    spu_SetKPrivRx((unsigned char*)&gcKpriv1_22);

    // compute kh
    crypto_sha256Init(&c);
	crypto_sha256Update(&c, (unsigned char*)&gcKpriv1_22, KPRIVRX_SIZE);
	crypto_sha256Final(&c, tmp);
	spu_SetKH(&tmp[16]);
#else

	DPRINT(("\033[0;31;31m[DHDCP] LOAD HDCP 2.1, 2.0 TEST KEY\033[m\n"));

    spu_SetLc128(gcLc128);
    spu_SetCertRx(gcCert1);
    hdcp22_SetProtocolDescriptorBitFlag(gcCert1);

    spu_SetKPrivRx((unsigned char*)&gcKpriv1);

    // compute kh
	crypto_sha256Init(&c);
	crypto_sha256Update(&c, (unsigned char*)&gcKpriv1, KPRIVRX_SIZE);
	crypto_sha256Final(&c, tmp);
	spu_SetKH(&tmp[16]);

#endif
    return H2_OK;
}

#endif

/**
 * Zero out session secrets, called from CID_INIT
 */
H2status hdcp2_init( void )
{
    
   h2Init();	
   gHDCP22ProtocolDescriptorBit =0;
   hdcp2_load_from_flash();
   memset( (char *)&SessionSecrets, 0, sizeof( SessionSecrets ));
   memset( (char *)&gKsvInfo, 0, sizeof( gKsvInfo ));
   /* Disable the HDMI police */
#if (HDCP_REPEATER == 1)
   hdmi_policeEnable(FALSE);
#endif

   return H2_OK;
}

/**
 * Zero out session secrets
 */
H2status hdcp2_reset( void )
{
   memset( (char *)&SessionSecrets, 0, sizeof( SessionSecrets ));

   /* Disable the HDMI police */
#if (HDCP_REPEATER == 1)
   hdmi_policeEnable(FALSE);
#endif

   return H2_OK;
}

/**
 * Generate the Pseudo-Random 64 bit value rRx.
 *
 * Results are stored in SessionSecrets.rRx.
 *
 */
H2status hdcp2_GenrRx( void )
{
   H2uint32 rRx[2];
   unsigned int ii=0;

   for (ii=0;ii<sizeof(rRx)/sizeof(rRx[0]);ii++)
   {
      rRx[ii] = crypto_random32();
   }
   memcpy( SessionSecrets.rRx, rRx, sizeof(SessionSecrets.rRx ));

   return H2_OK;
}

/**
 * Copy RTX to Session Secrets.
 */
H2status hdcp2_Setrtx( const H2uint8 *rtx )
{
   memcpy( SessionSecrets.rtx, rtx, sizeof(SessionSecrets.rtx ));

   return H2_OK;
}

 /**
 * Copy L-Prime to Session Secrets.
 */
H2status hdcp2_SetLPrime( const H2uint8 *lprime )
{
   memcpy( SessionSecrets.lPrime, lprime, sizeof(SessionSecrets.lPrime ));

   return H2_OK;
}

/**
 * Copy Rn to Session Secrets.
 */
H2status hdcp2_Setrn( const H2uint8 *rn )
{
   memcpy( SessionSecrets.rn, rn, sizeof(SessionSecrets.rn ));

   return H2_OK;
}

/**
 * Set initialization vector RIV.
 */
H2status hdcp2_SetRiv( const H2uint8 *riv )
{
   spu_SetRiv(riv);

   return H2_OK;
}

/**
 * Get initialization vector RIV.
 */
H2status hdcp2_GetRiv( H2uint8 *riv )
{
   spu_GetRiv(riv);

   return H2_OK;
}

/**
 * Get CWOffset of initialization vector RIV.
 */
int hdcp2_GetRiv_CWOffset( H2uint8 *riv )
{
   return SRAM_RIV_ENTRY;
}


/**
 * Get CWoffset of initialization vector KS.
 */
int hdcp2_GetKSXorLc128_CWOffset()
{
   return SRAM_KS_XOR_LC128_ENTRY;
}


/**
 * Get initialization vector KS.
 */
H2status hdcp2_GetKSXorLc128( H2uint8 *ks )
{
   spu_GetKsXorLc128(ks);

   return H2_OK;
}

/**
 * Receive EKpubKm from userspace and process it.
 *
 * Results are stored in SessionSecrets.
 * @todo Support 'repeater'=1.
 *
 */
H2status hdcp2_SetEKpubKm( const H2uint8 *EKpubKm )
{
        MYDEBUG("hdcp2_SetEKpubKm start !\r\n");
		H2uint8 Km_tmp[MASTERKEY_SIZE]={0};
		H2uint8 Kd_tmp[KD_SIZE]={0};
		H2uint8 KPriv_tmp[KPRIVRX_SIZE]={0};
		H2status rc;
				
		/* Decrypt Km */				
		spu_GetKPrivRx(&KPriv_tmp[0]);
		rc = Decrypt_EKpubKm_kPrivRx(KPriv_tmp, Km_tmp, EKpubKm);
			
		if (rc!=H2_OK){ 	 
			MYDEBUG("Error decrypting master key!\r\n");
			goto end_proc;
		}
		// when it's done, save to KmCW
		spu_SetKM(Km_tmp);
		
		MYDEBUG("hdcp2_SetEKpubKm end !\r\n");
			
		//move to next step to reduce the time replying the rrx pkt
		#if 0
		/** Generate Kd = dKey0 || dKey1 */
		RCP_HDCP2_GenKd(SRAM_KM_ENTRY, SessionSecrets.rtx, SessionSecrets.rn, SRAM_KD_ENTRY);
		spu_GetKD(Kd_tmp);
		
		/** Compute HPrime from Kd, RTX, and Repeater */
		Compute_Hprime(Kd_tmp, SessionSecrets.rtx, HDCP_REPEATER, SessionSecrets.hPrime);
		#endif
		
	end_proc:
	// clear meta buffer.
		memset(Kd_tmp, 0, sizeof(Kd_tmp));
		memset(Km_tmp, 0, sizeof(Km_tmp));	  
		memset(KPriv_tmp, 0, sizeof(KPriv_tmp));   
		return rc;
}

/**
 * Set EKhKm and m. m should be stored after EKhKm.
 */

H2status hdcp2_SetEKhKm( const H2uint8 *EKhKm, const H2uint8 *m )
{
    static H2uint8 Kh_temp[H_SIZE];
    static H2uint8 Km_temp[MASTERKEY_SIZE];
    static H2uint8 Kd_temp[KD_SIZE];
            
    memcpy(SessionSecrets.m, m, M_SIZE );
    spu_GetKH(Kh_temp);
        
    /** Compute Km */
    RCP_AES_CTR_Cipher(Kh_temp, SessionSecrets.m, EKhKm, Km_temp, EKHKM_SIZE);     
    spu_SetKM(Km_temp);

#if 0
    /** Compute Kd */
    int modeHDCP22=0;
    
	if(getHDCPCAP()==CAP_HDCP2_2 && getRTKHDCPSTR()->transmitterVersion==0x2)
	{
		MYDEBUG("hdcp kelly hdcp2_interface.c[%d] CAP_HDCP2_2 !\r\n",__LINE__);		
		modeHDCP22=1;
	}
		
    RCP_HDCP2_GenKd(SRAM_KM_ENTRY, SessionSecrets.rtx, SessionSecrets.rRx, SessionSecrets.rn, SRAM_KD_ENTRY,modeHDCP22);
    spu_GetKD(Kd_temp);
    
    /** Compute HPrime */
    Compute_Hprime(Kd_temp, SessionSecrets.rtx, HDCP_REPEATER, SessionSecrets.hPrime);
#endif
    
    memset(Kh_temp, 0, sizeof(Kh_temp));    
    memset(Km_temp, 0, sizeof(Km_temp));  
    memset(Kd_temp, 0, sizeof(Kd_temp));    
    return H2_OK;
}

/**
 * Process EdKeyKs message. Compute sessionKey based on
 * EdKeyKs and dKey2
 */
H2status hdcp2_SetEdKeyKs( const H2uint8 *EdKeyKs )
{
	/** Compute dKey2 */
	int modeHDCP22=0;
	if(getHDCPCAP()==CAP_HDCP2_2 && getRTKHDCPSTR()->transmitterVersion==0x2)
	{
		MYDEBUG("hdcp kelly hdcp2_interface.c[%d] CAP_HDCP2_2 !\r\n",__LINE__);	
		modeHDCP22=1;
	}
	RCP_HDCP2_GenDKey(SRAM_KM_ENTRY, SessionSecrets.rtx,SessionSecrets.rRx, SessionSecrets.rn, 2, SRAM_DK2_ENTRY,modeHDCP22);

	/** Compute Session Key */	  
	RCP_HDCP2_GenKs(SRAM_DK2_ENTRY, EdKeyKs, SessionSecrets.rRx, SRAM_KS_XOR_LC128_ENTRY); 
	
	/** Compute AES Key */	  
	RCP_HDCP2_GenKsXorLc128(SRAM_LC128_ENTRY, SRAM_KS_XOR_LC128_ENTRY, SRAM_KS_XOR_LC128_ENTRY); 
	
	/*
	 * Enable the HDMI police
	 */
#if (HDCP_REPEATER == 1)
   hdmi_policeEnable(TRUE);
#endif

   return H2_OK;

}

/**
 * Fetch the generated rRx value to send to the TX.
 *
 */
H2status hdcp2_GetrRx( H2uint8* pOut, H2uint32 ulSize )
{
    if (( NULL == pOut ) || ( ulSize < sizeof(SessionSecrets.rRx)))
     {
       return H2_ERROR;
     }
    memcpy( pOut, SessionSecrets.rRx , sizeof(SessionSecrets.rRx) );
    return H2_OK;
}

/**
 * Read the public key certificate from Serialization Secrets.
 */
H2status hdcp2_GetCertRx( H2uint8* pOut, H2uint32 ulSize )
{
    spu_GetCertRx(pOut); 
    MYDEBUG("spu_GetCertRx %02x %02x %02x %02x\n", pOut[0], pOut[1], pOut[2], pOut[3]);
    return H2_OK;
}

/**
 * Get the computed EKhKm value to be sent to the TX
 */
H2status hdcp2_GetEKhKm( H2uint8* pOut, H2uint32 ulSize )
{
	// Verbose print during QA phase.
	DPRINT(("\033[0;31;31m[DHDCP] hdcp2_GetEKhKm() HDCPCap=%d TransmitterVersion=%d [%s %d]\033[m\n",
			getHDCPCAP(),
			getRTKHDCPSTR()->transmitterVersion,
			__FILE__,
			__LINE__ ));

	// receiver is HDCP 2.2 & transmitter is HDCP 2.2 (indicated by transmitterVersion)
	if(getHDCPCAP()==CAP_HDCP2_2 && getRTKHDCPSTR()->transmitterVersion==0x2)
	{
		MYDEBUG("hdcp kelly hdcp2_interface.c[%d] CAP_HDCP2_2 !\r\n",__LINE__);	
		RCP_HDCP2_EkhKm(SRAM_KH_ENTRY, SRAM_KM_ENTRY, SessionSecrets.rtx, SessionSecrets.rRx, pOut,1);
	}
	else
	{
		MYDEBUG("hdcp kelly hdcp2_interface.c[%d] CAP_HDCP2_2 !\r\n",__LINE__);	
		RCP_HDCP2_EkhKm(SRAM_KH_ENTRY, SRAM_KM_ENTRY, SessionSecrets.rtx, SessionSecrets.rRx, pOut,0);
	}
	return H2_OK;
}

/**
 * Get HPrime.
 */
H2status hdcp2_GethPrime( H2uint8* pOut, H2uint32 ulSize )
{
    H2uint32 size = sizeof( SessionSecrets.hPrime );
 
    if (( NULL == pOut ) || ( ulSize < size ))
     {
       return H2_ERROR;
     }
 
    memcpy( pOut, &SessionSecrets.hPrime, sizeof(SessionSecrets.hPrime));
    return H2_OK;
}

  /**
  * Get 256 bits m prime..
  */
 H2status hdcp2_GetMPrime( H2_RepeaterAuthReady_PayLoad* pOut, unsigned char *message, int length )
 {
 H2status rc = H2_ERROR;
 H2Sha256Ctx_t c;
 unsigned char mp[1024];
 unsigned char sha256_kd[32]={0};
 int seq_num_M=0;
 int k=0,i=0;
 unsigned char num[4]={0};
 H2uint8 Kd_tmp[KD_SIZE];
 
 memset(mp, 0x00, 1024);
 
 do
 {
	if (( NULL == pOut) || ( message == NULL))
	{
	   rc = H2_ERROR;
	   break;
	}

	//read bytes into int
	//seq_num_M = *(message+(1+3-1));
	//k = *(message+(1+3+2-1));
	seq_num_M = B4TL(0x00, message[1], message[2], message[3]);
	k = B4TL(0x00, 0x00, message[4], message[5]);
	MYDEBUG("[LY] seq_num_M:%02x, k=%02x\n", seq_num_M, k);
	for (i=0;i<k;i++)
	{
		//streamCtr
		memcpy(mp+7*i, message+(1+3+2+7*i), 4);
		//ContentStreamID
		memcpy(mp+4+7*i, message+(1+3+2+7*i+4), 2);
		//Type
		mp[4+2+7*i] = *(message+(1+3+2+7*i+4+2));
	}

	//or seq_num_M at bottom of mp
	//MYDEBUG("[LY] i=%02x\n", i);
	mp[4+2+(7*(i-1))+1] = message[1];
	mp[4+2+(7*(i-1))+2] = message[2];
	mp[4+2+(7*(i-1))+3] = message[3];

	//count for sha256(kd)
	#if 1
     	crypto_sha256Init(&c);
	spu_GetKD(Kd_tmp);
 			crypto_sha256Update(&c, Kd_tmp, KD_SIZE);
 			crypto_sha256Final(&c, sha256_kd);  //256bits

	hdcp2_SaveStringInFile("/tmp/_streamid_type",mp, 7*k+3);
	rc = hmacsha256( sha256_kd, sizeof(sha256_kd), mp, (7*k)+3, pOut->MPrime);
	#else
	rc = hmacsha256( SessionSecrets.Kd, KD_SIZE, mp, 4+2+1+4+2+1+3, pOut->MPrime);
	#endif
 } while(0);
 memset(Kd_tmp, 0x00, sizeof(Kd_tmp));
 
 return rc;
 
 }

 /**
 * Get Most 128bits of L prime, called in process RTT_Challenge
 */
H2status hdcp2_GetMostlPrime( H2uint8* pOut, H2uint32 ulSize )
{
	 H2uint8 Kd_tmp[KD_SIZE];				
   H2uint32 size = sizeof( L_SIZE/2 );
 
   if (( NULL == pOut ) || ( ulSize < size ))
   {
      return H2_ERROR;
   }

	 spu_GetKD(Kd_tmp);
	 int modeHDCP22=0;
	 if(getHDCPCAP()==CAP_HDCP2_2 && getRTKHDCPSTR()->transmitterVersion==0x2)
	 {
	     MYDEBUG("hdcp kelly hdcp2_interface.c[%d] CAP_HDCP2_2 !\r\n",__LINE__);	
		 modeHDCP22=1;
	 }
	 Compute_Lprime(Kd_tmp, SessionSecrets.rRx, SessionSecrets.rn, SessionSecrets.lPrime,modeHDCP22);
	 memcpy( pOut, SessionSecrets.lPrime, ulSize );
	 memset(Kd_tmp, 0, sizeof(Kd_tmp));
	 return H2_OK;
}

H2status hdcp2_SetLeastVPrime( unsigned char * MVPrime )
{
	memcpy(SessionSecrets.vPrime, MVPrime+V_SIZE/2, V_SIZE/2); 
	return H2_OK;
}
H2status hdcp2_JudgeLeastVPrime( unsigned char * MVPrime , H2uint8 cnt )
{
	int ret=-1;
	H2uint32 size = V_SIZE/2;
	
  if (( NULL == MVPrime ) || ( cnt != size ))
  {
		return H2_ERROR;
  }
  
	//compare the least 128bits of V Prime
	ret = memcmp(SessionSecrets.vPrime, MVPrime, cnt); 
	if (ret != 0)
	{
		return H2_ERROR;
	}
  return H2_OK;
}

H2status hdcp2_JudgeLeastLPrime( H2_AkeRTTChallengePayLoad* payLoad , H2uint8 cnt )
{
	int ret=-1;
	H2uint32 size = L_SIZE/2;

	if (( NULL == payLoad ) || ( cnt != size ))
	{
  		return H2_ERROR;
	}
	//compare the least 128bits of L Prime
	ret = memcmp(SessionSecrets.lPrime+size, payLoad->L, cnt); 
	if (ret != 0)
	{
			return H2_ERROR;
	}
	return H2_OK;
}

/**
 * Get L prime.
 * The way to calculate L' is the same in both pre-compute and none pre-compute mode.
 */
H2status hdcp2_GetlPrime( H2uint8* pOut, H2uint32 ulSize)
{
    H2uint8 Kd_tmp[KD_SIZE];    
    H2uint32 size = sizeof( L_SIZE );
    memset(Kd_tmp, 0, sizeof(Kd_tmp));
    
    if (( NULL == pOut ) || ( ulSize < size ))
    {
      return H2_ERROR;
    }
               
    assert(pOut);
    assert(ulSize>=L_SIZE);    
    spu_GetKD(Kd_tmp);

    // Verbose print during QA phase
	DPRINT(("\033[0;31;31m[DHDCP] hdcp2_GetlPrime() HDCPCap=%d TransmitterVersion=%d TransmitterPreCompute=%d SelfPreCompute=%d [%s %d]\033[m\n",
			getHDCPCAP(),
			getRTKHDCPSTR()->transmitterVersion,
			getPreComputation(),
			getSelfPreComputation(),
			__FILE__,
			__LINE__ ));

	// HDCP2.2 && transmitter has pre-computation && receiver has pre-computation:
	if( getHDCPCAP()==CAP_HDCP2_2 &&
		((getPreComputation()&0x01)==H2_LC_INIT_PRECOMPUTATION) &&
		((getSelfPreComputation()&0x01)==H2_LC_INIT_PRECOMPUTATION))
	{
		// transmitter version suppose to be 0x2
		if(getRTKHDCPSTR()->transmitterVersion!=0x1)
		{
		    ALOGI("===========hdcp2_GetlPrime [%d][%s] ",__LINE__,__FILE__);
			Compute_Lprime(Kd_tmp, SessionSecrets.rRx, SessionSecrets.rn, pOut, 1);	// use (rn||rn)
		}
		else
		{
		    ALOGI("======hdcp2_GetlPrime [%d][%s] ",__LINE__,__FILE__);
			Compute_Lprime(Kd_tmp, SessionSecrets.rRx, SessionSecrets.rn, pOut, 0);
	    }
	}
	// none-HDCP2.2
	else
	{
	    ALOGI("======hdcp2_GetlPrime [%d][%s] ",__LINE__,__FILE__);
		Compute_Lprime(Kd_tmp, SessionSecrets.rRx, SessionSecrets.rn, pOut, 0);
	}

	// original implementation, out-dated since it is not readable.
#if 0
	if(getHDCPCAP()==CAP_HDCP2_2 && isPreCompute>0 && getRTKHDCPSTR()->transmitterVersion==0x2)
		Compute_Lprime(Kd_tmp, SessionSecrets.rRx, SessionSecrets.rn, pOut, 1);
	else
		Compute_Lprime(Kd_tmp, SessionSecrets.rRx, SessionSecrets.rn, pOut, 0);
#endif


    return H2_OK;
}

/**
 * The KSVs message contains:
 * Device Count - 1 byte
 * Depth ( 1 byte )
 * Max Devs Exceeded ( 1 byte )
 * Max Cascade Exceeded ( 1 byte )
 * if ( Max Devs Exceeded == 0 && MaxCascadeExceeded == 0 )
 * KSVs ( 5 bytes each )
 */
H2status hdcp2_SetKsvs( unsigned char *pMsg, unsigned int len )
{
		memset(&gKsvInfo, 0, sizeof(gKsvInfo));
		gKsvInfo.DeviceCount = *pMsg;
			
		if (gKsvInfo.DeviceCount==0)	
			goto end_proc;
		
		gKsvInfo.Depth = *(pMsg+1) + 1; // We add 1 to depth because HDMI depth does not include the HDCP 2.0 RX
		gKsvInfo.DevicesExceeded = *(pMsg+2);
		gKsvInfo.DepthExceeded = *(pMsg+3);
		
		if ((gKsvInfo.DeviceCount < MAX_DEVICECOUNT) && 
			(gKsvInfo.DevicesExceeded==0) && 
			(gKsvInfo.DepthExceeded==0)) 
		{
			memcpy( gKsvInfo.Ksvs, pMsg+4, 5*gKsvInfo.DeviceCount );
		}
		else	
			gKsvInfo.DevicesExceeded = 1;	 
		
	end_proc:
		
		return H2_OK;
}

/**
 * Compute and return vPrime.
 */
H2status hdcp2_GetvPrime( H2uint8* pOut, H2uint32 ulSize )
{  
   H2status rc = H2_ERROR;
   unsigned num_V[4]={0};
   H2uint8 Kd_tmp[KD_SIZE];
   static H2uint8 vPrimeBuff[5*MAX_DEVICECOUNT + 9];
   
   if (( NULL == pOut ) || ( ulSize < 32 ))
   {
      return H2_ERROR;
   }
	
   memcpy( vPrimeBuff, gKsvInfo.Ksvs, gKsvInfo.DeviceCount*5 );
   vPrimeBuff[gKsvInfo.DeviceCount*5] = gKsvInfo.Depth;
   vPrimeBuff[gKsvInfo.DeviceCount*5+1] = gKsvInfo.DeviceCount;
   vPrimeBuff[gKsvInfo.DeviceCount*5+2] = gKsvInfo.DevicesExceeded;
   vPrimeBuff[gKsvInfo.DeviceCount*5+3] = gKsvInfo.DepthExceeded;
	 if (IsCapHDCP2Plus())
	 {
	 	  MYDEBUG("hdcp kelly hdcp2_interface.c[%d] CAP_HDCP2_2 !\r\n",__LINE__);	
		  gKsvInfo.HDCP20RepeaterDownStream = 0;
		  gKsvInfo.HDCP1DeviceDownStream = 1;
		  vPrimeBuff[gKsvInfo.DeviceCount*5+4] = gKsvInfo.HDCP20RepeaterDownStream;
		  vPrimeBuff[gKsvInfo.DeviceCount*5+5] = gKsvInfo.HDCP1DeviceDownStream;
		  LTB4(num_V[0], num_V[1], num_V[2], num_V[3], g_REP_seq_num);
		  vPrimeBuff[gKsvInfo.DeviceCount*5+6] = num_V[1];
		  vPrimeBuff[gKsvInfo.DeviceCount*5+7] = num_V[2];
		  vPrimeBuff[gKsvInfo.DeviceCount*5+8] = num_V[3];

		  spu_GetKD(Kd_tmp);
		  rc = hmacsha256( Kd_tmp, KD_SIZE, vPrimeBuff, gKsvInfo.DeviceCount*5+9, pOut );
		  hdcp2_SaveStringInFile("/tmp/_kd", Kd_tmp, KD_SIZE );
		  hdcp2_SaveStringInFile("/tmp/_vPrimeBuff", vPrimeBuff, gKsvInfo.DeviceCount*5+9 );
	 }
	 else
	 {
	 	MYDEBUG("hdcp kelly hdcp2_interface.c[%d] CAP_HDCP2_2 !\r\n",__LINE__);	
		 
	  	spu_GetKD(Kd_tmp);
      	rc = hmacsha256( Kd_tmp, KD_SIZE, vPrimeBuff, gKsvInfo.DeviceCount*5+4, pOut );
	 }

	 memset(Kd_tmp, 0, sizeof(Kd_tmp));

   return rc;

}


H2status hdcp2_GetKsvInfo( H2uint8 *Devices, H2uint8 *Depth, H2uint8 *DevicesExceeded, H2uint8 *DepthExceeded, H2uint8 *pKSVs )
{
    unsigned char Buff[5*MAX_DEVICECOUNT + 16];        
    int ret = read_binary_file("/tmp/ksv_list.bin", Buff, sizeof(Buff)); 
   
    //sometimes the ksv_list.bin will combine the garbage zero after normal 6 bytes
    ret = (Buff[0]+1)*5+1;
    if (ret <6)
    {        
        gKsvInfo.DeviceCount = 0;
        gKsvInfo.Depth = 0;
        gKsvInfo.DevicesExceeded = 0;
        gKsvInfo.DepthExceeded = 0;
        memset(gKsvInfo.Ksvs, 0, sizeof(gKsvInfo.Ksvs));
    }
    else
    {
    	//TODO : fix me, sometimes the zero bytes is pretended as one device
		if (ret > 6)
		{
			if ((Buff[6] == 0x00) && (Buff[7] == 0x00) && (Buff[8] == 0x00) && (Buff[9] == 0x00) && (Buff[10] == 0x00))
				ret -= 5;
		}
        gKsvInfo.DeviceCount = ret / 5;
        gKsvInfo.Depth = Buff[0] + 1;
        gKsvInfo.DevicesExceeded = (gKsvInfo.DeviceCount>H2_MAX_DEVICECOUNT) ? 1 : 0 ; 
        gKsvInfo.DepthExceeded = (gKsvInfo.Depth > 4) ? 1 : 0;            
        memcpy(gKsvInfo.Ksvs, &Buff[1], gKsvInfo.DeviceCount * 5);
    }

   if ( Devices )
   {
      *Devices = gKsvInfo.DeviceCount;
   }

   if ( Depth )
   {
      *Depth = gKsvInfo.Depth ;
   }
   if ( DevicesExceeded )
   {
      *DevicesExceeded = gKsvInfo.DevicesExceeded;
   }
   if ( DepthExceeded )
   {

      *DepthExceeded = gKsvInfo.DepthExceeded;
   }
   if ( pKSVs && !gKsvInfo.DevicesExceeded && !gKsvInfo.DepthExceeded )
   {
      memcpy( pKSVs, gKsvInfo.Ksvs, gKsvInfo.DeviceCount * 5 );
   }

	 //save it
	 hdcp2_SaveStringInFile("/tmp/_gKsvInfo", pKSVs, gKsvInfo.DeviceCount * 5);

   return H2_OK;
}

/**
 * Set Ks^lc128. (clear the ks due to auth. fail)
 */
H2status hdcp2_SetKsXorLc128(const H2uint8 *AESKey)
{   
   spu_SetKsXorLc128(AESKey);
   return H2_OK;
}

static void hdcp22_SetProtocolDescriptorBitFlag(unsigned char *cert)
{
	// Receiver ID         :   5 bytes
	// Receiver public Key : 131 bytes
	unsigned char *pPDAddr = cert+5+131;
	// 0 or 1
	DPRINT(("[DHDCP] hdcp22_SetProtocolDescriptorBitFlag dump reserved fields: %.2x %.2x\n",
		(pPDAddr[0]&0xff),
		(pPDAddr[1]&0xff)));

	gHDCP22ProtocolDescriptorBit = ((*pPDAddr)>>4)&0x0f;

	DPRINT(("\033[0;31;31m[DHDCP] key protocol descriptor bit = %d\033[m\n",gHDCP22ProtocolDescriptorBit));
}

int hdcp22_GetProtocolDescriptorBit()
{
	return gHDCP22ProtocolDescriptorBit;
}
