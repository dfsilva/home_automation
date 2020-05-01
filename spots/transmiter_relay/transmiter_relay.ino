#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"

RF24 radio(9,10);
const uint64_t w_pipes[2] =  {0xABCDABCD71LL, 0x544d52687CLL};
const uint64_t r_pipe = 0xABCDABCD71AA;

int last_pipe = 0;

const int send_delay = 2000;
unsigned long last_send = 0;

int last_relay = 0;
const byte RELAY1_PIN = 7;

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
    
    pinMode(RELAY1_PIN, OUTPUT);
    digitalWrite(RELAY1_PIN, LOW);

}

void loop() {
  sendValues();
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
        
        if(id_Str.equals("s_10")){
          if(type_Str.equals("ld")){
              String val_str = val;
              if(val_str.equals("true\n")){
                last_relay = 1;
                digitalWrite(RELAY1_PIN, HIGH);
              }else{
                last_relay = 0;
                digitalWrite(RELAY1_PIN, LOW);
              }
          }
        }        
   } 
}

void sendRelay(){        
    char msgTemp[30] = "";
    sprintf(msgTemp,"id:s_10,sen:ld,val:%d\n", last_relay);
    Serial.print(msgTemp); 
    radio.write(&msgTemp,strlen(msgTemp));
}

void sendValues(){
  if(millis() > (last_send + send_delay)){
    radio.stopListening();
    radio.openWritingPipe(w_pipes[last_pipe]);

    sendRelay();
    
    radio.startListening();
    
    last_pipe = (last_pipe == 0) ? 1 : 0;
    last_send = millis();
  }
}
