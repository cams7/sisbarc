package br.com.cams7.sisbarc.aal.backing;

import static javax.faces.context.FacesContext.getCurrentInstance;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.com.cams7.sisbarc.aal.ejb.service.ArduinoService;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.Color;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.Event;

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
@ManagedBean(name = "ledBean")
@ViewScoped
public class LEDBean implements Serializable {

	/**
	 * Container injeta a referencia p/ o ejb MercadoriaService
	 */
	@EJB
	private ArduinoService service;

	private Short selectedID;

	/**
	 * Lista com a(s) <code>Mercadoria</code>(s) apresentandas no
	 * <code>Datatable</code>.
	 */
	private Iterable<LedEntity> leds;

	/**
	 * Referência para a mercadoria utiliza na inclusão (nova) ou edição.
	 */
	private LedEntity led;

	public LEDBean() {
		super();
	}

	public void setSelectedID(Short selectedID) {
		this.selectedID = selectedID;
	}

	public Short getSelectedID() {
		return selectedID;
	}

	public LedEntity getLed() {
		return led;
	}

	public void newEntity() {
		led = new LedEntity();
		// log.debug("Pronto pra incluir");
	}

	public void editEntity() {
		if (selectedID == null) {
			return;
		}
		led = service.findOne(selectedID);
		// log.debug("Pronto pra editar");
	}

	public Iterable<LedEntity> getLeds() {
		if (leds == null) {
			leds = service.findAll();
		}
		return leds;
	}

	public String saveEntity() {
		try {
			service.save(led);
		} catch (Exception ex) {
			// log.error("Erro ao salvar mercadoria.", ex);
			addMessage(getMessageFromI18N("error.msg.led.save"),
					ex.getMessage());
			return "";
		}
		// log.debug("Salvour mercadoria "+mercadoria.getId());
		return "ledList";
	}

	public String removeEntity() {
		try {
			service.remove(led);
		} catch (Exception ex) {
			// log.error("Erro ao remover mercadoria.", ex);
			addMessage(getMessageFromI18N("error.msg.led.delete"),
					ex.getMessage());
			return "";
		}
		// log.debug("Removeu mercadoria "+mercadoria.getId());
		return "ledList";
	}

	/**
	 * @param key
	 * @return Recupera a mensagem do arquivo properties
	 *         <code>ResourceBundle</code>.
	 */
	private String getMessageFromI18N(String key) {
		ResourceBundle bundle = ResourceBundle.getBundle("messages",
				getCurrentInstance().getViewRoot().getLocale());
		return bundle.getString(key);
	}

	/**
	 * Adiciona um mensagem no contexto do Faces (<code>FacesContext</code>).
	 * 
	 * @param summary
	 * @param detail
	 */
	private void addMessage(String summary, String detail) {
		getCurrentInstance().addMessage(
				null,
				new FacesMessage(summary, summary.concat("<br/>")
						.concat(detail)));
	}

	public Color[] getColors() {
		return Color.values();
	}

	public Event[] getEvents() {
		return Event.values();
	}

}
