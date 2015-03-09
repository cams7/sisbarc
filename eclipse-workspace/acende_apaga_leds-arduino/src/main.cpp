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

#define D13_LED_PISCA    13   //Pino 13 Digital

#define D11_LED_AMARELO  11   //Pino 11 PWM
#define D10_LED_VERDE    10   //Pino 10 PWM
#define D09_LED_VERMELHO 9    //Pino 09 PWM
#define D06_LED_AMARELO  6    //Pino 06 PWM
#define D05_LED_VERDE    5    //Pino 05 PWM
#define D04_LED_VERMELHO 4    //Pino 04 Digital

#define D12_BOTAO_LED_AMARELO  12 //Pino 12 Digital
#define D08_BOTAO_LED_VERDE    8  //Pino 08 Digital
#define D07_BOTAO_LED_VERMELHO 7  //Pino 07 Digital

#define A0_POTENCIOMETRO      0  //Pino 0 Analogico

#define INTERVALO_LED_PISCA    1000 // 1 segundo
#define INTERVALO_BOTAO        500  // 1/2 segundo
#define INTERVALO_POTENCIOMETRO 100  // 1/10 de segundo

#define ACENDE_APAGA 0 // Acende ou apaga
#define PISCA_PISCA  1 // Pisca-pisca
#define FADE         2 // Acende ou apaga ao poucos
#define NENHUM       3 // O potenciometro não vai acende ou apagar os LEDs

#define INTERVALO_10MILISEGUNDOS  10    // 1/100 de segundo
#define INTERVALO_50MILISEGUNDOS  50    // 1/20 de segundo
#define INTERVALO_100MILISEGUNDOS 100   // 1/10 de segundo
#define INTERVALO_1SEGUNDO        1000  // 1 segundo
#define INTERVALO_3SEGUNDOS       3000  // 3 segundos
#define INTERVALO_5SEGUNDOS       5000  // 5 segundos
#define INTERVALO_10SEGUNDOS      10000 // 10 segundos
#define SEM_INTERVALO             0     // Não roda dentro da thread

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

bool callD13LEDPisca(ArduinoStatus* const);

bool callD11LEDAmarelo(ArduinoStatus* const);
bool callD10LEDVerde(ArduinoStatus* const);
bool callD09LEDVermelho(ArduinoStatus* const);
bool callD06LEDAmarelo(ArduinoStatus* const);
bool callD05LEDVerde(ArduinoStatus* const);
bool callD04LEDVermelho(ArduinoStatus* const);

bool callLEDBotao(ArduinoStatus* const);

bool callA0Potenciometro(ArduinoStatus* const);

const uint8_t TOTAL_LEDS = 0x06;
const uint8_t TOTAL_BOTOES = 0x03;

const uint8_t PINOS_LEDS[] = { D11_LED_AMARELO, D10_LED_VERDE, D09_LED_VERMELHO,
D06_LED_AMARELO, D05_LED_VERDE, D04_LED_VERMELHO };
const uint8_t PINOS_BOTOES[] = { D12_BOTAO_LED_AMARELO, D08_BOTAO_LED_VERDE,
D07_BOTAO_LED_VERMELHO };

uint8_t eventosLEDs[] = { ACENDE_APAGA, ACENDE_APAGA, ACENDE_APAGA,
ACENDE_APAGA, ACENDE_APAGA, PISCA_PISCA };
uint8_t ultimoValoresLEDs[] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

int8_t fadeAmountsLEDs[] = { 0x05, 0x05, 0x05, 0x05, 0x05, 0x05 };
bool ledsAcesa[] = { false, false, false, false, false, false };

uint16_t ultimoValorPotenciometro = 0x0000;    //Ultimo valor do Potenciometro
uint8_t eventoPotenciometro = NENHUM;

