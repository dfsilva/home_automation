#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "printf.h"

RF24 radio(9,10);

//Topology
const uint64_t pipe_central =  0xABCDABCD71LL;

void setup() {
    Serial.begin(57600);
    printf_begin();
    radio.begin();
    radio.enableDynamicPayloads();

    //radio.setDataRate(RF24_250KBPS);
    //radio.setPALevel(RF24_PA_MAX);
    radio.setChannel(55);

    radio.setRetries(15,15);
    //radio.setCRCLength(RF24_CRC_16);

    radio.openWritingPipe(pipe_central);
    radio.printDetails();

}

void loop() {
  transmitTemperature();
}

void transmitTemperature(){
    float t = 55.5f;
    char temp[10];
    dtostrf(t,6,2,temp);
    char msg[40];
    sprintf(msg, "id:%d,T:temp,value:%s",1,temp);
    Serial.println(msg); 
    bool ok = radio.write(&msg,strlen(msg));
}
