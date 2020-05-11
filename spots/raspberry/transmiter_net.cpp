#include <RF24/RF24.h>
#include <RF24Network/RF24Network.h>
#include <iostream>
#include <ctime>
#include <stdio.h>
#include <time.h>

RF24 radio(RPI_V2_GPIO_P1_15, BCM2835_SPI_CS0, BCM2835_SPI_SPEED_8MHZ);

RF24Network network(radio);

const uint16_t this_node = 00;
const uint16_t other_node = 01;

struct payload_t {
  unsigned long id;
  char sen[2];
  char val[6];
};

void send(payload_t payload)
{
    printf("enviando: %lu %s %s\n", payload.id, payload.sen, payload.val);
    RF24NetworkHeader header2(other_node);
    if(!network.write(header2, &payload, sizeof(payload))){
        printf("failed.\n");
    }else{
        printf("enviou.\n");
    }
}

void receive()
{
    if (network.available())
    {
        RF24NetworkHeader header;
        payload_t payload;
        network.read(header, &payload, sizeof(payload));
        printf("Recebeu: %lu %s %s\n", payload.id, payload.sen, payload.val);
        delay(1000);
        send(payload);
    }
}

int main(int argc, char **argv)
{
    radio.begin();
    delay(5);
    network.begin(90, this_node);
    radio.printDetails();

    while (1)
    {
        network.update();
        receive();
    }
    return 0;
}