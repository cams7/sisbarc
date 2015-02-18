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
#include <Sisbarc.h>
#include <SisbarcProtocol.h>
#include <SisbarcEEPROM.h>

#include <vo/ArduinoStatus.h>
#include <vo/ArduinoUSART.h>
#include <vo/ArduinoEEPROM.h>

#define PINO_LED_PISCA    13   //Pino 13 Digital

#define PINO_LED_AMARELO  11   //Pino 11 PWM
#define PINO_LED_VERDE    10   //Pino 10 PWM
#define PINO_LED_VERMELHO 9    //Pino 09 PWM

#define PINO_BOTAO_LED_AMARELO  12 //Pino 12 Digital
#define PINO_BOTAO_LED_VERDE    8  //Pino 8 Digital
#define PINO_BOTAO_LED_VERMELHO 7  //Pino 7 Digital

#define PINO_POTENCIOMETRO      0  //Pino 0 Analogico

#define INTERVALO_LED_PISCA    1000 // 1 segundo
#define INTERVALO_BOTAO        500  // 1/2 segundo
#define INTERVALO_POENCIOMETRO 100  // 1/10 de segundo

#define ACENDE_APAGA 0 // Acende ou apaga
#define PISCA_PISCA  1 // Pisca-pisca
#define FADE         2 // Acende ou apaga ao poucos

#define INTERVALO_100MILISEGUNDOS 100  // 1/10 de segundo
#define INTERVALO_250MILISEGUNDOS 250  // 1/4 de segundo
#define INTERVALO_500MILISEGUNDOS 500  // 1/2 de segundo
#define INTERVALO_1SEGUNDO        1000 // 1 segundo
#define INTERVALO_2SEGUNDOS       2000 // 2 segundos
#define INTERVALO_3SEGUNDOS       3000 // 3 segundos
#define INTERVALO_5SEGUNDOS       5000 // 5 segundos
#define SEM_INTERVALO             0    // Não roda dentro da thread

//Declared weak in Arduino.h to allow user redefinitions.
int atexit(void (*func)()) {
	return 0;
}

// Weak empty variant initialization function.
// May be redefined by variant files.
void initVariant() __attribute__((weak));
void initVariant() {
}

using namespace SISBARC;

void alteraValorLEDPorIndice(uint8_t const);

bool callLEDPisca(ArduinoStatus* const);

bool callLEDAmarelo(ArduinoStatus* const);
bool callLEDVerde(ArduinoStatus* const);
bool callLEDVermelho(ArduinoStatus* const);

bool callLEDBotao(ArduinoStatus* const);

bool callPotenciometro(ArduinoStatus* const);

const uint8_t TOTAL_LEDS = 0x03;

const uint8_t PINOS_LEDS[] = { PINO_LED_AMARELO, PINO_LED_VERDE,
PINO_LED_VERMELHO };
const uint8_t PINOS_BOTOES[] = { PINO_BOTAO_LED_AMARELO, PINO_BOTAO_LED_VERDE,
PINO_BOTAO_LED_VERMELHO };

uint8_t eventosLEDs[] = { ACENDE_APAGA, ACENDE_APAGA, ACENDE_APAGA };
uint8_t ultimosValoresLEDs[] = { 0x00, 0x00, 0x00 };

int8_t fadeAmountsLEDs[] = { 0x05, 0x05, 0x05 };
bool ledsAtivas[] = { false, false, false };

uint16_t ultimoValorPotenciometro = 0x0000;    //Ultimo valor do Potenciometro
uint8_t ultimoValorLEDPisca = (int8_t) HIGH;    //Utimo valor do LED do pino 13

int main(void) {
	init();

	initVariant();

#if defined(USBCON)
	USBDevice.attach();
#endif

	setup();

	for (;;)
		loop();

	return 0;
}

