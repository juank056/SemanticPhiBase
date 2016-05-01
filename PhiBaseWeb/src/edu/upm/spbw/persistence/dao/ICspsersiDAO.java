/**
 * 
 */
package edu.upm.spbw.persistence.dao;

import java.util.List;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Cspsersi;

/**
 * DAO para trabajar con la tabla Cspsersi
 * 
 * @author Juan Camilo Mesa
 * 
 */
public interface ICspsersiDAO extends GenericDAO<Cspsersi, String> {

	/**
	 * Obtiene los servicios del sistema activos
	 * 
	 * @return Lista de servicios del sistema activos
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Cspsersi> findActive() throws DAOException;

}
