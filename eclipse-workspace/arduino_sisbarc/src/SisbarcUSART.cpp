/*
 * SisbarcUSART.cpp
 *
 *  Created on: 04/02/2015
 *      Author: cams7
 */
#include "SisbarcUSART.h"

#include <Arduino.h>
#include "ArduinoUSART.h"
#include "SisbarcProtocol.h"
#include "SisbarcEEPROM.h"

namespace SISBARC {

CallbackUSART::CallbackUSART(bool (*callback)(ArduinoStatus*)) {
	onRun = callback;
}

CallbackUSART::~CallbackUSART() {
}

bool CallbackUSART::run(ArduinoStatus* arduino) {
	if (onRun != NULL)
		return onRun(arduino);

	return false;
}

SisbarcUSART::SisbarcUSART() :
		root(NULL), serialData(NULL), serialDataIndex(0x00) {
}

SisbarcUSART::~SisbarcUSART() {
}

void SisbarcUSART::onRun(bool (*callback)(ArduinoStatus*)) {
	struct CallbackNode *previous;
	struct CallbackNode *next;

	if (root != NULL) {
		previous = root;
		next = root->next;

		while (next != NULL) {
			previous = next;
			next = next->next;
		}

		next = ((CallbackNode*) malloc(sizeof(struct CallbackNode)));
		previous->next = next;
	} else {
		root = ((CallbackNode*) malloc(sizeof(struct CallbackNode)));
		next = root;
		previous = NULL;
	}

	next->callback = new CallbackUSART(callback);
	next->next = NULL;
	next->previous = previous;
}

void SisbarcUSART::run(ArduinoStatus* arduino) {
	struct CallbackNode *next;
	next = root;

	if (next != NULL)
		do {
			if (next->callback->run(arduino))
				break;

			next = next->next;
		} while (next != NULL);
}

void SisbarcUSART::receiveDataBySerial(uint8_t data) {
	uint8_t lastBit = data & 0x80;

	if (lastBit) {
		if (serialData == NULL)
			serialData = ((uint8_t*) malloc(
					SisbarcProtocol::TOTAL_BYTES_PROTOCOL));
		*(serialData) = data;

		serialDataIndex = 0x01;
	} else if (serialDataIndex > 0x00 && serialData != NULL) {
		*(serialData + serialDataIndex) = data;

		if (serialDataIndex == (SisbarcProtocol::TOTAL_BYTES_PROTOCOL - 1)) {
			ArduinoStatus* arduino = receive(serialData);
			if (arduino != NULL) {
				run(arduino);
				free(arduino);
			}
			//free(serialData);
		} else
			serialDataIndex++;
	} else {

	}
}

void SisbarcUSART::serialWrite(uint8_t* data) {
	if (data == NULL)
		return;

	Serial.write(data, SisbarcProtocol::TOTAL_BYTES_PROTOCOL);
	free(data);

}

void SisbarcUSART::send(ArduinoStatus* arduino) {
	if (arduino == NULL)
		return;

	serialWrite(SisbarcProtocol::getProtocol(arduino));
	//free() executado apos run()
}

void SisbarcUSART::sendPinDigital(status statusValue, uint8_t pin,
		bool pinValue) {
	bool pinOk = false;
	for (int8_t i = 0x00; i < ArduinoUSART::PINS_DIGITAL_SIZE; i++)
		if (pin == ArduinoUSART::PINS_DIGITAL[i]) {
			pinOk = true;
			break;
		}

	if (!pinOk)
		for (int8_t i = 0x00; i < ArduinoUSART::PINS_DIGITAL_PWM_SIZE; i++)
			if (pin == ArduinoUSART::PINS_DIGITAL_PWM[i]) {
				pinOk = true;
				break;
			}

	if (!pinOk)
		return;

	serialWrite(
			SisbarcProtocol::getProtocolUSART(statusValue,
					ArduinoUSART::EXECUTE, ArduinoUSART::DIGITAL, pin,
					(pinValue ? 0x0001 : 0x0000)));
}

void SisbarcUSART::sendPinPWM(status statusValue, uint8_t pin,
		uint8_t pinValue) {
	bool pinOk = false;
	for (int8_t i = 0x00; i < ArduinoUSART::PINS_DIGITAL_PWM_SIZE; i++)
		if (pin == ArduinoUSART::PINS_DIGITAL_PWM[i]) {
			pinOk = true;
			break;
		}

	if (!pinOk)
		return;

	if (pinValue < 0x00)
		return;

	if (pinValue > ArduinoUSART::DIGITAL_PIN_VALUE_MAX)
		return;

	serialWrite(
			SisbarcProtocol::getProtocolUSART(statusValue,
					ArduinoUSART::EXECUTE, ArduinoUSART::DIGITAL, pin,
					pinValue));
}

void SisbarcUSART::sendPinAnalog(status statusValue, uint8_t pin,
		uint16_t pinValue) {
	bool pinOk = false;
	for (int8_t i = 0x00; i < ArduinoUSART::PINS_ANALOG_SIZE; i++)
		if (pin == ArduinoUSART::PINS_ANALOG[i]) {
			pinOk = true;
			break;
		}

	if (!pinOk)
		return;

	if (pinValue < 0x00)
		return;

	if (pinValue > ArduinoUSART::ANALOG_PIN_VALUE_MAX)
		return;

	serialWrite(
			SisbarcProtocol::getProtocolUSART(statusValue,
					ArduinoUSART::EXECUTE, ArduinoUSART::ANALOG, pin,
					pinValue));
}

//Recebe protocolo
ArduinoStatus *SisbarcUSART::receive(uint8_t const message[]) {
	return SisbarcProtocol::decode(message);
}

SisbarcUSART SISBARC_USART;

} /* namespace SISBARC */
