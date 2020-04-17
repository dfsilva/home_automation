#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "printf.h"

RF24 radio(9,10);
const uint64_t r_pipes[2] =  {0xABCDABCD71LL, 0x544d52687CLL};
const uint64_t t_pipe = 0xABCDABCD71AA;

const int IDX_PIPE = 0;

void setup(){
    Serial.begin(57600);
    printf_begin();
    radio.begin();
    radio.enableDynamicPayloads();

    radio.setChannel(55);

    radio.setRetries(15,15);
    radio.setCRCLength(RF24_CRC_16);

    radio.openWritingPipe(t_pipe);
    radio.openReadingPipe(1,r_pipes[IDX_PIPE]);  
    radio.startListening();
}

void loop(){
    readWifi();
    readSerial();
}

void readWifi(){
  if (radio.available()) {
        int len = radio.getDynamicPayloadSize();
        char msg[40] = "";
        radio.read(&msg,len);
        Serial.println(msg);  
   }
}

void readSerial(){
  String serialMsg = Serial.readString();
  if(serialMsg != ""){
     Serial.println("Mensagem serial: "+serialMsg); 
     radio.openWritingPipe(r_pipe);
     char msg[20];
     serialMsg.toCharArray(msg, strlen(msg));
     radio.write(&msg,strlen(msg));
  }
}
