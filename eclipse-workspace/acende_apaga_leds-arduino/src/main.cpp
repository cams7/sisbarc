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

#define PIN_LED_PISCA    13   //Pino 13 Digital

#define PIN_LED_AMARELA  11   //Pino 11 PWM
#define PIN_LED_VERDE    10   //Pino 10 PWM
#define PIN_LED_VERMELHA 9    //Pino 09 PWM

#define PIN_BOTAO_LED_AMARELA  12 //Pino 12 Digital
#define PIN_BOTAO_LED_VERDE    8  //Pino 8 Digital
#define PIN_BOTAO_LED_VERMELHA 7  //Pino 7 Digital

#define PIN_POTENCIOMETRO      0  //Pino 0 Analogico

#define THREAD_INTERVAL_LED_PISCA    1000 // 1 segundo
#define THREAD_INTERVAL_BOTAO        500  // 1/2 segundo
#define THREAD_INTERVAL_POENCIOMETRO 100  // 1/10 de segundo

#define ON_OFF 0 // Acende ou apaga
#define BLINK  1 // Pisca-pisca
#define FADE   2// Acende ao poucos

#define THREAD_INTERVAL_100MILLIS 100  // 1/10 de segundo
#define THREAD_INTERVAL_250MILLIS 250  // 1/4 de segundo
#define THREAD_INTERVAL_500MILLIS 500  // 1/2 de segundo
#define THREAD_INTERVAL_1SECOUND  1000 // 1 segundo
#define THREAD_INTERVAL_2SECOUNDS 2000 // 2 segundos
#define THREAD_INTERVAL_3SECOUNDS 3000 // 3 segundos
#define THREAD_INTERVAL_5SECOUNDS 5000 // 5 segundos
#define NO_THREAD_INTERVAL        0    // Não roda dentro da thread

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

bool callLEDAmarela(ArduinoStatus* const);
bool callLEDVerde(ArduinoStatus* const);
bool callLEDVermelha(ArduinoStatus* const);

bool callLEDBotao(ArduinoStatus* const);

bool callPotenciometro(ArduinoStatus* const);

//SisbarcClass* Sisbarc = new SisbarcClass();

const uint8_t TOTAL_LEDS = 0x03;

const uint8_t PIN_LEDS[] = { PIN_LED_AMARELA, PIN_LED_VERDE, PIN_LED_VERMELHA };
const uint8_t PIN_BOTOES[] = { PIN_BOTAO_LED_AMARELA, PIN_BOTAO_LED_VERDE,
PIN_BOTAO_LED_VERMELHA };

uint8_t eventosLEDs[] = { ON_OFF, ON_OFF, ON_OFF };
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
		pinMode(PIN_LEDS[i], OUTPUT);
		pinMode(PIN_BOTOES[i], INPUT);

		digitalWrite(PIN_BOTOES[i], HIGH);
	}

	pinMode(PIN_LED_PISCA, OUTPUT);

	Sisbarc.begin(&Serial/*, BAUD_RATE*/);

	Sisbarc.addThreadInterval(0x00, THREAD_INTERVAL_100MILLIS);
	Sisbarc.addThreadInterval(0x01, THREAD_INTERVAL_250MILLIS);
	Sisbarc.addThreadInterval(0x02, THREAD_INTERVAL_500MILLIS);
	Sisbarc.addThreadInterval(0x03, THREAD_INTERVAL_1SECOUND);
	Sisbarc.addThreadInterval(0x04, THREAD_INTERVAL_2SECOUNDS);
	Sisbarc.addThreadInterval(0x05, THREAD_INTERVAL_3SECOUNDS);
	Sisbarc.addThreadInterval(0x06, THREAD_INTERVAL_5SECOUNDS);
	Sisbarc.addThreadInterval(0x07, NO_THREAD_INTERVAL);

	int16_t actionEvent = Sisbarc.onRun(ArduinoStatus::DIGITAL, PIN_LED_PISCA,
			callLEDPisca,
			THREAD_INTERVAL_LED_PISCA);

	actionEvent = Sisbarc.onRun(ArduinoStatus::DIGITAL, PIN_LED_AMARELA,
			callLEDAmarela);
	if (actionEvent != -1) {
		eventosLEDs[0] = actionEvent;
		alteraValorLEDPorIndice(0);
	}

	actionEvent = Sisbarc.onRun(ArduinoStatus::DIGITAL, PIN_LED_VERDE,
			callLEDVerde);
	if (actionEvent != -1) {
		eventosLEDs[1] = actionEvent;
		alteraValorLEDPorIndice(1);
	}

	actionEvent = Sisbarc.onRun(ArduinoStatus::DIGITAL, PIN_LED_VERMELHA,
			callLEDVermelha);
	if (actionEvent != -1) {
		eventosLEDs[2] = actionEvent;
		alteraValorLEDPorIndice(2);
	}

	actionEvent = Sisbarc.onRun(ArduinoStatus::DIGITAL, PIN_BOTAO_LED_AMARELA,
			callLEDBotao,
			THREAD_INTERVAL_BOTAO);

	actionEvent = Sisbarc.onRun(ArduinoStatus::DIGITAL, PIN_BOTAO_LED_VERDE,
			callLEDBotao,
			THREAD_INTERVAL_BOTAO);

	actionEvent = Sisbarc.onRun(ArduinoStatus::DIGITAL, PIN_BOTAO_LED_VERMELHA,
			callLEDBotao,
			THREAD_INTERVAL_BOTAO);

	actionEvent = Sisbarc.onRun(ArduinoStatus::ANALOG, PIN_POTENCIOMETRO,
			callPotenciometro,
			THREAD_INTERVAL_POENCIOMETRO);
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

