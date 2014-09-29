/***************************************************************************************************
  File        : MCPUtil.cpp   
  Description : API for MCP Content Protection Module
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
#include <hresult.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include "MCPUtil.h"




/*====================================================================== 
 * Func : MCP_FileHashWithRegion
 *
 * Desc : Compute hash value of a file (or part of file) via 128 bits AES_H_Hasing
 *
 * Parm : algo                      : hash algo
 *        fname                     : file name
 *        hash[MCP_MAX_DIGEST_SIZE] : 128 bits hash value of the file 
 *        block_size                : Block Size that used to compute Hashing Value
 *        start                     : Start Address
 *        end                       : End   Address
 *
 * Retn : >=0 : others size of the hash value
 *        < 0 : failed, 
 *======================================================================*/
int MCP_FileHashWithRegion(
    unsigned char           algo,
    const char*             fname,
    unsigned char           hash[MCP_MAX_DIGEST_SIZE],
    unsigned long           block_size,
    unsigned long           start,
    unsigned long           end
    )
{
    MCP_MD_CTX      ctx;   
    MCP_BUFF*       mcpb; 
    FILE*           fd;
    int             len;    
    unsigned int    hash_len = 0;    
    unsigned long   data_length;          
            
    if (start >= end)
    {
        printf("%s : %d : %s    FATAL : start address (%d) should less than stop address (%d)\n",
                __FILE__, __LINE__, __FUNCTION__, start, end);        
        goto err_invalid_range_setting;
    }
         
    if ((fd = fopen(fname, "r"))==NULL)    
    {
        //printf("%s : %d : %s    FATAL : Compute hash value failed, open file %s failed\n",
                //__FILE__, __LINE__, __FUNCTION__, fname);
        printf("%s open file %s failed\n",fname);        
        goto err_open_file_failed;
    }
    
    fseek(fd, 0, SEEK_END);            
        
    data_length = ftell(fd);          
    
    if (end > data_length)
        end = data_length;    
         
    data_length = end - start;
    
    // seek to start address                        
    fseek(fd, start, SEEK_SET);                  
            
    if ((mcpb = alloc_mcpb(block_size))==NULL)        
        goto err_alloc_mcpb_failed;

    // start compute hash value
    MCP_MD_CTX_init(&ctx);
    
    if (MCP_DigestInit(&ctx, (MCP_MD_TYPES) algo)!= S_OK)    
        goto err_init_dgst_failed;   
            
    while(data_length)
    {                                            
        mcpb_purge(mcpb);
        
        len = (data_length >= block_size) ? block_size : data_length;        
        
        if ((len = fread(mcpb->data, 1, len, fd))<=0)                    
            break;
                
        mcpb_put(mcpb, len);                
            
        MCP_DigestUpdate(&ctx, mcpb);     
        
        data_length -= len;   
        
        //MCP_DigestPeek(&ctx, hash, &hash_len);    
            
        //_dump_data(hash, hash_len);        
    }                
    
    MCP_DigestFinal(&ctx, hash, &hash_len);
    
    MCP_MD_CTX_cleanup(&ctx);
    
    free_mcpb(mcpb);
    
    fclose(fd);
    
    return (int) hash_len;
    
err_init_dgst_failed:
    free_mcpb(mcpb);
err_alloc_mcpb_failed:
    fclose(fd);
err_open_file_failed:    
err_invalid_range_setting:    
    return -1;    
}





/*====================================================================== 
 * Func : MCP_FileCipher
 *
 * Desc : Encrypt/Decrypt File with specified algorithm
 *
 * Parm : algo          : cipher algrithms
 *        enc           : 0 : decryption, othes : encryption
 *        key           : key 
 *        key_len       : size of key
 *        iv            : iv
 *        iv_len        : iv_len
 *        sbh           : short blcok handling algorithm 
 *        sfile         : source file
 *        dfile         : destination file
 *
 * Retn : >=0 : others size of the hash value
 *        < 0 : failed, 
 *======================================================================*/
