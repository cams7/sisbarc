package br.com.cams7.sisbarc.aal.ejb.service;

import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Evento;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Intervalo;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.EstadoLED;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity_;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;
import br.com.cams7.sisbarc.arduino.vo.ArduinoPin.ArduinoPinType;
import br.com.cams7.sisbarc.arduino.vo.EEPROMData;

@Stateless
@Local(LEDService.class)
@Remote(AppWildflyService.class)
public class LEDServiceImpl extends AALService<LEDEntity, PinPK> implements
		LEDService, AppWildflyService {

	public LEDServiceImpl() {
		super();
	}

	@Asynchronous
	public Future<LEDEntity> alteraLEDEstado(LEDEntity led) {
		if (getMbeanProxy() == null)
			return null;

		if (led.getEstado() == EstadoLED.ACESO && !led.isAtivo()) {
			led.setEstado(EstadoLED.APAGADO);
		} else {
			getMbeanProxy().alteraEstadoLED(led.getId(), led.getEstado());

			serialThreadTime();

			EstadoLED estado = getMbeanProxy().getEstadoLED(led.getId());
			led.setEstado(estado);

			if (estado != null) {
				getLog().info(
						"LED '" + led.getId() + "' esta '" + led.getEstado()
								+ "'");
			} else
				getLog().log(
						Level.WARNING,
						"Ocorreu um erro ao tenta buscar o ESTADO do LED '"
								+ led.getId() + "'");
		}

		return new AsyncResult<LEDEntity>(led);
	}

	@Asynchronous
	public Future<Boolean> atualizaLED(LEDEntity led) {
		if (getMbeanProxy() == null)
			return null;

		getMbeanProxy().alteraEventoLED(led.getId(), led.getEvento(),
				led.getIntervalo());

		serialThreadTime();

		Evento evento = getMbeanProxy().getEventoLED(led.getId());

		Boolean arduinoRun = Boolean.FALSE;

		if (evento != null) {
			led.setEvento(evento);
			save(led);
			arduinoRun = Boolean.TRUE;

			getLog().info(
					"O evento do LED '" + led.getId() + "' foi alterado '"
							+ led.getEvento() + "'");
		} else
			getLog().log(
					Level.WARNING,
					"Ocorreu um erro ao tenta buscar o EVENTO do LED '"
							+ led.getId() + "'");

		return new AsyncResult<Boolean>(arduinoRun);
	}

	@Override
	public EstadoLED getEstadoLEDAtivadoPorBotao(byte pin) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Boolean> criteria = builder.createQuery(Boolean.class);

		Root<LEDEntity> root = criteria.from(getEntityType());

		criteria.select(root.get(LEDEntity_.ativo));

		Predicate isActiveButton = builder.isTrue(root
				.get(LEDEntity_.ativadoPorBotao));
		Predicate equalPin = builder.equal(root.get(LEDEntity_.id), new PinPK(
				ArduinoPinType.DIGITAL, Short.valueOf(pin)));

		Predicate and = builder.and(isActiveButton, equalPin);

		criteria.where(and);

		TypedQuery<Boolean> query = getEntityManager().createQuery(criteria);
		Boolean active = query.getSingleResult();
		if (active == Boolean.TRUE)
			return EstadoLED.ACESO;

		return EstadoLED.APAGADO;
	}

	@Asynchronous
	public Future<Boolean> alteraLEDEventos(List<LEDEntity> leds) {
		if (getMbeanProxy() == null)
			return null;

		for (LEDEntity led : leds)
			getMbeanProxy().alteraEventoLED(led.getId(), led.getEvento(),
					led.getIntervalo());

		serialThreadTime();

		Boolean arduinoRun = Boolean.TRUE;

		for (LEDEntity led : leds) {
			Evento evento = getMbeanProxy().getEventoLED(led.getId());

			if (evento == null) {
				arduinoRun = Boolean.FALSE;
				break;
			}
		}

		if (arduinoRun)
			getLog().info("Os EVENTOs dos LEDs foram alterados");
		else
			getLog().log(Level.WARNING,
					"Ocorreu um erro ao tenta buscar os EVENTOs dos LEDs");

		return new AsyncResult<Boolean>(arduinoRun);
	}

	@Asynchronous
	public Future<Boolean> sincronizaLEDEventos(List<LEDEntity> leds) {
		if (getMbeanProxy() == null)
			return null;

		for (LEDEntity led : leds)
			getMbeanProxy().buscaDadosLED(led.getId());

		serialThreadTime();

		Boolean arduinoRun = Boolean.TRUE;

		for (LEDEntity led : leds) {
			EEPROMData data = getMbeanProxy().getDados(led.getId());
			if (data == null) {
				arduinoRun = Boolean.FALSE;
				break;
			}

			Evento evento = Evento.values()[data.getActionEvent()];
			Intervalo intervalo = Intervalo.values()[data.getThreadInterval()];

			led.setEvento(evento);
			led.setIntervalo(intervalo);
		}

		if (arduinoRun) {
			update(leds);
			getLog().info("Os EVENTOs dos LEDs foram sincronizados");
		} else
			getLog().log(Level.WARNING,
					"Ocorreu um erro ao tenta buscar os DADOs dos LEDs");

		return new AsyncResult<Boolean>(arduinoRun);
	}

}
