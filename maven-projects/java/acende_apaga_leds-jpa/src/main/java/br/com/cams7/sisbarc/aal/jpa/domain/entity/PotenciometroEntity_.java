/**
 * 
 */
package br.com.cams7.sisbarc.aal.jpa.domain.entity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Evento;
import br.com.cams7.sisbarc.aal.jpa.domain.Pin.Intervalo;
import br.com.cams7.sisbarc.aal.jpa.domain.pk.PinPK;

/**
 * @author cesar
 *
 */
@StaticMetamodel(PotenciometroEntity.class)
public class PotenciometroEntity_ {
	public static volatile SingularAttribute<PotenciometroEntity, PinPK> id;

	public static volatile SingularAttribute<PotenciometroEntity, Evento> evento;
	public static volatile SingularAttribute<PotenciometroEntity, Boolean> alteraEvento;
	public static volatile SingularAttribute<PotenciometroEntity, Intervalo> intervalo;
	public static volatile SingularAttribute<PotenciometroEntity, Boolean> alteraIntervalo;

}
