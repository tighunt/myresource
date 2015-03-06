/**
 * Includes
 */
#include "hdcp2_hal.h"
#include "hdcp2_messages.h"
#include "hdcp2_interface.h"

#define LOG_TAG "HDCP2_CRYPTO"
#include <utils/Log.h>

/**
 * This file is shared between the XTASK and the userspace application. Both
 * must be compiled with the same version.
 */

/**
 * Bigdigits library. See included copyright notice.
 */
#include "bigdigits.h"

/**
 * VTILT crypto API. Provides prototypes for most functions in this file.
 */
#include "crypto.h"

/**
 * Definition of the HMAC-SHA256 function.
 */
#include "hmac.h"

/**
 * Standard include files
 */
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>

/**
 * This one gets htonl() and ntohl(), etc.
 */
#include <arpa/inet.h>

/**
 * Data structure for RSA decrypt.
 *
 * Contains P, Q, dP, dQ, qInv from the public / private keypairs.
 */

typedef struct {
   DIGIT_T *p;
   DIGIT_T *q;
   DIGIT_T *dP;
   DIGIT_T *dQ;
   DIGIT_T *Qinv;
} crypto_RSAKey;


/**
 * @warn Make sure LOCALDBG is set to DISABLE for production
 */

// to enable or disable the debug messages of this source file, put 1 or 0 below
#ifdef DEBUG_CRYPTO
#define LOCALDBG ENABLE

/** @todo Detect prod / release or real/facsimile keys and set debug automatically */
#else
#define LOCALDBG DISABLE
#endif

//#define TRACE( ) H2DBGLOG((LOCALDBG, "File: %s, Line %d\n", __FILE__, __LINE__))

#if 0
#define INFO ENABLE
#else
#define INFO DISABLE
#endif
#define WARN ENABLE

/**
 * size of the key components for RSA decryption.
 * This should equal key size / 32.
 */
#define MOD_SIZE (1024/32)

/** Prototypes not included in crypto.h */
static int crypto_rsaPrivateDecryptOaepSha256( uint8_t const * pIn, uint8_t* pOut, const crypto_RSAKey *key );

/**
 * Swap byte order of 'length' bytes. This function is intended
 * to convert a SHA-256 digest into the correct byte order expected
 * as input to other functions. Length should be even.
 *
 * @param[inout] ptr Data to be byte-swapped
 * @param[inout] length Size of data to be modified.
 * @return H2_OK
 */
H2status crypto_swapBytes( H2uint8 *ptr, int length )
{
   int ii;

   H2status status = H2_ERROR;

   do
   {
      if( length <= 2 )
      {
         status = H2_ERROR;
         break;
      }

      for(ii=0;ii<length/2;ii++)
      {
         char c = ptr[ii];
         ptr[ii] = ptr[length-ii-1];
         ptr[length-ii-1] = c;
      }

      status = H2_OK;

   }while(0);

   return status;
}


/**
 *
 * Mask Generation Function for RSA-OAEP-SHA256.
 *
 * @see PKCS#1v2.1 for an explanation of the MGF.
 * @param[inout] mask Pointer to mask data
 * @param[in] length Size of mask data
 * @param[in] seed Seed for SHA256
 * @param[in] seedLen Size of seed data.
 * @scope static.
 * @return H2_OK.
 */
