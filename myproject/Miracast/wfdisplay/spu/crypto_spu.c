

/**
 * This file contains source code that interacts with SPU, 
 * and hardware ciphers for implementing the HDCP2
 * state machine. 
 */

/**
 * Includes
 */
#include "hdcp2_hal.h"
#include "crypto.h"


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
 * For openssl SHA-256 and AES
 */
#include <openssl/sha.h>
#include <openssl/aes.h>

#define CRYPTO_AES_KEY_LEN (16) // Bytes (128 bits)

#define LOG_TAG "HDCP2_CRYPTO_SPU"
#include <utils/Log.h>

/**
 * Generate 32 bits of random values 
 *
 * @retval  Random 32 bit value
 *
 */
H2uint32 crypto_random32( )
{
   return(rand());
}


/**
 * Initialize a SHA-256 context for use to calculate a SHA-256
 * hash. This must be called first before calling update and final.
 *
 * @param[in]  pCtx  Pointer to SHA-256 context. The contents to the
 *                   context are defined in hdcp2_hal.h
 *
 * @retval  True if successful, False if an error occurred.
 *
 */
H2bool crypto_sha256Init( H2Sha256Ctx_t* pCtx )
{
   bool bStatus = false;

   do
   {
      if( !SHA256_Init( &pCtx->c ) )
      {
         ALOGI("Failed to init SHA-256 context.\n");
         break;
      }

      bStatus = true;

   } while(0);

   return( bStatus );


   return( TRUE );
}

/**
 * Calculate the SHA-256 hash of the supplied memory buffer. This
 * function can be called multiple times to extend the hash 
 * calculation accross multiple buffers. To get the hash, the final 
 * function must be called to indicate no more buffers will be supplied.
 *
 * @param[in]  pCtx  Pointer to SHA-256 context. The contents to the
 *                   context are defined in hdcp2_hal.h
 * @param[in]  pBuf  Pointer to memory buffer to calculate hash on.
 * @param[in]  len   Length in bytes of the memory buffer.
 *
 * @retval  True if successful, False if an error occurred.
 *
 */
H2bool crypto_sha256Update( H2Sha256Ctx_t* pCtx, H2uint8 const * pBuf, H2uint32 len )
{
   bool bStatus = false;

   do
   {
      if( !SHA256_Update(&pCtx->c, pBuf, len) )
      {
         ALOGI("Failed to update SHA-256 hash.\n");
         break;
      }

      bStatus = true;

   } while(0);

   return( bStatus );
}

/**
 * Finalize the SHA-256 hash calculation and retrieve the hash. This
 * function also frees any resources associated with the supplied 
 * context. The context must be initialized again before reusing it.
 *
 * @param[in]  pCtx  Pointer to SHA-256 context. The contents to the
 *                   context are defined in hdcp2_hal.h
 * @param[out] pHash Pointer to buffer to copy hash into. The buffer must
 *                   be SHA256_DIGEST_SIZE bytes long.
 *
 * @retval  True if successful, False if an error occurred.
 *
 */
H2bool crypto_sha256Final( H2Sha256Ctx_t* pCtx, H2uint8* pHash )
{
   bool bStatus = false;

   do
   {
      if( !SHA256_Final(pHash, &pCtx->c) )
      {
         ALOGI( "Failed to finalize SHA-256 hash.\n");
         break;
      }

      bStatus = true;

   } while(0);

   return( bStatus );
}

/**
 * Encrypt/Decrypt a memory buffer using AES-CTR-128.
 *
 * @param[in]  gaIn  Pointer to cipher text.
 * @param[in]  len   Length in bytes of cipher text. This 
 *                   must be a multiple of 16 bytes.
 * @param[in]  pSK   Pointer to 16 byte AES Key.
 * @param[in]  pIV   Pointer to 16 byte AES IV.
 * @param[out] pOut  Pointer to plain text buffer. This must be 
 *                   the same length as pIn.
 *
 * @return H2_OK or H2_ERROR. 
 *
 */

H2status crypto_aesCtr128(uint8_t const *pIn, int len, uint8_t const * pSK, uint8_t const * pIV, uint8_t* pOut )
{
   H2status rc = H2_ERROR;

   uint32_t num = 0;

   AES_KEY key;

   // Openssl will modify the IV buffer so we make a copy of it.
   uint8_t pTmpIV[CRYPTO_AES_KEY_LEN];

   uint8_t pECount[CRYPTO_AES_KEY_LEN];

   memset(pECount, 0, sizeof(pECount));


   do
   {
      if( !pIn || !pSK || !pIV || !pOut )
      {
         break;
      }

      if( 0 != (len % CRYPTO_AES_KEY_LEN) )
      {
         break;
      }

      // Make a copy of the IV
      memcpy(pTmpIV, pIV, CRYPTO_AES_KEY_LEN);

      // Set the encrypt key
      AES_set_encrypt_key(pSK, CRYPTO_AES_KEY_LEN*8, &key);

      AES_ctr128_encrypt( pIn, pOut, len, &key, pTmpIV, pECount, &num);


      rc = H2_OK;

   } while(0);


   return( rc );
}
