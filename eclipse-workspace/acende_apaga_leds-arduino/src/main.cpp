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
#include <ArduinoEEPROM.h>
#include <ArduinoStatus.h>
#include <Thread.h>
#include <ThreadController.h>

#include <EEPROMData.h>
#include <ArduinoUSART.h>
#include <SisbarcEEPROM.h>
#include <SisbarcProtocol.h>
#include <SisbarcUSART.h>

#define BAUD_RATE 9600

#define PIN_LED_PISCA    13   //Pino 13 Digital

#define PIN_LED_AMARELA  11   //Pino 11 PWM
#define PIN_LED_VERDE    10   //Pino 10 PWM
#define PIN_LED_VERMELHA 9    //Pino 09 PWM

#define PIN_BOTAO_LED_AMARELA  12 //Pino 12 Digital
#define PIN_BOTAO_LED_VERDE    8  //Pino 8 Digital
#define PIN_BOTAO_LED_VERMELHA 7  //Pino 7 Digital

#define PIN_POTENCIOMETRO      0  //Pino 0 Analogico

#define LED_PISCA_TIME     1000 // 1 segundo
#define BOTAO_TIME         500  // 1/2 segundo
#define POTENCIOMETRO_TIME 100  // 1/10 de segundo
//#define LED_TIME           500  // 1/2 segundo

#define RETURN_ERROR -1

#define ON_OFF 0 // Acende ou apaga
#define BLINK  1 // Pisca-pisca
#define FADE   2// Acende ao poucos

#define TIME_100MILLIS  0 // 1/10 de segundo
#define TIME_250MILLIS  1 // 1/4 de segundo
#define TIME_500MILLIS  2 // 1/2 de segundo
#define TIME_1SECOUND   3 // 1 segundo
#define TIME_2SECOUNDS  4 // 2 segundos
#define TIME_3SECOUNDS  5 // 3 segundos
#define TIME_5SECOUNDS  6 // 5 segundos
#define NO_TIME         7 // Não roda dentro da thread

using namespace SISBARC;

//Declared weak in Arduino.h to allow user redefinitions.
int atexit(void (*func)()) {
	return 0;
}

// Weak empty variant initialization function.
// May be redefined by variant files.
void initVariant() __attribute__((weak));

bool acendeApagaLEDPorSerial(ArduinoStatus* arduino);

bool writeLEDEvent(ArduinoStatus* arduino);
bool readLEDEvent(ArduinoStatus* arduino);

void acendeApagaLEDPorBotao(void);
void changeValuePotenciometro(void);

void acendeApagaLEDPisca(void);

int16_t getThreadTime(uint8_t threadTime);
void changeThreadTime(pin_type pinType, uint8_t pin, int8_t threadTime);
void changeThreadTime(pin_type pinType, uint8_t pin);

ThreadController* controll = new ThreadController();

//Thread* ledThread = new Thread();
Thread* botaoThread = new Thread();
Thread* potenciometroThread = new Thread();

Thread* ledPiscaThread = new Thread();

const uint8_t TOTAL_LEDS = 0x03;

const uint8_t PIN_LEDS[] = { PIN_LED_AMARELA, PIN_LED_VERDE, PIN_LED_VERMELHA };
const uint8_t PIN_BOTOES[] = { PIN_BOTAO_LED_AMARELA, PIN_BOTAO_LED_VERDE,
PIN_BOTAO_LED_VERMELHA };

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
	//ledPiscaThread->setInterval(LED_PISCA_TIME);
	changeThreadTime(ArduinoEEPROM::DIGITAL, PIN_LED_PISCA);

	//controll->add(ledThread);
	controll->add(botaoThread);
	controll->add(potenciometroThread);
	controll->add(ledPiscaThread);

	SISBARC_USART.onRun(acendeApagaLEDPorSerial);
	SISBARC_USART.onRun(writeLEDEvent);
	SISBARC_USART.onRun(readLEDEvent);
}

void loop(void) {
	controll->run();
}