static H2status MGF1_SHA256( H2uint8 *mask, H2uint32 length, const H2uint8 *seed, int seedLen )
{
   H2uint32 ii=0, outBytes = 0;
   H2uint8       cnt[4];
   H2Sha256Ctx_t ctx;

   static H2uint8 digest[SHA256_DIGEST_SIZE];

   crypto_sha256Init( &ctx );

   for ( ii=0; outBytes < length; ii++)
   {
      /** Copy current byte offset to the counter */
      /** Since the copy is done this way, byte order is irrelevant */
      cnt[0] = (H2uint8) (( ii >> 24 ) & 0xFF );
      cnt[1] = (H2uint8) (( ii >> 16 ) & 0xFF );
      cnt[2] = (H2uint8) (( ii >> 8 ) & 0xFF );
      cnt[3] = (H2uint8) (( ii ) & 0xFF );
      /** Clear the context */
      memset( &ctx, 0, sizeof( ctx ));
      crypto_sha256Init( &ctx );
      /** Add the seed */
      crypto_sha256Update( &ctx, seed, seedLen );
      /** Add the counter */
      crypto_sha256Update( &ctx, cnt, 4 );
      if ( outBytes + SHA256_DIGEST_SIZE <= length )
      {
         /** If at least SHA256_DIGEST_SIZE output bytes are needed, copy to output and continue */
         crypto_sha256Final( &ctx, digest );

         memcpy( mask+outBytes, digest, SHA256_DIGEST_SIZE );
         outBytes += SHA256_DIGEST_SIZE;
      }
      else
      {
         /** Otherwise, only copy as many bytes as are still needed and terminate */
         crypto_sha256Final( &ctx, digest );

         memcpy( mask+outBytes, digest, length-outBytes );
         outBytes = length;
      }
   }
   return H2_OK;
}


/**
 * Decrypt EKpubKm using AES CTR decrypt with kPrivRx as AES key
 *
 * @param[in] KprivRx Private key used for decryption in p,q,dP,dQ,qInv form
 * @param[out] km Decrypted km. Should be MASTERKEY_SIZE bytes
 * @param[in] EKpubKm Encrypted Km to decrypt. Should be sent plaintext from TX
 * @return H2_OK or H2_ERROR
 */
H2status Decrypt_EKpubKm_kPrivRx( const H2uint8* KprivRx, H2uint8* km, const H2uint8 *EKpubKm)
{
   H2status rc = H2_ERROR;

   int kmSize = MASTERKEY_SIZE;

   do
   {
      crypto_RSAKey key;


      /**
       * BIGDITS buffers for private key.
       * MOD_SIZE = 32. DIGIT_T = 32 bit, 32*32 = 1024 bit total.
       *
       */
      DIGIT_T keyP[MOD_SIZE]={0};
      DIGIT_T keyQ[MOD_SIZE]={0};
      DIGIT_T keydP[MOD_SIZE]={0};
      DIGIT_T keydQ[MOD_SIZE]={0};
      DIGIT_T keyQinv[MOD_SIZE]={0};
      key.p = keyP;
      key.q = keyQ;
      key.dP = keydP;
      key.dQ = keydQ;
      key.Qinv = keyQinv;

      // Input parameter validation
      if( KprivRx == NULL || km == NULL || EKpubKm == NULL)
      {
         ALOGI("ERROR! Decrypt_EKpubKm_kPrivRx: Invaid Input parameters");
         break;
      }

      /**
       * Initialize data for the key - P, Q, dP, dQ and qInv
       */
      mpConvFromOctets( key.p, MOD_SIZE, KprivRx, KPUBRX_P );
      mpConvFromOctets( key.q, MOD_SIZE, KprivRx+KPUBRX_P, KPUBRX_Q );
      mpConvFromOctets( key.dP, MOD_SIZE, KprivRx+KPUBRX_P+KPUBRX_Q, KPUBRX_dP );
      mpConvFromOctets( key.dQ, MOD_SIZE, KprivRx+KPUBRX_P+KPUBRX_Q+KPUBRX_dP, KPUBRX_dQ );
      mpConvFromOctets( key.Qinv, MOD_SIZE, KprivRx+KPUBRX_P+KPUBRX_Q+KPUBRX_dP+KPUBRX_dQ, KPUBRX_qInv );

      /**
       * Perform decrypt. Return value is the size of decrypted data.
       */

      kmSize = crypto_rsaPrivateDecryptOaepSha256( EKpubKm, km, &key );

      /**
       * Check return size!
       */
      if ( kmSize != MASTERKEY_SIZE )
      {
         rc = H2_ERROR;
      }
      else
      {
         rc = H2_OK;
      }

   } while ( 0 );
   return rc;
}

