/**
 * 
 */
package edu.upm.spbw.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Clase para manejar el Application Context de Spring en una unica instancia
 * 
 * @author Juan Camilo
 */
public class SpringAppContext {

	private static ApplicationContext appContext;

	/**
	 * Constructor privado
	 */
	private SpringAppContext() {
	}

	/**
	 * Obtiene el Application Context para Spring
	 * 
	 * @return el application Context de Spring
	 */
	public static ApplicationContext getAppContext() {
		if (appContext == null)
			rebuildAppContext();
		return appContext;
	}

	/**
	 * Reconstruye el application context de spring.
	 */
	public static void rebuildAppContext() {
		if (appContext == null)
			appContext = new ClassPathXmlApplicationContext(
					new String[] { "/daos.xml" });
	}
}
