/**
 * 
 */
package br.com.cams7.sisbarc.aal.view;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.faces.event.ActionEvent;
import javax.persistence.metamodel.SingularAttribute;

import org.primefaces.context.RequestContext;

import br.com.cams7.as.service.BaseService;
import br.com.cams7.as.view.BaseView;
import br.com.cams7.jpa.domain.BaseEntity;
import br.com.cams7.sisbarc.aal.ejb.service.AALService;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Evento;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Intervalo;

/**
 * @author cams7
 *
 */
public abstract class AALView<S extends BaseService<E, ?>, E extends BaseEntity<?>>
		extends BaseView<S, E> {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public AALView(
			SingularAttribute<? extends BaseEntity<?>, ? extends BaseEntity<?>>... joins) {
		super(joins);
	}

	public void atualizaPino(ActionEvent event) {
		E entidade = getSelectedEntity();

		Pin pino = (Pin) entidade;

		final String MSG_ERROR_UPDATE = getMessageFromI18N(
				"error.msg.pin.update", pino.getId().getPinType().name(), pino
						.getId().getPin());

		RequestContext context = RequestContext.getCurrentInstance();
		final String CALLBACK_PARAM = "arduinoAtualizado";

		try {

			Future<Boolean> call = ((AALService<E, ?>) getService())
					.atualizaPino(entidade);

			boolean arduinoRun = call.get();

			if (arduinoRun) {
				String summary = getMessageFromI18N("info.msg.pin.update.ok");// Resumo
				String detail = getMessageFromI18N("info.msg.pin.update", pino
						.getId().getPinType().name(), pino.getId().getPin());// Detalhes

				addINFOMessage(summary, detail);
				context.addCallbackParam(CALLBACK_PARAM, true);
			} else {
				addMessageArduinoNotRun(MSG_ERROR_UPDATE);
				context.addCallbackParam(CALLBACK_PARAM, false);
			}
		} catch (InterruptedException | ExecutionException e) {
			addERRORMessage(MSG_ERROR_UPDATE, e.getMessage());
			context.addCallbackParam(CALLBACK_PARAM, false);
		} catch (NullPointerException e) {
			addMessageMonitorNotRun(MSG_ERROR_UPDATE);
			context.addCallbackParam(CALLBACK_PARAM, false);
		}

	}

	public void atualizaPinos(ActionEvent event) {
		final String MSG_ERROR_UPDATE = getMessageFromI18N("error.msg.pins.update");

		List<E> pinos = getService().findAll();
		try {
			Future<Boolean> call = ((AALService<E, ?>) getService())
					.alteraEventos(pinos);

			boolean arduinoRun = call.get();

			if (arduinoRun) {
				String summary = getMessageFromI18N("info.msg.pins.update.ok");// Resumo
				String detail = getMessageFromI18N("info.msg.pins.update");// Detalhes

				addINFOMessage(summary, detail);
			} else
				addMessageArduinoNotRun(MSG_ERROR_UPDATE);

		} catch (InterruptedException | ExecutionException e) {
			addERRORMessage(MSG_ERROR_UPDATE, e.getMessage());
		} catch (NullPointerException e) {
			addMessageMonitorNotRun(MSG_ERROR_UPDATE);
		}

	}

	public void sincronizaPinos(ActionEvent event) {
		final String MSG_ERROR_SYNCHRONIZE = getMessageFromI18N("error.msg.pins.synchronize");

		List<E> pinos = getService().findAll();
		try {
			Future<Boolean> call = ((AALService<E, ?>) getService())
					.sincronizaEventos(pinos);

			boolean arduinoRun = call.get();

			if (arduinoRun) {
				String summary = getMessageFromI18N("info.msg.pins.synchronize.ok");// Resumo
				String detail = getMessageFromI18N("info.msg.pins.synchronize");// Detalhes

				addINFOMessage(summary, detail);
			} else
				addMessageArduinoNotRun(MSG_ERROR_SYNCHRONIZE);

		} catch (InterruptedException | ExecutionException e) {
			addERRORMessage(MSG_ERROR_SYNCHRONIZE, e.getMessage());
		} catch (NullPointerException e) {
			addMessageMonitorNotRun(MSG_ERROR_SYNCHRONIZE);
		}

	}

	public Evento[] getEventos() {
		return Evento.values();
	}

	public Intervalo[] getIntervalos() {
		return Intervalo.values();
	}

}
