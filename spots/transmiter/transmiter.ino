#include <Arduino.h>
#include <SPI.h>
#include <Wire.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "Adafruit_Si7021.h"

#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

#include "printf.h"

RF24 radio(9,10);
const uint64_t w_pipes[2] =  {0xABCDABCD71LL, 0x544d52687CLL};
const uint64_t r_pipe = 0xABCDABCD71AA;

int last_pipe = 0;

const int send_delay = 2000;
unsigned long last_send = 0;

Adafruit_Si7021 sensor = Adafruit_Si7021();
Adafruit_SSD1306 display(128, 64, &Wire, 4);
byte last_view = 0;

float last_temp = 0.0;
float last_hum = 0.0;
int last_pir = 0;
int last_smoke = 0;

int last_id = 1;

const byte INTERRUP_PIN = 2;
const byte PIR_PIN = 3;

const int SMOKE_PIN = A0;

void setup() {
    Serial.begin(57600);
    printf_begin();
    radio.begin();
    radio.enableDynamicPayloads();
    radio.setChannel(55);
    radio.setRetries(15,15);
    radio.setAutoAck(true);
    radio.openReadingPipe(1,r_pipe);
    radio.setCRCLength(RF24_CRC_16);

    radio.powerUp() ;
    radio.startListening();
    
    if(!display.begin(SSD1306_SWITCHCAPVCC, 0x3C)) {
      Serial.println(F("Nao foi possivel inicializar o display"));
    }

    display.clearDisplay();
    display.display();

    pinMode(PIR_PIN, INPUT);
    pinMode(SMOKE_PIN, INPUT);

    attachInterrupt(digitalPinToInterrupt(INTERRUP_PIN), changeView, RISING);
}

void loop() {
  sendValues();
  readWifi();
}

void changeView() {
  Serial.println("changeView");
  if(last_view == 0){
    last_view = 1;
  }else{
    last_view = 0;
  }
}


void drawView(){
  char th[10];
  char subtitle[40];
    
 if(last_view == 0){
    const char *title = "TEMPERATURA";
    dtostrf(last_temp,6,2,th);
    sprintf(subtitle, "%s",th);
    drawTextCenter(title, subtitle);
  }
  if(last_view == 1){
    const char *title = "HUMIDADE";
    dtostrf(last_hum,6,2,th);
    sprintf(subtitle, "%s",th);
    drawTextCenter(title, subtitle);
  }
}

void drawTextCenter(const char* title, const char *subtitle) {
  display.clearDisplay();
  display.setTextSize(1);             
  display.setTextColor(SSD1306_WHITE);        
  display.setCursor(30,0);             
  display.print(title);      
  display.setCursor(0,30); 
  display.setTextSize(2);    
  display.print(subtitle);
  display.display();
}

void readWifi(){
   if (radio.available()) {
        int len = radio.getDynamicPayloadSize();
        char msg_inc[40] = "";
        radio.read(&msg_inc,len);
        Serial.print(msg_inc);
   } 
}

void sendHum(){
    last_hum = sensor.readHumidity();
    char th[10];
    dtostrf(last_hum,6,2,th);
    
    char msgHum[30] = "";
    sprintf(msgHum, "id:s_%d,sen:hm,val:%s\n",last_id,th);
    
    Serial.print(msgHum); 
    radio.write(&msgHum,strlen(msgHum));
    drawView();
}

void sendTemp(){
    last_temp = sensor.readTemperature();
    char tt[10];
    dtostrf(last_temp,6,2,tt);
    
    char msgTemp[30] = "";
    sprintf(msgTemp,"id:s_%d,sen:tp,val:%s\n",last_id,tt);
    
    Serial.print(msgTemp); 
    radio.write(&msgTemp,strlen(msgTemp));
    
    drawView();
}

void sendPresence(){
    last_pir = digitalRead(PIR_PIN);
        
    char msgTemp[30] = "";
    sprintf(msgTemp,"id:s_%d,sen:ps,val:%d\n",last_id, last_pir);
    
    Serial.print(msgTemp); 
    radio.write(&msgTemp,strlen(msgTemp));
    
    drawView();
}

void sendSmoke(){
    last_smoke = analogRead(SMOKE_PIN);
        
    char msgTemp[30] = "";
    
    sprintf(msgTemp,"id:s_%d,sen:sm,val:%d\n",last_id, last_smoke);
    
    Serial.print(msgTemp); 
    radio.write(&msgTemp,strlen(msgTemp));
    
    drawView();
}

void sendValues(){
  if(millis() > (last_send + send_delay)){
    radio.stopListening();
    radio.openWritingPipe(w_pipes[last_pipe]);

    last_id = random(1, 10);
    sendHum();
    sendTemp();
    sendPresence();
    sendSmoke();
    
    radio.startListening();
    
    last_pipe = (last_pipe == 0) ? 1 : 0;
    last_send = millis();
  }
}
