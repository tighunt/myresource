#include <stdio.h>
#include <stdlib.h>
#include <stddef.h>

#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/stat.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/time.h>
#include <time.h>
#include <pthread.h>
#include <directfb.h>

#include "wifi_rtsp.h"
#include "wifi_rtsp_cmds.h"

#define MAX_KEYBOARD_REPORT            6
static unsigned short pending_keys[MAX_KEYBOARD_REPORT];
static bool shift_down = false;
static bool alt_down = false;
static bool ctrl_down = false;

// return HID usage ID
static char translate_ascii_hid(unsigned short ascii) 
{
    if (DFB_KEY_TYPE(ascii) == DIKT_FUNCTION) {
      return (0x3a + (ascii - DIKS_F1));
    }

    if (DFB_KEY_TYPE(ascii) == DIKT_SPECIAL) {
      if (ascii == DIKS_CURSOR_LEFT) 
	return 0x50;
      if (ascii == DIKS_CURSOR_RIGHT)
	return 0x4f;
      if (ascii == DIKS_CURSOR_UP)
	return 0x52;
      if (ascii == DIKS_CURSOR_DOWN)
	return 0x51;
      if (ascii == DIKS_INSERT)
	return 0x49;
      if (ascii == DIKS_HOME)
	return 0x4a;
      if (ascii == DIKS_END) 
	return 0x4d;
      if (ascii == DIKS_PAGE_UP)
	return 0x4b;
      if (ascii == DIKS_PAGE_DOWN)
	return 0x4e;
      return 0x00;
    }

    if (DFB_KEY_TYPE(ascii) == DIKT_UNICODE) {
        if (ascii >= 'a' && ascii <= 'z') 
	    return (ascii - 'a' + 4);
    
	if (ascii >= 'A' && ascii <= 'Z') 
	  return (ascii - 'A' + 4);
    
	if (ascii >= '1' && ascii <= '9') 
	  return (ascii - '1' + 0x1e);
    
	if (ascii == '0' || ascii == ')')
	  return 0x27;
	if (ascii == '(')
	  return 0x26;
	if (ascii == '*')
	  return 0x25;
	if (ascii == '&')
	  return 0x24;
	if (ascii == '^')
	  return 0x23;
	if (ascii == '%')
	  return 0x22;
	if (ascii == '$')
	  return 0x21;
	if (ascii == '#')
	  return 0x20;
	if (ascii == '@')
	  return 0x1f;
	if (ascii == '!')
	  return 0x1e;
	if (ascii == 0x09) // tab
	  return 0x2b;
	if (ascii == 0x08) // backspace
	  return 0x2a;
	if (ascii == 0x0d) // newline
	  return 0x28;
	if (ascii == 0x1b) // escape
	  return 0x29;
	if (ascii == 127) // DEL
	  return 0x4c;
	if (ascii == ' ') // space
	  return 0x2c;
	
	if (ascii == '-' || ascii == '_') 
	  return 0x2d;
	if (ascii == '=' || ascii == '+')
	  return 0x2e;
	if (ascii == '[' || ascii == '{')
	  return 0x2f;
	if (ascii == ']' || ascii == '}')
	  return 0x30;
	if (ascii == '\\' || ascii == '|')
	  return 0x31;
	if (ascii == ';' || ascii == ':')
	  return 0x33;
	if (ascii == '\'' || ascii == '"')
	  return 0x34;
	if (ascii == '`' || ascii == '~')
	  return 0x35;
	if (ascii == ',' || ascii == '<')
	  return 0x36;
	if (ascii == '.' || ascii == '>')
	  return 0x37;
	if (ascii == '/' || ascii == '?') 
	  return 0x38;
    }
    return 0;
}


void hid_keyboard_init(void) 
{
    for (int i=0; i<MAX_KEYBOARD_REPORT; i++) 
        pending_keys[i] = 0;
    shift_down = false;
    alt_down = false;
    ctrl_down = false;
}

void send_hid_keyboard(struct wifi_source *src, unsigned char type, unsigned short ascii) 
{
    if (DFB_KEY_TYPE(ascii) == DIKT_MODIFIER) {
      if (ascii == DIKS_SHIFT) {
	if (type == KEYBOARD_DOWN) 
	  shift_down = true;
	else 
	  shift_down = false;
      }
      if (ascii == DIKS_CONTROL) {
	if (type == KEYBOARD_DOWN)
	  ctrl_down = true;
	else
	  ctrl_down = false;
      }
      if (ascii == DIKS_ALT) {
	if (type == KEYBOARD_DOWN) 
	  alt_down = true;
	else
	  alt_down = false;
      }
      return;
    }

    char kb_report[MAX_KEYBOARD_REPORT + 2 + 5];
    unsigned short len = (MAX_KEYBOARD_REPORT + 2);
    len = htons(len);

    if (type == KEYBOARD_DOWN) {
        for (int i=0; i<MAX_KEYBOARD_REPORT; i++) {
            if (pending_keys[i] == 0) {
                pending_keys[i] = ascii;
		break;
	    }
        }
    }
    else if (type == KEYBOARD_UP) {
        for (int i=0; i<MAX_KEYBOARD_REPORT; i++) {
            if (pending_keys[i] == ascii) 
                pending_keys[i] = 0;
        }
    }
    else 
        return;
    
    kb_report[0] = 1;   // usb
    kb_report[1] = 0;   // mouse
    kb_report[2] = 0;   // contains report    
    memcpy(kb_report+3, &len, sizeof(len));
        // next, keyboard modifier
    kb_report[5] = 0;
    if (shift_down) 
        kb_report[5] |= 0x02;
    if (alt_down)
        kb_report[5] |= 0x04;
    if (ctrl_down) 
        kb_report[5] |= 0x01;
        // reserved byte
    kb_report[6] = 0;
    
    int stindex = 7;
    for (int i=0; i<MAX_KEYBOARD_REPORT; i++) {
        kb_report[i + 7] = 0x00;
        if (pending_keys[i] != 0) {
            char hidch = translate_ascii_hid(pending_keys[i]);
            if (hidch != 0) {
                kb_report[stindex] = hidch;
                stindex ++;
            }   
        }    
    }
    wifi_uibc_event(INPUT_HIDC, (unsigned char*) kb_report, MAX_KEYBOARD_REPORT + 2 + 5);

    //write(src->uibcfd, kb_report, MAX_KEYBOARD_REPORT + 2 + 5);
}
