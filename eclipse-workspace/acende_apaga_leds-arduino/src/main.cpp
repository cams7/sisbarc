/*
 main.cpp - Main loop for Arduino sketches
 Copyright (c) 2005-2013 Arduino Team.  All right reserved.

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

#include <Arduino.h>
#include <ArduinoProtocol.h>
#include <Binary.h>
#include <ArduinoStatus.h>
#include <Thread.h>
#include <ThreadController.h>

#define BAUD_RATE 9600

#define PIN_LED_PISCA    13   //Pino 13 Digital

#define PIN_LED_AMARELA  11   //Pino 11 PWM
#define PIN_LED_VERDE    10   //Pino 10 PWM
#define PIN_LED_VERMELHA 9    //Pino 09 PWM

#define PIN_BOTAO_LED_AMARELA  12 //Pino 12 Digital
#define PIN_BOTAO_LED_VERDE    8  //Pino 8 Digital
#define PIN_BOTAO_LED_VERMELHA 7  //Pino 7 Digital

#define PIN_POTENCIOMETRO      0  //Pino 0 Analogico

#define LED_PISCA_TIME     3000 // 1 segundo
#define BOTAO_TIME         500  // 1/2 segundo
#define POTENCIOMETRO_TIME 100  // 1/10 de segundo
//#define LED_TIME           500  // 1/2 segundo

using namespace SISBARC;

//Declared weak in Arduino.h to allow user redefinitions.
int atexit(void (*func)()) {
	return 0;
}

// Weak empty variant initialization function.
// May be redefined by variant files.
void initVariant() __attribute__((weak));

void sendPinDigital(uint8_t pin, bool pinValue, status statusValue);
void sendPinPWM(uint8_t pin, uint8_t pinValue, status statusValue);
void sendPinAnalog(uint8_t pin, uint16_t pinValue, status statusValue);

void receiveDataBySerial(uint8_t data);
void receiveDataBySerial(ArduinoStatus* arduino);

uint8_t* serialDataReceive;
uint8_t serialDataReceiveIndex = 0x00;

void acendeApagaLEDPorSerial(ArduinoStatus* arduino);
void acendeApagaLEDPorBotao(void);
void changeValuePotenciometro(void);

void acendeApagaLEDPisca(void);

ThreadController* controll = new ThreadController();

//Thread* ledThread = new Thread();
Thread* botaoThread = new Thread();
Thread* potenciometroThread = new Thread();

Thread* ledPiscaThread = new Thread();

const uint8_t TOTAL_LEDS = 0x03;

const uint8_t PIN_LEDS[] = { PIN_LED_AMARELA, PIN_LED_VERDE, PIN_LED_VERMELHA };
const uint8_t PIN_BOTOES[] = { PIN_BOTAO_LED_AMARELA, PIN_BOTAO_LED_VERDE,
PIN_BOTAO_LED_VERMELHA };

//uint8_t lastValuesBotoes[TOTAL_LEDS];
uint16_t lastValuePotenciometro = 0x0000;

int8_t lastValueLEDPisca = (int8_t) HIGH;

int main(void) {
	init();

	initVariant();

#if defined(USBCON)
	USBDevice.attach();
#endif

	setup();

	for (;;) {
		loop();
		//if (serialEventRun)
		if (Serial.available() > 0) //verifica se existe comunicação com a porta serial
			serialEventRun();
	}

	return 0;
}

void initVariant() {
}

void setup(void) {
	Serial.begin(BAUD_RATE); //frequência da porta serial - USART

	for (int i = 0; i < TOTAL_LEDS; i++) {
		pinMode(PIN_LEDS[i], OUTPUT);

		pinMode(PIN_BOTOES[i], INPUT);

		//lastValuesBotoes[i] = (uint8_t) HIGH;
		digitalWrite(PIN_BOTOES[i], HIGH);
	}

	pinMode(PIN_LED_PISCA, OUTPUT);

	//ledThread->onRun(acendeApagaLED);
	//ledThread->setInterval(LED_TIME);

	botaoThread->onRun(acendeApagaLEDPorBotao);
	botaoThread->setInterval(BOTAO_TIME);

	potenciometroThread->onRun(changeValuePotenciometro);
	potenciometroThread->setInterval(POTENCIOMETRO_TIME);

	ledPiscaThread->onRun(acendeApagaLEDPisca);
	ledPiscaThread->setInterval(LED_PISCA_TIME);

	//controll->add(ledThread);
	controll->add(botaoThread);
	controll->add(potenciometroThread);
	controll->add(ledPiscaThread);
}

void loop(void) {
	controll->run();
}

void serialEventRun(void) {
	uint8_t data = (uint8_t) Serial.read(); //lê os dados da porta serial - Maximo 64 bytes
	receiveDataBySerial(data);
}

void sendPinDigital(uint8_t pin, bool pinValue, status statusValue) {
	uint8_t* serialDataSend;
	serialDataSend = ArduinoProtocol::sendPinDigital(pin, pinValue,
			statusValue);
	if (serialDataSend != NULL) {
		Serial.write(serialDataSend, ArduinoProtocol::TOTAL_BYTES_PROTOCOL);
		free(serialDataSend);
	}
}

void sendPinPWM(uint8_t pin, uint8_t pinValue, status statusValue) {
	uint8_t* serialDataSend;
	serialDataSend = ArduinoProtocol::sendPinPWM(pin, pinValue, statusValue);
	if (serialDataSend != NULL) {
		Serial.write(serialDataSend, ArduinoProtocol::TOTAL_BYTES_PROTOCOL);
		free(serialDataSend);
	}
}

void sendPinAnalog(uint8_t pin, uint16_t pinValue, status statusValue) {
	uint8_t* serialDataSend;
	serialDataSend = ArduinoProtocol::sendPinAnalog(pin, pinValue, statusValue);
	if (serialDataSend != NULL) {
		Serial.write(serialDataSend, ArduinoProtocol::TOTAL_BYTES_PROTOCOL);
		free(serialDataSend);
	}
}

void receiveDataBySerial(uint8_t data) {
	uint8_t lastBit = data & 0x80;

	if (lastBit) {
		serialDataReceive = (uint8_t*) malloc(
				ArduinoProtocol::TOTAL_BYTES_PROTOCOL);
		*(serialDataReceive) = data;

		serialDataReceiveIndex = 0x01;
	} else if (serialDataReceiveIndex > 0x00 && serialDataReceive != NULL) {
		*(serialDataReceive + serialDataReceiveIndex) = data;

		if (serialDataReceiveIndex
				== (ArduinoProtocol::TOTAL_BYTES_PROTOCOL - 1)) {
			ArduinoStatus* arduino = ArduinoProtocol::receive(
					serialDataReceive);
			if (arduino != NULL) {
				receiveDataBySerial(arduino);
				free(arduino);
			}
			free(serialDataReceive);
		} else
			serialDataReceiveIndex++;
	} else {
	}

}

void receiveDataBySerial(ArduinoStatus* arduino) {
	if (ArduinoStatus::PC != arduino->getTransmitterValue())
		return;

	if (ArduinoStatus::SEND_RESPONSE != arduino->getStatusValue()
			&& ArduinoStatus::RESPONSE_RESPONSE != arduino->getStatusValue())
		return;

	if (arduino->getPinType() == ArduinoStatus::DIGITAL) {
		switch (arduino->getPin()) {
		case PIN_LED_AMARELA:
		case PIN_LED_VERDE:
		case PIN_LED_VERMELHA: {
			acendeApagaLEDPorSerial(arduino);
			break;
		}
		default:
			break;
		}
	} else if (arduino->getPinType() == ArduinoStatus::ANALOG) {
	}

}

void acendeApagaLEDPorSerial(ArduinoStatus* arduino) {
	digitalWrite(arduino->getPin(), (uint8_t) arduino->getPinValue());
	sendPinDigital(arduino->getPin(), arduino->getPinValue(),
			ArduinoStatus::RESPONSE);
}

void acendeApagaLEDPorBotao(void) {
	for (uint8_t i = 0x00; i < TOTAL_LEDS; i++)
		if (digitalRead(PIN_BOTOES[i]) == LOW) {
			if (digitalRead(PIN_LEDS[i]) == LOW)
				sendPinDigital(PIN_LEDS[i], HIGH, ArduinoStatus::SEND_RESPONSE);
			else
				//digitalWrite(PIN_LEDS[i], LOW);
				sendPinDigital(PIN_LEDS[i], LOW, ArduinoStatus::SEND_RESPONSE);
		}
}

void changeValuePotenciometro(void) {
	uint16_t potenciometroValue = (uint16_t) analogRead(PIN_POTENCIOMETRO);
	if (potenciometroValue != lastValuePotenciometro) {
		lastValuePotenciometro = potenciometroValue;
		sendPinAnalog(PIN_POTENCIOMETRO, potenciometroValue,
				ArduinoStatus::SEND);
	}
}

void acendeApagaLEDPisca(void) {
	switch (lastValueLEDPisca) {
	case HIGH:
		lastValueLEDPisca = (int8_t) LOW;
		break;
	case LOW:
		lastValueLEDPisca = (int8_t) HIGH;
		break;
	default:
		break;
	}

	digitalWrite(PIN_LED_PISCA, lastValueLEDPisca);

	sendPinDigital(PIN_LED_PISCA, lastValueLEDPisca, ArduinoStatus::SEND);

}

