#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"

RF24 radio(8,10);
const uint64_t w_pipes[2] =  {0xABCDABCD71LL, 0x544d52687CLL};
const uint64_t r_pipe = 0xABCDABCD71AA;

int last_pipe = 0;

const int send_delay = 2000;
unsigned long last_send = 0;

int last_open_1 = 0;
const byte OPEN_1 = 4;

int last_open_2 = 0;
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
  sendValues();
  readWifi();
}


void readWifi(){
   if (radio.available()) {
        int len = radio.getDynamicPayloadSize();
        char msg_inc[40] = "";
        radio.read(&msg_inc,len);

//        Serial.println(F("recebido"));

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
          if(type_Str.equals("ld1")){
              String val_str = val;
              if(val_str.equals("true\n")){
                last_open_1 = 1;
                last_open_2 = 0;
                digitalWrite(OPEN_2, LOW);
                digitalWrite(OPEN_1, HIGH);            
              }else{
                last_open_1 = 0;
                digitalWrite(OPEN_1, LOW);
              }
          }

          if(type_Str.equals("ld2")){
              String val_str = val;
              if(val_str.equals("true\n")){
                last_open_2 = 1;
                last_open_1 = 0;
                digitalWrite(OPEN_1, LOW);
                digitalWrite(OPEN_2, HIGH);
              }else{
                last_open_2 = 0;
                digitalWrite(OPEN_2, LOW);
              }
          }
        }        
   } 
}

void sendOpen1(){        
    char msgTemp[30] = "";
    sprintf(msgTemp,"id:s_11,sen:ld1,val:%d\n", last_open_1);
    Serial.print(msgTemp); 
    radio.write(&msgTemp,strlen(msgTemp));
}

void sendOpen2(){        
    char msgTemp[30] = "";
    sprintf(msgTemp,"id:s_11,sen:ld2,val:%d\n", last_open_2);
    Serial.print(msgTemp); 
    radio.write(&msgTemp,strlen(msgTemp));
}

void sendValues(){
  if(millis() > (last_send + send_delay)){
    radio.stopListening();
    radio.openWritingPipe(w_pipes[last_pipe]);

    sendOpen1();
    delay(200);
    sendOpen2();
    
    radio.startListening();
    
    last_pipe = (last_pipe == 0) ? 1 : 0;
    last_send = millis();
  }
}
