/**
 * 
 */
package br.com.cams7.sisbarc.aal.jpa.domain.entity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.LedColor;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.LedEvent;
import br.com.cams7.sisbarc.aal.jpa.domain.entity.LedEntity.LedEventTime;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;

/**
 * @author cesar
 *
 */
@StaticMetamodel(LedEntity.class)
public class LedEntity_ {
	public static volatile SingularAttribute<LedEntity, PinPK> id;
	public static volatile SingularAttribute<LedEntity, LedColor> color;
	public static volatile SingularAttribute<LedEntity, LedEvent> event;
	public static volatile SingularAttribute<LedEntity, LedEventTime> eventTime;
	public static volatile SingularAttribute<LedEntity, Boolean> active;
	public static volatile SingularAttribute<LedEntity, Boolean> activeByButton;
}
