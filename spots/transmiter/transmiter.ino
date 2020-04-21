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

const int send_delay = 4000;
unsigned long last_send = 0;

Adafruit_Si7021 sensor = Adafruit_Si7021();

Adafruit_SSD1306 display(128, 64, &Wire, 4);

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
}

void loop() {
  sendValues();
  readWifi();
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
    float humValue = sensor.readHumidity();
    char th[10];
    dtostrf(humValue,6,2,th);
    
    char msgHum[30] = "";
    int id = random(1, 10);
    sprintf(msgHum, "id:s_%d,sen:h,val:%s\n",id,th);
    
    Serial.print(msgHum); 
    radio.write(&msgHum,strlen(msgHum));

    const char *title = "HUMIDADE";
    char subtitle[40];
    sprintf(subtitle, "%s",th);
    drawTextCenter(title, subtitle);
}

void sendTemp(){
    float tempValue = sensor.readTemperature();
    char tt[10];
    dtostrf(tempValue,6,2,tt);
    
    char msgTemp[30] = "";
    int id = random(1, 10);
    sprintf(msgTemp,"id:s_%d,sen:t,val:%s\n",id,tt);
    
    Serial.print(msgTemp); 
    radio.write(&msgTemp,strlen(msgTemp));
    
    const char *title = "TEMPERATURA";
    char subtitle[40];
    sprintf(subtitle, "%s",tt);
    drawTextCenter(title, subtitle);
}

void sendValues(){
  if(millis() > (last_send + send_delay)){
    radio.stopListening();
    radio.openWritingPipe(w_pipes[last_pipe]);

    sendHum();
    sendTemp();
    
    radio.startListening();
    
    last_pipe = (last_pipe == 0) ? 1 : 0;
    last_send = millis();
  }
}