/**
 * Decrypt Km from the SetEKhKm message.
 *
 * @param[in] Kh Private key to use for decryption.
 * @param[in] eKhKm Encrypted data.
 * @param[in] eKhKmSize Size of encrypted data.
 * @param[out] Km Plain text data.
 * @param[in] m IV for CTR decryption.
 *
 * WARNING: This uses crypto_aesCtr128, which may misbehave if the
 * pointer Km is not properly aligned. Be careful....
 * Data prior to Kh / Km could be corrupted if they are not aligned carefully.
 * @return H2_OK or H2_ERROR
 *
 */
H2status Decrypt_EKhKm ( const H2uint8 *Kh, const H2uint8* eKhKm, int eKhKmSize, H2uint8* Km, const H2uint8 *m)
{
   H2status rc = H2_ERROR;

   do
   {
      /**
       * Verify input parameters
       */
      if (( Kh == NULL ) || ( eKhKm == NULL ) || ( Km == NULL) | ( m == NULL ))
      {
         break;
      }

      /**
       * Perform AES-CTR-128 decryption.
       */

      /** Do the AES CTR 128 decryption */
      rc = crypto_aesCtr128( eKhKm , eKhKmSize, Kh, m, Km );

      if ( H2_ERROR == rc )
      {
         ALOGI("%s(): AES CTR 128 Decrypt failed!\n", __func__ );
         break;
      }

   } while( 0 );

   return rc;

}


/**
 * Encrypt 16 byte Km using AES-CTR-128 encrypt.
 *
 * @param[in] Kh AES key to use
 * @param[in] Km Data to encrypt
 * @param[in] m IV for AES decryption
 * @param[out] eKhKm Encrypted output.
 *
 * @return H2_OK or H2_ERROR
 */

H2status Compute_EKhKm (const H2uint8 *Kh,const H2uint8* Km,const H2uint8 *m, H2uint8* eKhKm)
{
   H2status rc = H2_ERROR;

   //static unsigned char eKhKmTemp[EKHKM_SIZE];

   /// validating Input parameters
   if( Kh == NULL || Km == NULL || m == NULL || eKhKm == NULL)
   {
      ALOGI("ERROR! Compute_EKhKm: Invaid Input parameters");

      return rc;
   }

   /**
    * Make sure eKhKm is properly aligned or data could be corrupted.
    */
   crypto_aesCtr128( Km, MASTERKEY_SIZE, Kh, m, eKhKm );


   return H2_OK;
}

/**
 * d = s1^r2 over len bytes.
 *
 * This implementation is not very efficient.
 *
 * @param[in] s1 Input to xor
 * @param[in] s2 Input to xor
 * @param[out] d Output = s1 ^ s2
 * @param[in] len Length of data to xor
 * @return H2_OK or H2_ERROR if s1, s2, or d are NULL.
 *
 */
H2status crypt_xor( const uint8_t *s1, const uint8_t *s2, uint8_t *d, int len )
{
   int ii;

   if ( !s1 || !s2 || !d )
   {
      return H2_ERROR;
   }

   /** @todo: Optimize by using 32 bit-xors if needed. */
   for ( ii = 0 ; ii < len ; ii++ )
   {
      d[ii] = s1[ii] ^ s2[ii];
   }

   return H2_OK;

}


/**
 * Compute dKeyN where N = the value passed via Ctr[].
 *
 * @param[in] Km Master Key.
 * @param[in] rn Rn. See HDCP2 spec for definition of Rn.
 * @param[in] rtx Rtx. See HDCP2 spec.
 * @param[in] Ctr 8 byte counter value used for AES decryption.
 * @param[out] dKey dKeyN, where N = Ctr[].
 * @param[in] dKeySize Should be AES_BLK_SIZE.
 *
 * @return H2_OK or H2_ERROR.
 */

