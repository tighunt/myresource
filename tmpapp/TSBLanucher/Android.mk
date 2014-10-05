#
# Copyright (C) 2008 The Android Open Source Project
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
LOCAL_STATIC_JAVA_LIBRARIES := android-common android-support-v13
LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform
# This is the target being built.
LOCAL_PACKAGE_NAME := TSBLauncher

# Only compile source java files in this apk.
LOCAL_SRC_FILES := $(call all-java-files-under, src/com) \
$(call all-java-files-under, src/android/widget)

LOCAL_CERTIFICATE := platform
# Link against the current Android SDK.
# LOCAL_SDK_VERSION := current

include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))