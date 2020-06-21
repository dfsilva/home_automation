#include <SPI.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "Adafruit_Si7021.h"

RF24 radio(9,10);
const uint64_t w_pipes[2] =  {0xABCDABCD71LL, 0x544d52687CLL};
const uint64_t r_pipe = 0xABCDABCD71AA;

int last_pipe = 0;

const int send_delay = 1000;
unsigned long last_send = 0;

Adafruit_Si7021 sensor = Adafruit_Si7021();

float last_temp = 0.0;
float last_hum = 0.0;
int last_pir = 0;
int last_smoke = 0;

const byte PIR_PIN = 3;
const int SMOKE_PIN = A0;

const char *MY_ID = "s_11";

int send_now = 0;

void setup() {
    Serial.begin(57600);

   if (!sensor.begin()) {
      Serial.println("Erro ao inicializar Si7021!");
    }

    radio.begin();
//    radio.setPALevel(RF24_PA_MAX);
//    radio.setDataRate(RF24_250KBPS);
    radio.enableDynamicPayloads();
    radio.setChannel(55);
    radio.setRetries(15,15);
    radio.setAutoAck(true);
    radio.openReadingPipe(1,r_pipe);
    radio.setCRCLength(RF24_CRC_16);

//    radio.printDetails();

    radio.powerUp() ;
    radio.startListening();
    
    pinMode(PIR_PIN, INPUT);
    pinMode(SMOKE_PIN, INPUT);
}

void loop() {
  sendValues();
//  readWifi();
}

void readWifi(){
   if (radio.available()) {
        int len = radio.getDynamicPayloadSize();
        char msg_inc[30];
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
//         Serial.println(F("retransmitindo"));
         radio.stopListening();
         radio.openWritingPipe(r_pipe);
         radio.write(&msg_inc,len);
         radio.startListening();
        }        
   } 
}


void sendHum(){
    last_hum = sensor.readHumidity();
    char msgHum[30];
    sprintf(msgHum, "id:%s,sen:hm1,val:%d.%02d\n",MY_ID,(int)last_hum,(int)(last_hum*100)%100);
    radio.write(&msgHum,sizeof(msgHum));
}

void sendTemp(){
    last_temp = sensor.readTemperature();
    char msgTemp[30];
    sprintf(msgTemp,"id:%s,sen:tp1,val:%d.%02d\n",MY_ID,(int)last_temp,(int)(last_temp*100)%100);
    radio.write(&msgTemp,sizeof(msgTemp));
}

void sendPresence(){
    last_pir = digitalRead(PIR_PIN);
    char msgTemp[30];
    sprintf(msgTemp,"id:%s,sen:ps1,val:%d\n",MY_ID,last_pir);
    radio.write(&msgTemp,sizeof(msgTemp));
}

void sendSmoke(){
    last_smoke = analogRead(SMOKE_PIN);
    char msgTemp[30];
    sprintf(msgTemp,"id:%s,sen:sm1,val:%d\n",MY_ID, last_smoke);
    radio.write(&msgTemp,sizeof(msgTemp));
}

void sendValues(){
  if(millis() > (last_send + send_delay)){
    radio.stopListening();
    radio.openWritingPipe(w_pipes[last_pipe]);

   switch(send_now){
      case 0:
        sendHum();
        send_now = 1;
        break;
      case 1:
        sendTemp();
        send_now = 2;
        break;
      case 2:
        sendPresence();
        send_now = 3;
        break;
      case 3:
        sendSmoke();
        send_now = 0;
        break;
      }
    
    radio.startListening();
    
    last_pipe = (last_pipe == 0) ? 1 : 0;
    last_send = millis();
  }
}
