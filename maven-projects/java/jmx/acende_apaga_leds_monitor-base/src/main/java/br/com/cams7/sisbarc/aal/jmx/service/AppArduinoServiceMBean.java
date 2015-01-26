/**
 * 
 */
package br.com.cams7.sisbarc.aal.jmx.service;

import br.com.cams7.arduino.ArduinoService;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.Status;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.Cor;

/**
 * @author cesar
 *
 */
public interface AppArduinoServiceMBean extends ArduinoService {

	/**
	 * @param cor
	 * @param status
	 * 
	 *            Altera o estado do LED ligado/desligado
	 */
	public void mudaStatusLED(Cor cor, Status status);

	/**
	 * @param cor
	 * @return
	 * 
	 *         Obtem o estado atual do LED informado
	 */
	public Status getStatusLED(Cor cor);

}
