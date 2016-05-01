/**
 * 
 */
package edu.upm.spbw.persistence.dao.hbm;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Seprelco;
import edu.upm.spbw.persistence.bo.SeprelcoId;
import edu.upm.spbw.persistence.dao.ISeprelcoDAO;
import edu.upm.spbw.persistence.dao.hbm.base.GenericHbmDAO;
import edu.upm.spbw.utils.ApplicationMessages;

/**
 * DAO para manejar la tabla de Seprelco
 * 
 * @author Juan Camilo Mesa
 */
public class SeprelcoDAO extends GenericHbmDAO<Seprelco, SeprelcoId> implements
		ISeprelcoDAO {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	public SeprelcoDAO() {
		super(Seprelco.class);
	}

	/**
	 * Obtiene registros por concepto semantico
	 * 
	 * @param cosccosak
	 *            Codigo de concepto semántico
	 * @return Lista de relaciones del concepto semantico
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Seprelco> findByConcept(String cosccosak) throws DAOException {
		// Crea los criterios
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(Restrictions.eq("id.cosccosak", cosccosak));
		return this.findByCriteria(crit);
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
	protected boolean checkForAssociations(Seprelco obj, Session local_session)
			throws DAOException {
		// Definicion de variables
		String query = null;
		Query queryObj = null;
		long value = 0;
		/*
		 * Valida relacion con SEDRELCO
		 */
		// String de query
		query = "select count(*) from Sedrelco s where s.cosccosak = :conce and s.rcocrcoak = :relco";
		// Crea query
		queryObj = local_session.createQuery(query);
		// Parametros
		queryObj.setParameter("conce", obj.getId().getCosccosak());
		queryObj.setParameter("relco", obj.getId().getRcocrcoak());
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
