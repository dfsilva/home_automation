#include <Arduino.h>
//#include <U8g2lib.h>
//#ifdef U8X8_HAVE_HW_SPI
#include <SPI.h>
//#endif
//#ifdef U8X8_HAVE_HW_I2C
//#include <Wire.h>
//#endif
#include "nRF24L01.h"
#include "RF24.h"
#include "printf.h"

//U8G2_SSD1306_128X64_NONAME_F_HW_I2C u8g2(U8G2_R0, /* reset=*/ U8X8_PIN_NONE);

RF24 radio(9,10);
const uint64_t w_pipes[2] =  {0xABCDABCD71LL, 0x544d52687CLL};
const uint64_t r_pipe = 0xABCDABCD71AA;

int last_pipe = 0;

const int send_delay = 4000;
unsigned long last_send = 0;

void setup() {
    Serial.begin(57600);
    printf_begin();
    radio.begin();
    radio.enableDynamicPayloads();
    radio.setChannel(55);
    radio.setRetries(15,15);
    radio.setAutoAck(true);
    radio.openReadingPipe(1,r_pipe);
    radio.setCRCLength(RF24_CRC_16);

    radio.powerUp() ;
    radio.startListening();
    
//    u8g2.begin();
}

void loop() {
  sendTest();
  readWifi();
}

void readWifi(){
   if (radio.available()) {
        int len = radio.getDynamicPayloadSize();
        char msg_inc[40] = "";
        radio.read(&msg_inc,len);
        Serial.print(msg_inc);
   } 
}

void sendTest(){
  if(millis() > (last_send + send_delay)){
    radio.stopListening();
    radio.openWritingPipe(w_pipes[last_pipe]);
    
    unsigned int t = random(10, 5000);
    char temp[10];
    dtostrf(t,6,2,temp);
    char msg[20] = "";
    int id = random(11, 20);
    sprintf(msg, "id:temp_%d,value:%s\n",id,temp);
    
    Serial.println(msg); 
    radio.write(&msg,strlen(msg));
    radio.startListening();
    
    last_pipe = (last_pipe == 0) ? 1 : 0;
    last_send = millis();
  }
}
