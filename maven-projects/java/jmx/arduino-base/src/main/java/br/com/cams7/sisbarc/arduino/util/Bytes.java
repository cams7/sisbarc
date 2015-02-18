/**
 * 
 */
package br.com.cams7.sisbarc.arduino.util;

/**
 * @author cams7
 *
 */
public final class Bytes {

	public static byte[] toPrimitiveArray(Byte[] values) {
		byte[] bytes = new byte[values.length];
		for (byte i = 0x00; i < values.length; i++)
			bytes[i] = values[i];

		return bytes;
	}

}