void serialEventRun(void) {
	SISBARC_USART.receiveDataBySerial((uint8_t) Serial.read());	//lê os dados da porta serial - Maximo 64 bytes
}

bool executeFromPC(ArduinoStatus* arduino) {
	if (ArduinoStatus::OTHER_DEVICE != arduino->getTransmitterValue())
		return false;

	if (!(ArduinoStatus::SEND_RESPONSE == arduino->getStatusValue()
			|| ArduinoStatus::RESPONSE_RESPONSE == arduino->getStatusValue()))
		return false;

	if (arduino->getEventValue() != ArduinoStatus::EXECUTE)
		return false;

	return true;
}

bool acendeApagaLEDPorSerial(ArduinoStatus* arduino) {

	if (!executeFromPC(arduino))
		return false;

	if (arduino->getPinType() != ArduinoStatus::DIGITAL)
		return false;

	if (!(arduino->getPin() == PIN_LED_AMARELA
			|| arduino->getPin() == PIN_LED_VERDE
			|| arduino->getPin() == PIN_LED_VERMELHA))
		return false;

	ArduinoUSART* arduinoUSART;
	arduinoUSART = (ArduinoUSART*) arduino;

	digitalWrite(arduinoUSART->getPin(), (uint8_t) arduinoUSART->getPinValue());
	SisbarcUSART::sendPinDigital(ArduinoUSART::RESPONSE, arduinoUSART->getPin(),
			arduinoUSART->getPinValue());

	return true;
}

bool writeLEDEvent(ArduinoStatus* arduino) {
	if (ArduinoStatus::OTHER_DEVICE != arduino->getTransmitterValue())
		return false;

	if (!(ArduinoStatus::SEND_RESPONSE == arduino->getStatusValue()
			|| ArduinoStatus::RESPONSE_RESPONSE == arduino->getStatusValue()))
		return false;

	if (arduino->getEventValue() != ArduinoStatus::WRITE)
		return false;

	if (arduino->getPinType() != ArduinoStatus::DIGITAL)
		return false;

	if (!(arduino->getPin() == PIN_LED_AMARELA
			|| arduino->getPin() == PIN_LED_VERDE
			|| arduino->getPin() == PIN_LED_VERMELHA
			|| arduino->getPin() == PIN_LED_PISCA))
		return false;

	ArduinoEEPROMWrite* arduinoEEPROMWrite;
	arduinoEEPROMWrite = ((ArduinoEEPROMWrite*) arduino);

	int16_t returnValue = SisbarcEEPROM::write(arduinoEEPROMWrite);
	if (returnValue == 0x0000 || returnValue == 0x0001) {
		//arduinoEEPROMWrite->setStatusValue(ArduinoEEPROM::RESPONSE);
		//SisbarcUSART::send(arduinoEEPROMWrite);

		EEPROMData* data;
		data = SisbarcEEPROM::read(arduinoEEPROMWrite->getPinType(),
				arduinoEEPROMWrite->getPin());

		if (data != NULL) {
			changeThreadTime(arduinoEEPROMWrite->getPinType(),
					arduinoEEPROMWrite->getPin(), data->getThreadTime());

			ArduinoEEPROMRead* arduinoEEPROMRead;
			arduinoEEPROMRead = new ArduinoEEPROMRead(ArduinoEEPROM::RESPONSE,
					arduinoEEPROMWrite->getPinType(),
					arduinoEEPROMWrite->getPin(), data);

			SisbarcUSART::send(arduinoEEPROMRead);

			free(arduinoEEPROMRead);
			free(data);
		}
	}

	return true;
}

