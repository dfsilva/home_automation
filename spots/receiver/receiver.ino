#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "printf.h"

RF24 radio(9,10);

const uint64_t pipe_spot1 = 0xABCDABCD71LL;

void setup(){
    Serial.begin(57600);
    printf_begin();
    radio.begin();
    radio.enableDynamicPayloads();

    //radio.setDataRate(RF24_250KBPS);
   // radio.setPALevel(RF24_PA_MAX);
    radio.setChannel(55);

    radio.setRetries(15,15);
    radio.setCRCLength(RF24_CRC_16);

    radio.openReadingPipe(1,pipe_spot1);  
    radio.startListening();
    radio.printDetails();
}

void loop(){
   if (radio.available()) {
        int len = radio.getDynamicPayloadSize();
        char msg[40] = "";
        radio.read(&msg,len);
        Serial.println(msg);  
   } 
}
