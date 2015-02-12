/*
 * Thread.cpp
 *
 *  Created on: 10/02/2015
 *      Author: cams7
 */

#include "Thread.h"

#include <Arduino.h>
//#include <stdio.h>
//#include "../util/_arduino_time.h"

namespace SISBARC {

Thread::Thread(void (*callback)(void), long interval) :
		_lastRun(0), _cachedNextRun(0), _enabled(true), _threadID(
				(unsigned long) this) {

	onRun(callback);
	setInterval(interval);

	//printf("New Thread\n");
}

Thread::~Thread() {
	//printf("Delete Thread\n");
}

unsigned long Thread::getThreadID(void) const {
	return _threadID;
}

void Thread::runned(long time) {
	// If less than 0, than get current ticks
	if (time < 0)
		time = millis();

	// Saves last_run
	_lastRun = time;

	// Cache next run
	_cachedNextRun = _lastRun + _interval;
}

void Thread::setInterval(long const interval) {
	// Filter intervals less than 0
	_interval = (interval < 0 ? 0 : interval);

	// Cache the next run based on the last_run
	_cachedNextRun = _lastRun + _interval;
}

bool Thread::shouldRun(long time) {
	// If less than 0, than get current ticks
	if (time < 0)
		time = millis();

	// Exceeded the time limit, AND is enabled? Then should run...
	return ((time >= _cachedNextRun) && _enabled);
}

void Thread::onRun(void (*callback)(void)) {
	this->_onRun = callback;
}

void Thread::run(void) {
	if (_onRun != NULL)
		_onRun();

	// Update last_run and _cached_next_run
	runned();
}

} /* namespace SISBARC */
