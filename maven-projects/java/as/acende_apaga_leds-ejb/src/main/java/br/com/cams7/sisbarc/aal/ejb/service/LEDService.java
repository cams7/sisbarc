/**
 * 
 */
package br.com.cams7.sisbarc.aal.ejb.service;

import java.util.List;
import java.util.concurrent.Future;

import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;

/**
 * @author cams7
 *
 */
public interface LEDService extends AALService<LEDEntity, PinPK> {

	public Future<LEDEntity> alteraLEDEstado(LEDEntity led);

	public Future<List<LEDEntity>> getLEDsAtivadoPorBotao();

}
