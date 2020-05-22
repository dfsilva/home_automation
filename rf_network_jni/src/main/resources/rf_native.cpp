#include "br_com_diegosilva_rfnative_RfNative.h"
#include <RF24/RF24.h>
#include <RF24Network/RF24Network.h>
#include <iostream>
#include <ctime>
#include <stdio.h>
#include <time.h>

#define PIN_GREEN RPI_GPIO_P1_11
#define PIN_BLUE RPI_GPIO_P1_12
#define PIN_RED RPI_V2_GPIO_P1_13

RF24 radio(RPI_V2_GPIO_P1_15, BCM2835_SPI_CS0, BCM2835_SPI_SPEED_8MHZ);
RF24Network network(radio);

JNIEXPORT void JNICALL Java_br_com_diegosilva_rfnative_RfNative_start(JNIEnv *env, jobject thiz, jint node)
{

  radio.begin();
  radio.setDataRate(RF24_250KBPS);
  delay(5);
  network.begin(90, node);
  radio.printDetails();

  jclass thisClass = env->GetObjectClass(thiz);
  jmethodID onReceive = env->GetMethodID(thisClass, "onReceive", "(Ljava/lang/String;)V");

  bcm2835_gpio_fsel(PIN_GREEN, BCM2835_GPIO_FSEL_OUTP);
  bcm2835_gpio_fsel(PIN_RED, BCM2835_GPIO_FSEL_OUTP);
  bcm2835_gpio_fsel(PIN_BLUE, BCM2835_GPIO_FSEL_OUTP);

  while (1)
  {
    network.update();
    if (network.available())
    {
      bcm2835_gpio_write(PIN_BLUE, HIGH); 
      char msg[30];
      RF24NetworkHeader header;
      network.read(header, &msg, sizeof(msg));
      jstring jmsg = env->NewStringUTF(msg);
      env->CallVoidMethod(thiz, onReceive, jmsg);
    }else{
      bcm2835_gpio_write(PIN_BLUE, LOW); 
    }
  }
}

JNIEXPORT jboolean JNICALL Java_br_com_diegosilva_rfnative_RfNative_send(JNIEnv *env, jobject thiz, jint node, jstring jmsg)
{
  const char *msgBuf = env->GetStringUTFChars(jmsg, 0);
  char msg[30];
  strncpy (msg, msgBuf, sizeof(msg) );
  RF24NetworkHeader header2(node);
  bool sent = network.write(header2, &msg, sizeof(msg));
  env->ReleaseStringUTFChars(jmsg, msgBuf);
  if(sent){
      bcm2835_gpio_write(PIN_GREEN, HIGH); 
      bcm2835_gpio_write(PIN_RED, LOW); 
  }else{
      bcm2835_gpio_write(PIN_GREEN, LOW); 
      bcm2835_gpio_write(PIN_RED, HIGH); 
  }
  return sent;
}
