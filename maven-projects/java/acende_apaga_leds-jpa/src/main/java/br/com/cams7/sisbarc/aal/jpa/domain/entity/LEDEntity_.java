/**
 * 
 */
package br.com.cams7.sisbarc.aal.jpa.domain.entity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Evento;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Intervalo;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LEDEntity.CorLED;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;

/**
 * @author cesar
 *
 */
@StaticMetamodel(LEDEntity.class)
public class LEDEntity_ {
	public static volatile SingularAttribute<LEDEntity, PinPK> id;
	public static volatile SingularAttribute<LEDEntity, CorLED> cor;
	public static volatile SingularAttribute<LEDEntity, Evento> evento;
	public static volatile SingularAttribute<LEDEntity, Boolean> alteraEvento;
	public static volatile SingularAttribute<LEDEntity, Intervalo> intervalo;
	public static volatile SingularAttribute<LEDEntity, Boolean> alteraIntervalo;
	public static volatile SingularAttribute<LEDEntity, Boolean> ativo;
	public static volatile SingularAttribute<LEDEntity, Boolean> ativadoPorBotao;
}
