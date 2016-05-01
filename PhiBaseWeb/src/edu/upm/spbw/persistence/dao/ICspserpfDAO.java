/**
 * 
 */
package edu.upm.spbw.persistence.dao;

import java.util.List;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Cspserpf;
import edu.upm.spbw.persistence.bo.CspserpfId;

/**
 * DAO para trabajar con la tabla Cspserpf
 * 
 * @author Juan Camilo Mesa
 * 
 */
public interface ICspserpfDAO extends GenericDAO<Cspserpf, CspserpfId> {

	/**
	 * Obtiene servicios activos de un perfil de usuario
	 * 
	 * @param pfucpfuak
	 *            Perfil de usuario
	 * @return Servicios del perfil de usuario
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Cspserpf> findByPerfilActive(String pfucpfuak)
			throws DAOException;

	/**
	 * Busca registros por perfil
	 * 
	 * @param pfucpfuak
	 *            Registros de perfil
	 * @return Servicios del perfil de usuario
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Cspserpf> findByProfile(String pfucpfuak) throws DAOException;

}
