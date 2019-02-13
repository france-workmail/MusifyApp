//
// Created by France Gelasque on 2/13/19.
//
#include <jni.h>

extern "C" JNIEXPORT jstring JNICALL
Java_france_apps_musify_MusifyApplication_invokeNativeFunction(JNIEnv *env, jobject instance) {
    return env->NewStringUTF("someVeryImportantKeys2d3==");
}
