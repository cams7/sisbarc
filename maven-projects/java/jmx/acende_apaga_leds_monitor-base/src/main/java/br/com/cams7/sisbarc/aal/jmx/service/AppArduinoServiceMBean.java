/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity;
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
	public void changeStatusLED(LedEntity.Color color, LedEntity.Status status);

	/**
	 * @param color
	 * @return
	 * 
	 *         Obtem o estado atual do LED informado
	 */
	public LedEntity.Status getStatusLED(LedEntity.Color color);

}
