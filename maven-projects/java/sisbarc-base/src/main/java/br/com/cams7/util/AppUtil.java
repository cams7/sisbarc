/**
 * 
 */
package br.com.cams7.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import br.com.cams7.jpa.domain.BaseEntity;

/**
 * @author cams7
 *
 */
public final class AppUtil {
	public static URL getURLFile(Class<?> classType, String fileName) {
		URL fileURL = classType.getClassLoader().getResource(fileName);
		return fileURL;
	}

	public static Properties getPropertiesFile(Class<?> classType,
			String fileName) throws AppException {
		URL fileURL = getURLFile(classType, fileName);

		InputStream in = null;
		try {
			in = fileURL.openStream();
			Reader reader = new InputStreamReader(in, "UTF-8");

			Properties properties = new Properties();

			try {
				properties.load(reader);
			} finally {
				reader.close();
			}

			return properties;
		} catch (IOException e) {
			throw new AppException(e.getCause());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static Class<?> getType(Class<?> type, byte argumentNumber) {
		Class<?> returnType = (Class<?>) ((ParameterizedType) type
				.getGenericSuperclass()).getActualTypeArguments()[argumentNumber];
		return returnType;
	}

	public static Class<?> getType(Object object, byte argumentNumber) {
		Class<?> type = getType(object.getClass(), argumentNumber);
		return type;
	}

	public static BaseEntity<?> getNewEntity(Class<BaseEntity<?>> entityType)
			throws AppException {
		try {
			BaseEntity<?> entity = entityType.newInstance();
			return entity;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new AppException(e.getMessage(), e.getCause());
		}
	}

	public static <ID extends Serializable> BaseEntity<ID> getNewEntity(
			Class<BaseEntity<?>> entityType, ID id) throws AppException {

		@SuppressWarnings("unchecked")
		BaseEntity<ID> entity = (BaseEntity<ID>) getNewEntity(entityType);
		entity.setId(id);

		return entity;
	}

	public static boolean isBoolean(Class<?> type) {
		return type.equals(Boolean.class) || type.equals(Boolean.TYPE);
	}

	private static boolean isInteger(Class<?> type) {
		if (type.equals(Byte.class) || type.equals(Byte.TYPE))
			return true;

		if (type.equals(Short.class) || type.equals(Short.TYPE))
			return true;

		if (type.equals(Integer.class) || type.equals(Integer.TYPE))
			return true;

		if (type.equals(Long.class) || type.equals(Long.TYPE))
			return true;

		return false;
	}

	private static boolean isFloat(Class<?> type) {
		if (type.equals(Float.class) || type.equals(Float.TYPE))
			return true;

		if (type.equals(Double.class) || type.equals(Double.TYPE))
			return true;

		return false;
	}

	public static boolean isNumber(Class<?> type) {
		if (isInteger(type))
			return true;

		if (isFloat(type))
			return true;

		return false;
	}

	public static boolean isDate(Class<?> type) {
		return type.equals(Date.class);
	}

	public static boolean isEnum(Class<?> type) {
		return type.isEnum();
	}

	private static String getCorrectValue(String value) {
		if (value == null || value.isEmpty())
			return null;

		return value.trim();
	}

	public static Map<Integer, Short>[] getDate(String value) {
		value = getCorrectValue(value);

		if (value == null)
			return null;

		final String REGEX_HOUR_MINUTE_SECOND = "(([01][0-9]|2[0-3]|[\\*])[:]([0-5][0-9]|[\\*])([:]([0-5][0-9]|[\\*]))?)";
		// TODO Ano bissexto não e valido
		final String REGEX_DATE = "(((((((0[1-9]|1[0-9]|2[0-8]|[\\*])[\\/])?(0[1-9]|1[012]|[\\*]))|((29|30|31)[\\/](0[13578]|1[02]|[\\*]))|((29|30)[\\/](0[4,6,9]|11)))[\\/])?((19|20)\\d{2}|[\\*]))";
		final String REGEX_DATETIME = "(" + REGEX_DATE + ")([ \t]"
				+ REGEX_HOUR_MINUTE_SECOND + ")?)";
		final String REGEX_BETWEEN_TWO_DATES = "(^(" + REGEX_DATETIME + "(\\-"
				+ REGEX_DATETIME + ")?)$)";

		if (!value.matches(REGEX_BETWEEN_TWO_DATES))
			return null;

		String[] stringDates = value.split("\\-");

		@SuppressWarnings("unchecked")
		Map<Integer, Short>[] between = new Map[stringDates.length];

		for (byte i = 0; i < stringDates.length; i++) {
			String[] dateAndTime = stringDates[i].split(" ");

			String date = dateAndTime[0];
			String time = dateAndTime.length > 1 ? dateAndTime[1] : null;

			Map<Integer, Short> map = new HashMap<Integer, Short>();

			String[] dateValues = date.split("\\/");

			String year = dateValues[dateValues.length - 1];
			if (!"*".equals(year))
				map.put(Calendar.YEAR, Short.valueOf(year));

			if (dateValues.length > 1) {
				String month = dateValues[dateValues.length - 2];
				if (!"*".equals(month))
					map.put(Calendar.MONTH, Short.valueOf(month));

				if (dateValues.length == 3) {
					String day = dateValues[dateValues.length - 3];

					if (!"*".equals(day))
						map.put(Calendar.DAY_OF_MONTH, Short.valueOf(day));
				}
			}

			if (time != null) {
				String[] timeValues = time.split("\\:");
				String hour = timeValues[0];
				String minute = timeValues[1];

				if (!"*".equals(hour))
					map.put(Calendar.HOUR_OF_DAY, Short.valueOf(hour));

				if (!"*".equals(minute))
					map.put(Calendar.MINUTE, Short.valueOf(minute));

				if (timeValues.length == 3) {
					String second = timeValues[2];
					if (!"*".equals(second))
						map.put(Calendar.SECOND, Short.valueOf(second));
				}
			}

			between[i] = map;

		}

		return between;

	}

	public static Calendar[] getDate(Map<Integer, Short>[] between) {
		if (between == null)
			return null;

		GregorianCalendar[] dates = new GregorianCalendar[between.length];

		boolean isTwoDates = between.length == 2;

		Locale ptBr = new Locale("pt", "BR"); // Locale para o Brasil

		dates[0] = new GregorianCalendar(ptBr);
		dates[0].set(Calendar.YEAR, 1900);
		dates[0].set(Calendar.MONTH, 0);
		dates[0].set(Calendar.DAY_OF_MONTH, 1);
		dates[0].set(Calendar.HOUR_OF_DAY, 0);
		dates[0].set(Calendar.MINUTE, 0);
		dates[0].set(Calendar.SECOND, 0);
		dates[0].set(Calendar.MILLISECOND, 0);

		if (isTwoDates) {
			dates[1] = new GregorianCalendar(ptBr);
			dates[1].set(Calendar.YEAR, 2099);
			dates[1].set(Calendar.MONTH, 11);
			dates[1].set(Calendar.DAY_OF_MONTH, 31);
			dates[1].set(Calendar.HOUR_OF_DAY, 23);
			dates[1].set(Calendar.MINUTE, 59);
			dates[1].set(Calendar.SECOND, 59);
			dates[1].set(Calendar.MILLISECOND, 999);
		}

		for (byte i = 0; i < between.length; i++) {
			if (between[i].get(Calendar.YEAR) != null)
				dates[i].set(Calendar.YEAR, between[i].get(Calendar.YEAR));

			if (between[i].get(Calendar.MONTH) != null) {
				int month = between[i].get(Calendar.MONTH);

				if (i == 1) {
					if (month == 4 || month == 6 || month == 9 || month == 11)
						dates[i].set(Calendar.DAY_OF_MONTH, 30);
					else if (month == 2)
						dates[i].set(Calendar.DAY_OF_MONTH, dates[i]
								.isLeapYear(dates[i].get(Calendar.YEAR)) ? 29
								: 28);
				}
				dates[i].set(Calendar.MONTH, month - 1);
			}

			if (between[i].get(Calendar.DAY_OF_MONTH) != null)
				dates[i].set(Calendar.DAY_OF_MONTH,
						between[i].get(Calendar.DAY_OF_MONTH));

			if (between[i].get(Calendar.HOUR_OF_DAY) != null)
				dates[i].set(Calendar.HOUR_OF_DAY,
						between[i].get(Calendar.HOUR_OF_DAY));

			if (between[i].get(Calendar.MINUTE) != null)
				dates[i].set(Calendar.MINUTE, between[i].get(Calendar.MINUTE));

			if (between[i].get(Calendar.SECOND) != null)
				dates[i].set(Calendar.SECOND, between[i].get(Calendar.SECOND));
		}

		return dates;
	}

	public static <K, V> boolean equalMaps(Map<K, V> map1, Map<K, V> map2) {

		if (map1 == null && map2 == null)
			return true;

		if ((map1 == null && map2 != null) || (map1 != null && map2 == null))
			return false;

		if (map1.isEmpty() && map2.isEmpty())
			return true;

		if ((map1.isEmpty() && !map2.isEmpty())
				|| (!map1.isEmpty() && map2.isEmpty()))
			return false;

		boolean isEquals = true;

		if (map1.size() == map2.size()) {
			keyMap: for (K key : map1.keySet()) {
				if (!map2.containsKey(key)) {
					isEquals = false;
					break;
				}

				V value1 = map1.get(key);
				V value2 = map2.get(key);

				if (value1 instanceof Object[] && value2 instanceof Object[]) {
					Object[] array1 = (Object[]) value1;
					Object[] array2 = (Object[]) value2;

					if (array1.length != array2.length) {
						isEquals = false;
						break;
					}

					List<?> list = Arrays.asList(array1);

					for (Object value : array2) {
						if (!list.contains(value)) {
							isEquals = false;
							break keyMap;
						}
					}

				} else if (!value1.equals(value2)) {
					isEquals = false;
					break;
				}

			}
		} else
			isEquals = false;

		return isEquals;
	}

	public static <K, V> Map<K, V> removeEmptyArray(Map<K, V> map) {
		if (map != null && !map.isEmpty()) {
			Iterator<Map.Entry<K, V>> i = map.entrySet().iterator();

			while (i.hasNext()) {
				Map.Entry<K, V> entry = i.next();
				V value = entry.getValue();

				if ((value instanceof Object[])
						&& ((Object[]) value).length == 0)
					i.remove();
			}

		}

		return map;
	}

}
