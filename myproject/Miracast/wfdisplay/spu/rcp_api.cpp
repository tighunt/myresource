/***************************************************************************************************
  File        : rcp_api.cpp   
  Description : Low Level API for RCP 
  Author      : Kevin Wang 
****************************************************************************************************
    Update List :
----------------------------------------------------------------------------------------------------     
    20090605    | Create Phase    
***************************************************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <assert.h>
#include "rcp_api.h"

#include <FileDescService/IFileDescService.h>
#include <binder/IBinder.h>
#include <binder/IServiceManager.h>

#define LOG_TAG "HDCP2_RCP_API"
#include <utils/Log.h>

static int m_fd = 0;

#define RCP_PHYS_REG_BASE						(0x18014000)
#define RCP_REG_BASE							(0xB8014000)
#define RCP_REG_KEY_OFFSET					(0x1034)
#define RCP_REG_DATA_IN_OFFSET				(0x1044)
#define RCP_REG_DATA_OUT_OFFSET				(0x1054)
#ifdef  RUN_ON_SIRIUS
#define RCP_REG_IV_OFFSET						(0x1064)
#else
#define RCP_REG_IV_OFFSET						(0x10D0)
#endif
#define RCP_REG_SET_OFFSET					(0x1074)
#ifdef  RUN_ON_SIRIUS
#define RCP_REG_TP_KEY_INFO_OFFSET			(0x0058)
#define RCP_REG_TP_KEY_CTRL_OFFSET			(0x0060)
#else
#define RCP_REG_TP_KEY_INFO_OFFSET			(0x00D4)
#define RCP_REG_TP_KEY_CTRL_OFFSET			(0x00DC)
#endif

static volatile unsigned long  rcp_init_rdy   = 0;
static volatile unsigned long* p_reg_key      = NULL;
static volatile unsigned long* p_reg_data_in  = NULL;
static volatile unsigned long* p_reg_data_out = NULL;
static volatile unsigned long* p_reg_iv       = NULL;
static volatile unsigned long* p_reg_set      = NULL;
static volatile unsigned long* p_tp_key_info  = NULL;
static volatile unsigned long* p_tp_key_ctrl  = NULL;
static unsigned char  rtp_cw[4][64];
static int rcp_in_use = 0;
static int rcp_cw_valid = 0;
#define S_OK 0
#define S_FALSE -1
#define MAX_CW_ENTRY       128



#define RCP_INFO(fmt, args...)          ALOGI("[RCP] Info, " fmt, ## args)
#define RCP_WARNING(fmt, args...)       ALOGI("[RCP] Warning, " fmt, ## args)



/*====================================================================== 
 * Func : RCP_Init 
 *
 * Desc : Init RCP module 
 *
 * Parm : N/A
 *
 * Retn : S_OK /  S_FALSE 
 *======================================================================*/
int RCP_Init()
{
    unsigned char* reg_base = NULL;

    if (rcp_init_rdy)
        return S_OK;
    
    if (m_fd)
        return S_OK;

    RCP_INFO("Init with FileDescService\n");             
	sp<IBinder> binder = defaultServiceManager()->getService(String16("com.realtek.FileDescService"));
	sp<IFileDescService> service = IFileDescService::asInterface(binder);
	m_fd = service->getfd("/dev/rtkmem", O_RDWR|O_SYNC);
    
    if (m_fd<0)        
    //RCP_INFO("Init with open\n");             
    //if ((m_fd = open("/dev/rtkmem", O_RDWR|O_SYNC))<0)        
    {
        RCP_WARNING("Init RCP failed, open /dev/rtkmem failed\n");
        goto err_open_file_failed;        
    }
    
    reg_base = (unsigned char*) mmap(NULL, 8192, PROT_READ | PROT_WRITE, MAP_SHARED, m_fd, RCP_PHYS_REG_BASE); 
        
    if (reg_base ==MAP_FAILED) 
    {
        RCP_WARNING("Init RCP failed, map register failed\n");
        goto err_map_reg_failed;        
    }
    
    p_reg_key      	= (unsigned long*) (reg_base + RCP_REG_KEY_OFFSET);
    p_reg_data_in  	= (unsigned long*) (reg_base + RCP_REG_DATA_IN_OFFSET);
    p_reg_data_out 	= (unsigned long*) (reg_base + RCP_REG_DATA_OUT_OFFSET);
    p_reg_iv       	= (unsigned long*) (reg_base + RCP_REG_IV_OFFSET);
    p_reg_set      	= (unsigned long*) (reg_base + RCP_REG_SET_OFFSET);
    p_tp_key_info  	= (unsigned long*) (reg_base + RCP_REG_TP_KEY_INFO_OFFSET);
    p_tp_key_ctrl  	= (unsigned long*) (reg_base + RCP_REG_TP_KEY_CTRL_OFFSET);

    memset(rtp_cw, 0, sizeof(rtp_cw));
    rcp_in_use     = 0;
    rcp_cw_valid   = 0;
    rcp_init_rdy = 1;  
    RCP_INFO("Init RCP successfully\n");             
    
    return S_OK;             
    
//-----------------------
err_map_reg_failed:
    close(m_fd);
    m_fd = 0;
    
err_open_file_failed: 
                    
    return S_FALSE;
}



