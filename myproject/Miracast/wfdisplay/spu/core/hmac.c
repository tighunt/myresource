#include "hdcp2_hal.h"
#include "hmac.h"
#include "crypto.h"

#define LOG_TAG "HDCP2_HMAC"
#include <utils/Log.h>

H2status hmacsha256( const H2uint8 *pKeyIn, unsigned int keylen, const H2uint8 *pSrc, H2uint32 ulSrcSize, H2uint8 *pOut )
{
   H2Sha256Ctx_t c;

   static H2uint8 ipad[64];
   static H2uint8 opad[64];
   static H2uint8 key[64];
   static H2uint8 hash[256/8];

   unsigned int ii;
   if ( keylen > sizeof(key))
   {
      crypto_sha256Init( &c );
      crypto_sha256Update( &c, pKeyIn, keylen );
      crypto_sha256Final( &c, key );
      keylen = SHA256_DIGEST_SIZE;
   }
   else
   {
      memcpy( key, pKeyIn, keylen );
   }

   if ( keylen < sizeof(key))
   {
      memset( key+keylen, 0, sizeof(key)-keylen);
      keylen = sizeof(key);
   }

   for ( ii=0;ii<keylen;ii++ )
   {
      ipad[ii] = 0x36 ^ key[ii];
      opad[ii] = 0x5c ^ key[ii];
   }

   crypto_sha256Init( &c );
   crypto_sha256Update( &c, ipad , sizeof(ipad) );
   crypto_sha256Update( &c, pSrc, ulSrcSize );
   crypto_sha256Final( &c, hash );
   crypto_sha256Init( &c );
   crypto_sha256Update( &c, opad, sizeof(opad));
   crypto_sha256Update( &c, hash, sizeof(hash));
   crypto_sha256Final( &c, pOut );
#ifdef DEBUG_HMAC
   ALOGI("hmac-sha256=\r\n");
   for(ii=0;ii<SHA256_DIGEST_SIZE;ii++)
   {
      ALOGI("%x ", pOut[ii]);
   }
   ALOGI("\r\n");
#endif
   return H2_OK;
}
