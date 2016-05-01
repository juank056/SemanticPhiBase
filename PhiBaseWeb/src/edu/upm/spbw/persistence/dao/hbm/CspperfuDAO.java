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
import edu.upm.spbw.persistence.bo.Cspperfu;
import edu.upm.spbw.persistence.dao.ICspperfuDAO;
import edu.upm.spbw.persistence.dao.hbm.base.GenericHbmDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;

/**
 * DAO para manejar la tabla de Cspperfu
 * 
 * @author Juan Camilo Mesa
 */
public class CspperfuDAO extends GenericHbmDAO<Cspperfu, String> implements
		ICspperfuDAO {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	public CspperfuDAO() {
		super(Cspperfu.class);
	}

	/**
	 * Obtiene perfiles de usuario activos en el sistema
	 * 
	 * @return Lista de perfiles de usuario activos en el sistema
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Cspperfu> findActive() throws DAOException {
		// Crea los criterios
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(Restrictions.eq("pfuiactsf", Constants.ONE));
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
	protected boolean checkForAssociations(Cspperfu obj, Session local_session)
			throws DAOException {
		// Definicion de variables
		String query = null;
		Query queryObj = null;
		long value = 0;
		/*
		 * Valida relacion con USMUSUAR
		 */
		// String de query
		query = "select count(*) from Usmusuar u where u.pfucpfuak = :perfu";
		// Crea query
		queryObj = local_session.createQuery(query);
		// Perfil
		queryObj.setParameter("perfu", obj.getPfucpfuak());
		// Ejecuta query
		value = (Long) queryObj.uniqueResult();
		// Revisa si hay algun valor
		if (value != 0) {
			// No se puede eliminar por registros asociados
			throw new DAOException(ApplicationMessages.getMessage("USR0034",
					ApplicationMessages.getMessage("USMUSUAR")));
		}
		/*
		 * Valida relacion con CSPSERPF
		 */
		// String de query
		query = "select count(*) from Cspserpf s where s.id.pfucpfuak = :perfu";
		// Crea query
		queryObj = local_session.createQuery(query);
		// Perfil
		queryObj.setParameter("perfu", obj.getPfucpfuak());
		// Ejecuta query
		value = (Long) queryObj.uniqueResult();
		// Revisa si hay algun valor
		if (value != 0) {
			// No se puede eliminar por registros asociados
			throw new DAOException(ApplicationMessages.getMessage("USR0034",
					ApplicationMessages.getMessage("CSPSERPF")));
		}
		// Finaliza
		return true;
	}
}
