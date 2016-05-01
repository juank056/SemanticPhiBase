/**
 * 
 */
package edu.upm.spbw.persistence.dao.hbm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Sedconce;
import edu.upm.spbw.persistence.dao.ISedconceDAO;
import edu.upm.spbw.persistence.dao.hbm.base.GenericHbmDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;

/**
 * DAO para manejar la tabla de Sedconce
 * 
 * @author Juan Camilo Mesa
 */
public class SedconceDAO extends GenericHbmDAO<Sedconce, Integer> implements
		ISedconceDAO {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	public SedconceDAO() {
		super(Sedconce.class);
	}

	/**
	 * Se encarga de eliminar todos los registros de la estructura
	 * 
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public void deleteAllRecords() throws DAOException {
		// Query de borrado
		String query = "delete from Sedconce";
		// Ejecuta query
		this.executeUpdateQuery(query, new HashMap<String, Object>());
	}

	/**
	 * Busca registros por código de concepto y por IRI
	 * 
	 * @param cosccosak
	 *            Código de concepto
	 * @param dcoiricaf
	 *            IRI del registro
	 * @param maxRecords
	 *            Maximo de registros a obtener
	 * @return Lista de registros entontrados
	 * @throws DAOException
	 */
	public List<Sedconce> findByConceptAndIRI(String cosccosak,
			String dcoiricaf, int maxRecords) throws DAOException {
		// Crea los criterios
		List<Criterion> crit = new ArrayList<Criterion>();
		if (!Constants.BLANKS.equals(cosccosak))
			crit.add(Restrictions.eq("cosccosak", cosccosak));
		// Reemplaza espacios por porcentajes
		dcoiricaf = dcoiricaf.replace(Constants.BLANK_SPACE,
				Constants.PERCENTAGE);
		crit.add(Restrictions.like("dcoiricaf", Constants.PERCENTAGE
				+ dcoiricaf + Constants.PERCENTAGE));
		// Lista de orders
		List<Order> orders = new ArrayList<>();
		orders.add(Order.asc("dcoiricaf"));
		return this.findByCriteria(crit, orders, maxRecords);
	}

	/**
	 * Busca objeto por IRI
	 * 
	 * @param dcoiricaf
	 *            IRI del objeto
	 * @return Objeto por IRI
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public Sedconce findByIRI(String dcoiricaf) throws DAOException {
		// Crea los criterios
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(Restrictions.eq("dcoiricaf", dcoiricaf));
		List<Sedconce> list = this.findByCriteria(crit);
		return list.size() > 0 ? list.get(0) : null;
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
	@Override
	protected boolean checkForAssociations(Sedconce obj, Session local_session)
			throws DAOException {
		// Definicion de variables
		String query = null;
		Query queryObj = null;
		long value = 0;
		/*
		 * Valida relacion con SEDRELCO
		 */
		// String de query
		query = "select count(*) from Sedrelco s where s.id.dconsecnk = :conce";
		// Crea query
		queryObj = local_session.createQuery(query);
		// Parametros
		queryObj.setParameter("conce", obj.getDconsecnk());
		// Ejecuta query
		value = (Long) queryObj.uniqueResult();
		// Revisa si hay algun valor
		if (value != 0) {
			// No se puede eliminar por registros asociados
			throw new DAOException(ApplicationMessages.getMessage("USR0034",
					ApplicationMessages.getMessage("SEDRELCO")));
		}
		// Finaliza
		return true;
	}
}
