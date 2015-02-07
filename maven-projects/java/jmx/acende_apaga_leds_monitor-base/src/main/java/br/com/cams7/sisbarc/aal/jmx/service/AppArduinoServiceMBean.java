/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.LedEvent;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.LedEventTime;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.LedStatus;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.arduino.ArduinoService;

/**
 * @author cesar
 *
 */
public interface AppArduinoServiceMBean extends ArduinoService {

	/**
	 * @param color
	 * @param status
	 * 
	 *            Altera o estado do LED ligado/desligado
	 */
	public void changeStatusLED(PinPK pin, LedStatus status);

	/**
	 * @param color
	 * @return
	 * 
	 *         Obtem o estado atual do LED informado
	 */
	public LedStatus getStatusLED(PinPK pin);

	public void changeEventLED(PinPK pin, LedEvent event, LedEventTime eventTime);

	public LedEvent getEventLED(PinPK pin);

}
