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
void buffer_clear();
unsigned int used_size();
int buffer_write(unsigned char* data, unsigned int size);
int buffer_read(unsigned char* data, unsigned int* size ,unsigned int block,unsigned int must);
#endif // IPANEL_RINGBUFFER_H
