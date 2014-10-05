LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_OVERRIDE_SRC_FILES := $(call all-java-files-under, $(TARGET_DEVICE))

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := RealtekOverscan
#LOCAL_JNI_SHARED_LIBRARIES :=
LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_SRC_FILES := $(foreach f,$(LOCAL_SRC_FILES), \
   $(if $(findstring $(notdir $(f)),$(LOCAL_OVERRIDE_SRC_FILES)),,$(f)))

LOCAL_SRC_FILES += $(LOCAL_OVERRIDE_SRC_FILES)


include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))
