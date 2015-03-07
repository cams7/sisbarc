/**
 * 
 */
package br.com.cams7.sisbarc.aal.rest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import br.com.cams7.sisbarc.aal.ejb.service.ArduinoService;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EstadoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.arduino.vo.ArduinoPin.ArduinoPinType;

/**
 * @author cams7
 *
 */
@Path("/arduino")
@RequestScoped
public class ArduinoResourceRESTService {

	@Inject
	private Logger log;

	@EJB
	private ArduinoService service;

	// LED Amarela - arduino/led?tipo_pino=DIGITAL&pino=11&estado=ON
	// LED Verde - arduino/led?tipo_pino=DIGITAL&pino=10&estado=OFF
	// LED Vermelha - arduino/led?tipo_pino=DIGITAL&pino=9&estado=OFF
	@GET
	@Path("/led")
	@Produces(MediaType.APPLICATION_JSON)
	public LEDEntity changeStatusLED(
			@QueryParam("tipo_pino") String stringTipoPino,
			@QueryParam("pino") String stringPino,
			@QueryParam("estado") String stringEstado) {

		ArduinoPinType tipoPino = ArduinoPinType.valueOf(stringTipoPino);
		Short pino = Short.valueOf(stringPino);

		LEDEntity led = service.findOne(new PinPK(tipoPino, pino));
		led.setEstado(EstadoLED.valueOf(stringEstado));

		try {
			Future<LEDEntity> call = service.alteraLEDEstado(led);
			return call.get();
		} catch (InterruptedException | ExecutionException e) {
			log.log(Level.SEVERE, e.getMessage());
		} catch (NullPointerException e) {
			log.log(Level.WARNING, e.getMessage());
		}

		return null;
	}

}
