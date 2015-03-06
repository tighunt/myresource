
/**
 * This file contains the method for loading serialization data
 * from secure flash.
 */

/**
 * Includes
 */
#include <unistd.h>
#include "hdcp2_hal.h"
#include "hdcp2_session.h"
#include "hdcp2_messages.h"
#include "hdcp2_interface.h"

#include "openssl/aes.h"
#include "openssl/hmac.h"
#include "openssl/bio.h"
#include "openssl/evp.h"
#include "rcp_api.h"

