package edu.upm.spbw.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.upm.spbw.utils.exception.MissingConfigurationParameterException;

/**
 * Clase para manejar el log de la aplicacion
 * 
 * @author Juan Camilo Mesa
 * 
 */
public final class LogLogger {
	private static LogLogger instancia;
	public static String ERROR = "ERROR";
	public static String DEBUG = "DEBUG";
	public static String INFO = "INFO";
	public static String FATAL = "FATAL";
	public static String WARN = "WARN";

	static Logger logger = null;

	protected LogLogger() {
	}

	@SuppressWarnings("rawtypes")
	public static synchronized LogLogger getInstance(Class clase) {
		logger = Logger.getLogger(clase);
		if (instancia == null) {
			LogLogger instancia = new LogLogger();
			return instancia;
		} else {
			return instancia;
		}
	}

	/**
	 * @param String
	 *            msg
	 * @param priority
	 *            la prioridad puede ser ERROR,DEBUG,INFO,FATAL,WARN
	 */
	public void logger(String msg, String priority) {
		try {
			PropertyConfigurator.configure(ConfigApplicationManager
					.getParameter("filecfg"));
		} catch (MissingConfigurationParameterException e) {
			e.printStackTrace();
		}

		if (ERROR.equalsIgnoreCase(priority)) {
			logger.error(msg);
			return;
		} else if (DEBUG.equalsIgnoreCase(priority)) {
			logger.debug(msg);
			return;
		} else if (INFO.equalsIgnoreCase(priority)) {
			logger.info(msg);
			return;
		} else if (FATAL.equalsIgnoreCase(priority)) {
			logger.fatal(msg);
			return;
		} else if (WARN.equalsIgnoreCase(priority)) {
			logger.warn(msg);
			return;
		}
	}
}
