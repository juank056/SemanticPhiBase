/**
 * 
 */
package edu.upm.spbw.persistence.dao.hbm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Usdlogin;
import edu.upm.spbw.persistence.bo.UsdloginId;
import edu.upm.spbw.persistence.dao.IUsdloginDAO;
import edu.upm.spbw.persistence.dao.hbm.base.GenericHbmDAO;
import edu.upm.spbw.persistence.dao.hbm.base.HibernateFactory;

/**
 * DAO para manejar la tabla de Usdlogin
 * 
 * @author Juan Camilo Mesa
 */
public class UsdloginDAO extends GenericHbmDAO<Usdlogin, UsdloginId> implements
		IUsdloginDAO {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	public UsdloginDAO() {
		super(Usdlogin.class);
	}

	/**
	 * Metodo para guardar un objeto en la base de datos
	 * 
	 * @param obj
	 *            objeto a guardar
	 * @throws HibernateException
	 */
	public void save(Usdlogin obj) throws DAOException {
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
			// Obtiene ultima secuencia
			Criteria criteria = local_session.createCriteria(this.clase);
			criteria.add(Restrictions.eq("id.usuemaiak", obj.getId()
					.getUsuemaiak()));
			criteria.add(Restrictions.eq("id.liufechfk", obj.getId()
					.getLiufechfk()));
			criteria.setProjection(Projections.max("id.liunsecnk"));
			// Asigna secuencia
			Object zeroValue = criteria.list().get(0);
			int lastSec = zeroValue != null ? ((Integer) zeroValue).intValue()
					: 0;
			// Asigna secuencia
			obj.getId().setLiunsecnk(lastSec + 1);
			// Guarda objeto
			local_session.save(obj);
			if (!this.getReuseSession())/* Solo si no esta re-usando */
				local_tx.commit();
		} catch (Exception e) {
			handleException(e, local_tx);
		} finally {
			if (!this.getReuseSession())/* Solo si no esta re-usando */
				HibernateFactory.close(local_session);
		}
	}

	/**
	 * Busca registros por rango de fecha
	 * 
	 * @param sinceDate
	 *            Fecha desde de busqueda
	 * @param untilDate
	 *            Fecha hasta de busqueda
	 * @param maxRecords
	 *            Maximo de registros a buscar
	 * @return Lista de registros encontrados
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Usdlogin> findByDateRange(Date sinceDate, Date untilDate,
			int maxRecords) throws DAOException {
		// Crea los criterios
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(Restrictions.ge("id.liufechfk", sinceDate));
		crit.add(Restrictions.le("id.liufechfk", untilDate));
		// Orders
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id.liufechfk"));
		orders.add(Order.desc("liuhoratf"));
		// Ejecuta query
		return this.findByCriteria(crit, orders, maxRecords);
	}

	/**
	 * Busca registros por rango de fecha
	 * 
	 * @param usuemaiak
	 *            Nombre de usuario de busqueda
	 * @param sinceDate
	 *            Fecha desde de busqueda
	 * @param untilDate
	 *            Fecha hasta de busqueda
	 * @param maxRecords
	 *            Maximo de registros a buscar
	 * @return Lista de registros encontrados
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Usdlogin> findByDateRangeAndUser(String usuemaiak,
			Date sinceDate, Date untilDate, int maxRecords) throws DAOException {
		// Crea los criterios
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(Restrictions.eq("id.usuemaiak", usuemaiak));
		crit.add(Restrictions.ge("id.liufechfk", sinceDate));
		crit.add(Restrictions.le("id.liufechfk", untilDate));
		// Orders
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id.liufechfk"));
		orders.add(Order.desc("liuhoratf"));
		// Ejecuta query
		return this.findByCriteria(crit, orders, maxRecords);
	}

	/**
	 * Se encarga de obtener los codigos de usuarios registrados en la
	 * estructura
	 * 
	 * @return Lista de los codigos de usuarios de la estructura
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Object[]> findUserCodes() throws DAOException {
		// Query a ejecutar
		String query = "SELECT DISTINCT log.id.usuemaiak, usu.usunusuaf"
				+ " FROM Usdlogin log, Usmusuar usu WHERE usu.usuemaiak=log.id.usuemaiak";
		// Ejecuta
		@SuppressWarnings("unchecked")
		List<Object[]> result = (List<Object[]>) this.findByQueryGeneric(query,
				new HashMap<String, Object>());
		// Retorna
		return result;
	}
}