H2status Compute_dKey(const H2uint8 *Km, const H2uint8 *rn, const H2uint8 *rtx, const H2uint8 * Ctr, H2uint8* dKey, int dKeySize)
{
   H2status rc = H2_ERROR;

   H2uint8 pKey[SESSIONKEY_SIZE]={0};   // To store key = Km XOR Rn
   static H2uint8 pSrc[] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
   static H2uint8 pAES[16]={0};       // To store AES result
   H2uint8 pCtrValue[16]={0};  // To store counter value = Rtx || Ctr
   H2uint8 temp[SESSIONKEY_SIZE]={0};

   do
   {
      // Validating Input parameters
      if( (Km == NULL) || (rn == NULL) ||(rtx == NULL) || (Ctr == NULL) || (dKey == NULL) )
      {
         ALOGI("ERROR! Compute_dKey: Invaid Input parameters");
         break;
      }

      memset(pAES, 0, sizeof(pAES));

      // Computing  pKey = Km XOR Rn
      memset(temp,0,SESSIONKEY_SIZE);
      memcpy(temp+SESSIONKEY_SIZE-RRX_SIZE,rn,RN_SIZE);

      crypt_xor ( Km, temp, pKey, SESSIONKEY_SIZE );

      // Compute  pCtrValue = rtx || ctr
      memset(pCtrValue, 0, sizeof(pCtrValue));

      memcpy(pCtrValue, rtx , RTX_SIZE);
      memcpy(pCtrValue+RTX_SIZE,Ctr, sizeof(pCtrValue)-RTX_SIZE );

      /// Perform AES CTR encryption (same as AES CTR decryption).

      rc = crypto_aesCtr128( pSrc , sizeof( pSrc ), pKey, pCtrValue, pAES );

      if(rc != H2_OK)
      {
         ALOGI("ERROR! HDCP-Sink(Rx) Control SM: Compute_dKey: Compute_AESEncryptCTR error\n" );
         break;
      }

      /// Compute dKey = pDst XOR pSrc
      crypt_xor(pAES, pSrc,dKey,dKeySize);
   } while( 0 );

   return rc;

}

/**
 * Compute kd = dKey0 || dKey1
 *
 * @param[in] km - Master Key
 * @param[in] rn - Rn
 * @param[in] rtx - Rtx
 * @param[out] Kd - dKey0 || dKey1. Should be at least 2*DKEY_SIZE bytes.
 * @return H2_OK on success.
 *
 */

H2status Compute_Kd(const H2uint8 *km, const H2uint8 *rn, const H2uint8* rtx, H2uint8* Kd)
{
   unsigned int ii;
   H2status rc = H2_ERROR;
   H2uint8 dKey0[DKEY_SIZE]={0};
   H2uint8 dKey1[DKEY_SIZE]={0};
   H2uint8 Ctr[8]={0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

   do
   {
      if( km == NULL || rtx == NULL || Kd == NULL )
      {
         ALOGI("ERROR! Compute_Kd: Invaid Input parameters");
         break;
      }

      memset(Ctr, 0, sizeof(Ctr));

      rc = Compute_dKey(km, rn, rtx ,Ctr, dKey0, sizeof(dKey0));

      #if 0
      ALOGI( "Dkey0: ");
      for( ii=0;ii<DKEY_SIZE;ii++)
      {
         ALOGI( "%x ", (unsigned char)dKey0[ii]);
      }
      #endif

      if(rc != H2_OK)
      {
         ALOGI("ERROR! HDCP-Sink(Rx) Control SM: Compute_dKey0 error\n" );
         break;
      }

      /// Computing dKey1
      Ctr[7] = 1;
      rc = Compute_dKey(km, rn, rtx ,Ctr, dKey1, sizeof(dKey1));

      if(rc != H2_OK)
      {
         ALOGI( "ERROR! HDCP-Sink(Rx) Control SM: Compute_dKey1 error\n" );
         break;
      }

      #if 0
      ALOGI("\r\nDkey1: ");
      for( ii=0;ii<DKEY_SIZE;ii++)
      {
         ALOGI( "%x ", (unsigned char)dKey1[ii]));
      }
      #endif

      /** Kd = dKey0 || dKey1 */
      memset(Kd, 0, KD_SIZE);
      memcpy(Kd,dKey0, DKEY_SIZE );
      memcpy(Kd+DKEY_SIZE, dKey1, DKEY_SIZE);
      #if 0
      ALOGI( "\r\nKd: ");
      for( ii=0;ii<KD_SIZE;ii++)
      {
         ALOGI( "%x ", (unsigned char)Kd[ii]);
      }
      #endif
   } while( 0 );

   return rc;

}

