#include "Threads.h"
#include <sys/timeb.h>
#include <assert.h>
#include <stdio.h>
#include <errno.h>
//#include <string.h>

enum {
    OSAL_OBJECT_TYPE_THREAD = 0x7ABC0000,
    OSAL_OBJECT_TYPE_MUTEX,
    OSAL_OBJECT_TYPE_EVENT,
    OSAL_OBJECT_TYPE_SEM
};

#ifdef WIN32

#ifndef _WIN32_WINNT
# define _WIN32_WINNT 0x500
#endif

/* OSAL thread related implementation for Windows */

#include <windows.h>
#include <process.h>
#undef S_OK
#undef E_FAIL
#undef S_FALSE
#include <hresult.h>	// for S_OK, E_FAIL, etc... to override WIN32 definition

/* thread */

typedef struct tag_win32_thread_t {
    int type;
    void (*start_address)(void*);
    void* arglist;
    unsigned thrdaddr;
    HANDLE handle;
} win32_thread_t;

unsigned __stdcall win32_start_address(
    void* arglist)
{
    win32_thread_t* pThis = (win32_thread_t*)arglist;
    pThis->start_address(pThis->arglist);
    return 0;
}

HRESULT osal_ThreadBegin(
    void (*start_address)(void*),
    void* arglist,
    unsigned long stack_size,
    long priority,
    osal_thread_t* pThreadID)
{
    assert(sizeof(osal_thread_t) >= sizeof(win32_thread_t));
    win32_thread_t* pThis = (win32_thread_t*)pThreadID;

    pThis->type = 0;
    pThis->start_address = start_address;
    pThis->arglist = arglist;

    pThis->handle = (HANDLE) _beginthreadex(
                        0, /* security attribute, not used */
                        (unsigned)stack_size,
                        win32_start_address,
                        (void*)pThis,
                        0, /* initflag, 0 means create a running thread */
                        &pThis->thrdaddr); /* thread identifier */

    /* @FIXME: priority parameter is NOT honored yet */

    if(pThis->handle == 0)
        return E_FAIL;

    pThis->type = OSAL_OBJECT_TYPE_THREAD;
    return S_OK;
}

HRESULT osal_ThreadEnd(
    osal_thread_t* pThreadID,
    long millisecondsMaxWait)
{
    win32_thread_t* pThis = (win32_thread_t*)pThreadID;

    /* wait for thread to terminate 'naturally' */
    DWORD waitRes = WaitForSingleObject(pThis->handle, millisecondsMaxWait);
    assert(waitRes != WAIT_ABANDONED);

    if(waitRes == WAIT_TIMEOUT)
        return E_FAIL; /* fatal error, should be handled by caller */

    CloseHandle(pThis->handle);
    pThis->type = 0;
    return S_OK;
}
HRESULT osal_ThreadDetach(
            osal_thread_t* pThreadID)
{
    return S_OK;
}

HRESULT osal_ThreadExit(void)
{
    _endthread();
    return S_OK;
}

void osal_Sleep(
    long millisecondsSleep)
{
    Sleep((DWORD)millisecondsSleep);
}

/* mutex */

typedef struct tag_win32_mutex_t {
    int type;
    CRITICAL_SECTION cs;
} win32_mutex_t;

HRESULT osal_MutexCreate(
    osal_mutex_t* pMutexID)
{
    assert(sizeof(osal_mutex_t) >= sizeof(win32_mutex_t));
    win32_mutex_t* pThis = (win32_mutex_t*)pMutexID;
    InitializeCriticalSection(&pThis->cs);
    pThis->type = OSAL_OBJECT_TYPE_MUTEX;
    return S_OK;
}

HRESULT osal_MutexLock(
    osal_mutex_t* pMutexID)
{
    win32_mutex_t* pThis = (win32_mutex_t*)pMutexID;
    EnterCriticalSection(&pThis->cs);
    return S_OK;
}

