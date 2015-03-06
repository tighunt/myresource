LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_JAVA_LIBRARIES := bouncycastle
LOCAL_STATIC_JAVA_LIBRARIES := guava liblauncher

LOCAL_SRC_FILES := $(call all-java-files-under, src) $(call all-renderscript-files-under, src)

LOCAL_PACKAGE_NAME := HaierLauncher
LOCAL_CERTIFICATE := platform

LOCAL_OVERRIDES_PACKAGES := Launcher2

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := liblauncher:libs/TVapi.jar

include $(BUILD_MULTI_PREBUILT)
include $(call all-makefiles-under,$(LOCAL_PATH))
