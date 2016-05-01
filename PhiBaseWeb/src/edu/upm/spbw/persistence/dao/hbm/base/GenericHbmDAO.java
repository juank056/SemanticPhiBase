package edu.upm.spbw.persistence.dao.hbm.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.FieldCondition;
import edu.upm.spbw.persistence.bo.AbstractBO;
import edu.upm.spbw.persistence.dao.GenericDAO;
import edu.upm.spbw.utils.LogLogger;
import edu.upm.spbw.utils.exception.MissingConfigurationParameterException;

/**
 * DAO generico Contiene todos los metodos basicos para acceso y modificacion de
 * base de datos
 * 
 * @author Juan Camilo..
 */
public class GenericHbmDAO<T extends AbstractBO<PK>, PK extends Serializable>
		implements GenericDAO<T, PK> {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Sesion de hibernate
	 */
	protected Session session;

	/**
	 * Transaccion hibernate
	 */
	protected Transaction tx;

	/**
	 * Clase con la que trabaja el DAO
	 */
	protected Class<T> clase;

	/**
	 * Indicador de Reuso de session
	 */
	protected boolean reuseSession;

	/**
	 * Maximo Tamaño para limpiar
	 */
	private static final int MAX_SIZE_CLEAN = 5000;

	/**
	 * Constructor
	 * 
	 * @param clase
	 *            La clase con la que va a trabajar este DAO.
	 * @throws MissingConfigurationParameterException
	 */
	public GenericHbmDAO(Class<T> clase) {
		try {
			HibernateFactory.buildIfNeeded();
			// Por default no reusa session
			reuseSession = false;
		} catch (MissingConfigurationParameterException e) {
			System.out.println(ExceptionUtils.getFullStackTrace(e));
		}
		this.clase = clase;
	}

	/**
	 * Metodo para guardar un objeto en la base de datos
	 * 
	 * @param obj
	 *            objeto a guardar
	 * @throws HibernateException
	 */
	public void save(T obj) throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			// Operacion de base de datos
			local_session.save(obj);
			// Commit si no hay reutilizacion de sesiones
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			// Cierra sesion si no re-usa
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
	}

	/**
	 * Metodo para actualizar un objeto en la base de datos
	 * 
	 * @param obj
	 *            objeto a actualizar
	 * @throws DAOException
	 *             Error en la Base de datos
	 */
	public void update(T obj) throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			// Actualiza
			local_session.update(obj);
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
	}

	/**
	 * Metodo para guardar o actualizar un objeto en la base de datos
	 * 
	 * @param obj
	 *            objeto a actualizar o a guardar
	 * @throws DAOException
	 *             Error en la Base de datos
	 */
	public void saveOrUpdate(T obj) throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			// Guarda o actualiza
			local_session.saveOrUpdate(obj);
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
	}

	/**
	 * Metodo para borrar un objeto en la base de datos
	 * 
	 * @param obj
	 *            objeto a borrar en la base de datos
	 * @throws DAOException
	 */
	public void delete(T obj) throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			// Revisa si tiene asociaciones
			checkForAssociations(obj, local_session);
			// Elimina
			local_session.delete(obj);
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
	}

	/**
	 * Se encarga de revisar si el objeto tiene asociaciones
	 * 
	 * @param obj
	 *            Objeto a revisar por asociaciones
	 * @param local_session
	 *            Sesion con la base de datos
	 * @return Booleano de retorno
	 * @throws DAOException
	 *             Se lanzará en caso de que hayan asociaciones en la base de
	 *             datos
	 */
	protected boolean checkForAssociations(T obj, Session local_session)
			throws DAOException {
		// Retorna true por defecto
		return true;
	}

	/**
	 * Metodo para encontrar un objeto en la base de datos dada su llave
	 * 
	 * @param id
	 *            identificador del objeto
	 * @return objeto encontrado
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public T findById(PK id) throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		T obj = null;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			obj = (T) local_session.get(clase, id);
			if (obj != null) {/* Limpia el objeto de ser entontrado */
				obj.prepareObject();
			}
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
		return obj;
	}

	/**
	 * Metodo para encontrar todos los objetos de una clase
	 * 
	 * @param clazz
	 *            clase
	 * @return lista de objetos buscados
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAll() throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		List<T> objects = null;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			Criteria criteria = local_session.createCriteria(clase);
			objects = criteria.list();
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
		// Revisa si limpia objetos
		if (objects.size() < MAX_SIZE_CLEAN) {
			for (T object : objects) {
				object.prepareObject();
			}
		}
		return objects;
	}

	/**
	 * Encuentra Objetos dadas condiciones sobre sus campos
	 * 
	 * @param conditions
	 *            Condiciones de los campos
	 * @return Lista de objetos que cumplen las condiciones
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<T> findByConditions(FieldCondition[] conditions)
			throws DAOException {
		List<Criterion> criterions = new ArrayList<Criterion>();
		// Recorre condiciones
		for (int i = 0; i < conditions.length; i++) {
			// Obtiene condicion
			FieldCondition condition = conditions[i];
			// Revisa que el valor de la condicion sea diferente de null
			if (condition.getValue() == null)
				continue;/* No procesa condicion */
			// Crea criterio
			Criterion crit = null;
			// Revisa caso de la condicion
			switch (condition.getContidion()) {
			case FieldCondition.EQ:
				crit = Restrictions.eq(condition.getName(),
						condition.getValue());
				break;
			case FieldCondition.LE:
				crit = Restrictions.le(condition.getName(),
						condition.getValue());
				break;
			case FieldCondition.GE:
				crit = Restrictions.ge(condition.getName(),
						condition.getValue());
				break;
			case FieldCondition.LT:
				crit = Restrictions.lt(condition.getName(),
						condition.getValue());
				break;
			case FieldCondition.GT:
				crit = Restrictions.gt(condition.getName(),
						condition.getValue());
				break;
			case FieldCondition.IN:
				crit = Restrictions.in(condition.getName(),
						condition.getValues());
				break;
			case FieldCondition.ILIKE:
				crit = Restrictions.ilike(condition.getName(),
						condition.getValue());
				break;
			case FieldCondition.LIKE:
				crit = Restrictions.like(condition.getName(),
						condition.getValue());
				break;
			case FieldCondition.NE:
				crit = Restrictions.ne(condition.getName(),
						condition.getValue());
				break;
			}
			if (crit != null) {/* Hubo criterio */
				criterions.add(crit);
			}
		}
		// Retorna por criterios
		return this.findByCriteria(criterions);
	}

	/**
	 * Busca objetos por criterios
	 * 
	 * @param criterions
	 *            los criterios por los que se quieren buscar los objetos
	 * @return lista de objetos que fueron encontrados
	 * @throws DAOException
	 *             en caso de error con la base de datos
	 */
	@SuppressWarnings("unchecked")
	protected List<T> findByCriteria(List<Criterion> criterions)
			throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		List<T> objects = null;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			Criteria criteria = local_session.createCriteria(clase);
			for (Criterion c : criterions) {
				criteria.add(c);
			}
			objects = criteria.list();
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
		// Revisa si limpia objetos
		if (objects.size() < MAX_SIZE_CLEAN) {
			for (T object : objects) {
				object.prepareObject();
			}
		}
		return objects;
	}

	/**
	 * Busca objetos por criterios y orden
	 * 
	 * @param criterions
	 *            los criterios por los que se quieren buscar los objetos
	 * @param orders
	 *            El orden por el que se quiere retornar
	 * @return lista de objetos que fueron encontrados
	 * @throws DAOException
	 *             en caso de error con la base de datos
	 */
	@SuppressWarnings("unchecked")
	protected List<T> findByCriteria(List<Criterion> criterions,
			List<Order> orders) throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		List<T> objects = null;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			Criteria criteria = local_session.createCriteria(clase);
			for (Criterion c : criterions) {
				criteria.add(c);
			}
			// Orders
			for (Order order : orders) {
				criteria.addOrder(order);
			}
			objects = criteria.list();
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
		// Revisa si limpia objetos
		if (objects.size() < MAX_SIZE_CLEAN) {
			for (T object : objects) {
				object.prepareObject();
			}
		}
		return objects;
	}

	/**
	 * Busca objetos por criterios y orden
	 * 
	 * @param criterions
	 *            los criterios por los que se quieren buscar los objetos
	 * @param orders
	 *            El orden por el que se quiere retornar
	 * @param fetchSize
	 *            Tamano de la lista que se quiere devolver
	 * @return lista de objetos que fueron encontrados
	 * @throws DAOException
	 *             en caso de error con la base de datos
	 */
	@SuppressWarnings("unchecked")
	protected List<T> findByCriteria(List<Criterion> criterions,
			List<Order> orders, int fetchSize) throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		List<T> objects = null;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			Criteria criteria = local_session.createCriteria(clase);
			for (Criterion c : criterions) {
				criteria.add(c);
			}
			// Orders
			for (Order order : orders) {
				criteria.addOrder(order);
			}
			criteria.setFetchSize(fetchSize);
			criteria.setMaxResults(fetchSize);
			objects = criteria.list();
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
		// Revisa si limpia objetos
		if (objects.size() < MAX_SIZE_CLEAN) {
			for (T object : objects) {
				object.prepareObject();
			}
		}
		return objects;
	}

	/**
	 * Busca objetos por query
	 * 
	 * @param query
	 *            Query que se quiere realizar
	 * @param parameters
	 *            parametros a usar en el query
	 * @return Lista de objetos que se hayan encontrado
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	@SuppressWarnings("unchecked")
	protected List<T> findByQuery(String query, Map<String, Object> parameters)
			throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		List<T> objects = null;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			// Crea query
			Query queryObj = local_session.createQuery(query);
			// Asigna Parametros
			for (String key : parameters.keySet()) {
				// Obtiene valor
				Object value = parameters.get(key);
				// Asigna parametro
				queryObj.setParameter(key, value);
			}
			objects = queryObj.list();
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
		// Revisa si limpia objetos
		if (objects.size() < MAX_SIZE_CLEAN) {
			for (T object : objects) {
				object.prepareObject();
			}
		}
		return objects;
	}

	/**
	 * Busca objetos por query
	 * 
	 * @param query
	 *            Query que se quiere realizar
	 * @param parameters
	 *            parametros a usar en el query
	 * @return Lista de objetos que se hayan encontrado
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	@SuppressWarnings("unchecked")
	protected List<T> findByQuery(String query, Map<String, Object> parameters,
			int fetchSize) throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		List<T> objects = null;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			// Crea query
			Query queryObj = local_session.createQuery(query);
			// Asigna Parametros
			for (String key : parameters.keySet()) {
				// Obtiene valor
				Object value = parameters.get(key);
				// Asigna parametro
				queryObj.setParameter(key, value);
			}
			// Numero maximo de registros
			queryObj.setFetchSize(fetchSize);
			queryObj.setMaxResults(fetchSize);
			objects = queryObj.list();
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
		// Revisa si limpia objetos
		if (objects.size() < MAX_SIZE_CLEAN) {
			for (T object : objects) {
				object.prepareObject();
			}
		}
		return objects;
	}

	/**
	 * Busca registros por una query que devuelve objetos genericos
	 * 
	 * @param query
	 *            Query a ejecutar
	 * @param parameters
	 *            Parametros de la query
	 * @return Lista de registros encontrados
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	protected List<?> findByQueryGeneric(String query,
			Map<String, Object> parameters) throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		List<?> objects = null;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			// Crea query
			Query queryObj = local_session.createQuery(query);
			// Asigna Parametros
			for (String key : parameters.keySet()) {
				// Obtiene valor
				Object value = parameters.get(key);
				// Asigna parametro
				queryObj.setParameter(key, value);
			}
			objects = queryObj.list();
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
		// Revisa si limpia objetos
		if (objects.size() < MAX_SIZE_CLEAN) {
			for (Object object : objects) {
				if (object instanceof AbstractBO<?>)
					((AbstractBO<?>) object).prepareObject();
			}
		}
		return objects;
	}

	/**
	 * Ejecuta un query de Update
	 * 
	 * @param query
	 *            Query que se quiere realizar
	 * @param parameters
	 *            parametros a usar en el query
	 * @return Numero de objetos actualizados
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	protected int executeUpdateQuery(String query,
			Map<String, Object> parameters) throws DAOException {
		// Sesion y transaccion locales
		Session local_session = null;
		Transaction local_tx = null;
		int objects = 0;
		try {
			if (!reuseSession) {/* No re-usa */
				// Inicia nueva sesion y transaccion
				local_session = HibernateFactory.openSession();
				local_tx = local_session.beginTransaction();
			} else {/* Esta re-usando */
				// Asigna a variables temporales
				local_session = session;
				local_tx = tx;
			}
			// Crea query
			Query queryObj = local_session.createQuery(query);
			// Asigna Parametros
			for (String key : parameters.keySet()) {
				// Obtiene valor
				Object value = parameters.get(key);
				// Asigna parametro
				queryObj.setParameter(key, value);
			}
			objects = queryObj.executeUpdate();
			if (!reuseSession)/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!reuseSession)/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
		return objects;
	}

	/**
	 * Metodo para el manejo de excepcion
	 * 
	 * @param e
	 *            excepcion
	 * @throws DAOException
	 *             Lanza una nueva exception de tipo DAOException
	 */
	protected void handleException(Exception e, Transaction t)
			throws DAOException {
		// Rollback de la operacion
		HibernateFactory.rollback(t);
		// Imprime en log por si no se hace por fuera
		LogLogger.getInstance(this.getClass()).logger(
				ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
		// Nueva excepcion
		throw new DAOException(e);
	}

	/**
	 * Metodo para abrir la sesion hibernate y comenzar la transaccion
	 * 
	 * @throws HibernateException
	 *             Error de Hibernate
	 * @throws MissingConfigurationParameterException
	 *             Parametro de Configuracion
	 */
	protected void startOperation() throws HibernateException,
			MissingConfigurationParameterException {
		session = HibernateFactory.openSession();
		tx = session.beginTransaction();
	}

	/**
	 * Sobre escribe el toString para acciones de log
	 */
	public String toString() {
		return clase.getName();
	}

	/**
	 * Asigna indicador de reutilizacion de session
	 */
	public void setReuseSession(boolean reuseSession) {
		this.reuseSession = reuseSession;
	}

	/**
	 * Retorna indicador de reuso de session
	 */
	public boolean getReuseSession() {
		return reuseSession;
	}

	/**
	 * Inicia Operacion de la base de datos
	 */
	public void initDbOperation() throws DAOException {
		try {
			this.startOperation();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} catch (MissingConfigurationParameterException e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Finaliza Operacion de la base de datos
	 */
	public void finishDBOperation() throws DAOException {
		// Realiza commit de la transaccion
		tx.commit();
		// Cierra session
		HibernateFactory.close(session);
	}
}
