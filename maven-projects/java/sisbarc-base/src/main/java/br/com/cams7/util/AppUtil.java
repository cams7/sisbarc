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
}
