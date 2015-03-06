# Copyright (C) 2011 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)  
LOCAL_MODULE := libwfdisplay 
LOCAL_SRC_FILES := ../libs/libwfdisplay.so
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE    := libnative-media-jni
LOCAL_SRC_FILES := com_pluses_wifidisplay_WifiDisplayEngine.cpp \
		RingBuffer.c    \
		MiracastIo.c    \


LOCAL_C_INCLUDES:= \
        bionic                                 \
        external/stlport/stlport                \
        $(LOCAL_PATH)/include

# for native multimedia
#LOCAL_LDLIBS    +=-Llib -lOpenMAXAL
# for logging
#LOCAL_LDLIBS    += -llog
# for native windows
#LOCAL_LDLIBS    += -landroid

LOCAL_CFLAGS    += -UNDEBUG

LOCAL_SHARED_LIBRARIES := libwfdisplay libgabi++ liblog libcutils libOpenMAXAL libandroid libandroid_runtime libnativehelper libutils libbinder

include $(BUILD_SHARED_LIBRARY)
include $(call all-makefiles-under,$(LOCAL_PATH))