HRESULT osal_MutexTryLock(
    osal_mutex_t* pMutexID)
{
    win32_mutex_t* pThis = (win32_mutex_t*)pMutexID;
    // NOTE: TryEnterCriticalSection is available on WinNT/2K/XP and WinCE 3.0~ only
    return TryEnterCriticalSection(&pThis->cs)? S_OK : E_FAIL;
}

HRESULT osal_MutexUnlock(
    osal_mutex_t* pMutexID)
{
    win32_mutex_t* pThis = (win32_mutex_t*)pMutexID;
    LeaveCriticalSection(&pThis->cs);
    return S_OK;
}

HRESULT osal_MutexDestroy(
    osal_mutex_t* pMutexID)
{
    win32_mutex_t* pThis = (win32_mutex_t*)pMutexID;
    DeleteCriticalSection(&pThis->cs);
    pThis->type = 0;
    return S_OK;
}

/* event */

typedef struct tag_win32_event_t {
    int type;
    HANDLE handle;
} win32_event_t;

HRESULT osal_EventCreate(
    osal_event_t* pEventID)
{
    assert(sizeof(osal_event_t) >= sizeof(win32_event_t));
    win32_event_t* pThis = (win32_event_t*)pEventID;
    pThis->type = 0;

    pThis->handle = CreateEvent(
                        0, /* security attribute, not used */
                        TRUE, /* manual event reset */
                        FALSE, /* initial state - event is NOT signaled */
                        0); /* name, not used */

    if(pThis->handle == 0)
        return E_FAIL;

    pThis->type = OSAL_OBJECT_TYPE_EVENT;
    return S_OK;
}

HRESULT osal_EventSet(
    osal_event_t* pEventID)
{
    win32_event_t* pThis = (win32_event_t*)pEventID;
    SetEvent(pThis->handle);
    return S_OK;
}

HRESULT osal_EventReset(
    osal_event_t* pEventID)
{
    win32_event_t* pThis = (win32_event_t*)pEventID;
    ResetEvent(pThis->handle);
    return S_OK;
}

HRESULT osal_EventWait(
    osal_event_t* pEventID,
    long millisecondsTimeout)
{
    win32_event_t* pThis = (win32_event_t*)pEventID;
    DWORD waitRes = WaitForSingleObject(pThis->handle, millisecondsTimeout);
    assert(waitRes != WAIT_ABANDONED);
    return (waitRes == WAIT_TIMEOUT)? E_FAIL : S_OK;
}

HRESULT osal_EventDestroy(
    osal_event_t* pEventID)
{
    win32_event_t* pThis = (win32_event_t*)pEventID;
    CloseHandle(pThis->handle);
    pThis->type = 0;
    return S_OK;
}

/* semaphore */

typedef struct tag_win32_sem_t {
    int type;
    HANDLE handle;
} win32_sem_t;

HRESULT osal_SemCreate(
    unsigned int maxCount,
    unsigned int initialCount,
    osal_sem_t* pSemID)
{
    assert(sizeof(osal_sem_t) >= sizeof(win32_sem_t));
    win32_sem_t* pThis = (win32_sem_t*)pSemID;
    pThis->type = 0;

    pThis->handle = CreateSemaphore(
                        0, /* security attribute, not used */
                        initialCount,
                        maxCount,
                        0); /* name, not used */

    if(pThis->handle == 0)
        return E_FAIL;

    pThis->type = OSAL_OBJECT_TYPE_SEM;
    return S_OK;
}

HRESULT osal_SemGive(
    osal_sem_t* pSemID)
{
    win32_sem_t* pThis = (win32_sem_t*)pSemID;
    ReleaseSemaphore(pThis->handle, 1, 0);
    return S_OK;
}

HRESULT osal_SemWait(
    osal_sem_t* pSemID,
    long millisecondsTimeout)
{
    win32_sem_t* pThis = (win32_sem_t*)pSemID;
    DWORD waitRes = WaitForSingleObject(pThis->handle, millisecondsTimeout);
    assert(waitRes != WAIT_ABANDONED);
    return (waitRes == WAIT_TIMEOUT)? E_FAIL : S_OK;
}

