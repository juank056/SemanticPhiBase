/**
 * 
 */
package edu.upm.spbw.persistence.dao;

import java.util.List;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Sedconce;

/**
 * DAO para trabajar con la tabla Sedconce
 * 
 * @author Juan Camilo Mesa
 * 
 */
public interface ISedconceDAO extends GenericDAO<Sedconce, Integer> {

	/**
	 * Se encarga de eliminar todos los registros de la estructura
	 * 
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public void deleteAllRecords() throws DAOException;

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
			String dcoiricaf, int maxRecords) throws DAOException;

	/**
	 * Busca objeto por IRI
	 * 
	 * @param dcoiricaf
	 *            IRI del objeto
	 * @return Objeto por IRI
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public Sedconce findByIRI(String dcoiricaf) throws DAOException;

}
