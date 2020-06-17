#include <RF24Network.h>
#include <RF24.h>
#include <SPI.h>
#include "Adafruit_Si7021.h"

RF24 radio(9, 10);

RF24Network network(radio);

const uint16_t this_node = 02;
const uint16_t other_node = 00;

int last_pipe = 0;

const int send_delay = 500;
unsigned long last_send = 0;

Adafruit_Si7021 sensor = Adafruit_Si7021();

float last_temp = 0.0;
float last_hum = 0.0;
int last_pir = 0;
int last_smoke = 0;

const byte PIR_PIN = 3;
const int SMOKE_PIN = A0;

const byte GLED_PIN = 4;
const byte RLED_PIN = 5;
const byte BLED_PIN = 6;

const char *MY_ID = "02";

int send_now = 0;

void setup()
{
  Serial.begin(115200);

  if (!sensor.begin())
  {
    Serial.println("Erro ao iniciar Si7021!");
  }

  SPI.begin();
  radio.begin();
  radio.setPALevel(RF24_PA_MAX);
  radio.setDataRate(RF24_250KBPS);
    
  network.begin(90, this_node);

  pinMode(PIR_PIN, INPUT);
  pinMode(SMOKE_PIN, INPUT);
  pinMode(GLED_PIN, OUTPUT);
  pinMode(RLED_PIN, OUTPUT);
  pinMode(BLED_PIN, OUTPUT);
}

void loop()
{
  network.update();
  receive();
  sendValues();
}

void sendValues()
{
  if (millis() > (last_send + send_delay))
  {
    switch (send_now)
    {
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
    last_pipe = (last_pipe == 0) ? 1 : 0;
    last_send = millis();
  }
}

void receive()
{
  while (network.available())
  {
    digitalWrite(BLED_PIN, HIGH);
    RF24NetworkHeader header;
    char msg[30];
    network.read(header, msg, sizeof(msg));
    Serial.println(msg);
  }
  digitalWrite(BLED_PIN, LOW);
}

void updateSentLed(bool sent)
{
  if (sent)
  {
    digitalWrite(GLED_PIN, HIGH);
    digitalWrite(RLED_PIN, LOW);
  }
  else
  {
    digitalWrite(GLED_PIN, LOW);
    digitalWrite(RLED_PIN, HIGH);
  }
}

bool sendHum()
{
  last_hum = sensor.readHumidity();
  char msgHum[30] = "";
  sprintf(msgHum, "id:%s,sen:hm1,val:%d.%02d\n", MY_ID, (int)last_hum, (int)(last_hum * 100) % 100);
  RF24NetworkHeader header2(other_node);
  bool sent = network.write(header2, &msgHum, sizeof(msgHum));
  updateSentLed(sent);
  return sent;
}

bool sendTemp()
{
  last_temp = sensor.readTemperature();
  char msgTemp[30] = "";
  sprintf(msgTemp, "id:%s,sen:tp1,val:%d.%02d\n", MY_ID, (int)last_temp, (int)(last_temp * 100) % 100);
  RF24NetworkHeader header2(other_node);
  bool sent = network.write(header2, &msgTemp, sizeof(msgTemp));
  updateSentLed(sent);
  return sent;
}

bool sendPresence()
{
  last_pir = digitalRead(PIR_PIN);
  char msgTemp[30] = "";
  sprintf(msgTemp, "id:%s,sen:ps1,val:%d\n", MY_ID, last_pir);
  RF24NetworkHeader header2(other_node);
  bool sent = network.write(header2, &msgTemp, sizeof(msgTemp));
  updateSentLed(sent);
  return sent;
}

bool sendSmoke()
{
  last_smoke = analogRead(SMOKE_PIN);
  char msgSmk[30] = "";
  sprintf(msgSmk, "id:%s,sen:sm1,val:%d\n", MY_ID, last_smoke);
  RF24NetworkHeader header2(other_node);
  bool sent = network.write(header2, &msgSmk, sizeof(msgSmk));
  updateSentLed(sent);
  return sent;
}