HRESULT osal_SemSetCount(
    osal_sem_t* pSemID,
    unsigned int count)
{
    /* @FIXME: Win32 semaphore doesn't support set-count, find other way ... */
    assert(false);
    return E_FAIL;
}

HRESULT osal_SemDestroy(
    osal_sem_t* pSemID)
{
    win32_sem_t* pThis = (win32_sem_t*)pSemID;
    CloseHandle(pThis->handle);
    pThis->type = 0;
    return S_OK;
}

#else

/* OSAL thread related implementation for pthread based system (Linux,...etc) */

#include <pthread.h>
#include <unistd.h>
#include <hresult.h>
#include <sys/time.h>
#include <errno.h>
#include <sys/time.h>
#include <sys/resource.h>

/* thread */

typedef struct tag_posix_thread_t {
    int type;
    void (*start_address)(void*);
    void* arglist;
    pthread_t handle;
    osal_event_t end_event;
} posix_thread_t;

void* posix_start_address(
    void* arglist)
{
    posix_thread_t* pThis = (posix_thread_t*)arglist;
    pThis->start_address(pThis->arglist);
    osal_EventSet(&pThis->end_event);
    return 0;
}

HRESULT osal_ThreadBegin(
    void (*start_address)(void*),
    void* arglist,
    unsigned long stack_size,
    long priority,
    osal_thread_t* pThreadID)
{
    assert(sizeof(osal_thread_t) >= sizeof(posix_thread_t));
    posix_thread_t* pThis = (posix_thread_t*)pThreadID;

    pThis->type = 0;
    pThis->start_address = start_address;
    pThis->arglist = arglist;
    osal_EventCreate(&pThis->end_event);

    pthread_attr_t attr;
    //memset(&attr, 0, sizeof(pthread_attr_t));
    pthread_attr_init(&attr);

#ifndef _MIPSEL_LINUX 
    if(stack_size > 0) /* 0 means to use default stack size */
        pthread_attr_setstacksize(&attr, stack_size);
#endif
    int err = pthread_create(
                  &pThis->handle,
                  &attr,
                  posix_start_address,
                  (void*)pThis);

    /* @FIXME: priority parameter is NOT honored yet */

    pthread_attr_destroy(&attr);

    if(err != 0) {
        osal_EventDestroy(&pThis->end_event);
        return E_FAIL;
    }

    pThis->type = OSAL_OBJECT_TYPE_THREAD;
    return S_OK;
}

HRESULT osal_ThreadEnd(
    osal_thread_t* pThreadID,
    long millisecondsMaxWait)
{
    posix_thread_t* pThis = (posix_thread_t*)pThreadID;

    /* wait for thread to terminate 'naturally' (with timeout) */
    if(millisecondsMaxWait >= 0) {
        if(osal_EventWait(&pThis->end_event, millisecondsMaxWait) != S_OK)
            return E_FAIL; /* fatal error, should be handled by caller */
    }

    pthread_join(pThis->handle, 0);
    osal_EventDestroy(&pThis->end_event);
    pThis->type = 0;
    return S_OK;
}
HRESULT osal_ThreadDetach(
            osal_thread_t* pThreadID)
{
    posix_thread_t* pThis = (posix_thread_t*)pThreadID;
    
    //pthread_detach(pThis->handle);
    return S_OK;
}

HRESULT osal_ThreadCancel(
    osal_thread_t* pThreadID)
{
    posix_thread_t* pThis = (posix_thread_t*)pThreadID;
    //pthread_cancel(pThis->handle);
    return S_OK;
}

HRESULT osal_ThreadExit(void)
{
    pthread_exit(NULL);
    return S_OK;
}

void osal_Sleep(
    long millisecondsSleep)
{
    usleep(millisecondsSleep*1000);
}

/* mutex */

typedef struct tag_posix_mutex_t {
    int type;
    pthread_mutex_t handle;
  int lockCount;
} posix_mutex_t;


