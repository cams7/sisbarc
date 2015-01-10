/*
 * main.cpp
 *
 *  Created on: 29/12/2014
 *      Author: cams7
 */
#include <Arduino.h>
#include <Thread.h>
#include <ThreadController.h>

#define BAUD_RATE 9600

#define PIN_LED_VERDE    12
#define PIN_LED_AMARELA  13 //atribui o pino 13 a variável ledPin
#define PIN_LED_VERMELHA 2
#define PIN_LED_AZUL     3

#define PIN_BOTAO_LED_VERDE    7
#define PIN_BOTAO_LED_AMARELA  8
#define PIN_BOTAO_LED_VERMELHA 4

#define PIN_POTENCIOMETRO     0

#define LED_TIME           1000 // 1 segundo
#define BOTAO_TIME         500  // 1/2 segundo
#define POTENCIOMETRO_TIME 100  // 1/10 de segundo
#define LED_AZUL_TIME      3000 // 3 segundo

const int TOTAL_LEDS = 3;

const int PIN_LEDS[] = { PIN_LED_VERDE, PIN_LED_AMARELA, PIN_LED_VERMELHA };
const int PIN_BOTOES[] = { PIN_BOTAO_LED_VERDE, PIN_BOTAO_LED_AMARELA,
PIN_BOTAO_LED_VERMELHA };

const int STATUS_LED = 10;
const int STATUS_BOTAO = 20;

const int MIN_STATUS_POTENCIOMETRO = 100;
const int MAX_STATUS_POTENCIOMETRO = 200;

int ultimoStatusPotenciometro = 0;
int ultimoStatusBotoes[TOTAL_LEDS];

// ThreadController that will controll all threads
ThreadController controll = ThreadController();

//My Thread (as a pointer)
Thread* ledsThread = new Thread();
Thread* botoesThread = new Thread();
Thread* potenciometroThread = new Thread();

//His Thread (not pointer)
Thread ledAzulThread = Thread();

void acendeApagaLEDPorSerial(void);
void acendeApagaLEDPorBotao(void);
void alteraStatusPotenciometro(void);
void piscaLEDAzul(void);

int main(void) {
	init();
	setup();

	while (true) {
		loop();
	}
	return 0;
}

void setup(void) {
	Serial.begin(BAUD_RATE); //frequência da porta serial - USART

	for (int i = 0; i < TOTAL_LEDS; i++) {
		pinMode(PIN_LEDS[i], OUTPUT);

		pinMode(PIN_BOTOES[i], INPUT);

		ultimoStatusBotoes[i] = HIGH;
		digitalWrite(PIN_BOTOES[i], ultimoStatusBotoes[i]);
	}

	pinMode(PIN_LED_AZUL, OUTPUT);

	// Configure Thread
	ledsThread->onRun(acendeApagaLEDPorSerial);
	ledsThread->setInterval(LED_TIME);

	botoesThread->onRun(acendeApagaLEDPorBotao);
	botoesThread->setInterval(BOTAO_TIME);

	potenciometroThread->onRun(alteraStatusPotenciometro);
	potenciometroThread->setInterval(POTENCIOMETRO_TIME);

	ledAzulThread.onRun(piscaLEDAzul);
	ledAzulThread.setInterval(LED_AZUL_TIME);

	// Adds both threads to the controller
	controll.add(ledsThread);
	controll.add(botoesThread);
	controll.add(potenciometroThread);

	controll.add(&ledAzulThread); // & to pass the pointer to it
}

void loop(void) {
	controll.run();
}

void acendeApagaLEDPorSerial(void) {
	if (Serial.available() > 0) { //verifica se existe comunicação com a porta serial
		int dado = Serial.read(); //lê os dados da porta serial - Maximo 64 bytes

		for (int i = 0; i < TOTAL_LEDS; i++) {
			int statusLED = STATUS_LED + (i * 2);
			if (statusLED == dado) {
				digitalWrite(PIN_LEDS[i], LOW);
				Serial.write(statusLED);
			} else if ((statusLED + 1) == dado) {
				digitalWrite(PIN_LEDS[i], HIGH);
				Serial.write(statusLED + 1);
			}
		}
	}
}

void acendeApagaLEDPorBotao(void) {
	for (int i = 0; i < TOTAL_LEDS; i++) {
		int statusBotao = digitalRead(PIN_BOTOES[i]);

		if (statusBotao != ultimoStatusBotoes[i]) {
			//estado = estado | (statusBotao << i);
			//sensors[i] = chegou & (0x01 << i)
			if (statusBotao == HIGH) {
				digitalWrite(PIN_LEDS[i], LOW);
				Serial.write(STATUS_BOTAO + (i * 2));
			} else {
				digitalWrite(PIN_LEDS[i], HIGH);
				Serial.write(STATUS_BOTAO + (i * 2) + 1);
			}
			//Serial.write(estado);
			ultimoStatusBotoes[i] = statusBotao;
		}
	}
}

void alteraStatusPotenciometro(void) {
	int statusPotenciometro = analogRead(PIN_POTENCIOMETRO);
	if (statusPotenciometro != ultimoStatusPotenciometro) {
		ultimoStatusPotenciometro = statusPotenciometro;
		/* Mapeia um valor analogico para 8 bits (0 to 255) */
		Serial.write(
				map(statusPotenciometro, 0, 1023, MIN_STATUS_POTENCIOMETRO,
						MAX_STATUS_POTENCIOMETRO));
	}
}

void piscaLEDAzul(void) {
	digitalWrite(PIN_LED_AZUL, !digitalRead(PIN_LED_AZUL));
}