bool ledPiscaAcesa = false;    //Utimo valor do LED do pino 13

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
	pinMode(D13_LED_PISCA, OUTPUT);

	for (uint8_t i = 0x00; i < TOTAL_LEDS; i++)
		pinMode(PINOS_LEDS[i], OUTPUT);

	for (uint8_t i = 0x00; i < TOTAL_BOTOES; i++) {
		pinMode(PINOS_BOTOES[i], INPUT);
		digitalWrite(PINOS_BOTOES[i], HIGH);
	}

	//----------------------------------------------------------

	Sisbarc.begin(&Serial);

	//----------------------------------------------------------

	Sisbarc.addThreadInterval(0x00, INTERVALO_10MILISEGUNDOS);
	Sisbarc.addThreadInterval(0x01, INTERVALO_50MILISEGUNDOS);
	Sisbarc.addThreadInterval(0x02, INTERVALO_100MILISEGUNDOS);
	Sisbarc.addThreadInterval(0x03, INTERVALO_1SEGUNDO);
	Sisbarc.addThreadInterval(0x04, INTERVALO_3SEGUNDOS);
	Sisbarc.addThreadInterval(0x05, INTERVALO_5SEGUNDOS);
	Sisbarc.addThreadInterval(0x06, INTERVALO_10SEGUNDOS);
	Sisbarc.addThreadInterval(0x07, SEM_INTERVALO);

	//----------------------------------------------------------

	int16_t evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, D13_LED_PISCA,
			callD13LEDPisca,
			INTERVALO_LED_PISCA);

	//----------------------------------------------------------

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, D11_LED_AMARELO,
			callD11LEDAmarelo);
	if (evento != -1) {
		eventosLEDs[0] = evento;
		alteraValorLEDPorIndice(0);
	}

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, D10_LED_VERDE,
			callD10LEDVerde);
	if (evento != -1) {
		eventosLEDs[1] = evento;
		alteraValorLEDPorIndice(1);
	}

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, D09_LED_VERMELHO,
			callD09LEDVermelho);
	if (evento != -1) {
		eventosLEDs[2] = evento;
		alteraValorLEDPorIndice(2);
	}

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, D06_LED_AMARELO,
			callD06LEDAmarelo);
	if (evento != -1) {
		eventosLEDs[3] = evento;
		alteraValorLEDPorIndice(3);
	}

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, D05_LED_VERDE,
			callD05LEDVerde);
	if (evento != -1) {
		eventosLEDs[4] = evento;
		alteraValorLEDPorIndice(4);
	}

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, D04_LED_VERMELHO,
			callD04LEDVermelho);
	if (evento != -1) {
		eventosLEDs[5] = evento;
		alteraValorLEDPorIndice(5);
	}

	//----------------------------------------------------------

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, D12_BOTAO_LED_AMARELO,
			callLEDBotao,
			INTERVALO_BOTAO);

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, D08_BOTAO_LED_VERDE,
			callLEDBotao,
			INTERVALO_BOTAO);

	evento = Sisbarc.onRun(ArduinoStatus::DIGITAL, D07_BOTAO_LED_VERMELHO,
			callLEDBotao,
			INTERVALO_BOTAO);

	//----------------------------------------------------------

	evento = Sisbarc.onRun(ArduinoStatus::ANALOG, A0_POTENCIOMETRO,
			callA0Potenciometro,
			INTERVALO_POTENCIOMETRO);
	if (evento != -1)
		eventoPotenciometro = evento;

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

int16_t getIndiceLEDPino(uint8_t const pin) {
	int16_t indice = -1;

	for (uint8_t i = 0x00; i < TOTAL_LEDS; i++)
		if (PINOS_LEDS[i] == pin) {
			indice = i;
			break;
		}

	return indice;
}

void alteraValorLEDPorIndice(uint8_t const indicePino) {
	if (eventosLEDs[indicePino] == FADE) {
		ultimoValoresLEDs[indicePino] = 0xFF;
		fadeAmountsLEDs[indicePino] = -5;
	} else
		ultimoValoresLEDs[indicePino] = 0x00;
}