int MCP_FileCipher(
    unsigned char           algo,                   
    unsigned char           enc,  
    unsigned char*          key,
    unsigned char           key_len,
    unsigned char*          iv,
    unsigned char           iv_len,    
    unsigned char           sbh,
    const char*             sfile,
    const char*             dfile    
    )
{
    FILE* fd1;    
    FILE* fd2;    
    MCP_CIPHER_CTX  ctx;
    MCP_BUFF*       mcpb;         
    unsigned char*  buff;
    int             buff_len;
    int             len;
                     
    if ((fd1 = fopen(sfile, "rb"))==NULL)    
    {
        printf("[MCP] Do File Cipher failed - open src file %s failed\n", sfile);        
        goto err_open_source_file_failed;
    }    
    
    if ((fd2 = fopen(dfile, "wb"))==NULL)    
    {
        printf("[MCP] Do File Cipher failed - open dst file %s failed\n", dfile);        
        goto err_open_dest_file_failed;
    }    
    
    if ((mcpb = alloc_mcpb(16 * 1024))==NULL)        
        goto err_alloc_mcpb_failed;
                
    buff = mcpb->data;
    buff_len = mcpb_tailroom(mcpb);
    
    MCP_CIPHER_CTX_init(&ctx); 
    
    if (MCP_CipherInitEx(&ctx, algo, key, key_len, iv, iv_len, enc, sbh) <0)
    {
        printf("[MCP] Do File Cipher failed - init cipher failed\n");        
        goto err_init_cipher_failed;
    }            

    while(!feof(fd1))
    {                              
        if ((len = fread(buff, 1, buff_len, fd1))<=0)                    
            break;                                    
            
        if ((len = MCP_CipherUpdate(&ctx, buff, len, buff))<0)
        {
            printf("[MCP] Do File Cipher failed - update cipher failed\n");        
            goto err_update_cipher_failed;
        }               
        
        fwrite(buff, 1, len, fd2);         
    }                
    
    if ((len = MCP_CipherFinal(&ctx, buff))>0)
    {
        fwrite(buff, 1, len, fd2);         
    }                

    MCP_CIPHER_CTX_cleanup(&ctx);        
        
    fclose(fd1);
    fclose(fd2);
    free_mcpb(mcpb);
    
    return 0;
    
err_update_cipher_failed:    
    MCP_CIPHER_CTX_cleanup(&ctx);
        
err_init_cipher_failed:
    free_mcpb(mcpb);
    
err_alloc_mcpb_failed:
    fclose(fd2);
err_open_dest_file_failed:
    fclose(fd1);
err_open_source_file_failed:
    return -1;    
}



/*====================================================================== 
 * Func : MCP_DataCipher
 *
 * Desc : Encrypt/Decrypt Data with specified algorithm
 *
 * Parm : algo          : cipher algrithms
 *        enc           : 0 : decryption, othes : encryption 
 *        key           : key 
 *        key_len       : size of key
 *        iv            : iv
 *        iv_len        : iv_len
 *        sbh           : short blcok handling algorithm
 *        data_in       : data source
 *        data_out      : data sink
 *
 * Retn : >=0 : others size of the hash value
 *        < 0 : failed, 
 *======================================================================*/
int MCP_DataCipher(    
    unsigned char           algo,
    unsigned char           enc,
    unsigned char*          key,
    unsigned char           key_len,
    unsigned char*          iv,
    unsigned char           iv_len,
    unsigned char           sbh,
    unsigned char*          data_in,
    unsigned char*          data_out,
    unsigned long           len
    )
{
    MCP_CIPHER_CTX  ctx;    
    int             out = 0;             
                
    MCP_CIPHER_CTX_init(&ctx); 
    
    if (MCP_CipherInitEx(&ctx, algo, key, key_len, iv, iv_len, enc, sbh) <0)
    {
        printf("[MCP] Do Data Cipher failed - init cipher failed\n");        
        goto err_init_cipher_failed;
    }            
    
    if ((len = MCP_CipherUpdate(&ctx, data_in, len, data_out))<0)
    {
        printf("[MCP] Do Data Cipher failed - update cipher failed\n");        
        goto err_update_cipher_failed;
    }        
    
    out      +  len;
    data_out += len;
    
    if ((out = MCP_CipherFinal(&ctx, data_out))<0)
    {
        printf("[MCP] Do Data Cipher failed - finalize cipher failed\n");        
        goto err_finalize_cipher_failed;
    }       
    
    out += len;

    MCP_CIPHER_CTX_cleanup(&ctx);         
               
    return out;
    
///-----------------------
err_finalize_cipher_failed:        
err_update_cipher_failed:    
    MCP_CIPHER_CTX_cleanup(&ctx);        
err_init_cipher_failed:    
    return S_FALSE;       
}
