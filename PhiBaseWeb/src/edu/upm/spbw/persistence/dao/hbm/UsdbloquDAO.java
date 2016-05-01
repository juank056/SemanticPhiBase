/**
 * 
 */
package edu.upm.spbw.persistence.dao.hbm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Usdbloqu;
import edu.upm.spbw.persistence.bo.UsdbloquId;
import edu.upm.spbw.persistence.dao.IUsdbloquDAO;
import edu.upm.spbw.persistence.dao.hbm.base.GenericHbmDAO;

/**
 * DAO para manejar la tabla de Usdbloqu
 * 
 * @author Juan Camilo Mesa
 */
public class UsdbloquDAO extends GenericHbmDAO<Usdbloqu, UsdbloquId> implements
		IUsdbloquDAO {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	public UsdbloquDAO() {
		super(Usdbloqu.class);
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
	public List<Usdbloqu> findByDateRange(long sinceDate, long untilDate,
			int maxRecords) throws DAOException {
		// Crea los criterios
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(Restrictions.ge("id.usutblonf", sinceDate));
		crit.add(Restrictions.le("id.usutblonf", untilDate));
		// Orders
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id.usutblonf"));
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
	public List<Usdbloqu> findByDateRangeAndUser(String usuemaiak,
			long sinceDate, long untilDate, int maxRecords) throws DAOException {
		// Crea los criterios
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(Restrictions.eq("id.usuemaiak", usuemaiak));
		crit.add(Restrictions.ge("id.usutblonf", sinceDate));
		crit.add(Restrictions.le("id.usutblonf", untilDate));
		// Orders
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id.usutblonf"));
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
				+ " FROM Usdbloqu log, Usmusuar usu WHERE usu.usuemaiak=log.id.usuemaiak";
		// Ejecuta
		@SuppressWarnings("unchecked")
		List<Object[]> result = (List<Object[]>) this.findByQueryGeneric(query,
				new HashMap<String, Object>());
		// Retorna
		return result;
	}
}
