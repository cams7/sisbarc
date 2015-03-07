/**
 * 
 */
package br.com.cams7.sisbarc.arduino.vo;

import java.io.Serializable;

/**
 * @author cams7
 *
 */
public interface EEPROMData extends Serializable {

	public static final byte THREAD_INTERVAL_MAX = 0x07; // 7

	public static final byte DIGITAL_ACTION_EVENT_MAX = 0x1F; // 31
	public static final byte ANALOG_ACTION_EVENT_MAX = 0x7F; // 127

	public byte getThreadInterval();

	public void setThreadInterval(byte threadInterval);

	public byte getActionEvent();

	public void setActionEvent(byte actionEvent);
}
