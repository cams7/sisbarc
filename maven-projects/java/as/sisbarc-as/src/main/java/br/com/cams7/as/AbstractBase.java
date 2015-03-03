/**
 * 
 */
package br.com.cams7.as;

import java.util.logging.Logger;

import br.com.cams7.jpa.domain.BaseEntity;

/**
 * @author cesar
 *
 */
public abstract class AbstractBase<E extends BaseEntity<?>> {

	private final byte ENTITY_ARGUMENT_NUMBER = 0;

	private Logger log;

	public AbstractBase() {
		super();
		
		log = Logger.getLogger(this.getClass().getName());
	}

	protected byte getEntityArgumentNumber() {
		return ENTITY_ARGUMENT_NUMBER;
	}

	protected Logger getLog() {
		return log;
	}

}
