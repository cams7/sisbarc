/**
 * 
 */
package br.com.cams7.sisbarc.aal.ejb.service;

import java.util.List;
import java.util.concurrent.Future;

import br.com.cams7.as.service.BaseService;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;

/**
 * @author cams7
 *
 */
public interface LEDService extends BaseService<LEDEntity, PinPK> {

	public Future<LEDEntity> alteraLEDEstado(LEDEntity led);

	public Future<Boolean> atualizaLED(LEDEntity led);

	public Future<Boolean> sincronizaLEDEventos(List<LEDEntity> leds);

	public Future<Boolean> alteraLEDEventos(List<LEDEntity> leds);

}