void setup(void) {
	for (int i = 0; i < TOTAL_LEDS; i++) {
		pinMode(PINOS_LEDS[i], OUTPUT);
		pinMode(PINOS_BOTOES[i], INPUT);

		digitalWrite(PINOS_BOTOES[i], HIGH);
	}

	pinMode(PINO_LED_PISCA, OUTPUT);

	Sisbarc.begin(&Serial/*, BAUD_RATE*/);

	Sisbarc.addThreadInterval(0x00, INTERVALO_100MILISEGUNDOS);
	Sisbarc.addThreadInterval(0x01, INTERVALO_250MILISEGUNDOS);
	Sisbarc.addThreadInterval(0x02, INTERVALO_500MILISEGUNDOS);
	Sisbarc.addThreadInterval(0x03, INTERVALO_1SEGUNDO);
	Sisbarc.addThreadInterval(0x04, INTERVALO_2SEGUNDOS);
	Sisbarc.addThreadInterval(0x05, INTERVALO_3SEGUNDOS);
	Sisbarc.addThreadInterval(0x06, INTERVALO_5SEGUNDOS);
	Sisbarc.addThreadInterval(0x07, SEM_INTERVALO);

	int16_t evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, PINO_LED_PISCA,
			callLEDPisca,
			INTERVALO_LED_PISCA);

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, PINO_LED_AMARELO,
			callLEDAmarelo);
	if (evento != -1) {
		eventosLEDs[0] = evento;
		alteraValorLEDPorIndice(0);
	}

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, PINO_LED_VERDE,
			callLEDVerde);
	if (evento != -1) {
		eventosLEDs[1] = evento;
		alteraValorLEDPorIndice(1);
	}

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, PINO_LED_VERMELHO,
			callLEDVermelho);
	if (evento != -1) {
		eventosLEDs[2] = evento;
		alteraValorLEDPorIndice(2);
	}

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, PINO_BOTAO_LED_AMARELO,
			callLEDBotao,
			INTERVALO_BOTAO);

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, PINO_BOTAO_LED_VERDE,
			callLEDBotao,
			INTERVALO_BOTAO);

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, PINO_BOTAO_LED_VERMELHO,
			callLEDBotao,
			INTERVALO_BOTAO);

	evento = Sisbarc.onRun(ArduinoStatus::ANALOG, PINO_POTENCIOMETRO,
			callPotenciometro,
			INTERVALO_POENCIOMETRO);
}

void loop(void) {
	Sisbarc.run();
}

bool isCallBySerial(ArduinoStatus* const arduino) {
	if (ArduinoStatus::OTHER_DEVICE != arduino->getTransmitterValue())
		return false;

	if (ArduinoStatus::SEND_RESPONSE != arduino->getStatusValue()
			&& ArduinoStatus::RESPONSE_RESPONSE != arduino->getStatusValue())
		return false;

	return true;
}

bool isCallBySerialToPinDigital(ArduinoStatus* const arduino) {
	if (!isCallBySerial(arduino))
		return false;

	if (arduino->getPinType() != ArduinoStatus::DIGITAL)
		return false;

	return true;
}

bool isCallBySerialToPinAnalog(ArduinoStatus* const arduino) {
	if (!isCallBySerial(arduino))
		return false;

	if (arduino->getPinType() != ArduinoStatus::ANALOG)
		return false;

	return true;
}

uint8_t getIndicePino(uint8_t const pin) {
	uint8_t indicePino = 0x00;
	for (; indicePino < TOTAL_LEDS; indicePino++)
		if (PINOS_LEDS[indicePino] == pin)
			break;
	return indicePino;
}

void alteraValorLEDPorIndice(uint8_t const ledIndex) {
	if (eventosLEDs[ledIndex] == FADE) {
		ultimosValoresLEDs[ledIndex] = 0xFF;
		fadeAmountsLEDs[ledIndex] = -5;
	} else
		ultimosValoresLEDs[ledIndex] = 0x00;
}

void alteraEEPROM(ArduinoStatus* const arduino) {
	ArduinoEEPROMWrite* arduinoEEPROMWrite = ((ArduinoEEPROMWrite*) arduino);

	int16_t returnValue = SisbarcEEPROM::write(arduinoEEPROMWrite);
	if (returnValue == 0x0000 || returnValue == 0x0001) {
		if (arduino->getPin() == PINO_LED_AMARELO
				|| arduino->getPin() == PINO_LED_VERDE
				|| arduino->getPin() == PINO_LED_VERMELHO) {

			uint8_t ledIndex = getIndicePino(arduinoEEPROMWrite->getPin());
			eventosLEDs[ledIndex] = arduinoEEPROMWrite->getActionEvent();

			alteraValorLEDPorIndice(ledIndex);
		}

		arduinoEEPROMWrite->setTransmitterValue(ArduinoStatus::ARDUINO);
		arduinoEEPROMWrite->setStatusValue(ArduinoStatus::RESPONSE);
		Sisbarc.send(arduinoEEPROMWrite);
	}
}

//Pisca o LED do pino 13
void pisca(void) {
	switch (ultimoValorLEDPisca) {
	case HIGH:
		ultimoValorLEDPisca = (int8_t) LOW;
		break;
	case LOW:
		ultimoValorLEDPisca = (int8_t) HIGH;
		break;
	default:
		break;
	}

	digitalWrite(PINO_LED_PISCA, ultimoValorLEDPisca);

	Sisbarc.sendPinDigital(ArduinoStatus::SEND, PINO_LED_PISCA,
			ultimoValorLEDPisca);

}

