#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <assert.h>
#include <Platform_Lib/PlatformCommon/I2CUtil.h>
#include <io/i2c.h>
#include "wifi_rtsp.h"
#include "wifi_rtsp_cmds.h"

static bool XferMsg(int m_fd, struct i2c_msg* p_msgs, unsigned char n_msg)
{        
    struct i2c_rdwr_ioctl_data  msgset;    
    msgset.msgs  = p_msgs;
    msgset.nmsgs = n_msg;

    if (ioctl(m_fd, I2C_RDWR, &msgset) < 0)                
        return false;    
        
    return true;            
};

static bool I2C_Write_EX(
    unsigned short          Addr,
    unsigned short          nData, 
    unsigned char*          pData,
    int                     Flag
    )
{    
    struct i2c_msg  i2c_message;    
    int mfd = open("/dev/i2c/0", O_RDWR);
    if (mfd <= 0)
        return false;
    Flag &= ~0x0001;        
    
    if (nData == 0) {
    	close(mfd);
        return false;
    }
        
    i2c_message.addr  = Addr;
    i2c_message.flags = Flag;
    i2c_message.len   = nData;
    i2c_message.buf   = pData;
            
    bool ret = XferMsg(mfd, &i2c_message, 1);
    close(mfd);
    return ret;
}

static bool I2C_Read_EX(
    unsigned short          Addr, 
    unsigned short          nData, 
    unsigned char*          pData,
    unsigned short          rAddr,
    unsigned short          rnData,
    unsigned char*          rpData,
    int                     Flag
    )
{        
    struct i2c_msg  i2c_message[2];    
    bool ret;
    
    int mfd = open("/dev/i2c/0", O_RDWR);
    if (mfd <= 0)
        return false;
    Flag &= ~0x0001;           
  
    if (nData==0) {
        if (rnData == 0) {
            close(mfd);
            return false;
        }
        i2c_message[0].addr  = rAddr;
        i2c_message[0].flags = I2C_M_RD|Flag;
        i2c_message[0].len   = rnData;
        i2c_message[0].buf   = rpData;

        ret = XferMsg(mfd, i2c_message, 1);
        close(mfd);
        return ret;
    }
    

    i2c_message[0].addr  = Addr;
    i2c_message[0].flags = Flag;
    i2c_message[0].len   = nData;
    i2c_message[0].buf   = pData;
    i2c_message[1].addr  = rAddr;
    i2c_message[1].flags = I2C_M_RD;
    i2c_message[1].len   = rnData;
    i2c_message[1].buf   = rpData;

    ret = XferMsg(mfd, i2c_message, 2);
    close(mfd);
    return ret;
}


/* 
 * initialize the i2c component
 */
int wifi_i2c_init(void) 
{
    int servSock;                    /* Socket descriptor for server */
    struct sockaddr_in echoServAddr; /* Local address */
    unsigned short echoServPort;     /* Server port */


    echoServPort = WIFI_I2C_DEFAULT_PORT;

    /* Create socket for incoming connections */
    if ((servSock = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP)) < 0)
        return -1;
      
    /* Construct local address structure */
    memset(&echoServAddr, 0, sizeof(echoServAddr));   /* Zero out structure */
    echoServAddr.sin_family = AF_INET;                /* Internet address family */
    echoServAddr.sin_addr.s_addr = htonl(INADDR_ANY); /* Any incoming interface */
    echoServAddr.sin_port = htons(echoServPort);      /* Local port */

    /* Bind to the local address */
    if (bind(servSock, (struct sockaddr *) &echoServAddr, sizeof(echoServAddr)) < 0){
        close(servSock);
        return -1;
    }

    /* Mark the socket so it will listen for incoming connections */
    if (listen(servSock, 3) < 0) {
        close(servSock);
        return -1;
    }
    
    return servSock;
}

static bool wifi_i2c_read(int nfd) 
{
    unsigned char ntrans;
    unsigned char addr;
    unsigned char nbyte = 0;
    unsigned char data[256];
    unsigned char stop;
    unsigned char delay;
    unsigned char rAddr, nRead;
    bool error_ret = false;
    unsigned char reply;
    unsigned char status;
    bool ret;
    unsigned char rdata[256];

    if (read(nfd, &ntrans, 1) < 1) 
        return false;
    while(ntrans > 0) {
        
        if (read(nfd, &addr, 1) < 1)
            goto error_return;
	printf("i2c addr = %x\n", addr);
        if (read(nfd, &nbyte, 1) < 1)
            goto error_return;
        for (int i =0; i<nbyte; i++) {
            read(nfd, data+i, 1);
        }
        if (read(nfd, &stop, 1) < 1)
            goto error_return;
        if (read(nfd, &delay, 1) < 1)
            goto error_return;
        if (stop == 0) {
                // stop bit set, 
            if (!I2C_Write_EX(addr, nbyte, data,  I2C_FLAG_GPIO_RW)) {
                    // send a write nak
                goto error_return;
            }
            if (delay > 0 && delay < 15) 
                usleep(delay * 10000);
            
            nbyte = 0;
        }
        else {
                // no stop bit, we cannot support 2 writes, do we have more transaction?
            if (ntrans > 1) {
                goto error_return;
            }
        }
        ntrans --;
    }

    if (read(nfd, &rAddr, 1) < 1) 
        goto error_return;
    if (read(nfd, &nRead, 1) < 1) 
        goto error_return;
    printf("i2c readaddr = %x, len=%x\n", rAddr, nRead);
    ret = I2C_Read_EX(addr, nbyte, data, rAddr, nRead, rdata, I2C_FLAG_GPIO_RW);
    if (!ret) 
        goto error_return;
    
        // success, send back result
    status = 0x00;
    write(nfd, &status, 1);
    write(nfd, &nRead, 1);
    write(nfd, rdata, nRead);
    return true;

  error_return:
    reply = 0x80;
    write(nfd, &reply, 1);
    return error_ret;
}

static bool wifi_i2c_write(int nfd)  
{
    unsigned char addr;
    unsigned char nbyte = 0;
    unsigned char data[257];
    unsigned char stat = 0x81;
    bool ret = false;

    if (read(nfd, &addr, 1) < 1) 
        goto exit_return;
    if (read(nfd, &nbyte, 1) < 1) 
        goto exit_return;
    if (nbyte > 256) 
	goto exit_return;
    read(nfd, data, nbyte);

    if (!I2C_Write_EX(addr, nbyte, data,  I2C_FLAG_GPIO_RW)) {
	printf("oops, write to i2c failed\n");
	goto exit_return;
    }

    stat = 0x01;
    ret = true;

  exit_return:
    write(nfd, &stat, 1);
    return ret;
}

/*
 *handle wfd i2c request
 */
bool wifi_i2c_incoming(int nfd) 
{
    unsigned char x;
    if (read(nfd, &x, 1) < 1) 
        return false;
    if (x == 0) {
        return wifi_i2c_read(nfd);
    }
    else if (x == 1) {
        return wifi_i2c_write(nfd);
    }
    return false;
}

