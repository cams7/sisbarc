/*
 * SisbarcUSART.h
 *
 *  Created on: 04/02/2015
 *      Author: cams7
 */

#ifndef SISBARCUSART_H_
#define SISBARCUSART_H_

#include <inttypes.h>
#include "EEPROMData.h"
#include "ArduinoStatus.h"

namespace SISBARC {

class CallbackUSART {
private:
	bool (*onRun)(ArduinoStatus*);

public:
	CallbackUSART(bool (*callback)(ArduinoStatus*));
	virtual ~CallbackUSART();

	bool run(ArduinoStatus* arduino);
};

struct CallbackNode {
	CallbackUSART* callback;

	struct CallbackNode *next;
	struct CallbackNode *previous;
};

class SisbarcUSART {
private:
	CallbackNode* root;

	uint8_t* serialData;
	uint8_t serialDataIndex;

	void run(ArduinoStatus* arduino);

	static void serialWrite(uint8_t* data);

	static ArduinoStatus *receive(uint8_t const message[]);

public:
	SisbarcUSART();
	virtual ~SisbarcUSART();

	// Callback set
	void onRun(bool (*callback)(ArduinoStatus*));

	void receiveDataBySerial(uint8_t data);

	static void send(ArduinoStatus* arduino);

	static void sendPinDigital(status statusValue, uint8_t pin, bool pinValue);
	static void sendPinPWM(status statusValue, uint8_t pin, uint8_t pinValue);
	static void sendPinAnalog(status statusValue, uint8_t pin,
			uint16_t pinValue);
};

extern SisbarcUSART SISBARC_USART;

} /* namespace SISBARC */

#endif /* SISBARCUSART_H_ */
