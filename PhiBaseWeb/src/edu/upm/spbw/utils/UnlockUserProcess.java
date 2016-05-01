/**
 * 
 */
package edu.upm.spbw.utils;

import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.lang.exception.ExceptionUtils;

import edu.upm.spbw.persistence.bo.Usdbloqu;
import edu.upm.spbw.persistence.bo.Usmusuar;
import edu.upm.spbw.persistence.dao.IUsdbloquDAO;
import edu.upm.spbw.persistence.dao.IUsmusuarDAO;

/**
 * Proceso para desbloquear a un usuario que ha sido bloqueado por intentos
 * fallidos de login
 * 
 * @author JuanCamilo
 * 
 */
public class UnlockUserProcess extends TimerTask {

	/**
	 * Usuario a desbloquear
	 */
	private Usmusuar usuar;

	/**
	 * Registro de bloqueo USDBLOQU
	 */
	private Usdbloqu bloqu;

	/**
	 * DAO para leer USMUSUAR
	 */
	private IUsmusuarDAO usmusuarDao;

	/**
	 * DAO para escribir en USDBLOQU
	 */
	private IUsdbloquDAO usdbloquDao;

	/**
	 * Constructor de tarea para desbloqueo de usuario
	 * 
	 * @param usuar
	 *            Usuario a desbloquear
	 * @param bloqu
	 *            Registro de bloqueo
	 * @param usmusuarDao
	 *            DAO para leer usuario
	 * @param usdbloquDao
	 *            DAO para leer usdbloqu
	 */
	public UnlockUserProcess(Usmusuar usuar, Usdbloqu bloqu,
			IUsmusuarDAO usmusuarDao, IUsdbloquDAO usdbloquDao) {
		this.usuar = usuar;
		this.bloqu = bloqu;
		this.usmusuarDao = usmusuarDao;
		this.usdbloquDao = usdbloquDao;
	}

	/**
	 * Ejecuta tarea de desbloqueo de usuario
	 */
	@Override
	public void run() {
		try {
			// Vuelve a buscar registro de USDBLOQU para saber si sigue
			// bloqueado
			bloqu = usdbloquDao.findById(bloqu.getId());
			// Revisa si ya fue desbloqueado
			if (bloqu.getBlutdesnf() != 0) {/* Ya desbloqueado */
				// Finaliza
				return;
			}
			// Fecha actual
			Date current = DateUtils.getCurrentUtilDate();
			// Tiempo de desbloqueo
			bloqu.setBlutdesnf(current.getTime());
			// Usuario que realiza desbloqueo
			bloqu.setUsuemadaf(Constants.BLANKS);
			// Desbloqueo automatico
			bloqu.setBludautsf(Constants.ONE);
			// Actualiza registro en la base de datos
			usdbloquDao.update(bloqu);
			// Actualiza usuario desbloqueandolo
			// Estado
			usuar.setUsuestavf(Constants.ONE);
			// Tiempo de bloqueo usuario
			usuar.setUsutblonf(0);
			// Intentos fallidos a cero
			usuar.setUsuilognf(0);
			// Actualiza registro en la base de datos
			usmusuarDao.update(usuar);
		} catch (Exception e) {/* Error */
			// Log
			LogLogger.getInstance(getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
		}
	}
}
