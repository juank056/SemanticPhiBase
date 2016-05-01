/**
 * 
 */
package edu.upm.spbw.persistence.dao.hbm;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Cspserpf;
import edu.upm.spbw.persistence.bo.CspserpfId;
import edu.upm.spbw.persistence.dao.ICspserpfDAO;
import edu.upm.spbw.persistence.dao.hbm.base.GenericHbmDAO;
import edu.upm.spbw.utils.Constants;

/**
 * DAO para manejar la tabla de Cspserpf
 * 
 * @author Juan Camilo Mesa
 */
public class CspserpfDAO extends GenericHbmDAO<Cspserpf, CspserpfId> implements
		ICspserpfDAO {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	public CspserpfDAO() {
		super(Cspserpf.class);
	}

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
			throws DAOException {
		// Crea los criterios
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(Restrictions.eq("id.pfucpfuak", pfucpfuak));
		crit.add(Restrictions.eq("svpiactsf", Constants.ONE));
		return this.findByCriteria(crit);
	}

	/**
	 * Busca registros por perfil
	 * 
	 * @param pfucpfuak
	 *            Registros de perfil
	 * @return Servicios del perfil de usuario
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<Cspserpf> findByProfile(String pfucpfuak) throws DAOException {
		// Crea los criterios
		List<Criterion> crit = new ArrayList<Criterion>();
		crit.add(Restrictions.eq("id.pfucpfuak", pfucpfuak));
		return this.findByCriteria(crit);
	}
}