void alteraEEPROM(ArduinoStatus* const arduino) {
	ArduinoEEPROMWrite* arduinoEEPROMWrite = ((ArduinoEEPROMWrite*) arduino);

	int16_t returnValue = SisbarcEEPROM::write(arduinoEEPROMWrite);
	if (returnValue == 0x0000 || returnValue == 0x0001) {
		if (arduino->getPin() == D11_LED_AMARELO
				|| arduino->getPin() == D10_LED_VERDE
				|| arduino->getPin() == D09_LED_VERMELHO
				|| arduino->getPin() == D06_LED_AMARELO
				|| arduino->getPin() == D05_LED_VERDE
				|| arduino->getPin() == D04_LED_VERMELHO) {

			int16_t indicePino = getIndiceLEDPino(arduinoEEPROMWrite->getPin());
			if (indicePino != -1) {
				eventosLEDs[indicePino] = arduinoEEPROMWrite->getActionEvent();
				alteraValorLEDPorIndice(indicePino);
			}
		} else if (arduino->getPin() == A0_POTENCIOMETRO)
			eventoPotenciometro = arduinoEEPROMWrite->getActionEvent();

		arduinoEEPROMWrite->setTransmitterValue(ArduinoStatus::ARDUINO);
		arduinoEEPROMWrite->setStatusValue(ArduinoStatus::RESPONSE);
		Sisbarc.send(arduinoEEPROMWrite);
	}
}

void buscaEEPROM(ArduinoStatus* const arduino) {
	ArduinoEEPROMRead* arduinoEEPROMRead = ((ArduinoEEPROMRead*) arduino);

	EEPROMData* data = SisbarcEEPROM::read(arduinoEEPROMRead);
	if (data == NULL)
		return;

	arduinoEEPROMRead->setThreadInterval(data->getThreadInterval());
	arduinoEEPROMRead->setActionEvent(data->getActionEvent());

	arduinoEEPROMRead->setTransmitterValue(ArduinoStatus::ARDUINO);
	arduinoEEPROMRead->setStatusValue(ArduinoStatus::RESPONSE);

	Sisbarc.send(arduinoEEPROMRead);
}

void alteraValorLEDPorSerial(ArduinoStatus* const arduino) {
	ArduinoUSART* arduinoUSART = ((ArduinoUSART*) arduino);

	arduinoUSART->setTransmitterValue(ArduinoStatus::ARDUINO);
	arduinoUSART->setStatusValue(ArduinoStatus::RESPONSE);

	digitalWrite(arduinoUSART->getPin(), (uint8_t) arduinoUSART->getPinValue());

	int16_t indicePino = getIndiceLEDPino(arduinoUSART->getPin());

	if (indicePino != -1)
		ledsAcesa[indicePino] = arduinoUSART->getPinValue() == 0x0001;

	Sisbarc.send(arduinoUSART);
}

void buscaEstadoLED(ArduinoStatus* const arduino) {
	ArduinoUSARTMessage* arduinoUSART = ((ArduinoUSARTMessage*) arduino);

	arduinoUSART->setTransmitterValue(ArduinoStatus::ARDUINO);
	arduinoUSART->setStatusValue(ArduinoStatus::RESPONSE);

	int16_t indicePino = getIndiceLEDPino(arduinoUSART->getPin());

	if (indicePino != -1)
		arduinoUSART->setPinValue(ledsAcesa[indicePino] ? 0x0001 : 0x0000);

	Sisbarc.send(arduinoUSART);
}

bool isValidEvent(ArduinoStatus* const arduino) {
	if (arduino->getEventValue() == ArduinoStatus::EXECUTE) {
		alteraValorLEDPorSerial(arduino);
		return true;
	}

	if (arduino->getEventValue() == ArduinoStatus::MESSAGE) {
		buscaEstadoLED(arduino);
		return true;
	}

	if (arduino->getEventValue() == ArduinoStatus::WRITE) {
		alteraEEPROM(arduino);
		return true;
	}

	if (arduino->getEventValue() == ArduinoStatus::READ) {
		buscaEEPROM(arduino);
		return true;
	}

	return false;
}

//Pisca o LED do pino 13
void pisca(void) {
	ledPiscaAcesa = !ledPiscaAcesa;

	digitalWrite(D13_LED_PISCA, ledPiscaAcesa ? HIGH : LOW);

	Sisbarc.sendPinDigital(ArduinoStatus::SEND, D13_LED_PISCA, ledPiscaAcesa);

}

bool callD13LEDPisca(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		pisca();

		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != D13_LED_PISCA)
		return false;

	return isValidEvent(arduino);
}

