package edu.upm.spbw.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.exception.ExceptionUtils;

import edu.upm.spbw.utils.exception.MissingConfigurationParameterException;

/**
 * Clase para manejar los parametros del archivo de configuracion
 * 
 * @author Juan Camilo Mesa
 * 
 */
public final class ConfigApplicationManager {

	/**
	 * Indica la ruta donde se encuentra el archivo de configuracion de la
	 * aplicacion unica ruta en codigo "quemada".
	 */
	private static final String CONFIGURATION_FILE = "../conf/config.properties";
	private static Map<String, String> parameters;
	static {
		reloadParameters();
	} // static

	/**
	 * Constructor privado
	 */
	private ConfigApplicationManager() {
	}

	/**
	 * Metodo para obtener el valor de un parametro
	 * 
	 * @param name
	 *            el nombre del parametro a obtener
	 * @return el valor del parametro.
	 * @throws MissingConfigurationParameterException
	 *             en caso de no encontrar el parametro
	 */
	public static String getParameter(String name)
			throws MissingConfigurationParameterException {
		String value = parameters.get(name);
		if (value == null) {
			throw new MissingConfigurationParameterException(
					"Parameter Not Found: " + name);
		}
		return value;
	}

	/**
	 * Regarga los parametros del archivo de configuracion
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void reloadParameters() {
		try {
			// Crea propiedades
			Properties properties = new Properties();
			// Input stream para el archivo
			InputStream inputStream = null;
			try {
				Class configParametersManagerClass = ConfigApplicationManager.class;
				ClassLoader classLoader = configParametersManagerClass
						.getClassLoader();
				inputStream = classLoader
						.getResourceAsStream(CONFIGURATION_FILE);
			} catch (Exception e) {
				System.out
						.println("Input stream null. The application will now terminate.\nStack Trace:\n");
				System.out.println(ExceptionUtils.getFullStackTrace(e));
				System.exit(-1);
			}
			properties.load(inputStream);
			inputStream.close();
			parameters = new HashMap(properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
} // class

