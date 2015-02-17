/*
 * SisbarcUSART.cpp
 *
 *  Created on: 04/02/2015
 *      Author: cams7
 */
#include "Sisbarc.h"

#include "SisbarcProtocol.h"
#include "SisbarcEEPROM.h"

#include "vo/ArduinoUSART.h"
#include "vo/ArduinoEEPROM.h"

#include "util/Iterator.h"

//#include <stdio.h>

namespace SISBARC {

unsigned long const SisbarcClass::BAUD_RATE = 9600;

SisbarcClass::SisbarcClass() :
		ThreadController(), _serialData(
				new uint8_t[SisbarcProtocol::TOTAL_BYTES_PROTOCOL]), _serialDataIndex(
				0x00), _threadIntervals(
				new uint16_t[ArduinoEEPROM::THREAD_INTERVAL_MAX + 1]), _totalThreadIntervals(
				0x00), _serial(NULL) {

//printf("New Sisbarc\n");
}

SisbarcClass::~SisbarcClass() {
	delete _serialData;
	delete _threadIntervals;

//printf("Delete Sisbarc\n");
}

void SisbarcClass::begin(HardwareSerial* const serial,
		unsigned long const baudRate) {
	_serial = serial;
	_serial->begin(baudRate); //frequência da porta serial - USART
}

//Recebe protocolo
ArduinoStatus *SisbarcClass::receive(uint8_t const message[]) {
	return SisbarcProtocol::decode(message);
}

void SisbarcClass::receiveDataBySerial(ArduinoStatus* const arduino) {
	if (!ThreadController::getThreads()->isEmpty()) {
		Iterator<Thread>* i = ThreadController::getThreads()->iterator();
		while (i->hasNext()) {
			// Object exists? Is enabled? Timeout exceeded?
			Thread* thread = i->next();
			bool executeBySerial = thread->run(arduino);

			if (executeBySerial) {
				long threadInterval = -1;

				EEPROMData* data = SisbarcEEPROM::read((ArduinoPin*) arduino);
				if (data != NULL) {
					threadInterval = getThreadInterval(data, -1);
					delete data;
				}

				if (threadInterval > 0) {
					thread->setInterval(threadInterval);
					thread->setEnabled(true);
					//printf("interval thread now is %i\n", interval);
				} else if (threadInterval == 0) {
					thread->setEnabled(false);
					//printf("thread now is not enabled\n");
				} //else 	printf("interval not found in EEPROM\n");

				break;
			}
		}
	}
}

void SisbarcClass::serialEventRun(void) {
	//uint8_t data = (uint8_t) Serial.read();
	uint8_t data = _serial->read();

	if (data & 0x80) { //Last bit
		_serialData[0] = data;
		_serialDataIndex = 0x01;
	} else if (_serialDataIndex > 0x00) {
		_serialData[_serialDataIndex] = data;
		_serialDataIndex++;

		if (_serialDataIndex == SisbarcProtocol::TOTAL_BYTES_PROTOCOL) {
			ArduinoStatus* arduino = receive(_serialData);
			if (arduino != NULL) {
				receiveDataBySerial(arduino);
				delete arduino;
			}
			_serialDataIndex = 0x00;
		}
	}
}

void SisbarcClass::serialWrite(uint8_t* const data) {
	if (data == NULL)
		return;

	_serial->write(data, SisbarcProtocol::TOTAL_BYTES_PROTOCOL);
	free(data);
}

void SisbarcClass::send(ArduinoStatus* const arduino) {
	if (arduino == NULL)
		return;

	serialWrite(SisbarcProtocol::getProtocol(arduino));
}

void SisbarcClass::sendPinDigital(status const statusValue, uint8_t const pin,
		bool const pinValue) {
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

void SisbarcClass::sendPinPWM(status const statusValue, uint8_t const pin,
		uint8_t const pinValue) {
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

void SisbarcClass::sendPinAnalog(status const statusValue, uint8_t const pin,
		uint16_t const pinValue) {
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

long SisbarcClass::getThreadInterval(EEPROMData* const data,
		long threadInterval) {
	if (data == NULL)
		return threadInterval;

	if (_totalThreadIntervals > 0x00) {
		uint8_t index = data->getThreadInterval();

		if (_totalThreadIntervals > index)
			threadInterval = _threadIntervals[index];

	}
	return threadInterval;
}

void SisbarcClass::addThreadInterval(uint8_t const index,
		uint16_t threadInterval) {
	if (index <= ArduinoEEPROM::THREAD_INTERVAL_MAX) {
		_threadIntervals[index] = threadInterval;
		_totalThreadIntervals++;
	}
}

int16_t SisbarcClass::onRun(ArduinoPin* const pin,
		bool (*callback)(ArduinoStatus*), long thredInterval) {

	int16_t actionEvent = -1;

	EEPROMData* data = SisbarcEEPROM::read(pin);
	if (data != NULL) {
		thredInterval = getThreadInterval(data, thredInterval);
		actionEvent = data->getActionEvent();
		delete data;
	}

	Thread* thread = new Thread(callback, thredInterval);

	if (thredInterval == 0)
		thread->setEnabled(false);

	ThreadController::add(thread);

	return actionEvent;
}

int16_t SisbarcClass::onRun(pin_type const pinType, uint8_t const pin,
		bool (*callback)(ArduinoStatus*), long threadInterval) {
	ArduinoPin* arduinoPin = new ArduinoPin(pinType, pin);
	int16_t actionEvent = onRun(arduinoPin, callback, threadInterval);
	delete arduinoPin;

	return actionEvent;
}

void SisbarcClass::run(void) {
	ThreadController::run();

	if (_serial->available() > 0) //verifica se existe comunicação com a porta serial
		serialEventRun(); //lê os dados da porta serial - Maximo 64 bytes
}

SisbarcClass Sisbarc;

} /* namespace SISBARC */
