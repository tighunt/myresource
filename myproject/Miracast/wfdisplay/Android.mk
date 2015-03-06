LOCAL_PATH:= $(call my-dir)


ANDROID_TOP := ../../../../../
SRCTREE_TOP := ../../../$(ANDROID_TOP)

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
		spu/core/bigdigits.c			\
		spu/core/crypto.c				\
		spu/core/hmac.c					\
		spu/core/hdcp2_interface.c		\
		spu/core/hdcp2_session.c		\
		spu/core/hdcp2_message.c        \
		spu/crypto_spu.c				\
		spu/secure_flash.c				\
		spu/hdmi.c						\
		spu/rcp_api.cpp					\
        spu/spu_main.c					\


LOCAL_C_INCLUDES:= \
		bionic							\
		external/stlport/stlport		\
		external/openssl/include		\
        $(LOCAL_PATH)/spu				\
        $(LOCAL_PATH)/spu/core			\
        $(LOCAL_PATH)/spu/include		\
        ../../../../../../../../system/src/Include/Platform_Lib/SocketAVData

LOCAL_CFLAGS:= -DHDCP_REPEATER=0		\
			   -DHDCP2_KEY_PROTECT_WITH_KIMG \

ifeq ($(TARGET_NO_TVSERVER), true)
LOCAL_CFLAGS += -DNOT_INCLUDE_TVSERVER
endif

ifeq ($(TARGET_BOARD_PLATFORM), rtd298x)
LOCAL_CFLAGS += -DRUN_ON_SIRIUS
endif

ifeq ($(TARGET_BOARD_PLATFORM), rtd299x)
LOCAL_CFLAGS += -DRUN_ON_MAGELLAN
endif
			   
LOCAL_SHARED_LIBRARIES:= \
        libcutils                       \
        liblog                          \
		libstlport						\
		libc							\
		libstdc++						\
		libcrypto						\
		libRT_MediaPlayClient			\
        libutils                        \
		libbinder						\
        libFileDescService              \

LOCAL_MODULE:= libhdcp2

LOCAL_MODULE_TAGS:= optional

include $(BUILD_SHARED_LIBRARY)

################################################################################

include $(CLEAR_VARS)


LOCAL_C_INCLUDES:= \
		bionic							\
		external/stlport/stlport		\
		external/openssl/include		\
		${TOP}/frameworks/av/media/libmediaplayerservice \
		${TOP}/frameworks/base/include/RT_MediaPlayClient \
        $(LOCAL_PATH)/spu				\
        $(LOCAL_PATH)/spu/core			\
        $(LOCAL_PATH)/spu/include		\
        ../../../../../../../../system/src/Include/Platform_Lib/SocketAVData


LOCAL_SRC_FILES:= \
		deliver_info_to_dvdplayer.cpp	\
		systemCommand.cpp				\
		wifi_hdcp2.cpp					\
		wifi_rtsp_cmds.cpp				\
		wifi_rtsp.cpp					\
		RTKWifiDisplaySink.cpp			\

	    

LOCAL_CFLAGS:= -DHDCP_REPEATER=0		\

ifeq ($(TARGET_NO_TVSERVER), true)
LOCAL_CFLAGS += -DNOT_INCLUDE_TVSERVER
endif

ifeq ($(TARGET_BOARD_PLATFORM), rtd298x)
LOCAL_CFLAGS += -DRUN_ON_SIRIUS
endif

ifeq ($(TARGET_BOARD_PLATFORM), rtd299x)
LOCAL_CFLAGS += -DRUN_ON_MAGELLAN
endif

LOCAL_SHARED_LIBRARIES:= \
        libcutils                       \
        liblog                          \
		libstlport						\
		libc							\
		libstdc++						\
		libcrypto						\
		libhdcp2						\
		libRT_MediaPlayClient			\
        libutils                        \

LOCAL_MODULE:= libwfdisplay

LOCAL_MODULE_TAGS:= optional

include $(BUILD_SHARED_LIBRARY)

################################################################################

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
		rtk_wfd.cpp						\

LOCAL_C_INCLUDES:= \
		bionic							\
		external/stlport/stlport		\
		external/openssl/include		\
		${TOP}/frameworks/av/media/libmediaplayerservice \
        $(LOCAL_PATH)/spu				\
        $(LOCAL_PATH)/spu/core			\
        $(LOCAL_PATH)/spu/include		\
        ../../../../../../../../system/src/Include/Platform_Lib/SocketAVData

LOCAL_SHARED_LIBRARIES:= \
        libcutils                       \
        liblog                          \
		libstlport						\
		libc							\
		libstdc++						\
		libcrypto						\
		libhdcp2						\
		libwfdisplay					\
		libmediaplayerservice			\
		libRT_MediaPlayClient			\
        libutils                        \

LOCAL_CFLAGS:= -DHDCP_REPEATER=0		\

ifeq ($(TARGET_NO_TVSERVER), true)
LOCAL_CFLAGS += -DNOT_INCLUDE_TVSERVER
endif

ifeq ($(TARGET_BOARD_PLATFORM), rtd298x)
LOCAL_CFLAGS += -DRUN_ON_SIRIUS
endif

ifeq ($(TARGET_BOARD_PLATFORM), rtd299x)
LOCAL_CFLAGS += -DRUN_ON_MAGELLAN
endif

LOCAL_MODULE:= rtk_wfd

LOCAL_MODULE_TAGS := debug

include $(BUILD_EXECUTABLE)

################################################################################
