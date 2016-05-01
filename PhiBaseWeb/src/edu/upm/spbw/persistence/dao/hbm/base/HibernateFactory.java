package edu.upm.spbw.persistence.dao.hbm.base;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import edu.upm.spbw.utils.ConfigApplicationManager;
import edu.upm.spbw.utils.DBDataChiper;
import edu.upm.spbw.utils.exception.MissingConfigurationParameterException;

/**
 * Fabrica para el manejo de las sessiones Hibernate
 * 
 * @author Juan Camilo Mesa
 */

public class HibernateFactory {

	/*************************************
	 * Atributos
	 *************************************/

	/**
	 * Fabrica de sesiones
	 */
	private static SessionFactory sessionFactory;

	/****************************************
	 * Metodos
	 ****************************************/

	/**
	 * Constructs a new Singleton SessionFactory
	 * 
	 * @return una nueva fabrica de sesiones
	 * @throws HibernateException
	 *             Error de Hibernate
	 * @throws MissingConfigurationParameterException
	 *             Falta parametro de Configuracion
	 */
	public static SessionFactory buildSessionFactory()
			throws HibernateException, MissingConfigurationParameterException {
		if (sessionFactory != null) {
			closeFactory();
		}
		return configureSessionFactory();
	}

	/**
	 * Builds a SessionFactory, if it hasn't been already.
	 * 
	 * @return la fabrica de sesiones
	 * @throws MissingConfigurationParameterException
	 */
	public static SessionFactory buildIfNeeded()
			throws MissingConfigurationParameterException {
		if (sessionFactory != null) {
			return sessionFactory;
		}
		try {
			return configureSessionFactory();
		} catch (HibernateException e) {
			System.out.println(ExceptionUtils.getFullStackTrace(e));
			return null;
		}
	}

	/**
	 * Abrir una nueva sesion de Hibernate
	 * 
	 * @return la nueva session
	 * @throws HibernateException
	 *             Error de Hibernate
	 * @throws MissingConfigurationParameterException
	 *             Falta parametro de Configuracion
	 */
	public static Session openSession() throws HibernateException,
			MissingConfigurationParameterException {
		buildIfNeeded();
		return sessionFactory.openSession();
	}

	/**
	 * Metodo para cerrar la fabrica de sesiones
	 */
	public static void closeFactory() {
		if (sessionFactory != null) {
			try {
				sessionFactory.close();
			} catch (HibernateException ignored) {
				System.out.println(ExceptionUtils.getFullStackTrace(ignored));
			}
		}
	}

	/**
	 * Metodo para cerrar una session
	 * 
	 * @param session
	 *            la session que va a ser cerrada
	 */
	public static void close(Session session) {
		if (session != null) {
			try {
				session.close();
			} catch (HibernateException ignored) {
				System.out.println(ExceptionUtils.getFullStackTrace(ignored));
			}
		}
	}

	/**
	 * Metodo para realizar el rollback de una transaccion
	 * 
	 * @param tx
	 *            la transaccion a la que se le va a hacer rollback
	 */
	public static void rollback(Transaction tx) {
		try {
			if (tx != null) {
				tx.rollback();
			}
		} catch (HibernateException ignored) {
			System.out.println(ExceptionUtils.getFullStackTrace(ignored));
		}
	}

	/**
	 * Metodo para configurar una fabrica de sessiones
	 * 
	 * @return la fabrica de sessiones que ha sido configurada
	 * @throws HibernateException
	 *             Error de Hibernate
	 * @throws MissingConfigurationParameterException
	 *             Falta parametro de Configuracion
	 */
	private static SessionFactory configureSessionFactory()
			throws HibernateException, MissingConfigurationParameterException {
		Configuration configuration = new Configuration();
		configuration.configure();
		// Asigna parametros del archivo de configuracion
		// 1. Url de la base de datos
		configuration.setProperty("hibernate.connection.url",
				ConfigApplicationManager.getParameter("edu.upm.spbw.db.url"));
		// 2. Driver de la base de datos
		configuration
				.setProperty("hibernate.connection.driver_class",
						ConfigApplicationManager
								.getParameter("edu.upm.spbw.db.driver"));
		// 3. Nombre del Usuario
		configuration.setProperty(
				"hibernate.connection.username",
				DBDataChiper.decrypt(ConfigApplicationManager.getParameter(
						"edu.upm.spbw.db.username").trim()));
		// 4. Password del Usuario
		configuration.setProperty(
				"hibernate.connection.password",
				DBDataChiper.decrypt(ConfigApplicationManager.getParameter(
						"edu.upm.spbw.db.passwd").trim()));
		// 5. Dialecto de la base de datos
		configuration.setProperty("hibernate.dialect", ConfigApplicationManager
				.getParameter("edu.upm.spbw.db.dialect"));
		sessionFactory = configuration.buildSessionFactory();
		return sessionFactory;
	}

	/**
	 * Reconstruye fabrica de sessiones
	 * 
	 * @throws MissingConfigurationParameterException
	 *             Falta parametro de configuracion
	 * @throws HibernateException
	 *             Error de hibernate
	 */
	public static void rebuildSessionFactory() throws HibernateException,
			MissingConfigurationParameterException {
		// Llama al metodo de construit el session factory
		configureSessionFactory();
	}
}
