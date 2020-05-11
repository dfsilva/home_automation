#include <RF24Network.h>
#include <SPI.h>
 #include <string.h>
#include "nRF24L01.h"
#include "RF24.h"
#include "Adafruit_Si7021.h"

RF24 radio(9, 10);

RF24Network network(radio);

const uint16_t this_node = 01;
const uint16_t other_node = 00;

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

struct payload_t {
  unsigned long id;
  char sen[2];
  char val[6];
};

void setup()
{
  Serial.begin(57600);

  if (!sensor.begin())
  {
    Serial.println("Erro ao iniciar Si7021!");
  }

  SPI.begin();
  radio.begin();
  network.begin(90, this_node);

  pinMode(PIR_PIN, INPUT);
  pinMode(SMOKE_PIN, INPUT);
}

void loop()
{
  network.update();
  readInc();
  sendValues();
}

void sendValues()
{
  if (millis() > (last_send + send_delay))
  {
    while (!sendHum())
    {
      Serial.println(F("erro"));
    }

   Serial.println(F("enviou"));
//    sendTemp();
//    delay(1000);
//    sendPresence();
//    delay(1000);
//    sendSmoke();

    last_pipe = (last_pipe == 0) ? 1 : 0;
    last_send = millis();
  }
}

void readInc()
{
  while (network.available())
  {
    Serial.println(F("recebeu"));
    RF24NetworkHeader header;
    payload_t payload;
    network.read(header, &payload, sizeof(payload));

    Serial.println(payload.id);
    Serial.println(payload.sen);
    Serial.println(payload.val);
  }
}

bool sendHum()
{
  last_hum = sensor.readHumidity();
  
  char val[6];
  sprintf(val, "%d.%02d\n",(int)last_hum,(int)(last_hum*100)%100);
  payload_t payload;
  payload.id = 12;
  payload.sen[0] = 'h';
  payload.sen[1] = 'm';
  for(int j = 0; j < 6; j++){
    payload.val[j] = val[j];
  }
  RF24NetworkHeader header2(other_node);
  return network.write(header2, &payload, sizeof(payload));
}

bool sendTemp()
{
  last_temp = sensor.readTemperature();

  char msgTemp[30] = "";
  sprintf(msgTemp,"id:%s,sen:tp,val:%d.%02d\n",MY_ID,(int)last_temp,(int)(last_temp*100)%100);

  Serial.print(msgTemp);
  RF24NetworkHeader header2(other_node);
  return network.write(header2, &msgTemp, sizeof(msgTemp));
}

bool sendPresence()
{
  last_pir = digitalRead(PIR_PIN);

  char msgTemp[30] = "";
  sprintf(msgTemp, "id:%s,sen:ps,val:%d\n", MY_ID, last_pir);

  Serial.print(msgTemp);
  RF24NetworkHeader header2(other_node);
  return network.write(header2, &msgTemp, sizeof(msgTemp));
}

bool sendSmoke()
{
  last_smoke = analogRead(SMOKE_PIN);

  char msgSmk[30] = "";

  sprintf(msgSmk, "id:%s,sen:sm,val:%d\n", MY_ID, last_smoke);

  Serial.print(msgSmk);
  RF24NetworkHeader header2(other_node);
  network.write(header2, &msgSmk, sizeof(msgSmk));
}
