#include <cstdlib>
#include <iostream>
#include <sstream>
#include <string>
#include "./RF24.h"

using namespace std;

RF24 radio(RPI_V2_GPIO_P1_15, RPI_V2_GPIO_P1_24, BCM2835_SPI_SPEED_8MHZ);

const uint64_t r_pipes[2] = {0xABCDABCD71BB};
// const uint64_t t_pipe = 0xABCDABCD71LL;
const uint64_t t_pipe = 0xABCDABCD71LL;

char receive_payload[40];

void send(const char *msgTemp)
{
    printf("\nEnviando: %s", msgTemp);
    radio.stopListening();
    radio.openWritingPipe(t_pipe);
    if (radio.write(&msgTemp, strlen(msgTemp)))
    {
        printf("\n Enviouiuuu\n");
    }
    else
    {
        printf("\n Erro ao enviar\n");
    }
    radio.startListening();
}

void receiver()
{
    while (radio.available())
    {
        uint8_t len = radio.getDynamicPayloadSize();
        radio.read(receive_payload, len);
        receive_payload[len] = 0;
        printf("Recebeu size=%i value=%s\n\r", len, receive_payload);
        send(receive_payload);
    }
}

int main(int argc, char **argv)
{
    cout << "Iniciando transmissor\n";

    radio.begin();
    radio.enableDynamicPayloads();
    radio.setChannel(55);
    radio.setRetries(15, 15);
    radio.setAutoAck(true);
    radio.setCRCLength(RF24_CRC_16);
    radio.printDetails();

    radio.openReadingPipe(1, r_pipes[0]);
    radio.startListening();

    while (1)
    {
        receiver();
    }
}
