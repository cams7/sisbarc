/**
 * 
 */
package br.com.cams7.sisbarc.aal.ejb.service;

import java.text.SimpleDateFormat;
import java.util.concurrent.Future;

import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.as.service.BaseService;

/**
 * @author cams7
 *
 */
public interface ArduinoService extends BaseService<LedEntity, PinPK> {
	public static final String YEARS_TO_MILLIS = "dd/MM/yyyy HH:mm:ss.SSS";
	public static final SimpleDateFormat DF = new SimpleDateFormat(
			YEARS_TO_MILLIS);

	public Future<LedEntity> changeStatusLED(LedEntity led);

}
