LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDFLAGS := -Wl,-rpath-link=/home/dmitrydzz/android-ndk/platforms/android-14/arch-arm/usr/lib/ -rpath-link=/home/dmitrydzz/android-ndk/platforms/android-14/arch-arm/usr/lib/
LOCAL_MODULE := libavformat
LOCAL_SRC_FILES := ../obj/local/armeabi/libavformat.a
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon #надо именно эти 3 параметра! Нашёл это в android-ndk/docs/STANDALONE-TOOLCHAIN.html
LOCAL_LDLIBS := -lz -lm -llog -lc -L$(call host-path, $(LOCAL_PATH))/$(TARGET_ARCH_ABI) -landprof

include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_LDFLAGS := -Wl,-rpath-link=/home/dmitrydzz/android-ndk/platforms/android-14/arch-arm/usr/lib/ -rpath-link=/home/dmitrydzz/android-ndk/platforms/android-14/arch-arm/usr/lib/
LOCAL_MODULE := libavcodec
LOCAL_SRC_FILES := ../obj/local/armeabi/libavcodec.a
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon
LOCAL_LDLIBS := -lz -lm -llog -lc -L$(call host-path, $(LOCAL_PATH))/$(TARGET_ARCH_ABI) -landprof

include $(PREBUILT_STATIC_LIBRARY)
include $(CLEAR_VARS)

LOCAL_MODULE := libpostproc
LOCAL_SRC_FILES := ../obj/local/armeabi/libpostproc.a
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon

include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := libswscale
LOCAL_SRC_FILES := ../obj/local/armeabi/libswscale.a
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon

include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := libavutil
LOCAL_SRC_FILES := ../obj/local/armeabi/libavutil.a
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon

include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_LDLIBS += -llog -lz
LOCAL_STATIC_LIBRARIES := libavformat libavcodec libpostproc libswscale libavutil
LOCAL_C_INCLUDES += $(LOCAL_PATH)/ffmpeg
LOCAL_SRC_FILES := ffmpegproxy.c
LOCAL_CFLAGS := -march=armv7-a -mfloat-abi=softfp -mfpu=neon
LOCAL_MODULE := ffmpegproxy

include $(BUILD_SHARED_LIBRARY)

LOCAL_PATH := $(call my-dir)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/ffmpeg
include $(all-subdir-makefiles)