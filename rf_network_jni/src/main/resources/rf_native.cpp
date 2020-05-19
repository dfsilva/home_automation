#include "br_com_diegosilva_rfnative_RfNative.h"
#include <stdio.h>
#include <RF24/RF24.h>
#include <RF24Network/RF24Network.h>
#include <iostream>
#include <ctime>
#include <stdio.h>
#include <time.h>

RF24 radio(RPI_V2_GPIO_P1_15, BCM2835_SPI_CS0, BCM2835_SPI_SPEED_8MHZ);

RF24Network network(radio);

JNIEXPORT void JNICALL Java_br_com_diegosilva_rfnative_RfNative_start
  (JNIEnv *env, jobject thiz, jint node){
    radio.begin();
    radio.setDataRate(RF24_250KBPS);
    delay(5);
    network.begin(90, node);
    radio.printDetails();

    jclass thisClass = env->GetObjectClass(thiz);
    jmethodID onReceive = env->GetMethodID(thisClass, "onReceive", "(Ljava/lang/String;)V");

    while (1)
    {
        network.update();
        if (network.available())
        {
            char msg[30];
            RF24NetworkHeader header;
            network.read(header, &msg, sizeof(msg));
            printf("Recebeu: %s", msg);
            char *buf = (char*)malloc(30);
            strcpy(buf, msg);
            jstring jstrBuf = env->NewStringUTF(buf);
            env->CallVoidMethod(thiz, onReceive, jstrBuf);
        }
    }
  }

JNIEXPORT jboolean JNICALL Java_br_com_diegosilva_rfnative_RfNative_send
  (JNIEnv *env, jobject thiz, jint node, jstring msg){
      printf("enviando: %s", msg);
      RF24NetworkHeader header2(node);
      if(!network.write(header2, &msg, sizeof(msg))){
          return true;
      }else{
          return false;
      }
  }