/*====================================================================== 
 * Func : UnInit 
 *
 * Desc : Uninit RCP module 
 *
 * Parm : N/A
 *
 * Retn : S_OK /  S_FALSE 
 *======================================================================*/
void RCP_UnInit()
{
    if (m_fd)
    {
        close(m_fd);
        m_fd          = NULL;
        rcp_init_rdy   = 0;
        p_reg_key      = NULL;
        p_reg_data_in  = NULL;
        p_reg_data_out = NULL;
        p_reg_iv       = NULL;
        p_reg_set      = NULL;        
        p_tp_key_info  = NULL;
        p_tp_key_ctrl  = NULL;               
        rcp_in_use     = 0;    
        rcp_cw_valid   = 0;
    }
    RCP_INFO("RCP_UNINIT\n");
}



#define B4TL(b1, b2, b3, b4)       ((b1<<24) + (b2<<16) + (b3<<8) + b4)

#define LTB4(b1, b2, b3, b4, L)    { b1 = ((L>>24)); b2 = ((L>>16)); \
                                     b3 = ((L>> 8)); b4 = ( L     ); }
                                   
#define B16TL4(L, B) do{ L[0] = B4TL(B[ 0], B[ 1], B[ 2], B[ 3]); \
                         L[1] = B4TL(B[ 4], B[ 5], B[ 6], B[ 7]); \
                         L[2] = B4TL(B[ 8], B[ 9], B[10], B[11]); \
                         L[3] = B4TL(B[12], B[13], B[14], B[15]); \
                       }while(0)         

#define L4TB16(B, L) do{ LTB4(B[ 0], B[ 1], B[ 2], B[ 3], L[0]); \
                         LTB4(B[ 4], B[ 5], B[ 6], B[ 7], L[1]); \
                         LTB4(B[ 8], B[ 9], B[10], B[11], L[2]); \
                         LTB4(B[12], B[13], B[14], B[15], L[3]); \
                       }while(0)

#define B8TL2(L, B)  do{ L[0] = B4TL(B[ 0], B[ 1], B[ 2], B[ 3]); \
                         L[1] = B4TL(B[ 4], B[ 5], B[ 6], B[ 7]); \
                       }while(0)         

#define L2TB8(B, L)  do{ LTB4(B[ 0], B[ 1], B[ 2], B[ 3], L[0]); \
                         LTB4(B[ 4], B[ 5], B[ 6], B[ 7], L[1]); \
                       }while(0)

/*====================================================================== 
 * Func : RCP_SET_MODE 
 *
 * Desc : 
 *
 * Parm : N/A
 *
 * Retn : NULL / data pointer 
 *======================================================================*/
