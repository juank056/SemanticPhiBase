/**
 * 
 */
package edu.upm.spbw.persistence.dao.hbm;

import java.util.ArrayList;
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
import edu.upm.spbw.persistence.bo.Sedrelco;
import edu.upm.spbw.persistence.bo.SedrelcoId;
import edu.upm.spbw.persistence.dao.ISedrelcoDAO;
import edu.upm.spbw.persistence.dao.hbm.base.GenericHbmDAO;
import edu.upm.spbw.persistence.dao.hbm.base.HibernateFactory;
import edu.upm.spbw.utils.Constants;

/**
 * DAO para manejar la tabla de Sedrelco
 * 
 * @author Juan Camilo Mesa
 */
public class SedrelcoDAO extends GenericHbmDAO<Sedrelco, SedrelcoId> implements
		ISedrelcoDAO {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	public SedrelcoDAO() {
		super(Sedrelco.class);
	}

	/**
	 * Metodo para guardar un objeto en la base de datos
	 * 
	 * @param obj
	 *            objeto a guardar
	 * @throws HibernateException
	 */
	public void save(Sedrelco obj) throws DAOException {
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
			criteria.add(Restrictions.eq("id.dconsecnk", obj.getId()
					.getDconsecnk()));
			criteria.setProjection(Projections.max("id.drcnsecnk"));
			// Asigna secuencia
			Object zeroValue = criteria.list().get(0);
			int lastSec = zeroValue != null ? ((Integer) zeroValue).intValue()
					: 0;
			// Asigna secuencia
			obj.getId().setDrcnsecnk(lastSec + 1);
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
	 * Busca registros por padre
	 * 
	 * @param dconsecnk
	 *            Llave de detalle concepto
	 * @return Lista de relaciones del detalle concepto
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Sedrelco> findByConcept(Integer dconsecnk) throws DAOException {
		// Crea los criterios
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(Restrictions.eq("id.dconsecnk", dconsecnk));
		return this.findByCriteria(crit);
	}

	/**
	 * Se encarga de eliminar todos los registros de la estructura
	 * 
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public void deleteAllRecords() throws DAOException {
		// Query de borrado
		String query = "delete from Sedrelco";
		// Ejecuta query
		this.executeUpdateQuery(query, new HashMap<String, Object>());
	}

	/**
	 * Busca registros por concepto
	 * 
	 * @param cosccosak
	 *            Codigo de Concepto
	 * @param rcocrcoak
	 *            Codigo de Relacion
	 * @param drcvalraf
	 *            Valor de la relacion
	 * @param maxRecords
	 *            Maximo de registros a traer
	 * @return Registros encontrados
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Sedrelco> findByConceptAndDescription(List<String> cosccosak,
			String rcocrcoak, String drcvalraf, int maxRecords)
			throws DAOException {
		// Crea los criterios
		List<Criterion> crit = new ArrayList<Criterion>();
		if (cosccosak != null) {
			crit.add(Restrictions.in("cosccosak", cosccosak));
		}
		if (rcocrcoak != null && !Constants.BLANKS.equals(rcocrcoak))
			crit.add(Restrictions.eq("rcocrcoak", rcocrcoak));
		// Reemplaza espacios por porcentajes
		drcvalraf = drcvalraf.replace(Constants.BLANK_SPACE,
				Constants.PERCENTAGE);
		crit.add(Restrictions.like("drcvalraf", Constants.PERCENTAGE
				+ drcvalraf + Constants.PERCENTAGE));
		// Lista de orders
		List<Order> orders = new ArrayList<>();
		orders.add(Order.asc("drcvalraf"));
		return this.findByCriteria(crit, orders, maxRecords);
	}
}
