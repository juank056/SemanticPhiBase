/**
 * 
 */
package edu.upm.spbw.persistence.dao;

import java.util.List;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Cspperfu;

/**
 * DAO para trabajar con la tabla Cspperfu
 * 
 * @author Juan Camilo Mesa
 * 
 */
public interface ICspperfuDAO extends GenericDAO<Cspperfu, String> {

	/**
	 * Obtiene perfiles de usuario activos en el sistema
	 * 
	 * @return Lista de perfiles de usuario activos en el sistema
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Cspperfu> findActive() throws DAOException;

}