unsigned char* RCP_SET_MODE(int Mode, unsigned char* pData, unsigned long DataLen, unsigned int Byte, unsigned char* pKey)
{
	switch(Mode)
	{
		case KEY_MODE:
			if (pData == NULL) 
			{
				p_reg_set[1] |= REG_SET1_KEY_MODE_OTP;
			}
			else if ((unsigned long)pData <= MAX_CW_ENTRY)     
			{
				p_reg_set[1] |= REG_SET1_KEY_MODE_CW;             
				p_reg_set[2] |= REG_SET2_KEY_ENTRY(((unsigned long)pData)-1);  // each cw takes 8 entry
			}
			else
			{
				B16TL4(p_reg_key, pData); 
			}
			break;
		case IV_MODE:
			if ((unsigned long)pData <= MAX_CW_ENTRY) 
			{
				p_reg_set[1] |= REG_SET1_IV_MODE_CW;
				p_reg_set[2] |= REG_SET2_IV_ENTRY(((unsigned long)pData)-1);  // each cw takes 8 entry
			}
			else 
			{
				p_reg_set[1] |= REG_SET1_IV_MODE_REG;
				B16TL4(p_reg_iv,  pData);    
			}
			break;
		case INPUT_MODE:
			if ((unsigned long)pData <= MAX_CW_ENTRY) 
			{
				if (((unsigned long)pData) + (DataLen>>3) > MAX_CW_ENTRY) {
					RCP_WARNING("Input from CW, but out of range\n");
					return NULL;
				}
				p_reg_set[1] |= REG_SET1_INPUT_MODE_CW;
			}
			break;
		case OUTPUT_MODE:
			if ((unsigned long)pData <= MAX_CW_ENTRY) 
			{
				if (((unsigned long)pData) + (DataLen>>3) > MAX_CW_ENTRY) {
					RCP_WARNING("Output to CW, but out of range\n");
					return NULL;
				}
				p_reg_set[1] |= REG_SET1_OUTPUT_MODE_CW;
			}
			break;
		case INPUT_ENTRY:
			if ((unsigned long)pData <= MAX_CW_ENTRY) 
			{         
				p_reg_set[2] = (p_reg_set[2] & 0xff00ffff) | REG_SET2_INPUT_ENTRY(((unsigned long)pData)-1);  // each cw takes 8 entry            
				p_reg_data_in[0] = 0; 
				p_reg_data_in[1] = 0; 
				p_reg_data_in[2] = 0; 
				p_reg_data_in[3] = 0; 
				pData += Byte/8;         // jump 2 cw entry...
			}
			else
			{
				if(Byte == 8) {
					B8TL2(p_reg_data_in, pData);
					if(pKey == NULL) {
						p_reg_data_in[2] = 0; 
						p_reg_data_in[3] = 0; 
					}
					else {
						p_reg_data_in[2] = B4TL(pKey[16], pKey[17], pKey[18], pKey[19]);
						p_reg_data_in[3] = B4TL(pKey[20], pKey[21], pKey[22], pKey[23]);
					}
				} 
				else {
					B16TL4(p_reg_data_in, pData);
				}
				pData += Byte;
			}
			break;
		case OUTPUT_ENTRY:
			if ((unsigned long)pData <= MAX_CW_ENTRY) 
			{
				p_reg_set[2] = (p_reg_set[2] & 0xffffff) | REG_SET2_OUTPUT_ENTRY((unsigned long)pData-1);  
				p_reg_data_out[0] = 0; 
				p_reg_data_out[1] = 0; 
				p_reg_data_out[2] = 0; 
				p_reg_data_out[3] = 0; 
				pData += Byte/8;
			}
			else
			{
				L4TB16(pData, p_reg_data_out);
				pData += Byte;
			}
			break;
		default:
			break;
	}
	
	return pData;
}

/*====================================================================== 
 * Func : RCP_AES_ECB_Cipher 
 *
 * Desc : 
 *
 * Parm : N/A
 *
 * Retn : S_OK /  S_FALSE 
 *======================================================================*/
int RCP_AES_ECB_Cipher(    
    unsigned char           EnDe,
    unsigned char           Key[16],     
    unsigned char*          pDataIn, 
    unsigned char*          pDataOut, 
    unsigned long           DataLen
    )
{                      
    unsigned long  out_len = 0;
	unsigned char  DataBuf[16];
    
    RCP_Init();
    
    while(rcp_in_use);
    
    rcp_in_use = 1;    

    p_reg_set[0] = (EnDe) ? RCP_AES_128_ECB_ENC : RCP_AES_128_ECB_DEC ;    
    p_reg_set[1] = 0;
    p_reg_set[2] = 0;
    
    RCP_SET_MODE(KEY_MODE, Key, DataLen, 0, NULL);	
    RCP_SET_MODE(INPUT_MODE, pDataIn, DataLen, 0, NULL);	
    RCP_SET_MODE(OUTPUT_MODE, pDataOut, DataLen, 0, NULL);	

    while(DataLen >= 16)
    {                
	    if ((unsigned long)pDataOut <= MAX_CW_ENTRY) 
	    {
		    p_reg_set[2] = (p_reg_set[2] & 0xffffff) | REG_SET2_OUTPUT_ENTRY((unsigned long)pDataOut-1);  
		    pDataOut += 2; 
		    if(!(pDataIn = RCP_SET_MODE(INPUT_ENTRY, pDataIn, DataLen, 16, NULL)))
			    return 0;
	    }
	    else
	    {
		    if(!(pDataIn = RCP_SET_MODE(INPUT_ENTRY, pDataIn, DataLen, 16, NULL)))
			    return 0;
		    L4TB16(pDataOut, p_reg_data_out);
		    pDataOut+= 16;
	    }
        
        DataLen -= 16;
        out_len += 16;
        
        p_reg_set[0] &= ~REG_SET_FIRST_128;
    }   
    

    rcp_in_use = 0;    
    
    return out_len;    
}



