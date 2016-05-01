/**
 * 
 */
package edu.upm.spbw.persistence.dao.hbm;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Usmusuar;
import edu.upm.spbw.persistence.dao.IUsmusuarDAO;
import edu.upm.spbw.persistence.dao.hbm.base.GenericHbmDAO;
import edu.upm.spbw.utils.ApplicationMessages;

/**
 * DAO para manejar la tabla de Usmusuar
 * 
 * @author Juan Camilo Mesa
 */
public class UsmusuarDAO extends GenericHbmDAO<Usmusuar, String> implements
		IUsmusuarDAO {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	public UsmusuarDAO() {
		super(Usmusuar.class);
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
	protected boolean checkForAssociations(Usmusuar obj, Session local_session)
			throws DAOException {
		// Definicion de variables
		String query = null;
		Query queryObj = null;
		long value = 0;
		/*
		 * Valida relacion con USDBLOQU (Llave)
		 */
		// String de query
		query = "select count(*) from Usdbloqu u where u.id.usuemaiak = :usuar";
		// Crea query
		queryObj = local_session.createQuery(query);
		// Perfil
		queryObj.setParameter("usuar", obj.getUsuemaiak());
		// Ejecuta query
		value = (Long) queryObj.uniqueResult();
		// Revisa si hay algun valor
		if (value != 0) {
			// No se puede eliminar por registros asociados
			throw new DAOException(ApplicationMessages.getMessage("USR0034",
					ApplicationMessages.getMessage("USDBLOQU")));
		}
		/*
		 * Valida relacion con USDBLOQU (Usuario desbloqueo)
		 */
		// String de query
		query = "select count(*) from Usdbloqu u where u.usuemadaf = :usuar";
		// Crea query
		queryObj = local_session.createQuery(query);
		// Perfil
		queryObj.setParameter("usuar", obj.getUsuemaiak());
		// Ejecuta query
		value = (Long) queryObj.uniqueResult();
		// Revisa si hay algun valor
		if (value != 0) {
			// No se puede eliminar por registros asociados
			throw new DAOException(ApplicationMessages.getMessage("USR0034",
					ApplicationMessages.getMessage("USDBLOQU")));
		}
		/*
		 * Valida relacion con USDLOGIN
		 */
		// String de query
		query = "select count(*) from Usdlogin u where u.id.usuemaiak = :usuar";
		// Crea query
		queryObj = local_session.createQuery(query);
		// Perfil
		queryObj.setParameter("usuar", obj.getUsuemaiak());
		// Ejecuta query
		value = (Long) queryObj.uniqueResult();
		// Revisa si hay algun valor
		if (value != 0) {
			// No se puede eliminar por registros asociados
			throw new DAOException(ApplicationMessages.getMessage("USR0034",
					ApplicationMessages.getMessage("USDLOGIN")));
		}
		/*
		 * Valida relacion con USDLOGQU
		 */
		// String de query
		query = "select count(*) from Usdlogqu u where u.id.usuemaiak = :usuar";
		// Crea query
		queryObj = local_session.createQuery(query);
		// Perfil
		queryObj.setParameter("usuar", obj.getUsuemaiak());
		// Ejecuta query
		value = (Long) queryObj.uniqueResult();
		// Revisa si hay algun valor
		if (value != 0) {
			// No se puede eliminar por registros asociados
			throw new DAOException(ApplicationMessages.getMessage("USR0034",
					ApplicationMessages.getMessage("USDLOGQU")));
		}
		// Finaliza
		return true;
	}
}
