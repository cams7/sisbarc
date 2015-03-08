/**
 * 
 */
package br.com.cams7.sisbarc.aal.ejb.service;

import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.Stateless;

import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Evento;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Intervalo;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.PotenciometroEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.arduino.vo.EEPROMData;

/**
 * @author cams7
 *
 */
@Stateless
@Local(PotenciometroService.class)
public class PotenciometroServiceImpl extends
		AALService<PotenciometroEntity, PinPK> implements PotenciometroService {

	public PotenciometroServiceImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.sisbarc.aal.ejb.service.PotenciometroService#
	 * atualizaPotenciometro
	 * (br.com.cams7.sisbarc.aal.jpa.domain.entity.PotenciometroEntity)
	 */
	@Asynchronous
	public Future<Boolean> atualizaPotenciometro(
			PotenciometroEntity potenciometro) {
		if (getMbeanProxy() == null)
			return null;

		getMbeanProxy().alteraEventoPotenciometro(potenciometro.getId(),
				potenciometro.getEvento(), potenciometro.getIntervalo());

		serialThreadTime();

		Evento evento = getMbeanProxy().getEventoPotenciometro(
				potenciometro.getId());

		Boolean arduinoRun = Boolean.FALSE;

		if (evento != null) {
			potenciometro.setEvento(evento);
			save(potenciometro);
			arduinoRun = Boolean.TRUE;

			getLog().info(
					"O evento do Potenciometro '" + potenciometro.getId()
							+ "' foi alterado '" + potenciometro.getEvento()
							+ "'");
		} else
			getLog().log(
					Level.WARNING,
					"Ocorreu um erro ao tenta buscar o EVENTO do Potenciometro '"
							+ potenciometro.getId() + "'");

		return new AsyncResult<Boolean>(arduinoRun);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.sisbarc.aal.ejb.service.PotenciometroService#
	 * alteraPotenciometroEventos(java.util.List)
	 */
	@Asynchronous
	public Future<Boolean> alteraPotenciometroEventos(
			List<PotenciometroEntity> potenciometros) {
		if (getMbeanProxy() == null)
			return null;

		for (PotenciometroEntity potenciometro : potenciometros)
			getMbeanProxy().alteraEventoPotenciometro(potenciometro.getId(),
					potenciometro.getEvento(), potenciometro.getIntervalo());

		serialThreadTime();

		Boolean arduinoRun = Boolean.TRUE;

		for (PotenciometroEntity potenciometro : potenciometros) {
			Evento evento = getMbeanProxy()
					.getEventoPotenciometro(potenciometro.getId());

			if (evento == null) {
				arduinoRun = Boolean.FALSE;
				break;
			}
		}

		if (arduinoRun)
			getLog().info("Os EVENTOs dos Potenciometros foram alterados");
		else
			getLog().log(Level.WARNING,
					"Ocorreu um erro ao tenta buscar os EVENTOs dos Potenciometros");

		return new AsyncResult<Boolean>(arduinoRun);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.sisbarc.aal.ejb.service.PotenciometroService#
	 * sincronizaPotenciometroEventos(java.util.List)
	 */
	@Asynchronous
	public Future<Boolean> sincronizaPotenciometroEventos(
			List<PotenciometroEntity> potenciometros) {
		if (getMbeanProxy() == null)
			return null;

		for (PotenciometroEntity potenciometro : potenciometros)
			getMbeanProxy().buscaDadosPotenciometro(potenciometro.getId());

		serialThreadTime();

		Boolean arduinoRun = Boolean.TRUE;

		for (PotenciometroEntity potenciometro : potenciometros) {
			EEPROMData data = getMbeanProxy().getDados(potenciometro.getId());
			if (data == null) {
				arduinoRun = Boolean.FALSE;
				break;
			}

			Evento evento = Evento.values()[data
					.getActionEvent()];
			Intervalo intervalo = Intervalo.values()[data
					.getThreadInterval()];

			potenciometro.setEvento(evento);
			potenciometro.setIntervalo(intervalo);
		}

		if (arduinoRun) {
			update(potenciometros);
			getLog().info("Os EVENTOs dos Potenciometros foram sincronizados");
		} else
			getLog().log(Level.WARNING,
					"Ocorreu um erro ao tenta buscar os DADOs dos Potenciometros");

		return new AsyncResult<Boolean>(arduinoRun);
	}

}
