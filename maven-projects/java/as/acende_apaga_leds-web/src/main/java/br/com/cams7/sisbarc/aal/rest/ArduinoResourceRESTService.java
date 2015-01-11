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
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity;

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

	// arduino/led?led=AMARELA&status=ACESA
	// arduino/led?led=VERDE&status=APAGADA
	// arduino/led?led=VERMELHA&status=ACESA
	@GET
	@Path("/led")
	@Produces(MediaType.APPLICATION_JSON)
	public LedEntity mudaStatusLED(@QueryParam("led") String stringLed,
			@QueryParam("status") String stringStatus) {

		LedEntity.Cor ledCor = LedEntity.Cor.valueOf(stringLed);
		LedEntity.Status ledStatus = LedEntity.Status.valueOf(stringStatus);

		LedEntity led = new LedEntity();
		led.setCor(ledCor);
		led.setStatus(ledStatus);

		log.info("mudaStatusLED('" + led.getCor() + "', '" + led.getStatus()
				+ "') - Before sleep: " + ArduinoService.DF.format(new Date()));

		Future<LedEntity> call = service.mudaStatusLED(led);

		if (call != null)
			try {
				led = call.get();

				log.info("LED '" + led.getCor() + "' esta '" + led.getStatus()
						+ "'");
			} catch (InterruptedException | ExecutionException e) {
				log.log(Level.WARNING, e.getMessage());
			}

		log.info("mudaStatusLED('" + led.getCor() + "', '" + led.getStatus()
				+ "') - After sleep: " + ArduinoService.DF.format(new Date()));

		return led;
	}

}
