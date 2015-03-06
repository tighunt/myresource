/**
 * Includes
 */
#include <syslog.h>
#include <unistd.h>
#include "hdcp2_hal.h"
#include "hdmi.h"
#include "crypto.h"
#include "hdcp2_interface.h"

#define LOGNAME       __FILE__ " "
#define DEBUG(...)    syslog( LOG_DEBUG, LOGNAME __VA_ARGS__ )
#define NOTICE(...)   syslog( LOG_NOTICE, LOGNAME __VA_ARGS__ )
#define CRITICAL(...) syslog( LOG_CRIT, LOGNAME __VA_ARGS__ )
#define ERROR(...)    syslog( LOG_ERR, LOGNAME __VA_ARGS__ )

/*** Globals ***/
static volatile H2uint32 gHdcp1CheckCount = 0;
static volatile H2bool   gbPoliceEnabled = FALSE;

/*** Prototypes ***/


/**
 * Call that checks to see if HDCP 1.x is on. If it is on, the function
 * will atomically increment gHdcp1CheckCount. The police task will
 * periodically check this count to see if we're doing what we're suppose
 * to do.
 *
 * @param [out] bEnabled
 *              Return TRUE if HDCP-1 is on or FALSE if HDCP-1 is off
 *
 * @return Returns H2_OK if successful
 */
int hdmi_CheckHdcp1x( H2bool* bEnabled )
{
   *bEnabled = FALSE;

   /*
    * Add code here to check to see if HDCP 1.x is on
    *
    * For simulation we just set bEnabled to TRUE
    */   
   if (access("/tmp/ksv_list.bin", R_OK)==0)
      *bEnabled = TRUE;

   // Increment the check count if we detected encryption
   if( *bEnabled )
   {        
        gHdcp1CheckCount++;
   }

   return 0;
}

/**
 * Turn policing on/off
 *
 */
void hdmi_policeEnable( H2bool bEnable )
{
   gbPoliceEnabled = bEnable;

   return;
}

/**
 * Initialize HDMI policing
 *
 */
H2status hdmi_init( )
{

   return(H2_OK);
}

/**
 * HDMI policing task
 *
 * Main should call this ~5 seconds
 *
 */
void hdmi_task( )
{
   static H2uint32 count = 0;
   unsigned char zks[SESSIONKEY_SIZE];
   
   DEBUG( "hdmi_task awake!\n" );

   if( gbPoliceEnabled && (count == gHdcp1CheckCount) )
   {
      NOTICE( "HDCP 1.x police detected a problem!" );

      /* Clear DEMUX KEY */
      memset(zks, 0, sizeof(zks));   
      hdcp2_SetKsXorLc128(zks);      
   }

   count = gHdcp1CheckCount;

   return;
}