/**
 * Compute Kh = HMAC-SHA256( Kpriv_Rx ).
 *
 * @param[in] Kpriv_rx Private key
 * @param[in] Kprivrx_len Size of KprivRx.
 * @param[out] Kh HMAC-SHA256( Kpriv_Rx )
 * @return H2_OK on success.
 *
 */
H2status Compute_Kh (const H2uint8 *Kpriv_rx, int Kprivrx_len, H2uint8* Kh)
{
   H2status rc = H2_ERROR;

   H2Sha256Ctx_t c;

   do
   {

      if( Kpriv_rx == NULL || Kh == NULL )
      {
         ALOGI("ERROR! Compute_Kh: Invalid Input parameters");
         break;
      }

      crypto_sha256Init( &c );
      crypto_sha256Update( &c, Kpriv_rx, KPRIVRX_SIZE );
      crypto_sha256Final( &c, Kh );

      rc = H2_OK;

   } while( 0 );

   return rc;
}

/**
 * Compute Eks = dKey2 xor rRx xor Ks
 *
 * @param[in] Ks Session Key
 * @param[in] dKey2 dKey2 @see Compute_dKey
 * @param[in] rRx 64-bit pseudo-random RRX
 * @param[out] Edkey_Ks - dKey2 xor rRx xor Ks
 * @return H2_OK on success.
 */
H2status Compute_Eks(const H2uint8* Ks, const H2uint8* dKey2, const H2uint8* rRx, H2uint8* Edkey_Ks)
{
   H2uint8 temp_rRx[DKEY_SIZE]={0};
   H2uint8 temp[DKEY_SIZE]={0};  
   H2status rc = H2_ERROR;

   do
   {
      if( (Ks == NULL) || (dKey2 == NULL) || (rRx == NULL) || (Edkey_Ks == NULL))
      {
         ALOGI( "ERROR! Compute_Eks: Invalid Input Paramter \n" );
         break;
      }

      memset(temp_rRx, 0, DKEY_SIZE);
      memcpy(temp_rRx+DKEY_SIZE - RRX_SIZE, rRx, RRX_SIZE);

      ///dKey2 XOR rRx
      crypt_xor(temp_rRx, dKey2, temp, DKEY_SIZE);

      ///ks XOR temp
      crypt_xor(temp,Ks, Edkey_Ks, DKEY_SIZE);

      rc = H2_OK;
   } while( 0 );

   return rc;
}


/**
 * Compute Lprime = HMAC-SHA256(rn, kd XOR rRx).
 *
 * @param[in] Kd - Input to XOR.
 * @param[in] rRx - Second input to XOR.
 * @param[in] rn - rn value to use.
 * @param[out] L - H_SIZE bytes. HMAC-SHA256(rn, kd XOR rRx ).
 * @return H2_OK on success.
 *
 */

H2status Compute_Lprime(const H2uint8 * Kd, const H2uint8 *rRx, const H2uint8 *rn, H2uint8 * L, int bUseRnRn)
{
   H2status rc = H2_ERROR;
   static H2uint8 pKey[KD_SIZE]={0};
   static H2uint8 temp[KD_SIZE]={0};
   
   ALOGI( "Compute_Lprime bUseRnRn [%d]  \n" ,bUseRnRn);

   do
   {
      if( (Kd == NULL) || (rRx == NULL) || (rn == NULL) || (L == NULL))
      {
         ALOGI( "ERROR! Compute_Lprime: Invalid Input Parameter \n" );
         break;
      }

      memset(pKey, 0, sizeof(pKey));
      memset(temp, 0, sizeof(temp));

      /** Copy rRx to the end of temp */
      memcpy(temp+KD_SIZE-RRX_SIZE,rRx,RRX_SIZE);

      /** pKey = Kd | ( 0 || rRx ) */
      crypt_xor(Kd, temp, pKey, KD_SIZE);

      /// Compute HMAC value
      if(bUseRnRn>0)
	  {
	    // Bruce add HDCP 2.2 implementation
    	  // rnrn = rn || rn;
	    H2uint8 rnrn[DOUBLE_RN_BUF_SIZE];
		memset(rnrn,0,sizeof(DOUBLE_RN_BUF_SIZE));
	    memcpy(rnrn,rn,RN_SIZE);
	    memcpy(rnrn+RN_SIZE,rn,RN_SIZE);
	    rc = hmacsha256( pKey, KD_SIZE, rnrn , DOUBLE_RN_BUF_SIZE, L );
	  }
	  else
	  {
	    rc = hmacsha256( pKey, KD_SIZE, rn , RN_SIZE, L );
	  }
   } while( 0 );

   return rc;
}


