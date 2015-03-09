package br.com.cams7.sisbarc.aal.view;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.cams7.sisbarc.aal.ejb.service.LEDService;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Evento;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.CorLED;

/**
 * Componente responsável por integrar o front-end (páginas JSF) c/ camada de
 * serviço (EJB), para resolver o cadastro de <code>LED</code>.
 * 
 * <p>
 * Trata-se de um <code>Managed Bean</code>, ou seja, as instâncias dessa classe
 * são controladas pelo <code>JSF</code>. Um objeto é criado ao carregar alguma
 * página do cadastro (Lista / Novo / Editar). Enquanto a página permanecer
 * aberta no browser, o objeto <code>LEDBean</code> permanece no servidor.
 * </p>
 * 
 * <p>
 * Esse componente atua com um papel parecido com o <code>Controller</code> de
 * outros frameworks <code>MVC</code>, ele resolve o fluxo de navegação e liga
 * os componentes visuais com os dados.
 * </p>
 * 
 * @author cams7
 *
 */
@ManagedBean(name = "ledView")
@ViewScoped
public class LEDView extends AALView<LEDService, LEDEntity> {

	private static final long serialVersionUID = 1L;

	/**
	 * Container injeta a referencia p/ o ejb MercadoriaService
	 */
	@EJB
	private LEDService service;

	@SuppressWarnings("unchecked")
	public LEDView() {
		super();
	}

	@Override
	protected void init() {
		getLog().info("Init View");
	}

	@Override
	protected LEDService getService() {
		return service;
	}

	public CorLED[] getCores() {
		return CorLED.values();
	}

	@Override
	public Evento[] getEventos() {
		Evento[] eventos = new Evento[3];
		eventos[0] = Evento.ACENDE_APAGA;
		eventos[1] = Evento.PISCA_PISCA;
		eventos[2] = Evento.FADE;

		return eventos;
	}

}
