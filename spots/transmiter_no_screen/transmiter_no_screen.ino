#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "Adafruit_Si7021.h"


RF24 radio(9,10);
const uint64_t w_pipes[2] =  {0xABCDABCD71LL, 0x544d52687CLL};
const uint64_t r_pipe = 0xABCDABCD71AA;

int last_pipe = 0;

const int send_delay = 2000;
unsigned long last_send = 0;

Adafruit_Si7021 sensor = Adafruit_Si7021();

float last_temp = 0.0;
float last_hum = 0.0;
int last_pir = 0;
int last_smoke = 0;

const byte PIR_PIN = 3;
const int SMOKE_PIN = A0;

const char *MY_ID = "s_10";

void setup() {
    Serial.begin(57600);

   if (!sensor.begin()) {
      Serial.println("Did not find Si7021 sensor!");
    }

    radio.begin();
    radio.enableDynamicPayloads();
    radio.setChannel(55);
    radio.setRetries(15,15);
    radio.setAutoAck(true);
    radio.openReadingPipe(1,r_pipe);
    radio.setCRCLength(RF24_CRC_16);

    radio.powerUp() ;
    radio.startListening();
    
    pinMode(PIR_PIN, INPUT);
    pinMode(SMOKE_PIN, INPUT);
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
        
        if(id_Str.equals(MY_ID)){
//          if(type_Str.equals("ld")){
//              String val_str = val;
//              if(val_str.equals("true\n")){
//                last_relay = 1;
//                digitalWrite(RELAY1_PIN, HIGH);
//              }else{
//                last_relay = 0;
//                digitalWrite(RELAY1_PIN, LOW);
//              }
//          }else{
//            //Caso a mensagem seja para esse device mas ele nao 
//            //possuir o sensor ele retransmite
//            radio.stopListening();
//            radio.openWritingPipe(r_pipe);
//            radio.write(&msg_inc,len);
//            radio.startListening();
//          }

        //apenas retransmite pq nao possui nenhum relay
        Serial.println(F("retransmitindo"));
         radio.stopListening();
         radio.openWritingPipe(r_pipe);
         radio.write(&msg_inc,len);
         radio.startListening();
        }        
   } 
}


void sendHum(){
    last_hum = sensor.readHumidity();
    char th[10];
    dtostrf(last_hum,6,2,th);
    
    char msgHum[30] = "";
    sprintf(msgHum, "id:%s,sen:hm,val:%s\n",MY_ID,th);
    
    Serial.print(msgHum); 
    radio.write(&msgHum,strlen(msgHum));
}

void sendTemp(){
    last_temp = sensor.readTemperature();
    char tt[10];
    dtostrf(last_temp,6,2,tt);
    
    char msgTemp[30] = "";
    sprintf(msgTemp,"id:%s,sen:tp,val:%s\n",MY_ID,tt);
    
    Serial.print(msgTemp); 
    radio.write(&msgTemp,strlen(msgTemp));
}

void sendPresence(){
    last_pir = digitalRead(PIR_PIN);
        
    char msgTemp[30] = "";
    sprintf(msgTemp,"id:%s,sen:ps,val:%d\n",MY_ID,last_pir);
    
    Serial.print(msgTemp); 
    radio.write(&msgTemp,strlen(msgTemp));
}

void sendSmoke(){
    last_smoke = analogRead(SMOKE_PIN);
        
    char msgTemp[30] = "";
    
    sprintf(msgTemp,"id:%s,sen:sm,val:%d\n",MY_ID, last_smoke);
    
    Serial.print(msgTemp); 
    radio.write(&msgTemp,strlen(msgTemp));
}

void sendValues(){
  if(millis() > (last_send + send_delay)){
    radio.stopListening();
    radio.openWritingPipe(w_pipes[last_pipe]);

    sendHum();
    delay(500);
    sendTemp();
    delay(500);
    sendPresence();
    delay(500);
    sendSmoke();
    
    radio.startListening();
    
    last_pipe = (last_pipe == 0) ? 1 : 0;
    last_send = millis();
  }
}
