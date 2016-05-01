/**
 * 
 */
package edu.upm.spbw.persistence.dao.hbm;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Sepconce;
import edu.upm.spbw.persistence.dao.ISepconceDAO;
import edu.upm.spbw.persistence.dao.hbm.base.GenericHbmDAO;
import edu.upm.spbw.utils.ApplicationMessages;

/**
 * DAO para manejar la tabla de Sepconce
 * 
 * @author Juan Camilo Mesa
 */
public class SepconceDAO extends GenericHbmDAO<Sepconce, String> implements
		ISepconceDAO {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	public SepconceDAO() {
		super(Sepconce.class);
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
	protected boolean checkForAssociations(Sepconce obj, Session local_session)
			throws DAOException {
		// Definicion de variables
		String query = null;
		Query queryObj = null;
		long value = 0;
		/*
		 * Valida relacion con SEPRELCO
		 */
		// String de query
		query = "select count(*) from Seprelco s where s.id.cosccosak = :conce";
		// Crea query
		queryObj = local_session.createQuery(query);
		// Perfil
		queryObj.setParameter("conce", obj.getCosccosak());
		// Ejecuta query
		value = (Long) queryObj.uniqueResult();
		// Revisa si hay algun valor
		if (value != 0) {
			// No se puede eliminar por registros asociados
			throw new DAOException(ApplicationMessages.getMessage("USR0034",
					ApplicationMessages.getMessage("SEPRELCO")));
		}
		/*
		 * Valida relacion con SEPCONCE
		 */
		// String de query
		query = "select count(*) from Sepconce s where s.cosccopak = :conce";
		// Crea query
		queryObj = local_session.createQuery(query);
		// Perfil
		queryObj.setParameter("conce", obj.getCosccosak());
		// Ejecuta query
		value = (Long) queryObj.uniqueResult();
		// Revisa si hay algun valor
		if (value != 0) {
			// No se puede eliminar por registros asociados
			throw new DAOException(ApplicationMessages.getMessage("USR0034",
					ApplicationMessages.getMessage("SEPCONCE")));
		}
		/*
		 * Valida relacion con SEDCONCE
		 */
		// String de query
		query = "select count(*) from Sedconce s where s.cosccosak = :conce";
		// Crea query
		queryObj = local_session.createQuery(query);
		// Perfil
		queryObj.setParameter("conce", obj.getCosccosak());
		// Ejecuta query
		value = (Long) queryObj.uniqueResult();
		// Revisa si hay algun valor
		if (value != 0) {
			// No se puede eliminar por registros asociados
			throw new DAOException(ApplicationMessages.getMessage("USR0034",
					ApplicationMessages.getMessage("SEDCONCE")));
		}
		// Finaliza
		return true;
	}
}