/**
 * RSA private key decrypt a memory buffer. PKCS#1 1.5 padding is used.
 * The key size is 1024 bits so all buffers are to be this length.
 *
 * @param[in]  pIn   Pointer to memory buffer to decrypt.
 * @param[out] pOut  Pointer to clear text buffer.
 *
 * Notes: Only a NULL label is supported. This function is non-reentrant.
 *
 * @retval Size of decrypted buffer. Zero if decryption failed.
 *
 */

static int crypto_rsaPrivateDecryptOaepSha256( uint8_t const * pIn, uint8_t* pOut, const crypto_RSAKey *key )
{
   int rc = 0;
   int bad = 0;
   /** The below values are static to move them off of the stack. */
   static DIGIT_T c[MOD_SIZE];
   static DIGIT_T m[MOD_SIZE];
   static DIGIT_T m1[MOD_SIZE];
   static DIGIT_T m2[MOD_SIZE];
   static DIGIT_T h[MOD_SIZE];
   static DIGIT_T hq[2*MOD_SIZE];       /* Multiply requires 2x size of it's inputs */
   static H2uint8 seedMask[SHA256_DIGEST_SIZE];
   static H2uint8 seed[SHA256_DIGEST_SIZE];
   static H2uint8 dbMask[1024/8];
   static H2uint8 db[128-SHA256_DIGEST_SIZE-1];
   static H2uint8 pTemp[1024/8];
   static H2uint8 *ptr;
   static const H2uint8 sha256NullHash[SHA256_DIGEST_SIZE] = { 0xe3, 0xb0, 0xc4, 0x42,
      0x98, 0xfc, 0x1c, 0x14, 0x9a, 0xfb, 0xf4, 0xc8, 0x99, 0x6f,
      0xb9, 0x24, 0x27, 0xae, 0x41, 0xe4, 0x64, 0x9b, 0x93, 0x4c,
      0xa4, 0x95, 0x99, 0x1b, 0x78, 0x52, 0xb8, 0x55 };

   /** Copy */
   mpConvFromOctets( c, MOD_SIZE, pIn, 1024/8 );
   mpPrint( c, MOD_SIZE );
   /** TODO: Add Blinding to avoid giving away hints based on decrypt time. */

   /** Decrypt using Chinese Remainder Theorem */
   /** Let m_1 = c^dP mod p. */
   mpModExp( m1, c, key->dP, key->p, MOD_SIZE );

   /** Let m_2 = c^dQ mod q. */
   mpModExp( m2, c, key->dQ, key->q, MOD_SIZE );

   if ( mpCompare( m1, m2, MOD_SIZE ) < 0 )
   {
      mpAdd( m1, m1, key->p, MOD_SIZE );
   }

   mpSubtract( m1, m1, m2, MOD_SIZE );

   /** Let h - qInv( m_1 - m_2 ) mod p */
   mpModMult( h, key->Qinv, m1, key->p, MOD_SIZE );
   mpMultiply( hq, h, key->q, MOD_SIZE );

   /** Let m = m_2 + hq */
   mpAdd( m, m2, hq, MOD_SIZE );

   /** Copy the output to pTemp */
   mpConvToOctets(m, MOD_SIZE, pTemp, sizeof(pTemp));

   /**
    * pTemp still has OAEP-SHA256 padding.
    *
    * pTemp is:
    * Y || maskedSeed || maskedDB
    * Y is one octet, and should be zero.
    * maskedSeed is hLen ( SHA256_DIGEST_SIZE) bytes.
    * maskedDB is k-hLen-1 bytes
    */

   /** split it up */
   H2uint8 *maskedSeed = pTemp+1;
   H2uint8 *maskedDB = pTemp+1+SHA256_DIGEST_SIZE;

   /** Do not exit on error conditions, or reveal what the errors were,
    * to avoid leaking timing information.
    */

   if ( *pTemp != 0 )
   {
      bad = 1;
   }

   /** Reverse OAEP-SHA256 */
   /** seedMask = MGF( maskedDB, hLen ) */
   MGF1_SHA256( seedMask, SHA256_DIGEST_SIZE, maskedDB, 1024/8-SHA256_DIGEST_SIZE-1 );

   /** seed = maskedSeed xor seedMask */
   crypt_xor( maskedSeed, seedMask, seed, SHA256_DIGEST_SIZE );

   /** dbMask = MGF( seed, k-hLen-1 ) */
   MGF1_SHA256( dbMask, 1024/8-SHA256_DIGEST_SIZE-1, seed, SHA256_DIGEST_SIZE );

   /** DB = maskedDB XOR dbMask */
   crypt_xor( dbMask, maskedDB, db, 1024/8-SHA256_DIGEST_SIZE-1 );

   /**
    * DB should contain:
    * lHash' || PS || 0x01 || M
    *
    * Where:
    * lhash' = SHA256(Label), which should be SHA256( NULL, 0 )
    * PS is a (possibly empty) padding string of all zeroes
    * 0x01 immediately precedes the data
    * M is the message
    */


   /* Verify the hash */
   if ( memcmp( db, sha256NullHash, SHA256_DIGEST_SIZE ) != 0)
   {
      bad = 1;
   }

   ptr = db + SHA256_DIGEST_SIZE;
   do
   {
      /** Search for the first non-zero octet, and make sure it's 0x01.
       * Since PS is possibly empty, we do not check for zero until AFTER
       * checking for 0x01.
       */
      if ( ptr[0] != 0x00 )
      {
         if ( ptr[0] != 0x01 )
         {
            /** For security purposes, we cannot reveal which part of
             * the decryption has failed.
             *
             * However, if LOCALDBG is set, the keys and ciphertext are
             * printed out, so it is not an issue.
             *
             */
            ALOGI( "Found non-zero that wasn't 0x01 fail.\n");
            bad = 1;
         }
         /** Found it! First non-zero octet was 0x01 */
         break;
      }
      ptr++;
   } while( ptr < db + sizeof(db));

   if ( ptr >= db + sizeof(db))
   {
      bad = 1;
   }
   else
   {
      ptr++;
      /** Copy the results */
      memcpy( pOut, ptr, db + sizeof(db) - ( ptr ));
      rc = db + sizeof(db) - (ptr);
   }
   if ( bad )
   {
      rc = 0;
   }
   return rc;

}

