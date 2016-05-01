/**
 * 
 */
package edu.upm.spbw.persistence.dao;

import java.util.Date;
import java.util.List;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Usdlogqu;
import edu.upm.spbw.persistence.bo.UsdlogquId;

/**
 * DAO para trabajar con la tabla Usdlogqu
 * 
 * @author Juan Camilo Mesa
 * 
 */
public interface IUsdlogquDAO extends GenericDAO<Usdlogqu, UsdlogquId> {

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
	public List<Usdlogqu> findByDateRange(Date sinceDate, Date untilDate,
			int maxRecords) throws DAOException;

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
	public List<Usdlogqu> findByDateRangeAndUser(String usuemaiak,
			Date sinceDate, Date untilDate, int maxRecords) throws DAOException;

	/**
	 * Se encarga de obtener los codigos de usuarios registrados en la
	 * estructura
	 * 
	 * @return Lista de los codigos de usuarios de la estructura Posicion 0 el
	 *         codigo del usuario. Posicion 1 el nombre del usuario
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Object[]> findUserCodes() throws DAOException;
}
