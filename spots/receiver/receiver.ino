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
    radio.setAutoAck(true);
    radio.setCRCLength(RF24_CRC_16);

    radio.openWritingPipe(t_pipe);
    radio.openReadingPipe(1,r_pipes[IDX_PIPE]);  
    radio.powerUp();
    radio.startListening();
}

void loop(){
    readWifi();
    delay(500);
    readSerial();
}

void readWifi(){
  if (radio.available()) {
        int len = radio.getDynamicPayloadSize();
        char inc_msg[40] = "";
        radio.read(&inc_msg,len);
        Serial.write((byte*)&inc_msg, sizeof(inc_msg)); 
        Serial.flush();
   }
}

void readSerial(){
  if(Serial.available() > 1){
      radio.stopListening();
      String serialMsg = Serial.readString();
      char out_msg[40] = "";
      serialMsg.toCharArray(out_msg, 40);
      Serial.println(out_msg);
      radio.write(&out_msg,strlen(out_msg));
      radio.startListening();
    }
  
  
//   char out_msg[40] = "";
//   int availableBytes = Serial.available(); 
//   char first = Serial.read();
//
//   if(availableBytes > 0 && first == '*'){
//     radio.stopListening();
//     for(int i = 0; i < availableBytes; i++){
//         out_msg[i] = Serial.read();
//         if(out_msg[i] == '\n'){
//            Serial.println("env");
//            radio.write(&out_msg,strlen(out_msg));
//         }
//     }
//     radio.startListening();
//   }
   
}
