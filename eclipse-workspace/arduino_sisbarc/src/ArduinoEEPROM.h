/*
 * ArduinoEEPROM.h
 *
 *  Created on: 31/01/2015
 *      Author: cams7
 */

#ifndef ARDUINOEEPROM_H_
#define ARDUINOEEPROM_H_

#include "ArduinoStatus.h"

namespace SISBARC {

class ArduinoEEPROM: public ArduinoStatus {
private:
	uint8_t threadTime;
	uint8_t actionEvent;
public:
	static const uint8_t THREAD_TIME_MAX;
	static const uint8_t DIGITAL_ACTION_EVENT_MAX;
	//static const uint8_t DIGITAL_ACTION_EVENT_PIN0_MIN;
	static const uint8_t ANALOG_ACTION_EVENT_MAX;

	ArduinoEEPROM();
	ArduinoEEPROM(status statusValue, event eventValue, pin_type pinType,
			uint8_t pin, uint8_t threadTime, uint8_t actionEvent);

	virtual ~ArduinoEEPROM();

	uint8_t getThreadTime(void);
	void setThreadTime(uint8_t threadTime);

	uint8_t getActionEvent(void);
	void setActionEvent(uint8_t actionEvent);
};

class ArduinoEEPROMRead: public ArduinoEEPROM {
public:
	ArduinoEEPROMRead();
	ArduinoEEPROMRead(status statusValue, pin_type pinType, uint8_t pin,
			uint8_t threadTime, uint8_t actionEvent);
	virtual ~ArduinoEEPROMRead();
};

class ArduinoEEPROMWrite: public ArduinoEEPROM {
public:
	ArduinoEEPROMWrite();
	ArduinoEEPROMWrite(status statusValue, pin_type pinType, uint8_t pin,
			uint8_t threadTime, uint8_t actionEvent);
	virtual ~ArduinoEEPROMWrite();
};

} /* namespace SISBARC */

#endif /* ARDUINOEEPROM_H_ */