/*====================================================================== 
 * Func : RCP_AES_CBC_Cipher 
 *
 * Desc : 
 *
 * Parm : N/A
 *
 * Retn : S_OK /  S_FALSE 
 *======================================================================*/
int RCP_AES_CBC_Cipher(    
    unsigned char           EnDe,
    unsigned char           Key[16],     
    unsigned char           IV[16],     
    unsigned char*          pDataIn, 
    unsigned char*          pDataOut, 
    unsigned long           DataLen
    )
{                      
    unsigned long  out_len = 0;
    
    if (IV==NULL)
    {
        RCP_WARNING("Run RCP_AES_CBC_Cipher failed, IV should not be NULL \n");
        return 0;
    }        
    
    RCP_Init();

    while(rcp_in_use)
        usleep(1);
    
    rcp_in_use = 1;
    
    p_reg_set[0] = (EnDe) ? RCP_AES_128_CBC_ENC : RCP_AES_128_CBC_DEC ;    
    p_reg_set[1] = 0;
    p_reg_set[2] = 0;

    RCP_SET_MODE(KEY_MODE, Key, DataLen, 0, NULL);
    RCP_SET_MODE(IV_MODE, IV, DataLen, 0, NULL);	
    RCP_SET_MODE(INPUT_MODE, pDataIn, DataLen, 0, NULL);	
    RCP_SET_MODE(OUTPUT_MODE, pDataOut, DataLen, 0, NULL);	

    while(DataLen >= 16)
    {                
	    if ((unsigned long)pDataOut <= MAX_CW_ENTRY) 
	    {
		    p_reg_set[2] = (p_reg_set[2] & 0xffffff) | REG_SET2_OUTPUT_ENTRY((unsigned long)pDataOut-1);  
		    pDataOut += 2;
		    if(!(pDataIn = RCP_SET_MODE(INPUT_ENTRY, pDataIn, DataLen, 16, NULL)))
			    return 0;
	    }
	    else
	    {
		    if(!(pDataIn = RCP_SET_MODE(INPUT_ENTRY, pDataIn, DataLen, 16, NULL)))
			    return 0;
		    L4TB16(pDataOut, p_reg_data_out);
		    pDataOut+= 16;
	    }
        
        DataLen -= 16;
        out_len += 16;
          
        p_reg_set[0] &= ~REG_SET_FIRST_128;
    }   

    rcp_in_use = 0;
    
    return out_len;    
}



void xor_array(unsigned char* in1, unsigned char* in2, unsigned char* out, unsigned long len)
{
     int i;
     for (i=0; i<len; i++)
	out[i] = in1[i]^in2[i];
}


void aes_ctr_cnt_add(unsigned char cnt[16])
{
    unsigned char ov = 0;
    int i = 15;
    do {
        cnt[i]++;
        ov = (cnt[i]) ? 0 : 1;
    }while(i-->=0 && ov);
}



/*====================================================================== 
 * Func : RCP_AES_CTR_Cipher 
 *
 * Desc : 
 *
 * Parm : N/A
 *
 * Retn : S_OK /  S_FALSE 
 *======================================================================*/