uint8_t getIndiceLED(uint8_t const pin) {
	uint8_t ledIndex = 0x00;
	for (; ledIndex < TOTAL_LEDS; ledIndex++)
		if (PIN_LEDS[ledIndex] == pin)
			break;
	return ledIndex;
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
		if (arduino->getPin() == PIN_LED_AMARELA
				|| arduino->getPin() == PIN_LED_VERDE
				|| arduino->getPin() == PIN_LED_VERMELHA) {

			uint8_t ledIndex = getIndiceLED(arduinoEEPROMWrite->getPin());
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

	digitalWrite(PIN_LED_PISCA, ultimoValorLEDPisca);

	Sisbarc.sendPinDigital(ArduinoStatus::SEND, PIN_LED_PISCA,
			ultimoValorLEDPisca);

}

bool callLEDPisca(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		pisca();

		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != PIN_LED_PISCA)
		return false;

	if (arduino->getEventValue() == ArduinoStatus::WRITE) {
		alteraEEPROM(arduino);

		return true;
	}

	return false;
}

void alteraValorLED(uint8_t const pin) {
	uint8_t ledIndex = getIndiceLED(pin);

	if (ledsAtivas[ledIndex]) {
		switch (eventosLEDs[ledIndex]) {
		case ON_OFF: {
			break;
		}
		case BLINK: {
			switch (ultimosValoresLEDs[ledIndex]) {
			case HIGH:
				ultimosValoresLEDs[ledIndex] = (int8_t) LOW;
				break;
			case LOW:
				ultimosValoresLEDs[ledIndex] = (int8_t) HIGH;
				break;
			default:
				break;
			}

			digitalWrite(pin, ultimosValoresLEDs[ledIndex]);
			break;
		}
		case FADE: {
			analogWrite(pin, ultimosValoresLEDs[ledIndex]);
			ultimosValoresLEDs[ledIndex] += fadeAmountsLEDs[ledIndex];

			if (ultimosValoresLEDs[ledIndex] == 0x00
					|| ultimosValoresLEDs[ledIndex] == 0xFF)
				fadeAmountsLEDs[ledIndex] *= -1;
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

	uint8_t ledIndex = getIndiceLED(arduinoUSART->getPin());

	ledsAtivas[ledIndex] = arduinoUSART->getPinValue() != 0x0000;

	Sisbarc.send(arduino);
}

bool callLEDAmarela(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorLED(PIN_LED_AMARELA);
		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != PIN_LED_AMARELA)
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
		alteraValorLED(PIN_LED_VERDE);
		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != PIN_LED_VERDE)
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

bool callLEDVermelha(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorLED(PIN_LED_VERMELHA);
		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != PIN_LED_VERMELHA)
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
		if (digitalRead(PIN_BOTOES[i]) == LOW) {
			if (digitalRead(PIN_LEDS[i]) == LOW) {
				Sisbarc.sendPinDigital(ArduinoStatus::SEND_RESPONSE,
						PIN_LEDS[i], HIGH);
			} else {
				Sisbarc.sendPinDigital(ArduinoStatus::SEND_RESPONSE,
						PIN_LEDS[i], LOW);
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
	uint16_t potenciometroValue = (uint16_t) analogRead(PIN_POTENCIOMETRO);
	if (potenciometroValue != ultimoValorPotenciometro) {
		ultimoValorPotenciometro = potenciometroValue;
		Sisbarc.sendPinAnalog(ArduinoStatus::SEND, PIN_POTENCIOMETRO,
				potenciometroValue);
	}
}

bool callPotenciometro(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorPotenciometro();
		return false;
	}

	return false;
}

