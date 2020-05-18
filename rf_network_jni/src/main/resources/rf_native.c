#include "br_com_diegosilva_rfnative_RfNative.h"
#include <stdio.h>
 
#define DEFAULT_RET "HELLO WORLD !!!"
 
JNIEXPORT jstring JNICALL Java_br_com_diegosilva_rfnative_RfNative_getString
  (JNIEnv *env, jobject thiz) {
    char* ret = DEFAULT_RET;
    return (*env).NewStringUTF(ret);
}