int RCP_AES_CTR_Cipher(    
    unsigned char           Key[16],     
    unsigned char           IV[16],     
    unsigned char*          pDataIn, 
    unsigned char*          pDataOut, 
    unsigned long           DataLen
    )
{                      
    unsigned long  out_len = 0;    
    unsigned char  ecnt[16];
    unsigned char  word_align = (((unsigned long)pDataIn & 0x3) || ((unsigned long) pDataOut & 0x3)) ? 0 : 1; 
    unsigned long* pDI   = (unsigned long*) pDataIn;
    unsigned long* pDO   = (unsigned long*) pDataOut;    
    unsigned long* pEcnt = (unsigned long*) ecnt;    
    
    if ((unsigned long)IV <= MAX_CW_ENTRY)
    {
        RCP_WARNING("Run RCP_AES_CTR_Cipher failed, IV should not comes form OTP or CW\n");
        return 0;
    }    
    
    RCP_Init();
    
    while(rcp_in_use);
    
    rcp_in_use = 1;
        
    p_reg_set[0] = RCP_AES_128_ECB_ENC;        
    p_reg_set[1] = 0;
    p_reg_set[2] = 0;

    RCP_SET_MODE(KEY_MODE, Key, DataLen, 0, NULL);	

    while(DataLen >= 16)
    {
        B16TL4(p_reg_data_in, IV);        // Encrypt IV
        aes_ctr_cnt_add(IV);              // increase IV       
        L4TB16(ecnt, p_reg_data_out);
           
        if (word_align)
        {
            pDO[0] = pEcnt[0] ^ pDI[0];
            pDO[1] = pEcnt[1] ^ pDI[1];
            pDO[2] = pEcnt[2] ^ pDI[2];
            pDO[3] = pEcnt[3] ^ pDI[3];                      
            pDI+=4;
            pDO+=4;
        }
        else
        {
            xor_array(pDataIn, ecnt, pDataOut, 16);    
            pDataIn += 16;
            pDataOut+= 16;     
        }       
    
        DataLen -= 16;
        out_len += 16;        
        p_reg_set[0] &= ~REG_SET_FIRST_128;
    }   
    
    rcp_in_use = 0;
    
    return out_len;    
}


/*====================================================================== 
 * Func : RCP_WRITE_SRAM 
 *
 * Desc : Write 8 Bytes data to internal SRAM
 *
 * Parm : id   : SRAM Entry
 *        data : SRAM data
 *
 * Retn : S_OK /  S_FALSE 
 *======================================================================*/
void RCP_WRITE_SRAM(unsigned int id, unsigned char data[8])
{
    RCP_Init();
    p_tp_key_info[0] = B4TL(data[0], data[1], data[2], data[3]);
    p_tp_key_info[1] = B4TL(data[4], data[5], data[6], data[7]);
    *p_tp_key_ctrl   = (id | 0x80);  // write 8 bytes  
    p_tp_key_info[0] = 0;
    p_tp_key_info[1] = 0;
}



/*====================================================================== 
 * Func : RCP_READ_SRAM 
 *
 * Desc : Read 8 Bytes data from internal SRAM
 *
 * Parm : id   : SRAM Entry
 *        data : SRAM data output
 *
 * Retn : S_OK /  S_FALSE 
 *======================================================================*/
void RCP_READ_SRAM(unsigned int id, unsigned char data[8])
{
    unsigned char tmp[16]={0}; 
    
#if 0 //def SECURE_BOOT
    // in security modem the cw is un-readable, so we have to find an otherway to fetch it data...
    RCP_Init();    
    p_reg_set[0] = RCP_AES_128_ECB_ENC;    
    p_reg_set[1] = REG_SET1_KEY_MODE_CW | REG_SET1_INPUT_MODE_CW ;
    p_reg_set[2] = REG_SET2_KEY_ENTRY(id) | REG_SET2_INPUT_ENTRY(id);          
    
    B16TL4(p_reg_data_in, tmp); 
    L4TB16(tmp, p_reg_data_out);
    
    p_reg_set[0] = RCP_AES_128_ECB_DEC;    
    p_reg_set[1] = REG_SET1_KEY_MODE_CW;
    p_reg_set[2] = REG_SET2_KEY_ENTRY(id);   
    B16TL4(p_reg_data_in, tmp); 
    L4TB16(tmp, p_reg_data_out);   
    memcpy(data, tmp, 8);                
#else
    RCP_Init();
    *p_tp_key_ctrl = id;  // read 8 bytes    
    LTB4(data[0], data[1], data[2], data[3], p_tp_key_info[0]);
    LTB4(data[4], data[5], data[6], data[7], p_tp_key_info[1]);
    p_tp_key_info[0] = 0;
    p_tp_key_info[1] = 0;
#endif    
}



/*====================================================================== 
 * Func : RCP_SET_CW 
 *
 * Desc : Set Control Word to CW Area
 *
 * Parm : id   : CW Entry offset
 *        pCW  : CW data
 *        len  : size of CW data
 *
 * Retn : S_OK /  S_FALSE 
 *======================================================================*/