/**
 *
 * Compute HMAC-SHA256( Kd, rtx ^ Repeater ).
 *
 * @param[in] Kd
 * @param[in] rtx
 * @param[in] Repeater
 * @param[out] Hprime At least H_SIZE bytes for the results.
 *
 * @return H2_OK on success.
 */
H2status Compute_Hprime(const H2uint8 * Kd, const H2uint8 *rtx, const H2uint8 Repeater, H2uint8 * Hprime)
{
   H2status rc = H2_ERROR;
   H2uint8 pSrc[RTX_SIZE];

   do
   {
      if( Kd == NULL || rtx == NULL || Hprime == NULL)
      {
         ALOGI("ERROR! Compute_Hprime: Invalid Input Paramter \n" );
         break;
      }

      /** pSrc = rtx XOR REPEATER */
      memset(pSrc, 0, sizeof(pSrc));
      memcpy(pSrc, rtx, sizeof(pSrc));
      pSrc[RTX_SIZE -1 ] = rtx[RTX_SIZE-1] ^ Repeater;	// page 78, XORed with LSB of rtx

      /** Compute H' = HMAC-SHA256( Kd, RTX ^ Repeater ) */
      rc = hmacsha256(Kd, KD_SIZE, pSrc, sizeof(pSrc),  Hprime);
   } while ( 0 );

   return rc;
}