bool callLEDPisca(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		pisca();

		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != PINO_LED_PISCA)
		return false;

	if (arduino->getEventValue() == ArduinoStatus::WRITE) {
		alteraEEPROM(arduino);

		return true;
	}

	return false;
}

void alteraValorLED(uint8_t const pin) {
	uint8_t indicePino = getIndicePino(pin);

	if (ledsAtivas[indicePino]) {
		switch (eventosLEDs[indicePino]) {
		case ACENDE_APAGA: {
			break;
		}
		case PISCA_PISCA: {
			switch (ultimosValoresLEDs[indicePino]) {
			case HIGH:
				ultimosValoresLEDs[indicePino] = (int8_t) LOW;
				break;
			case LOW:
				ultimosValoresLEDs[indicePino] = (int8_t) HIGH;
				break;
			default:
				break;
			}

			digitalWrite(pin, ultimosValoresLEDs[indicePino]);
			break;
		}
		case FADE: {
			analogWrite(pin, ultimosValoresLEDs[indicePino]);
			ultimosValoresLEDs[indicePino] += fadeAmountsLEDs[indicePino];

			if (ultimosValoresLEDs[indicePino] == 0x00
					|| ultimosValoresLEDs[indicePino] == 0xFF)
				fadeAmountsLEDs[indicePino] *= -1;
			break;
		}
		default:
			break;

		}
	}
}

void alteraValorLEDPorSerial(ArduinoStatus* const arduino) {
	ArduinoUSART* arduinoUSART = ((ArduinoUSART*) arduino);

	arduinoUSART->setTransmitterValue(ArduinoStatus::ARDUINO);
	arduinoUSART->setStatusValue(ArduinoStatus::RESPONSE);

	digitalWrite(arduinoUSART->getPin(), (uint8_t) arduinoUSART->getPinValue());

	uint8_t indicePino = getIndicePino(arduinoUSART->getPin());

	ledsAtivas[indicePino] = arduinoUSART->getPinValue() != 0x0000;

	Sisbarc.send(arduino);
}

bool callLEDAmarelo(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorLED(PINO_LED_AMARELO);
		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != PINO_LED_AMARELO)
		return false;

	if (arduino->getEventValue() == ArduinoStatus::WRITE) {
		alteraEEPROM(arduino);

		return true;
	}

	if (arduino->getEventValue() == ArduinoStatus::EXECUTE) {
		alteraValorLEDPorSerial(arduino);

		return true;
	}

	return false;
}

bool callLEDVerde(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorLED(PINO_LED_VERDE);
		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != PINO_LED_VERDE)
		return false;

	if (arduino->getEventValue() == ArduinoStatus::WRITE) {
		alteraEEPROM(arduino);

		return true;
	}

	if (arduino->getEventValue() == ArduinoStatus::EXECUTE) {
		alteraValorLEDPorSerial(arduino);

		return true;
	}

	return false;
}

bool callLEDVermelho(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorLED(PINO_LED_VERMELHO);
		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != PINO_LED_VERMELHO)
		return false;

	if (arduino->getEventValue() == ArduinoStatus::WRITE) {
		alteraEEPROM(arduino);

		return true;
	}

	if (arduino->getEventValue() == ArduinoStatus::EXECUTE) {
		alteraValorLEDPorSerial(arduino);

		return true;
	}

	return false;
}

void alteraValorLEDPorBotao(void) {
	for (uint8_t i = 0x00; i < TOTAL_LEDS; i++) {
		if (digitalRead(PINOS_BOTOES[i]) == LOW) {
			if (digitalRead(PINOS_LEDS[i]) == LOW) {
				Sisbarc.sendPinDigital(ArduinoStatus::SEND_RESPONSE,
						PINOS_LEDS[i], HIGH);
			} else {
				Sisbarc.sendPinDigital(ArduinoStatus::SEND_RESPONSE,
						PINOS_LEDS[i], LOW);
			}
		}
	}
}

bool callLEDBotao(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorLEDPorBotao();
		return false;
	}

	return false;
}

void alteraValorPotenciometro(void) {
	uint16_t valorPotenciometro = (uint16_t) analogRead(PINO_POTENCIOMETRO);
	if (valorPotenciometro != ultimoValorPotenciometro) {
		ultimoValorPotenciometro = valorPotenciometro;
		Sisbarc.sendPinAnalog(ArduinoStatus::SEND, PINO_POTENCIOMETRO,
				valorPotenciometro);
	}
}

bool callPotenciometro(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorPotenciometro();
		return false;
	}

	return false;
}