int RCP_SET_CW(unsigned int id, unsigned char* pCW, unsigned int len)
{    
    assert(id + ((len + 7)>>3)<MAX_CW_ENTRY);    
        
    while (len)
    {
        if (len<8) 
        {
            unsigned char sram_data[8];
            memcpy(sram_data, pCW, len);
            RCP_WRITE_SRAM(id++, sram_data);
            break;
        }
       
        RCP_WRITE_SRAM(id++, pCW);                
        pCW += 8;
        len -= 8;
    }    
    
    return 0;
}


/*====================================================================== 
 * Func : RCP_GET_CW 
 *
 * Desc : Get Control Word from CW Area
 *
 * Parm : id   : CW Entry offset
 *        pCW  : CW data output
 *        len  : size of CW data to read
 *
 * Retn : S_OK /  S_FALSE 
 *======================================================================*/
int RCP_GET_CW(unsigned int id, unsigned char* pCW, unsigned int len)
{    
    assert(id + ((len + 7)>>3)<MAX_CW_ENTRY);  
        
    while (len)
    {
        if (len<8) 
        {
            unsigned char sram_data[8];            
            RCP_READ_SRAM(id++, sram_data);
            memcpy(pCW, sram_data, len);
            break;
        }

        RCP_READ_SRAM(id++, pCW);           
        pCW += 8;
        len -= 8;
    }    
    
    return 0;     
}



/*====================================================================== 
 * Func : RCP_CW_XOR 
 *
 * Desc : XOR two CWs
 *
 * Parm : CwIn1  : Source CW1
 *        CwIn2  : Source CW2
 *        CwOut  : Destination CW = CW1 ^ CW2
 *
 * Retn : S_OK /  S_FALSE 
 *======================================================================*/
void RCP_CW_XOR(int CwIn1, int CwIn2, int CwOut) 
{
    unsigned long tmp;
    
    // reg_iv = ECW1    
    p_reg_set[0] = RCP_AES_128_ECB_ENC;    
    p_reg_set[1] = REG_SET1_KEY_MODE_OTP | REG_SET1_INPUT_MODE_CW;
    p_reg_set[2] = REG_SET2_INPUT_ENTRY(CwIn1);
    p_reg_data_in[3] = 0; // triger computation  
    p_reg_iv[0] = p_reg_data_out[0];        // wait complete & store ECW1 in reg iv
    p_reg_iv[1] = p_reg_data_out[1];
    p_reg_iv[2] = p_reg_data_out[2];
    p_reg_iv[3] = p_reg_data_out[3];   
    
    // CWO = CW2 
    p_reg_set[0] = RCP_AES_128_ECB_ENC;    
    p_reg_set[1] = REG_SET1_KEY_MODE_OTP | REG_SET1_INPUT_MODE_CW;
    p_reg_set[2] = REG_SET2_INPUT_ENTRY(CwIn2); 
    p_reg_data_in[3] = 0;                   // triger : CWO=ECW2      
    tmp = p_reg_data_out[0];                // wait complete                          
    p_reg_set[0] = RCP_AES_128_ECB_DEC;    
    p_reg_set[1] = REG_SET1_KEY_MODE_OTP | REG_SET1_OUTPUT_MODE_CW;
    p_reg_set[2] = REG_SET2_OUTPUT_ENTRY(CwOut);                 
    p_reg_data_in[0] = p_reg_data_out[0];
    p_reg_data_in[1] = p_reg_data_out[1];
    p_reg_data_in[2] = p_reg_data_out[2];
    p_reg_data_in[3] = p_reg_data_out[3];   // triger : CWO = CW2       
    tmp = p_reg_data_out[0];                // wait complete                          
        
    // CWO = AES_CBC-1(k,IV=CWO=CW1, IN=ECW1)              
    p_reg_set[0] = RCP_AES_128_CBC_DEC;        
    p_reg_set[1] = REG_SET1_KEY_MODE_OTP | REG_SET1_IV_MODE_CW | REG_SET1_OUTPUT_MODE_CW;
    p_reg_set[2] = REG_SET2_IV_ENTRY(CwOut) | REG_SET2_OUTPUT_ENTRY(CwOut);     
    p_reg_data_in[0] = p_reg_iv[0];
    p_reg_data_in[1] = p_reg_iv[1];
    p_reg_data_in[2] = p_reg_iv[2];
    p_reg_data_in[3] = p_reg_iv[3];         // triger : CWO = D(ECW1) ^ CWO = CW1 ^ CW2    
    tmp = p_reg_data_out[0];                // wait complete
}

