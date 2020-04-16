#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "printf.h"

RF24 radio(9,10);
const uint64_t w_pipes[2] =  {0xABCDABCD71LL, 0x544d52687CLL};

int last_pipe = 0;

void setup() {
    Serial.begin(57600);
    printf_begin();
    radio.begin();
    radio.enableDynamicPayloads();
    radio.setChannel(55);
    radio.setRetries(15,15);
}

void loop() {
  Serial.println(last_pipe); 
  radio.openWritingPipe(w_pipes[last_pipe]);
  transmitTemperature();
  last_pipe = (last_pipe == 0) ? 1 : 0;
  delay(200);
}

void transmitTemperature(){
    int t = random(50, 50000);
    char temp[7];
    dtostrf(t,6,2,temp);
    char msg[20];
    int id = random(1, 10);
    sprintf(msg, "id:temp_%d,T:temp,value:%s",id,temp);
    Serial.println(msg); 
    bool ok = radio.write(&msg,strlen(msg));
}
