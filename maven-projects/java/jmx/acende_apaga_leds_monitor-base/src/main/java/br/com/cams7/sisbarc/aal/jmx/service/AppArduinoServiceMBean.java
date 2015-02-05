/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity;
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
	public void changeStatusLED(PinPK pin, LedEntity.Status status);

	/**
	 * @param color
	 * @return
	 * 
	 *         Obtem o estado atual do LED informado
	 */
	public LedEntity.Status getStatusLED(PinPK pin);

	public void changeEventLED(PinPK pin, LedEntity.Event event,
			LedEntity.EventTime eventTime);

	public LedEntity.Event getEventLED(PinPK pin);

}
