/*
 * SisbarcUSART.h
 *
 *  Created on: 04/02/2015
 *      Author: cams7
 */

#ifndef SISBARC_33_H_
#define SISBARC_33_H_

#include "ThreadController.h"
#include "vo/ArduinoStatus.h"
#include "vo/EEPROMData.h"

#include <HardwareSerial.h>
//#include "_Serial.h"

namespace SISBARC {

class SisbarcClass: public ThreadController {
private:
	static unsigned long const BAUD_RATE;

	uint8_t* _serialData;
	uint8_t _serialDataIndex;

	uint16_t* _threadIntervals;
	uint8_t _totalThreadIntervals;

	HardwareSerial* _serial;

	ArduinoStatus *receive(uint8_t const message[]);

	void serialEventRun(void);

	void receiveDataBySerial(ArduinoStatus* const);

	void serialWrite(uint8_t* const);

	long getThreadInterval(EEPROMData* const, long);

	int16_t onRun(ArduinoPin* const, bool (*callback)(ArduinoStatus*), long);

public:
	SisbarcClass();
	virtual ~SisbarcClass();

	void begin(HardwareSerial* const, unsigned long const baudRate = BAUD_RATE);

	void send(ArduinoStatus* const);

	void sendPinDigital(status const, uint8_t const, bool const);
	void sendPinPWM(status const, uint8_t const, uint8_t const);
	void sendPinAnalog(status const, uint8_t const, uint16_t const);

	void addThreadInterval(uint8_t const, uint16_t);

	// Callback set
	int16_t onRun(pin_type const, uint8_t const,
			bool (*callback)(ArduinoStatus*), long threadInterval = 0);

	void run(void);

};

extern SisbarcClass Sisbarc;

} /* namespace SISBARC */

#endif /* SISBARC_33_H_ */
