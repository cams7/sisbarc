/*
 * ArduinoProtocol.h
 *
 *  Created on: 14/01/2015
 *      Author: cams7
 */

#ifndef SISBARCPROTOCOL_H_
#define SISBARCPROTOCOL_H_

#include "Binary.h"
#include "Checksum.h"
#include "ArduinoStatus.h"
#include "ArduinoUSART.h"
#include "ArduinoEEPROM.h"

namespace SISBARC {

class SisbarcProtocol {
private:
	static const uint32_t EMPTY_BITS;

	static const uint8_t TOTAL_BITS_PROTOCOL;

	static const uint8_t TOTAL_BITS_INDEX;
	static const uint8_t TOTAL_BITS_CHECKSUM;
	static const uint8_t TOTAL_BITS_DATA;

	static const uint8_t TOTAL_BITS_DIGITAL_PIN;
	static const uint8_t TOTAL_BITS_ANALOG_PIN;

	static const uint8_t TOTAL_BITS_DIGITAL_PIN_VALUE;
	static const uint8_t TOTAL_BITS_ANALOG_PIN_VALUE;

	static const uint8_t TOTAL_BITS_THREAD_TIME;

	static const uint8_t TOTAL_BITS_DIGITAL_ACTION_EVENT;
	static const uint8_t TOTAL_BITS_ANALOG_ACTION_EVENT;

	static uint32_t encode(ArduinoStatus* arduino);

	static uint8_t *send(ArduinoStatus* arduino);

	static ArduinoStatus *decode(uint8_t const message[]);

public:
	static const uint8_t TOTAL_BYTES_PROTOCOL;

	static uint8_t *sendUSART(status statusValue, event eventValue,
			pin_type pinType, uint8_t pin, uint16_t pinValue);

	static uint8_t *sendEEPROM(status statusValue, event eventValue,
			pin_type pinType, uint8_t pin, uint8_t threadTime,
			uint8_t actionEvent);

	static uint8_t *sendPinDigital(status statusValue, uint8_t pinDigital,
			bool pinValue);

	static uint8_t *sendPinPWM(status statusValue, uint8_t pinPWM,
			uint8_t pinValue);

	static uint8_t *sendPinAnalog(status statusValue, uint8_t pinAnalog,
			uint16_t pinValue);

	static ArduinoStatus *receive(uint8_t const message[]);
};

} /* namespace SISBARC */

#endif /* SISBARCPROTOCOL_H_ */