// create timed mutex, might be slower. Change to PTHREAD_MUTEX_FAST in some cases 
HRESULT osal_MutexCreate(
    osal_mutex_t* pMutexID)
{
    assert(sizeof(osal_mutex_t) >= sizeof(posix_mutex_t));
    posix_mutex_t* pThis = (posix_mutex_t*)pMutexID;

    pthread_mutexattr_t attr;
    //memset(&attr, 0, sizeof(pthread_mutexattr_t));
    pthread_mutexattr_init(&attr);
    pthread_mutexattr_settype(&attr, PTHREAD_MUTEX_ERRORCHECK);
    pthread_mutex_init(&pThis->handle, &attr);
    pthread_mutexattr_destroy(&attr);

    pThis->type = OSAL_OBJECT_TYPE_MUTEX;
    pThis->lockCount = 0;
    return S_OK;
}

HRESULT osal_MutexLock(
    osal_mutex_t* pMutexID)
{
  int rc;
    posix_mutex_t* pThis = (posix_mutex_t*)pMutexID;
    rc = pthread_mutex_lock(&pThis->handle);
    if (rc == 0 || rc == EDEADLK){// EDEADLK : The current thread already owns the mutex.
      pThis->lockCount++; // to count recursive mutex in the thread
      return S_OK;
    }
    else {
      assert(0);
      return E_FAIL;
    }
}

HRESULT osal_MutexTimedLock(
			      osal_mutex_t* pMutexID,
			      int nseconds
			      )
{
    /*int      rc;
    struct timespec abstime;
    unsigned long temp;
    struct timeb tb;
    posix_mutex_t* pThis = (posix_mutex_t*)pMutexID;

    ftime(&tb);
    temp = nseconds + tb.millitm * 1000000;
    if (temp >= 1000000000){
      abstime.tv_sec  = tb.time+1;
      abstime.tv_nsec = temp - 1000000000;    
    }
    else {
      abstime.tv_sec  = tb.time;
      abstime.tv_nsec = temp;  
    }

    rc = pthread_mutex_timedlock(&pThis->handle, &abstime);
    if (rc == 0 || rc == EDEADLK){// EDEADLK : The current thread already owns the mutex.
      pThis->lockCount++; // to count recursive mutex in the thread
      return S_OK;
    }
    else if (rc == ETIMEDOUT){
      printf("MutexTimedLock timeout\n");
      return S_FALSE;
    }
    else {
      return E_FAIL;
    }*/

	return E_FAIL;
	
}

HRESULT osal_MutexLock_BoostPriority(
    osal_mutex_t* pMutexID, int priority)
{
    /*posix_mutex_t* pThis = (posix_mutex_t*)pMutexID;
    int orig_priority;
    orig_priority = getpriority(PRIO_PROCESS, 0);
    setpriority(PRIO_PROCESS, 0, priority);
    pthread_mutex_lock(&pThis->handle);
    setpriority(PRIO_PROCESS, 0, orig_priority);*/
    return S_OK;
}

HRESULT osal_MutexTryLock(
    osal_mutex_t* pMutexID)
{
  /*int rc;
    posix_mutex_t* pThis = (posix_mutex_t*)pMutexID;
    rc = pthread_mutex_trylock(&pThis->handle);
    if (rc == 0 || rc == EDEADLK){
      pThis->lockCount++; // to count recursive mutex in the thread
      return S_OK;
    }
    else {
      return E_FAIL;
    }*/

	return E_FAIL;
}

HRESULT osal_MutexUnlock(
    osal_mutex_t* pMutexID)
{
    /*posix_mutex_t* pThis = (posix_mutex_t*)pMutexID;
    pThis->lockCount--; // handle recursive lock.
    assert(pThis->lockCount >= 0);
    if (pThis->lockCount == 0){
    pthread_mutex_unlock(&pThis->handle);
    }

	*/
    return S_OK;
}

