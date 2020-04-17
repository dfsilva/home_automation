#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "printf.h"

RF24 radio(9,10);
const uint64_t w_pipes[2] =  {0xABCDABCD71LL, 0x544d52687CLL};
const uint64_t r_pipe = 0xABCDABCD71AA;

int last_pipe = 0;

void setup() {
    Serial.begin(57600);
    printf_begin();
    radio.begin();
    radio.enableDynamicPayloads();
    radio.setChannel(55);
    radio.setRetries(15,15);

    radio.setCRCLength(RF24_CRC_16);
    radio.openReadingPipe(1,r_pipe);  
    radio.startListening();
}

void loop() {
  radio.openWritingPipe(w_pipes[last_pipe]);
  transmitTemperature();
  last_pipe = (last_pipe == 0) ? 1 : 0;
  readWifi();
  delay(200);
  
}

void readWifi(){
   if (radio.available()) {
        int len = radio.getDynamicPayloadSize();
        char msg[40] = "";
        radio.read(&msg,len);
        String msgStr = msg;
        Serial.println("Mensagem wifi: "+msgStr);  
   } 
}

void transmitTemperature(){
    int t = random(50, 50000);
    char temp[7];
    dtostrf(t,6,2,temp);
    char msg[20];
    int id = random(1, 10);
    sprintf(msg, "id:temp_%d,T:temp,value:%s",id,temp);
//    Serial.println(msg); 
    radio.write(&msg,strlen(msg));
}