bool readLEDEvent(ArduinoStatus* arduino) {
	if (ArduinoStatus::OTHER_DEVICE != arduino->getTransmitterValue())
		return false;

	if (!(ArduinoStatus::SEND_RESPONSE == arduino->getStatusValue()
			|| ArduinoStatus::RESPONSE_RESPONSE == arduino->getStatusValue()))
		return false;

	if (arduino->getEventValue() != ArduinoStatus::READ)
		return false;

	if (arduino->getPinType() != ArduinoStatus::DIGITAL)
		return false;

	if (!(arduino->getPin() == PIN_LED_AMARELA
			|| arduino->getPin() == PIN_LED_VERDE
			|| arduino->getPin() == PIN_LED_VERMELHA
			|| arduino->getPin() == PIN_LED_PISCA))
		return false;
	/*
	 ArduinoEEPROMRead* arduinoEEPROM;
	 arduinoEEPROM = ((ArduinoEEPROMRead*) arduino);

	 arduinoEEPROM = SisbarcEEPROM::read(arduinoEEPROM->getPinType(),
	 arduinoEEPROM->getPin());

	 if (arduinoEEPROM != NULL) {
	 arduinoEEPROM->setStatusValue(ArduinoEEPROM::RESPONSE);
	 SisbarcUSART::send(arduinoEEPROM);
	 }
	 */

	return true;
}

void acendeApagaLEDPorBotao(void) {
	for (uint8_t i = 0x00; i < TOTAL_LEDS; i++)
		if (digitalRead(PIN_BOTOES[i]) == LOW) {
			if (digitalRead(PIN_LEDS[i]) == LOW)
				SisbarcUSART::sendPinDigital(ArduinoStatus::SEND_RESPONSE,
						PIN_LEDS[i], HIGH);
			else
				SisbarcUSART::sendPinDigital(ArduinoStatus::SEND_RESPONSE,
						PIN_LEDS[i], LOW);
		}
}

void changeValuePotenciometro(void) {
	uint16_t potenciometroValue = (uint16_t) analogRead(PIN_POTENCIOMETRO);
	if (potenciometroValue != lastValuePotenciometro) {
		lastValuePotenciometro = potenciometroValue;
		SisbarcUSART::sendPinAnalog(ArduinoStatus::SEND, PIN_POTENCIOMETRO,
				potenciometroValue);
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

	SisbarcUSART::sendPinDigital(ArduinoStatus::SEND, PIN_LED_PISCA,
			lastValueLEDPisca);

}

int16_t getThreadTime(uint8_t threadTime) {
	int16_t timeInMillis = RETURN_ERROR;
	switch (threadTime) {
	case TIME_100MILLIS:
		timeInMillis = 100;		//100 millis
		break;
	case TIME_250MILLIS:
		timeInMillis = 250;		//250 millis
		break;
	case TIME_500MILLIS:
		timeInMillis = 500;		//500 millis
		break;
	case TIME_1SECOUND:
		timeInMillis = 1000; //1 segundo
		break;
	case TIME_2SECOUNDS:
		timeInMillis = 2000; //2 segundos
		break;
	case TIME_3SECOUNDS:
		timeInMillis = 3000; //3 segundos
		break;
	case TIME_5SECOUNDS:
		timeInMillis = 5000; //5 segundos
		break;
	case NO_TIME:
		timeInMillis = 10000; //10 segundos
		break;
	default:
		break;
	}
	return timeInMillis;
}

void changeThreadTime(pin_type pinType, uint8_t pin, int8_t threadTime) {
	if (threadTime == RETURN_ERROR) {
		EEPROMData* data;
		data = SisbarcEEPROM::read(pinType, pin);
		if (data != NULL) {
			threadTime = data->getThreadTime();
			free(data);
		} else
			threadTime = TIME_1SECOUND;

	}

	int16_t millis = getThreadTime(threadTime);

	switch (pin) {
	case PIN_LED_PISCA:
		if (millis != RETURN_ERROR)
			ledPiscaThread->setInterval(millis);
		else
			ledPiscaThread->setInterval(LED_PISCA_TIME);
		break;
	case PIN_LED_AMARELA:
		break;
	case PIN_LED_VERDE:
		break;
	case PIN_LED_VERMELHA:
		break;
	default:
		break;
	}

}

void changeThreadTime(pin_type pinType, uint8_t pin) {
	changeThreadTime(pinType, pin, RETURN_ERROR);
}