/**
 * Compute H' based on HDCP 2.2 sepc
 * H' = HMAC-SHA256 (a, b)
 * a = (rtx ^ REPEATER ||
 *      AKE_Receiver_Info.VERSION || AKE_Receiver_Info.RECEIVER_CAP_MASK||
 *      AKE_Transmitter_Info.VERSION || AKE_Transmitter_Info.TRAN_CAP_MASK)
 * b = kd
 */
H2status Compute_Hprime_22(
		const H2uint8 *Kd,
		const H2uint8 *rtx,		// sizeof : 8
		const H2uint8 Repeater,
		const H2uint8 ReceiverVersion,
		const H2uint8 *ReceiverMask,		// we believe that it is treated as big endian data in previous stage
		const H2uint8 TransmitterVersion,
		const H2uint8 *TransmitterMask,		// we believe that it is treated as big endian data in previous stage
		H2uint8 * Hprime)
{
	H2status rc = H2_ERROR;
	H2uint8 pSrc[RTX_SIZE]={0};	// rtx XOR REPEATER
	H2uint8 pSrc22[AKE_COMPUTE_H_PRIME_META_BUF_SIZE]={0};

    ALOGI("Compute_Hprime_22[%d]===============",__LINE__ );


	do{
		// input checking
		if( Kd == NULL || rtx == NULL || Hprime == NULL || ReceiverMask == NULL || TransmitterMask == NULL)
		{
			printf("ERROR! Compute_Hprime_22: Invalid Input Paramter \n" );
			break;
		}

		// a. pSrc = rtx ^ Repeater
		memset(pSrc, 0, sizeof(pSrc));
		memcpy(pSrc, rtx, sizeof(pSrc));	// copy rtx to pSrc
		pSrc[RTX_SIZE -1 ] = rtx[RTX_SIZE-1] ^ Repeater;

		// b. do || ...
		// - clear pSrc22
		memset(pSrc22, 0, sizeof(pSrc22));

		H2uint8 *pTmp=pSrc22;

		// - rtx ^ REPEATER
		memcpy(pTmp,pSrc,sizeof(pSrc));
		pTmp=pTmp+sizeof(pSrc);

		// - AKE_Receiver_Info.VERSION
		*pTmp = ReceiverVersion; //memcpy(pTmp,&ReceiverVersion,1);
		pTmp=pTmp+1;
		// - AKE_Receiver_Info.RECEIVER_CAPABILITY_MASK   (endian issue)
		*pTmp=ReceiverMask[0]; //memcpy(pTmp,ReceiverMask[1],1);
		pTmp=pTmp+1;
		*pTmp=ReceiverMask[1]; //memcpy(pTmp,ReceiverMask[0],1);
		pTmp=pTmp+1;

		// - AKE_Transmitter_Info.VERSION
		*pTmp=TransmitterVersion; //memcpy(pTmp,&TransmitterVersion,1);
		pTmp=pTmp+1;
		// - AKE_Transmitter_Info.TRANSMITTER_CAPABILITY_MASK
		*pTmp=TransmitterMask[0]; //memcpy(pTmp,TransmitterMask[1],1);
		pTmp=pTmp+1;
		*pTmp=TransmitterMask[1]; //memcpy(pTmp,TransmitterMask[0],1);
		//pTmp=pTmp+1;

		// finally, we do HMAC-SHA256 (pSrc22, kd)
		rc = hmacsha256(Kd, KD_SIZE, pSrc22, sizeof(pSrc22),  Hprime);
	}while(0);

	return rc;
}
