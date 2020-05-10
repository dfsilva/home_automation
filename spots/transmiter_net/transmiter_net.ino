#include <RF24Network.h>
#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "Adafruit_Si7021.h"


RF24 radio(9,10);

RF24Network network(radio);   

const uint16_t w_pipe = 00;
const uint16_t r_pipe = 01;

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

const char *MY_ID = "s_12";

void setup() {
    Serial.begin(57600);

   if (!sensor.begin()) {
      Serial.println("Sensor Si7021 não encontrado!");
    }

    SPI.begin();
    radio.begin();
    network.begin(90,r_pipe);
    
    pinMode(PIR_PIN, INPUT);
    pinMode(SMOKE_PIN, INPUT);
}

void loop() {
  network.update(); 
  readWifi();
  sendValues();
}

void sendValues(){
  if(millis() > (last_send + send_delay)){
    sendHum();
    delay(200);
    sendTemp();
    delay(200);
    sendPresence();
    delay(200);
    sendSmoke();
    
    last_pipe = (last_pipe == 0) ? 1 : 0;
    last_send = millis();
  }
}

void readWifi(){
   
   while(network.available()) {
        RF24NetworkHeader header;
        char msg_inc[40] = "";
        network.read(header,&msg_inc,sizeof(msg_inc));

        Serial.println(F("recebido"));
        Serial.println(msg_inc);

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
//         radio.stopListening();
//         radio.openWritingPipe(r_pipe);
//         radio.write(&msg_inc,sizeof(msg_inc));
//         radio.startListening();
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
    RF24NetworkHeader header(w_pipe);
    network.write(header,&msgHum,strlen(msgHum));
}

void sendTemp(){
    last_temp = sensor.readTemperature();
    char tt[10];
    dtostrf(last_temp,6,2,tt);
    
    char msgTemp[30] = "";
    sprintf(msgTemp,"id:%s,sen:tp,val:%s\n",MY_ID,tt);
    
    Serial.print(msgTemp); 
    RF24NetworkHeader header(w_pipe);
    network.write(header,&msgTemp,strlen(msgTemp));
}

void sendPresence(){
    last_pir = digitalRead(PIR_PIN);
        
    char msgTemp[30] = "";
    sprintf(msgTemp,"id:%s,sen:ps,val:%d\n",MY_ID,last_pir);
    
    Serial.print(msgTemp); 
    RF24NetworkHeader header(w_pipe);
    network.write(header,&msgTemp,strlen(msgTemp));
}

void sendSmoke(){
    last_smoke = analogRead(SMOKE_PIN);
        
    char msgSmk[30] = "";
    
    sprintf(msgSmk,"id:%s,sen:sm,val:%d\n",MY_ID, last_smoke);
    
    Serial.print(msgSmk); 
    RF24NetworkHeader header(w_pipe);
    network.write(header,&msgSmk,strlen(msgSmk));
}
