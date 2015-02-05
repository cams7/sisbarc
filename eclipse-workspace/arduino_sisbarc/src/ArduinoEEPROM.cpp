/*
 * ArduinoEEPROM.cpp
 *
 *  Created on: 31/01/2015
 *      Author: cams7
 */

#include "ArduinoEEPROM.h"

namespace SISBARC {

const uint8_t ArduinoEEPROM::THREAD_TIME_MAX = 0x07; //7
const uint8_t ArduinoEEPROM::DIGITAL_ACTION_EVENT_MAX = 0x1F; //31
//const uint8_t ArduinoEEPROM::DIGITAL_ACTION_EVENT_PIN0_MIN = 0x01; //Quando o PINO DIGITAL for '0', o valor minimo para para o 'actionEvent' e 1
const uint8_t ArduinoEEPROM::ANALOG_ACTION_EVENT_MAX = 0x7F; //127

ArduinoEEPROM::ArduinoEEPROM() :
		ArduinoStatus(), threadTime(0x00), actionEvent(0x00) {
}

ArduinoEEPROM::ArduinoEEPROM(status statusValue, event eventValue,
		pin_type pinType, uint8_t pin, uint8_t threadTime, uint8_t actionEvent) :
		ArduinoStatus(statusValue, eventValue, pinType, pin), threadTime(
				threadTime), actionEvent(actionEvent) {
}

ArduinoEEPROM::~ArduinoEEPROM() {
	// TODO Auto-generated destructor stub
}

uint8_t ArduinoEEPROM::getThreadTime(void) {
	return threadTime;
}
void ArduinoEEPROM::setThreadTime(uint8_t threadTime) {
	this->threadTime = threadTime;
}

uint8_t ArduinoEEPROM::getActionEvent(void) {
	return actionEvent;
}
void ArduinoEEPROM::setActionEvent(uint8_t actionEvent) {
	this->actionEvent = actionEvent;
}

ArduinoEEPROMRead::ArduinoEEPROMRead() :
		ArduinoEEPROM() {
	setEventValue(READ);
}

ArduinoEEPROMRead::ArduinoEEPROMRead(status statusValue, pin_type pinType,
		uint8_t pin, uint8_t threadTime, uint8_t actionEvent) :
		ArduinoEEPROM(statusValue, READ, pinType, pin, threadTime, actionEvent) {
}

ArduinoEEPROMRead::~ArduinoEEPROMRead() {
	// TODO Auto-generated destructor stub
}

ArduinoEEPROMWrite::ArduinoEEPROMWrite() :
		ArduinoEEPROM() {
	setEventValue(WRITE);
}

ArduinoEEPROMWrite::ArduinoEEPROMWrite(status statusValue, pin_type pinType,
		uint8_t pin, uint8_t threadTime, uint8_t actionEvent) :
		ArduinoEEPROM(statusValue, WRITE, pinType, pin, threadTime, actionEvent) {
}

ArduinoEEPROMWrite::~ArduinoEEPROMWrite() {
	// TODO Auto-generated destructor stub
}

} /* namespace SISBARC */