/*
void RCP_CW_XOR(int CwIn1, int CwIn2, int CwOut) 
{
    unsigned char CW1[16];
    unsigned char CW2[16];
    
    RCP_GET_CW(CwIn1, CW1, sizeof(CW1));
    RCP_GET_CW(CwIn2, CW2, sizeof(CW2));
    
    xor_array(CW1, CW2, CW2, 16);
    
    RCP_SET_CW(CwOut, CW2, sizeof(CW2));
}*/




//////////////////////////// for HDCP2 support /////////////////////////////

// Bruce add comments:
// KhCw: KH_ENTRY
// KmCw: KM_ENTRY (m?)
// Rtx: Rtx
// pEkhKm: output
void RCP_HDCP2_EkhKm(int KhCw, int KmCw, unsigned char* Rtx,unsigned char* Rrx, unsigned char* pEkhKm, int useRrx)
{
    unsigned long tmp;
    memset(pEkhKm, 0, 16);
    memcpy(pEkhKm, Rtx, 8);
    // Bruce add for HDCP 2.2
	if(useRrx>0)
	{
		RCP_INFO("RCP_HDCP2_EkhKm[%d], useRrx >0 2.2\n",__LINE__);
		memcpy(pEkhKm+8,Rrx,8);
    }    
    // reg_iv = EKh(m)  
    p_reg_set[0] = RCP_AES_128_ECB_ENC;    
    p_reg_set[1] = REG_SET1_KEY_MODE_CW;
    p_reg_set[2] = REG_SET2_KEY_ENTRY(KhCw);          
    
    B16TL4(p_reg_data_in, pEkhKm);          // out = EKh(m)
    p_reg_iv[0]  = p_reg_data_out[0];
    p_reg_iv[1]  = p_reg_data_out[1];
    p_reg_iv[2]  = p_reg_data_out[2];
    p_reg_iv[3]  = p_reg_data_out[3];
    
    // out = AES_CBC(kh,IV=reg_iv=EKh(m),IN=Km) = E(Km ^ EKh(m))
    p_reg_set[0] = RCP_AES_128_CBC_ENC;
    p_reg_set[1] = REG_SET1_KEY_MODE_CW | REG_SET1_INPUT_MODE_CW;
    p_reg_set[2] = REG_SET2_KEY_ENTRY(KhCw) | REG_SET2_INPUT_ENTRY(KmCw);
    p_reg_data_in[3] = 0;
    tmp = p_reg_data_out[0];                // wait complete       
    
    //D(E(Km^EKh(m))=Km^EKh(m)    
    p_reg_set[0] = RCP_AES_128_ECB_DEC;    
    p_reg_set[1] = REG_SET1_KEY_MODE_CW;
    p_reg_set[2] = REG_SET2_KEY_ENTRY(KhCw);      
    p_reg_data_in[0] = p_reg_data_out[0];   // IV = EKh(m)
    p_reg_data_in[1] = p_reg_data_out[1];
    p_reg_data_in[2] = p_reg_data_out[2];
    p_reg_data_in[3] = p_reg_data_out[3];   // out = D(E(Km^EKh(m))=Km^EKh(m)    
    L4TB16(pEkhKm, p_reg_data_out);         // load out to pEKhKm
}


