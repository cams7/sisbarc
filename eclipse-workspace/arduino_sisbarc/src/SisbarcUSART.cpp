/*
 * SisbarcUSART.cpp
 *
 *  Created on: 04/02/2015
 *      Author: cams7
 */

#include "SisbarcUSART.h"

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
		root(NULL), serialDataReceive(NULL), serialDataReceiveIndex(0x00) {
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

		next = (CallbackNode*) malloc(sizeof(struct CallbackNode));
		previous->next = next;
	} else {
		root = (CallbackNode*) malloc(sizeof(struct CallbackNode));
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
		serialDataReceive = (uint8_t*) malloc(
				SisbarcProtocol::TOTAL_BYTES_PROTOCOL);
		*(serialDataReceive) = data;

		serialDataReceiveIndex = 0x01;
	} else if (serialDataReceiveIndex > 0x00 && serialDataReceive != NULL) {
		*(serialDataReceive + serialDataReceiveIndex) = data;

		if (serialDataReceiveIndex
				== (SisbarcProtocol::TOTAL_BYTES_PROTOCOL - 1)) {
			ArduinoStatus* arduino = SisbarcProtocol::receive(
					serialDataReceive);
			if (arduino != NULL) {
				run(arduino);
				free(arduino);
			}
			free(serialDataReceive);
		} else
			serialDataReceiveIndex++;
	} else {
	}
}

void SisbarcUSART::sendPinDigital(status statusValue, uint8_t pin,
		bool pinValue) {
	uint8_t* serialDataSend;
	serialDataSend = SisbarcProtocol::sendPinDigital(statusValue, pin,
			pinValue);
	if (serialDataSend != NULL) {
		Serial.write(serialDataSend, SisbarcProtocol::TOTAL_BYTES_PROTOCOL);
		free(serialDataSend);
	}
}

void SisbarcUSART::sendPinPWM(status statusValue, uint8_t pin,
		uint8_t pinValue) {
	uint8_t* serialDataSend;
	serialDataSend = SisbarcProtocol::sendPinPWM(statusValue, pin, pinValue);
	if (serialDataSend != NULL) {
		Serial.write(serialDataSend, SisbarcProtocol::TOTAL_BYTES_PROTOCOL);
		free(serialDataSend);
	}

}

void SisbarcUSART::sendPinAnalog(status statusValue, uint8_t pin,
		uint16_t pinValue) {
	uint8_t* serialDataSend;
	serialDataSend = SisbarcProtocol::sendPinAnalog(statusValue, pin, pinValue);
	if (serialDataSend != NULL) {
		Serial.write(serialDataSend, SisbarcProtocol::TOTAL_BYTES_PROTOCOL);
		free(serialDataSend);
	}
}

SisbarcUSART SISBARC_USART;

} /* namespace SISBARC */