HRESULT osal_MutexDestroy(
    osal_mutex_t* pMutexID)
{
/*
    posix_mutex_t* pThis = (posix_mutex_t*)pMutexID;
    pthread_mutex_unlock(&pThis->handle);
    pthread_mutex_destroy(&pThis->handle);
    pThis->type = 0;
	*/
    return S_OK;
}

/* common wait routine used by event and semaphore */

int posix_cond_wait(
    pthread_cond_t* cond,
    pthread_mutex_t* mutex,
    long millisecondsTimeout,
    struct timespec* abs_timeout)
{
  int err;
    /* not expected to be called when there is no need to wait */
    assert(millisecondsTimeout != 0);

    if(millisecondsTimeout < 0) { /* wait infinitely */
        return pthread_cond_wait(cond, mutex);
    }
    else { /* wait for specified number of milliseconds */

        if(abs_timeout->tv_sec == 0) {
            struct timeval cur;
            gettimeofday(&cur, 0);
            abs_timeout->tv_sec = cur.tv_sec + millisecondsTimeout/1000;
            abs_timeout->tv_nsec = cur.tv_usec*1000 +
                                   (millisecondsTimeout%1000)*1000000;
        }

        err = pthread_cond_timedwait(cond, mutex, abs_timeout);
	if (err != 0){
	  //	  perror("This");
	  //	  printf("errno = %d\n", err);
	}
	return err;
    }
}

/* event */

typedef struct tag_posix_event_t {
    int type;
    int state;
    pthread_mutex_t mutex;
    pthread_cond_t cond;
} posix_event_t;

HRESULT osal_EventCreate(
    osal_event_t* pEventID)
{
    assert(sizeof(osal_event_t) >= sizeof(posix_event_t));
    posix_event_t* pThis = (posix_event_t*)pEventID;

    pthread_mutex_init(&pThis->mutex, 0);
    pthread_cond_init(&pThis->cond, 0);

    pThis->state = 0;
    pThis->type = OSAL_OBJECT_TYPE_EVENT;
    return S_OK;
}

HRESULT osal_EventTest(
    osal_event_t* pEventID)
{
    posix_event_t* pThis = (posix_event_t*)pEventID;
    pthread_mutex_lock(&pThis->mutex);
    HRESULT res = pThis->state ? S_OK : E_FAIL;
    pthread_mutex_unlock(&pThis->mutex);
    return res;
}

HRESULT osal_EventSet(
    osal_event_t* pEventID)
{
    posix_event_t* pThis = (posix_event_t*)pEventID;
    pthread_mutex_lock(&pThis->mutex);

    if(pThis->state == 0) {
        pThis->state = 1;
        pthread_cond_broadcast(&pThis->cond);
    }

    pthread_mutex_unlock(&pThis->mutex);
    return S_OK;
}

HRESULT osal_EventReset(
    osal_event_t* pEventID)
{
    posix_event_t* pThis = (posix_event_t*)pEventID;
    pthread_mutex_lock(&pThis->mutex);
    pThis->state = 0;
    pthread_mutex_unlock(&pThis->mutex);
    return S_OK;
}

HRESULT osal_EventWait(
    osal_event_t* pEventID,
    long millisecondsTimeout)
{
    posix_event_t* pThis = (posix_event_t*)pEventID;
    struct timespec abs_timeout;
    abs_timeout.tv_sec = 0;

    pthread_mutex_lock(&pThis->mutex);

    while(pThis->state == 0 && millisecondsTimeout != 0) {

        int waitRes = posix_cond_wait(&pThis->cond,
                                      &pThis->mutex,
                                      millisecondsTimeout,
                                      &abs_timeout);

        if(waitRes != 0) {
	  //assert(waitRes == ETIMEDOUT);
	  break;
        }
    }

    HRESULT res = (pThis->state==0)? E_FAIL : S_OK;
    pthread_mutex_unlock(&pThis->mutex);
    return res;
}

HRESULT osal_EventDestroy(
    osal_event_t* pEventID)
{
    posix_event_t* pThis = (posix_event_t*)pEventID;
    pthread_cond_destroy(&pThis->cond);
    pthread_mutex_destroy(&pThis->mutex);
    pThis->type = 0;
    return S_OK;
}

