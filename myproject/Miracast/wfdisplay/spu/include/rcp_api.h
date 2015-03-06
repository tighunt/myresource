#ifndef __RCP_API_H__
#define __RCP_API_H__


#ifdef __cplusplus
extern "C" {
#endif

#ifdef  RUN_ON_SIRIUS
#define BIT(x)               (1 <<x)
#define REG_SET_WR_EN3      			BIT(17)
#define REG_SET_MODE(x)     			((x & 0xF) << 13)
#define REG_SET_MODE_AES    REG_SET_MODE(0)
#define REG_SET_MODE_TDES   REG_SET_MODE(1)
#define REG_SET_MODE_DES    REG_SET_MODE(2)
#define REG_SET_MODE_TDES_D_LSB   	REG_SET_MODE(5)
#define REG_SET_MODE_DES_D_LSB    	REG_SET_MODE(6)
#define REG_SET_MODE_TDES_D_MSB   	REG_SET_MODE(13)
#define REG_SET_MODE_DES_D_MSB    	REG_SET_MODE(14)

#define REG_SET_WR_EN2      			BIT(5)   
#define REG_SET_CBC         BIT(4)
#define REG_SET_ECB         0x0

#define REG_SET_WR_EN1      BIT(3)
#define REG_SET_FIRST_128   BIT(2)  

#define REG_SET_WR_EN0      BIT(1)
#define REG_SET_ENC         BIT(0)
#define REG_SET_DEC         0x0

#define RCP_DES_ECB_ENC      	(REG_SET_WR_EN3 | REG_SET_MODE_DES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_DES_ECB_DEC      	(REG_SET_WR_EN3 | REG_SET_MODE_DES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN0 | REG_SET_DEC)
#define RCP_DES_CBC_ENC      	(REG_SET_WR_EN3 | REG_SET_MODE_DES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_DES_CBC_DEC      	(REG_SET_WR_EN3 | REG_SET_MODE_DES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN0 | REG_SET_DEC)
#define RCP_TDES_ECB_ENC      	(REG_SET_WR_EN3 | REG_SET_MODE_TDES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_TDES_ECB_DEC      	(REG_SET_WR_EN3 | REG_SET_MODE_TDES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN0 | REG_SET_DEC)
#define RCP_TDES_CBC_ENC      	(REG_SET_WR_EN3 | REG_SET_MODE_TDES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_TDES_CBC_DEC      	(REG_SET_WR_EN3 | REG_SET_MODE_TDES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN0 | REG_SET_DEC)

#define RCP_AES_128_ECB_ENC      	(REG_SET_WR_EN3 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_AES_128_ECB_DEC      	(REG_SET_WR_EN3 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_DEC)
#define RCP_AES_128_CBC_ENC      	(REG_SET_WR_EN3 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_AES_128_CBC_DEC      	(REG_SET_WR_EN3 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_DEC)

#define REG_SET1_DES_KEY_ORDER		BIT(9)


#elif defined RUN_ON_MAGELLAN

#define BIT(x)               (1 <<x)
#define REG_SET_WR_EN5      			BIT(17)
#define REG_SET_MODE(x)     			((x & 0xF) << 13)
#define REG_SET_MODE_AES    REG_SET_MODE(0)
#define REG_SET_MODE_TDES   REG_SET_MODE(1)
#define REG_SET_MODE_DES    REG_SET_MODE(2)
#define REG_SET_MODE_SHA256    		REG_SET_MODE(3)
#define REG_SET_MODE_TDES_D_LSB   	REG_SET_MODE(5)
#define REG_SET_MODE_DES_D_LSB    	REG_SET_MODE(6)
#define REG_SET_MODE_TDES_D_MSB   	REG_SET_MODE(13)
#define REG_SET_MODE_DES_D_MSB    	REG_SET_MODE(14)
#define REG_SET_MODE_CW  				REG_SET_MODE(15)
#define REG_SET_MODE_MASK  			REG_SET_MODE(15)

#define REG_SET_WR_EN4      			BIT(11)
#define REG_SET_ENDIAN_SWAP         	BIT(10)

#define REG_SET_WR_EN3      			BIT(9)
#define REG_SET_CW_SEL(x)     		((x & 0x3) << 7)
#define REG_SET_CW_AND      			BIT(8)   
#define REG_SET_CW_OR      			BIT(7)   
#define REG_SET_CW_XOR      			0x0  

#define REG_SET_WR_EN2      			BIT(6)   
#define REG_SET_CTR         			BIT(5)
#define REG_SET_CBC         BIT(4)
#define REG_SET_ECB         0x0

#define REG_SET_WR_EN1      BIT(3)
#define REG_SET_FIRST_128   BIT(2)  

#define REG_SET_WR_EN0      BIT(1)
#define REG_SET_ENC         BIT(0)
#define REG_SET_DEC         0x0

#define RCP_DES_ECB_ENC      	(REG_SET_WR_EN5 | REG_SET_MODE_DES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_DES_ECB_DEC      	(REG_SET_WR_EN5 | REG_SET_MODE_DES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN0 | REG_SET_DEC)
#define RCP_DES_CBC_ENC      	(REG_SET_WR_EN5 | REG_SET_MODE_DES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_DES_CBC_DEC      	(REG_SET_WR_EN5 | REG_SET_MODE_DES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN0 | REG_SET_DEC)
#define RCP_TDES_ECB_ENC      	(REG_SET_WR_EN5 | REG_SET_MODE_TDES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_TDES_ECB_DEC      	(REG_SET_WR_EN5 | REG_SET_MODE_TDES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN0 | REG_SET_DEC)
#define RCP_TDES_CBC_ENC      	(REG_SET_WR_EN5 | REG_SET_MODE_TDES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_TDES_CBC_DEC      	(REG_SET_WR_EN5 | REG_SET_MODE_TDES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN0 | REG_SET_DEC)

#define RCP_AES_128_ECB_ENC      	(REG_SET_WR_EN5 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_AES_128_ECB_DEC      	(REG_SET_WR_EN5 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_DEC)
#define RCP_AES_128_CBC_ENC      	(REG_SET_WR_EN5 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_AES_128_CBC_DEC      	(REG_SET_WR_EN5 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_DEC)
#define RCP_AES_128_CTR_ENC      	(REG_SET_WR_EN5 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_CTR | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_AES_128_CTR_DEC      	(REG_SET_WR_EN5 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_CTR | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_DEC)

#define REG_SET1_DES_KEY_ORDER		BIT(9)

#else
#define BIT(x)               (1 <<x)
#define REG_SET_WR_EN3      BIT(16)
#define REG_SET_MODE(x)     ((x & 0x7)<<13)
#define REG_SET_MODE_AES    REG_SET_MODE(0)
#define REG_SET_MODE_TDES   REG_SET_MODE(1)
#define REG_SET_MODE_DES    REG_SET_MODE(2)
#define REG_SET_MODE_MASK   REG_SET_MODE(7)
#define REG_SET_WR_EN2      BIT(5)    
#define REG_SET_CBC         BIT(4)
#define REG_SET_ECB         0x0
#define REG_SET_WR_EN1      BIT(3)
#define REG_SET_FIRST_128   BIT(2)  
#define REG_SET_WR_EN0      BIT(1)
#define REG_SET_ENC         BIT(0)
#define REG_SET_DEC         0x0

#define RCP_AES_128_ECB_ENC      (REG_SET_WR_EN3 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_AES_128_ECB_DEC      (REG_SET_WR_EN3 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_ECB | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_DEC)
#define RCP_AES_128_CBC_ENC      (REG_SET_WR_EN3 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_ENC)
#define RCP_AES_128_CBC_DEC      (REG_SET_WR_EN3 | REG_SET_MODE_AES | REG_SET_WR_EN2 | REG_SET_CBC | REG_SET_WR_EN1 | REG_SET_FIRST_128 | REG_SET_WR_EN0 | REG_SET_DEC)
#endif

#define REG_SET1_KEY_MODE(x)         ((x & 0x3)<<6)
#define REG_SET1_KEY_MODE_REGISTER   REG_SET1_KEY_MODE(0)
#define REG_SET1_KEY_MODE_CW         REG_SET1_KEY_MODE(1)
#define REG_SET1_KEY_MODE_OTP        REG_SET1_KEY_MODE(2)
#define REG_SET1_KEY_MODE_MASK       REG_SET1_KEY_MODE(3)

#define REG_SET1_IV_MODE(x)          ((x & 0x3)<<4)
#define REG_SET1_IV_MODE_REG         REG_SET1_IV_MODE(0)
#define REG_SET1_IV_MODE_CW          REG_SET1_IV_MODE(1)
                                         
#define REG_SET1_INPUT_MODE(x)       ((x & 0x3)<<2)
#define REG_SET1_INPUT_MODE_REG      REG_SET1_INPUT_MODE(0)
#define REG_SET1_INPUT_MODE_CW       REG_SET1_INPUT_MODE(1)

#define REG_SET1_OUTPUT_MODE(x)      ((x & 0x3))
#define REG_SET1_OUTPUT_MODE_REG     REG_SET1_OUTPUT_MODE(0)
#define REG_SET1_OUTPUT_MODE_CW      REG_SET1_OUTPUT_MODE(1)

#define REG_SET2_OUTPUT_ENTRY(x)     ((x & 0x7F)<<24)
#define REG_SET2_INPUT_ENTRY(x)      ((x & 0x7F)<<16)        
#define REG_SET2_KEY_ENTRY(x)        ((x & 0x7F)<<8)
#define REG_SET2_IV_ENTRY(x)         ((x & 0x7F))

#define KEY_OTP                      NULL
#define KEY_CW(x)                    ((unsigned char*) (1 + x))

#define IV_OTP                       NULL
#define IV_CW(x)                     ((unsigned char*) (1 + x))

#define INPUT_CW(x)                  ((unsigned char*) (1 + x))
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

                                         
                                         
int  RCP_Init();        
void RCP_UnInit(); 

enum {
	KEY_MODE,
	IV_MODE,
	INPUT_MODE,
	OUTPUT_MODE,
	INPUT_ENTRY,
	OUTPUT_ENTRY,
	};

int RCP_AES_ECB_Cipher(    
    unsigned char           EnDe,
    unsigned char           Key[16],     
    unsigned char*          pDataIn, 
    unsigned char*          pDataOut, 
    unsigned long           DataLen
    );

#define RCP_AES_ECB_Encryption(Key, pDataIn, pDataOut, DataLen)       RCP_AES_ECB_Cipher(1, Key, pDataIn, pDataOut, DataLen)
#define RCP_AES_ECB_Decryption(Key, pDataIn, pDataOut, DataLen)       RCP_AES_ECB_Cipher(0, Key, pDataIn, pDataOut, DataLen)

int RCP_AES_CBC_Cipher(    
    unsigned char           EnDe,
    unsigned char           Key[16],     
    unsigned char           IV[16],     
    unsigned char*          pDataIn, 
    unsigned char*          pDataOut, 
    unsigned long           DataLen
    );
    
#define RCP_AES_CBC_Encryption(Key, IV, pDataIn, pDataOut, DataLen)       RCP_AES_CBC_Cipher(1, Key, IV, pDataIn, pDataOut, DataLen)
#define RCP_AES_CBC_Decryption(Key, IV, pDataIn, pDataOut, DataLen)       RCP_AES_CBC_Cipher(0, Key, IV, pDataIn, pDataOut, DataLen)

int RCP_AES_CTR_Cipher(
    unsigned char           Key[16],
    unsigned char           IV[16],
    unsigned char*          pDataIn,
    unsigned char*          pDataOut,
    unsigned long           DataLen
    );

#define RCP_AES_CTR_Encryption(Key, IV, pDataIn, pDataOut, DataLen)       RCP_AES_CTR_Cipher(Key, IV, pDataIn, pDataOut, DataLen)
#define RCP_AES_CTR_Decryption(Key, IV, pDataIn, pDataOut, DataLen)       RCP_AES_CTR_Cipher(Key, IV, pDataIn, pDataOut, DataLen)
        
int RCP_SET_CW(unsigned int id, unsigned char* pCW, unsigned int len);
int RCP_GET_CW(unsigned int id, unsigned char* pCW, unsigned int len);

void RCP_CW_XOR(int CwIn1, int CwIn2, int CwOut);

////////////////////////// Functions for HDCP2 //////////////////////////////
void RCP_HDCP2_EkhKm(int KhCw, int KmCw, unsigned char* Rtx, unsigned char* Rrx, unsigned char* pEkhKm, int useRrx);
void RCP_HDCP2_GenDKey(int KmCw, unsigned char* Rtx,unsigned char* Rrx, unsigned char* Rn, unsigned char ctr, int DKeyCW,int modeHDCP22);
void RCP_HDCP2_GenKd(int KmCw, unsigned char* Rtx,unsigned char* Rrx, unsigned char* Rn, int KdCW,int modeHDCP22);
void RCP_HDCP2_GenKs(int dKey2CW, unsigned char* EdKeyKs, unsigned char* Rrx, int KsCW);
void RCP_HDCP2_GenKsXorLc128(int Lc128CW, int KsCW, int KsXorLc128CW);
    
#ifdef __cplusplus
}
#endif

#endif  // __RCP_API_H__
