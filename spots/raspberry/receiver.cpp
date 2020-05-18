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

char msg[30];

void send()
{
    printf("enviando: %s", msg);
    RF24NetworkHeader header2(other_node);
    if(!network.write(header2, &msg, sizeof(msg))){
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
        network.read(header, &msg, sizeof(msg));
        printf("Recebeu: %s", msg);
        send();
    }
}

int main(int argc, char **argv)
{
    radio.begin();
    radio.setDataRate(RF24_250KBPS);
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