/* semaphore */

typedef struct tag_posix_sem_t {
    int type;
    unsigned int count;
    unsigned int max;
    pthread_mutex_t mutex;
    pthread_cond_t cond;
} posix_sem_t;

HRESULT osal_SemCreate(
    unsigned int maxCount,
    unsigned int initialCount,
    osal_sem_t* pSemID)
{
    assert(maxCount > 0 && initialCount <= maxCount);
    assert(sizeof(osal_sem_t) >= sizeof(posix_sem_t));
    posix_sem_t* pThis = (posix_sem_t*)pSemID;

    pthread_mutex_init(&pThis->mutex, 0);
    pthread_cond_init(&pThis->cond, 0);

    pThis->count = initialCount;
    pThis->max = maxCount;
    pThis->type = OSAL_OBJECT_TYPE_SEM;
    return S_OK;
}

HRESULT osal_SemGive(
    osal_sem_t* pSemID)
{
    posix_sem_t* pThis = (posix_sem_t*)pSemID;
    pthread_mutex_lock(&pThis->mutex);

    assert(pThis->count <= pThis->max);
    if(pThis->count == pThis->max) {
        pthread_mutex_unlock(&pThis->mutex);
        return E_FAIL;
    }

    pThis->count ++;
    if(pThis->count == 1)
        pthread_cond_signal(&pThis->cond);

    pthread_mutex_unlock(&pThis->mutex);
    return S_OK;
}

HRESULT osal_SemWait(
    osal_sem_t* pSemID,
    long millisecondsTimeout)
{
    posix_sem_t* pThis = (posix_sem_t*)pSemID;
    struct timespec abs_timeout;
    abs_timeout.tv_sec = 0;

    pthread_mutex_lock(&pThis->mutex);

    while(pThis->count == 0 && millisecondsTimeout != 0) {

        int waitRes = posix_cond_wait(&pThis->cond,
                                      &pThis->mutex,
                                      millisecondsTimeout,
                                      &abs_timeout);

        if(waitRes != 0) {
	  //assert(waitRes == ETIMEDOUT);
	  break;
        }
    }

    HRESULT res;
    if(pThis->count > 0) {
        pThis->count --;
        res = S_OK;
    }
    else {
        res = E_FAIL;
    }

    pthread_mutex_unlock(&pThis->mutex);
    return res;
}

HRESULT osal_SemTryWait(
    osal_sem_t* pSemID)
{
    posix_sem_t* pThis = (posix_sem_t*)pSemID;
    pthread_mutex_lock(&pThis->mutex);

    HRESULT res;
    if(pThis->count > 0) {
        pThis->count --;
        res = S_OK;
    }
    else {
        res = E_FAIL;
    }

    pthread_mutex_unlock(&pThis->mutex);
    return res;
}

HRESULT osal_SemTest(
    osal_sem_t* pSemID)
{
    posix_sem_t* pThis = (posix_sem_t*)pSemID;
    pthread_mutex_lock(&pThis->mutex);
    HRESULT res = pThis->count > 0 ? S_OK : E_FAIL;
    pthread_mutex_unlock(&pThis->mutex);
    return res;
}

HRESULT osal_SemSetCount(
    osal_sem_t* pSemID,
    unsigned int count)
{
    posix_sem_t* pThis = (posix_sem_t*)pSemID;
    pthread_mutex_lock(&pThis->mutex);
    pThis->count = count;
    assert(pThis->count <= pThis->max);
    pthread_cond_signal(&pThis->cond);
    pthread_mutex_unlock(&pThis->mutex);
    return S_OK;
}

HRESULT osal_SemDestroy(
    osal_sem_t* pSemID)
{
    posix_sem_t* pThis = (posix_sem_t*)pSemID;
    pthread_cond_destroy(&pThis->cond);
    pthread_mutex_destroy(&pThis->mutex);
    pThis->type = 0;
    return S_OK;
}

#endif
