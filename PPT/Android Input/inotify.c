#include <stdio.h>   
#include <unistd.h>   
#include <sys/select.h>   
#include <errno.h>   
#include <sys/inotify.h>   
#include <sys/epoll.h>

typedef struct _mask_str{

    unsigned int mask;
    const unsigned char* maskString;

}MaskStr;

static MaskStr g_ErrStr[] = {

    {	IN_ACCESS		,"File was accessed"},
    {	IN_MODIFY		,"File was modified"},
    {	IN_ATTRIB		,"Metadata changed"},
    {	IN_CLOSE_WRITE		,"Writtable file was closed"},
    {	IN_CLOSE_NOWRITE        ,"Unwrittable file closed"},
    {	IN_OPEN                 ,"File was opened"},
    {	IN_MOVED_FROM           ,"File was moved from"},
    {	IN_MOVED_TO             ,"File was moved to Y"},
    {	IN_CREATE               ,"Subfile was created"},
    {	IN_DELETE               ,"Subfile was deleted"},
    {	IN_DELETE_SELF          ,"Self was deleted"}, 
    {	IN_MOVE_SELF            ,"Self was moved"}, 
    //the following are legal events.  they are sent as needed to any watch
    {	IN_UNMOUNT              ,"Backing fs was unmounted"},
    {	IN_Q_OVERFLOW           ,"Event queued overflowed"},
    {	IN_IGNORED              ,"File was ignored"},

    //special flags
    {	IN_ONLYDIR              ,"only watch the path if it is a directory"},
    {	IN_DONT_FOLLOW          ,"don't follow a sym link"},
    {	IN_EXCL_UNLINK          ,"exclude events on unlinked objects"},
    {	IN_MASK_ADD             ,"add to the mask of an already existing watch"},
    {	IN_ISDIR                ,"event occurred against dir"},
    {	IN_ONESHOT              ,"only send event once"},
};

#define OUT_MASK_TO_STRING(m) \
{\
    printf("\r\nwatch Evnet print begin: \r\n");\
    int i = 0;\
    for(; i<sizeof(g_ErrStr)/sizeof(g_ErrStr[0]); i++)\
    {	\
	if( ((m)&g_ErrStr[i].mask) == g_ErrStr[i].mask)\
	    printf(">>>[%d]:%s\r\n",i,g_ErrStr[i].maskString);\
    }\
    printf("\r\nwatch Evnet print end !\r\n");\
}\

#define MAXEVENTS 64

// helper events
//{IN_CLOSE                (IN_CLOSE_WRITE | IN_CLOSE_NOWRITE) //close
//{IN_MOVE                 (IN_MOVED_FROM | IN_MOVED_TO) //moves

static void   _inotify_event_handler(struct inotify_event *event)
{   
    printf("event->name: %s\n", event->name);   
    printf("event->mask: 0x%08x\n", event->mask);   
    OUT_MASK_TO_STRING(event->mask);
}   

int  main(int argc, char **argv)   
{   
    if (argc != 2) {   
	printf("Usage: %s [file/dir]\n", argv[0]);   
	return -1;   
    }   

    unsigned char buf[1024] = {0};   
    struct inotify_event *event = NULL;              


    int fd,wd,ep,s;
    fd = inotify_init();
    wd = inotify_add_watch(fd, argv[1], IN_ALL_EVENTS);
    if(wd < 0){ perror("Invaild"); close(fd); exit(0); }
    ep = epoll_create(8);
    if(ep < 0){ perror("Invaild"); close(fd); exit(0); }
    struct epoll_event epev;
    struct epoll_event *epevs;
    memset(&epev, 0, sizeof(epev));
    epev.data.fd = fd;
    epev.events = EPOLLIN | EPOLLOUT;
    s = epoll_ctl(ep, EPOLL_CTL_ADD, fd, &epev);
    if(s < 0){ perror("Invaild"); close(ep); close(fd); exit(0); }
    epevs = calloc (MAXEVENTS, sizeof epev);

    for (;;) 
    {   
#if 0
	fd_set fds;   
	FD_ZERO(&fds);                
	FD_SET(fd, &fds);   
	if (select(fd + 1, &fds, NULL, NULL, NULL) > 0) 
	{   
	    int len, index = 0;   
	    while (((len = read(fd, &buf, sizeof(buf))) < 0) && (errno == EINTR)); 
	    while (index < len) 
	    {   
		event = (struct inotify_event *)(buf + index);                      
		_inotify_event_handler(event);                                    
		index += sizeof(struct inotify_event) + event->len; 
	    }   
	}   
#else

	int i = 0,len = 0,index=0,n=0;
	n = epoll_wait (ep, epevs, MAXEVENTS, 1000);
	if(n <= 0) continue;
	for(i=0; i<n; ++i) 
	{
	    if( epevs[i].events & EPOLLIN )
	    {  
		while((len=read(fd, &buf, sizeof(buf))) < 0 && (errno == EINTR));
		while(index < len)
		{
		    event = (struct inotify_event *)(buf + index);                      
		    _inotify_event_handler(event);                                    
		    index += sizeof(struct inotify_event) + event->len; 
		}
	    }  
	    else if(epevs[i].events & EPOLLOUT)
	    {  
		//
		printf("EPOLLOUT\r\n");
	    }
	    else  
	    {  
		//
		printf("EPOLL OTHERS\r\n");
	    }  
	}   
#endif
    }

    inotify_rm_watch(fd, wd);
    close(fd);

    return 0;   
}  
