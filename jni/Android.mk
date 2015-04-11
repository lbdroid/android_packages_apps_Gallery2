LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS += -DEGL_EGLEXT_PROTOTYPES

LOCAL_SRC_FILES := jni_egl_fence.cpp

LOCAL_SDK_VERSION := 9

LOCAL_MODULE_TAGS := optional

LOCAL_MODULE := libjni_eglfence

LOCAL_LDLIBS := -llog -lEGL


include $(BUILD_SHARED_LIBRARY)

# Filtershow

include $(CLEAR_VARS)

LOCAL_CPP_EXTENSION := .cc
LOCAL_SDK_VERSION := 9
LOCAL_MODULE    := libjni_filtershow_filters
LOCAL_SRC_FILES := filters/gradient.c \
                   filters/saturated.c \
                   filters/exposure.c \
                   filters/edge.c \
                   filters/contrast.c \
                   filters/hue.c \
                   filters/shadows.c \
                   filters/highlight.c \
                   filters/hsv.c \
                   filters/vibrance.c \
                   filters/geometry.c \
                   filters/negative.c \
                   filters/redEyeMath.c \
                   filters/fx.c \
                   filters/wbalance.c \
                   filters/redeye.c \
                   filters/bwfilter.c \
                   filters/tinyplanet.cc \
                   filters/kmeans.cc

LOCAL_CFLAGS    += -ffast-math -O3 -funroll-loops
LOCAL_LDLIBS := -llog -ljnigraphics
LOCAL_ARM_MODE := arm

include $(BUILD_SHARED_LIBRARY)

# Jpeg Streaming native
include $(CLEAR_VARS)

LOCAL_MODULE        := libjni_jpegstream

LOCAL_NDK_STL_VARIANT := stlport_static

LOCAL_C_INCLUDES := $(LOCAL_PATH) \
                    $(LOCAL_PATH)/jpegstream \
                    external/jpeg

LOCAL_SHARED_LIBRARIES := libjpeg

LOCAL_SDK_VERSION   := 9
LOCAL_ARM_MODE := arm

LOCAL_CFLAGS    += -ffast-math -O3 -funroll-loops
LOCAL_CPPFLAGS += $(JNI_CFLAGS)
LOCAL_LDLIBS := -llog

LOCAL_CPP_EXTENSION := .cpp
LOCAL_SRC_FILES     := \
    jpegstream/inputstream_wrapper.cpp \
    jpegstream/jpegstream.cpp \
    jpegstream/jerr_hook.cpp \
    jpegstream/jpeg_hook.cpp \
    jpegstream/jpeg_writer.cpp \
    jpegstream/jpeg_reader.cpp \
    jpegstream/outputstream_wrapper.cpp \
    jpegstream/stream_wrapper.cpp


include $(BUILD_SHARED_LIBRARY)

include $(LOCAL_PATH)/libjpeg-turbo/Android.mk