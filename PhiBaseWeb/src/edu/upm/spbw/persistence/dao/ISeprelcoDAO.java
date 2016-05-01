/**
 * 
 */
package edu.upm.spbw.persistence.dao;

import java.util.List;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Seprelco;
import edu.upm.spbw.persistence.bo.SeprelcoId;

/**
 * DAO para trabajar con la tabla Seprelco
 * 
 * @author Juan Camilo Mesa
 * 
 */
public interface ISeprelcoDAO extends GenericDAO<Seprelco, SeprelcoId> {

	/**
	 * Obtiene registros por concepto semantico
	 * 
	 * @param cosccosak
	 *            Codigo de concepto semántico
	 * @return Lista de relaciones del concepto semantico
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Seprelco> findByConcept(String cosccosak) throws DAOException;

}
