#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"

RF24 radio(9,10);
const uint64_t r_pipe = 0xABCDABCD71AA;

const byte OPEN_1 = 4;
const byte OPEN_2 = 5;

void setup() {
    Serial.begin(57600);

    radio.begin();
    radio.enableDynamicPayloads();
    radio.setChannel(55);
    radio.setRetries(15,15);
    radio.setAutoAck(true);
    radio.openReadingPipe(1,r_pipe);
    radio.setCRCLength(RF24_CRC_16);

    radio.powerUp() ;
    radio.startListening();
    
    pinMode(OPEN_1, OUTPUT);
    digitalWrite(OPEN_1, LOW);

    pinMode(OPEN_2, OUTPUT);
    digitalWrite(OPEN_2, LOW);
}

void loop() {
  readWifi();
}


void readWifi(){
   if (radio.available()) {
        int len = radio.getDynamicPayloadSize();
        char msg_inc[40] = "";
        radio.read(&msg_inc,len);

        Serial.println(F("recebido"));

        char *p = msg_inc;
        char *p_id = strtok_r(p, ",", &p);
        char *p_type = strtok_r(p, ",", &p);
        char *p_val = strtok_r(p, ",", &p);

        strtok_r(p_id, ":", &p_id);
        const char *id = strtok_r(p_id, ":", &p_id);

        strtok_r(p_type, ":", &p_type);
        const char *type = strtok_r(p_type, ":", &p_type);

        strtok_r(p_val, ":", &p_val);
        const char *val = strtok_r(p_val, ":", &p_val);

        String id_Str = id;
        String type_Str = type;
        
        if(id_Str.equals("s_11")){
          if(type_Str.equals("op1")){
              String val_str = val;
              if(val_str.equals("true\n")){
                digitalWrite(OPEN_1, HIGH);
              }else{
                digitalWrite(OPEN_1, LOW);
              }
          }

          if(type_Str.equals("op2")){
              String val_str = val;
              if(val_str.equals("true\n")){
                digitalWrite(OPEN_2, HIGH);
              }else{
                digitalWrite(OPEN_2, LOW);
              }
          }
        }        
   } 
}
