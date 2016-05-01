/**
 * 
 */
package edu.upm.spbw.persistence.dao;

import java.util.List;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Sedrelco;
import edu.upm.spbw.persistence.bo.SedrelcoId;

/**
 * DAO para trabajar con la tabla Sedrelco
 * 
 * @author Juan Camilo Mesa
 * 
 */
public interface ISedrelcoDAO extends GenericDAO<Sedrelco, SedrelcoId> {

	/**
	 * Busca registros por padre
	 * 
	 * @param dconsecnk
	 *            Llave de detalle concepto
	 * @return Lista de relaciones del detalle concepto
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Sedrelco> findByConcept(Integer dconsecnk) throws DAOException;

	/**
	 * Se encarga de eliminar todos los registros de la estructura
	 * 
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public void deleteAllRecords() throws DAOException;

	/**
	 * Busca registros por concepto
	 * 
	 * @param cosccosak
	 *            Codigos de Concepto
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
			throws DAOException;

}
