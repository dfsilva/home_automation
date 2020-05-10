#include <RF24Network.h>
#include <RF24.h>
#include <SPI.h>


RF24 radio(9,10);                // nRF24L01(+) radio attached using Getting Started board 

RF24Network network(radio);      // Network uses that radio
const uint16_t this_node = 00;    // Address of our node in Octal format ( 04,031, etc)
const uint16_t other_node = 01;   // Address of the other node in Octal format

struct payload_t {                 // Structure of our payload
  unsigned long ms;
  unsigned long counter;
};


void setup(void)
{
   Serial.begin(57600);
  Serial.println("Receptor transmissor");
 
  SPI.begin();
  radio.begin();
  network.begin(/*channel*/ 90, /*node address*/ this_node);
}


void send(const char* msg_inc){
  RF24NetworkHeader header(other_node);
  bool ok = network.write(header,&msg_inc,sizeof(msg_inc));
  if (ok){
     Serial.println(F("Enviou!!!"));
  }else{ 
    Serial.println(F("Falhou o envio!!!"));
  }
}

void receive(){
   if (network.available()) {
      RF24NetworkHeader header;
      char msg_inc[40] = "";
      network.read(header,&msg_inc,sizeof(msg_inc));
      Serial.println(F("Recebeu"));
      Serial.println(msg_inc);
      send(msg_inc);
    }  
}

void loop(void){
  network.update();
  receive();
}