void alteraValorLED(uint8_t const pin) {
	int16_t indicePino = getIndiceLEDPino(pin);
	if (indicePino == -1)
		return;

	if (ledsAcesa[indicePino]) {
		switch (eventosLEDs[indicePino]) {
		case ACENDE_APAGA: {
			break;
		}
		case PISCA_PISCA: {
			switch (ultimoValoresLEDs[indicePino]) {
			case 0x00:    //HIGH
				ultimoValoresLEDs[indicePino] = 0x01;
				break;
			default:    //LOW
				ultimoValoresLEDs[indicePino] = 0x00;
				break;
			}

			digitalWrite(pin, ultimoValoresLEDs[indicePino]);
			break;
		}
		case FADE: {
			analogWrite(pin, ultimoValoresLEDs[indicePino]);
			ultimoValoresLEDs[indicePino] += fadeAmountsLEDs[indicePino];

			if (ultimoValoresLEDs[indicePino] == 0x00
					|| ultimoValoresLEDs[indicePino] == 0xFF)
				fadeAmountsLEDs[indicePino] *= -1;
			break;
		}
		default:
			break;

		}
	}
}

bool callD11LEDAmarelo(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorLED(D11_LED_AMARELO);
		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != D11_LED_AMARELO)
		return false;

	return isValidEvent(arduino);
}

bool callD10LEDVerde(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorLED(D10_LED_VERDE);
		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != D10_LED_VERDE)
		return false;

	return isValidEvent(arduino);
}

bool callD09LEDVermelho(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorLED(D09_LED_VERMELHO);
		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != D09_LED_VERMELHO)
		return false;

	return isValidEvent(arduino);
}

bool callD06LEDAmarelo(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorLED(D06_LED_AMARELO);
		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != D06_LED_AMARELO)
		return false;

	return isValidEvent(arduino);
}

bool callD05LEDVerde(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorLED(D05_LED_VERDE);
		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != D05_LED_VERDE)
		return false;

	return isValidEvent(arduino);
}

bool callD04LEDVermelho(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorLED(D04_LED_VERMELHO);
		return false;
	}

	if (!isCallBySerialToPinDigital(arduino))
		return false;

	if (arduino->getPin() != D04_LED_VERMELHO)
		return false;

	return isValidEvent(arduino);
}

void alteraValorLEDPorBotao(void) {
	for (uint8_t i = 0x00; i < TOTAL_BOTOES; i++) {
		if (digitalRead(PINOS_BOTOES[i]) == LOW) {
			Sisbarc.sendPinDigital(ArduinoStatus::SEND_RESPONSE, PINOS_LEDS[i],
					!ledsAcesa[i]);
			Sisbarc.sendPinDigital(ArduinoStatus::SEND_RESPONSE,
					PINOS_LEDS[i + 3], !ledsAcesa[i + 3]);

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
	uint16_t valorPotenciometro = (uint16_t) analogRead(A0_POTENCIOMETRO);
	if (valorPotenciometro != ultimoValorPotenciometro) {
		ultimoValorPotenciometro = valorPotenciometro;

		if (eventoPotenciometro == ACENDE_APAGA) {
			uint8_t i = 0x00;
			for (; i < TOTAL_LEDS; i++) {
				digitalWrite(PINOS_LEDS[i], LOW);
				ledsAcesa[i] = false;
			}

			i = 0x00;

			for (uint16_t j = 0x0000; j < ultimoValorPotenciometro; j += 171) {
				Sisbarc.sendPinDigital(ArduinoStatus::SEND_RESPONSE,
						PINOS_LEDS[i], !ledsAcesa[i]);
				i++;
			}

			Sisbarc.sendPinAnalog(ArduinoStatus::SEND, A0_POTENCIOMETRO,
					valorPotenciometro);
		}

	}
}

bool callA0Potenciometro(ArduinoStatus* const arduino) {
	if (arduino == NULL) {
		alteraValorPotenciometro();
		return false;
	}

	if (!isCallBySerialToPinAnalog(arduino))
		return false;

	if (arduino->getPin() != A0_POTENCIOMETRO)
		return false;

	return isValidEvent(arduino);
}

