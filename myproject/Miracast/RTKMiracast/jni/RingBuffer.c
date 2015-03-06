
#include "include/RingBuffer.h"
#include <string.h>
#include <android/log.h>
#define TAG "RingBuffer"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

    unsigned int m_wp = 0;
    unsigned int m_rp = 0;
    unsigned int m_size = 100000*188;
    unsigned char m_data[100000*188];//18M
    unsigned int m_totalLength = 0;

    static const unsigned int INVALID_WP = 0xFFFFFFFF;

	void buffer_clear()
		{
		    m_wp = 0;
    		m_rp = 0;
		}

unsigned int space()
{
	unsigned int rp_snapshot = m_rp;

    if(rp_snapshot > m_wp)
        return rp_snapshot - m_wp - 1;
    else
        return m_size + rp_snapshot - m_wp - 1;
}

unsigned int used_size()
{
    return m_size - space();
}


int buffer_write( unsigned char* data, unsigned int size)
{
	int space1 = space();
	LOGE("write: space = %d", space1);
    if(space1 < size)
    {
    	//LOGE("[RingBuffer]: not enough space to write!\n");
        return 0; // not enough space in ring buffer
    }

    if(m_wp + size >= m_size) // wrap-around
    {
    	unsigned int half = m_size - m_wp;

        memcpy(&m_data[m_wp], data, half);
        size -= half;

        if(size > 0)
            memcpy(&m_data[0], &data[half], size);

        m_wp = size;
    }
    else if(size > 0)
    {
        memcpy(&m_data[m_wp], data, size);
        m_wp += size;
    }

	//RB_DBG("### write wp = %d, rp = %d ###\n", m_wp, m_rp);

    return 1;
}

int buffer_read(unsigned char* data, unsigned int* size ,unsigned int block,unsigned int must)
{
	unsigned int rp = m_rp;
	unsigned int wp = m_wp;

	unsigned int length = 0;

	if (wp >= rp)
		length = wp - rp;
	else
		length = m_size - rp + wp;

	if (length == 0)
	{
		//RB_DBG("[RingBuffer]: WARN: no data to read!\n");
		*size = 0;
		//LOGE("read 0 length = %d",length);
		return 0;
	}

	if (*size > length)
	{
		if(must == 1 || length <block)
			return 0;
		//LOGE("[RingBuffer]: WARN: not enough size to read!\n");
		*size = length - (length%block);
	}

	//LOGE("[RingBuffer]: read size:%d\n", *size);

    if (wp > rp)
    {
		memcpy(data, &m_data[rp], *size);
		m_rp += *size;
    }
    else
    {
		if (rp + *size > m_size)
		{
			unsigned int half = m_size - rp;
			memcpy(data, &m_data[rp], half);
			memcpy(data+half, &m_data[0], *size - half);
			m_rp = *size - half;
		}
		else
		{
			memcpy(data, &m_data[rp], *size);
			m_rp += *size;
		}
    }

    //LOGE("### [Ringbuffer]: read wp:%d, rp:%d, size:%d ###\n", m_wp, m_rp, size);
    //LOGE("read data length = %d",*size);
	return 1;
}
