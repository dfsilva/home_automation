#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "printf.h"

RF24 radio(9,10);
const uint64_t r_pipes[2] =  {0xABCDABCD71LL, 0x544d52687CLL};

void setup(){
    Serial.begin(57600);
    printf_begin();
    radio.begin();
    radio.enableDynamicPayloads();

    radio.setChannel(55);

    radio.setRetries(15,15);
    radio.setCRCLength(RF24_CRC_16);

    radio.openReadingPipe(1,r_pipes[0]);  
    radio.startListening();
}

void loop(){
   if (radio.available()) {
        int len = radio.getDynamicPayloadSize();
        char msg[40] = "";
        radio.read(&msg,len);
        Serial.println(msg);  
   } 
}
