/*
 * (c) 2010- , Inc.
 *
 *
 *
 *
 *
 */

#ifndef RINGBUFFER_H
#define RINGBUFFER_H

void init();

unsigned int space();
void clear();
unsigned int used_size();
int write(unsigned char* data, unsigned int size);
int read(unsigned char* data, unsigned int* size ,unsigned int block,unsigned int must);
#endif // IPANEL_RINGBUFFER_H