void RCP_HDCP2_GenDKey(int KmCw, unsigned char* Rtx,unsigned char* Rrx, unsigned char* Rn, unsigned char ctr , int DKeyCW,int modeHDCP22)
{


	RCP_INFO("RCP_HDCP2_GenDKey[%d], modeHDCP22:[%d]\n",__LINE__,modeHDCP22);
	//RCP_INFO("RCP_HDCP2_GenDKey[%d], KmCw:[%d] *Rtx:[%d] *Rrx:[%d] *Rn:[%d] ctr:[%d] DKeyCW:[%d]\n",__LINE__,KmCw,*Rtx,*Rrx,Rn,ctr,DKeyCW);
	
    unsigned char tmp[16];
    unsigned char ctr_rrx_out[8];
    memset(ctr_rrx_out,0x0,8);	// used in HDCP 2.2 mode
    
    // Generate Km^Rn in DKeyCW
    // Rn is XORed with the least-significant 64-bits of km
    // LSB of Big endian data -> place it to high addr
    memset(tmp, 0, 16);        
    memcpy(&tmp[8], Rn, 8);  
    RCP_SET_CW(DKeyCW, tmp, 16);  // store Rn
    RCP_CW_XOR(KmCw, DKeyCW, DKeyCW); 	// Km^Rn, the result is stored in KmCW
    
    // Calculate IV (stored in tmp)
	// none HDCP 2.2 tmp = rtx || ctr
	// HDCP 2.2      tmp = rtx || (rrx XOR ctr)
    memset(tmp, 0, 16);       	// clear buffer
    memcpy(tmp, Rtx, 8);  		// set Rtx, high byte -> low addr
	
    if(modeHDCP22==0)
	{
    	tmp[15] = ctr;	// place data in big endian order
    	//RCP_INFO("RCP_HDCP2_GenDKey[%d] ,tmp[0~3]:[%d %d %d %d %d ]\n",__LINE__,tmp[0],tmp[1],tmp[2],tmp[3]);		
		//RCP_INFO("RCP_HDCP2_GenDKey[%d] ,tmp[4~7]:[%d %d %d %d %d ]\n",__LINE__,tmp[4],tmp[5],tmp[6],tmp[7]);
		//RCP_INFO("RCP_HDCP2_GenDKey[%d] ,tmp[4~7]:[%d %d %d %d %d ]\n",__LINE__,tmp[8],tmp[9],tmp[10],tmp[11]);		
		//RCP_INFO("RCP_HDCP2_GenDKey[%d] ,tmp[4~7]:[%d %d %d %d %d ]\n",__LINE__,tmp[12],tmp[13],tmp[14],tmp[15]);
    }
	else
	{
		unsigned char ctr_array[8];
		memset(ctr_array,0,8);
		ctr_array[7]=ctr;
	
		xor_array(Rrx,ctr_array,ctr_rrx_out,8);
		memcpy(&tmp[8],ctr_rrx_out,8);

		//RCP_INFO("RCP_HDCP2_GenDKey[%d] 2.2,tmp[0~3]:[%d %d %d %d %d ]\n",__LINE__,tmp[0],tmp[1],tmp[2],tmp[3]);		
		//RCP_INFO("RCP_HDCP2_GenDKey[%d] 2.2,tmp[4~7]:[%d %d %d %d %d ]\n",__LINE__,tmp[4],tmp[5],tmp[6],tmp[7]);
		//RCP_INFO("RCP_HDCP2_GenDKey[%d] 2.2,tmp[4~7]:[%d %d %d %d %d ]\n",__LINE__,tmp[8],tmp[9],tmp[10],tmp[11]);		
		//RCP_INFO("RCP_HDCP2_GenDKey[%d] 2.2,tmp[4~7]:[%d %d %d %d %d ]\n",__LINE__,tmp[12],tmp[13],tmp[14],tmp[15]);
	}

    
    // Generate Kd    
    // follow original implementation
    p_reg_set[0] = RCP_AES_128_ECB_ENC;    
    p_reg_set[1] = REG_SET1_KEY_MODE_CW | REG_SET1_OUTPUT_MODE_CW;
    p_reg_set[2] = REG_SET2_KEY_ENTRY(DKeyCW) | REG_SET2_OUTPUT_ENTRY(DKeyCW);           
    B16TL4(p_reg_data_in, tmp);     // input data    
    L4TB16(tmp, p_reg_data_out);
}



void RCP_HDCP2_GenKd(int KmCw, unsigned char* Rtx,unsigned char* Rrx, unsigned char* Rn, int KdCW, int modeHDCP22)
{
	RCP_INFO("RCP_HDCP2_GenKd modeHDCP22:[%d]\n",modeHDCP22);  

	// control word 8 bytes per unit
	// 2 unit -> shift 16 bytes -> 128bits.

    RCP_HDCP2_GenDKey(KmCw, Rtx,Rrx, Rn, 0, KdCW,modeHDCP22);
    RCP_HDCP2_GenDKey(KmCw, Rtx,Rrx, Rn, 1, KdCW + 2,modeHDCP22);
}


void RCP_HDCP2_GenKs(int dKey2CW, unsigned char* EdKeyKs, unsigned char* rRx, int KsCW) 
{
    // Ks = EdKey2 ^ dKey2CW ^ rrx 
    unsigned char tmp[16]={0};
    memcpy(tmp, EdKeyKs, 16);
    xor_array(&tmp[8], rRx, &tmp[8], 8);
    RCP_SET_CW(KsCW, tmp, 16); 
    RCP_CW_XOR(dKey2CW, KsCW, KsCW);  
}


void RCP_HDCP2_GenKsXorLc128(int Lc128Cw, int KsCW, int KsXorLc128CW) 
{   
    RCP_CW_XOR(Lc128Cw, KsCW, KsXorLc128CW);
}
