/**
 * 
 */
package br.com.cams7.sisbarc.aal.rest;

import java.util.Date;
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
import br.com.cams7.sisbarc.aal.jpa.domain.Pin;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;

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

	// LED Amarela - arduino/led?pin_type=DIGITAL&pin=11&status=ON
	// LED Verde - arduino/led?pin_type=DIGITAL&pin=10&status=OFF
	// LED Vermelha - arduino/led?pin_type=DIGITAL&pin=9&status=OFF
	@GET
	@Path("/led")
	@Produces(MediaType.APPLICATION_JSON)
	public LedEntity changeStatusLED(@QueryParam("pin_type") String pinType,
			@QueryParam("pin") String pin, @QueryParam("status") String status) {

		Pin.PinType ledPinType = Pin.PinType.valueOf(pinType);
		Short ledPin = Short.valueOf(pin);
		LedEntity.Status ledStatus = LedEntity.Status.valueOf(status);

		LedEntity led = new LedEntity();
		led.setId(new PinPK(ledPinType, ledPin));
		led.setStatus(ledStatus);

		log.info("LED " + led.getColor() + " -> mudaStatusLED('"
				+ led.getId().getPinType() + " " + led.getId().getPin()
				+ "', '" + led.getStatus() + "') - Before sleep: "
				+ ArduinoService.DF.format(new Date()));

		Future<LedEntity> call = service.changeStatusLED(led);

		if (call != null)
			try {
				led = call.get();

				log.info("LED " + led.getColor() + " -> '"
						+ led.getId().getPinType() + " " + led.getId().getPin()
						+ "' esta '" + led.getStatus() + "'");
			} catch (InterruptedException | ExecutionException e) {
				log.log(Level.WARNING, e.getMessage());
			}

		log.info("LED " + led.getColor() + " -> mudaStatusLED('"
				+ led.getId().getPinType() + " " + led.getId().getPin()
				+ "', '" + led.getStatus() + "') - After sleep: "
				+ ArduinoService.DF.format(new Date()));

		return led;
	}

}
