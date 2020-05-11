#include <RF24Network.h>
#include <RF24.h>
#include <SPI.h>


RF24 radio(9,10);

RF24Network network(radio);
const uint16_t this_node = 00;
const uint16_t other_node = 01;

struct payload_t {
  unsigned long id;
  char sen[2];
  char val[6];
};


void setup(void)
{
  Serial.begin(57600);
  Serial.println("Receptor transmissor");
 
  SPI.begin();
  radio.begin();
  network.begin(90,this_node);
}


void send(payload_t payload){
  RF24NetworkHeader header(other_node);
  bool ok = network.write(header,&payload,sizeof(payload));
  if (ok){
     Serial.println(F("Enviou!!!"));
  }else{ 
    Serial.println(F("Falhou o envio!!!"));
  }
}

void receive(){
   if (network.available()) {
      RF24NetworkHeader header;
      payload_t payload;
      network.read(header,&payload,sizeof(payload));
      Serial.println(F("Recebeu"));
      Serial.println(payload.id);
      Serial.println(payload.sen);
      Serial.println(payload.val);
      send(payload);
    }  
}

void loop(void){
  network.update();
  receive();
}
