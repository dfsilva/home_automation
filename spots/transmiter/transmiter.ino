#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "printf.h"

RF24 radio(9,10);

//Topology
const uint64_t pipes[2] =  {0xABCDABCD71LL, 0x544d52687CLL};

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
}

void loop() {
  int p_index = random(0, 1);
  radio.openWritingPipe(pipes[p_index]);
  transmitTemperature();
}

void transmitTemperature(){
    float t = 55.55f;
    char temp[10];
    dtostrf(t,6,2,temp);
    char msg[40];
    int id = random(1, 2000);
    sprintf(msg, "id:temp_%d,T:temp,value:%s",id,temp);
    Serial.println(msg); 
    bool ok = radio.write(&msg,strlen(msg));
}
