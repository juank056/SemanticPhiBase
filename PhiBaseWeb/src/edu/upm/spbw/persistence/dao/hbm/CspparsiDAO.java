/**
 * 
 */
package edu.upm.spbw.persistence.dao.hbm;

import edu.upm.spbw.persistence.bo.Cspparsi;
import edu.upm.spbw.persistence.dao.ICspparsiDAO;
import edu.upm.spbw.persistence.dao.hbm.base.GenericHbmDAO;

/**
 * DAO para manejar la tabla de Cspparsi
 * 
 * @author Juan Camilo Mesa
 */
public class CspparsiDAO extends GenericHbmDAO<Cspparsi, String> implements
		ICspparsiDAO {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	public CspparsiDAO() {
		super(Cspparsi.class);
	}
}
