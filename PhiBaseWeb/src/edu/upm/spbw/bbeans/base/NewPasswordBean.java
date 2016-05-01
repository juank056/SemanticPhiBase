/**
 * 
 */
package edu.upm.spbw.bbeans.base;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.exception.ExceptionUtils;

import edu.upm.spbw.persistence.bo.Usmusuar;
import edu.upm.spbw.persistence.dao.IUsmusuarDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.DateUtils;
import edu.upm.spbw.utils.LogLogger;
import edu.upm.spbw.utils.PasswordManager;

/**
 * @author Juan Camilo Bean para realizar el manejo del cambio de passwords
 */
public class NewPasswordBean extends BaseBean<Usmusuar, String> {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	/****************************
	 * ATRIBUTOS DEL BEAN
	 ***************************/

	/**
	 * Password Actual del Usuario
	 */
	private String currentPassword;

	/**
	 * Nuevo password del usuario
	 */
	private String newPassword;

	/**
	 * Numero minimo de caracteres para el password
	 */
	private int minCharPassword;

	/**
	 * Usuario en sesion
	 */
	private Usmusuar sessionUser;

	/**
	 * DAO para leer USMUSUAR
	 */
	private IUsmusuarDAO usmusuarDao;

	/**
	 * 
	 */

	/**
	 * Constructor del Bean
	 */
	public NewPasswordBean() {
		super("newPasswordBean", false);
	}

	/**
	 * Metodo para actualizar el password del usuario
	 * 
	 * @return se va al menu del usuario en caso de actualizacion correcta, en
	 *         caso de fallo se queda en la misma pantalla donde esta
	 */
	public String updatePassword() {
		// Asume que va a poder actualizar bien
		String pageForward = NavigationHandler.getInstance().newPasswordOK();
		// Realizamos las validaciones correspondientes antes de actualizar el
		// password
		try {
			// Realiza validaciones
			if (!this.validateFields(Constants.BLANKS)) {
				// Password mal
				pageForward = NavigationHandler.getInstance().newPasswordBad();
				// Procesa mensajes
				this.processErrorListMessages();
				// Finaliza
				return pageForward;
			}
			// Actualiza contraseña
			String hashPassword = PasswordManager.encryptPassword(newPassword);
			// Asigna contraseña
			sessionUser.setUsupassaf(hashPassword);
			// Password cadudado en no
			sessionUser.setUsupascsf(Constants.ZERO);
			// Fecha de ultimo cambio de contraseña
			sessionUser.setUsufulcff(DateUtils.getCurrentUtilDate());
			// Intentos fallidos a cero
			sessionUser.setUsuilognf(0);
			// Actualiza registro
			usmusuarDao.update(sessionUser);
			// Contrasena actualizada satisfactoriamente
			this.processMessage(ApplicationMessages.getMessage("USR0012"));
		} catch (Exception e) {/* Ocurrio error */
			pageForward = NavigationHandler.getInstance().newPasswordBad();
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			this.processErrorMessage(e.getMessage());
		}
		return pageForward;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.esf.backingbeans.util.BaseBean#receiveParameters(javax.faces.event
	 * .ActionEvent)
	 */
	@Override
	public void receiveParameters(ActionEvent e) {/* No recibe Parametros */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.esf.backingbeans.util.BaseBean#loadSubfile()
	 */
	@Override
	protected void loadSubfile() {/* No tiene subfile */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.esf.backingbeans.util.BaseBean#initObjects()
	 */
	@Override
	protected void initObjects() {
		currentPassword = Constants.BLANKS;
		newPassword = Constants.BLANKS;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.esf.backingbeans.util.BaseBean#initServices()
	 */
	@Override
	protected void initServices() {
		// Obtiene el Autentication bean
		AutenticationBean autBean = ((AutenticationBean) this
				.getBackingBean(Constants.AUTENTICATION_BEAN));
		// Asigna minimo de caracteres
		this.minCharPassword = autBean.getMinCharsPassword();
		// Usuario en sesion
		this.sessionUser = autBean.getSessionUser();
		// DAO USMUSUAR
		this.usmusuarDao = autBean.getUsmusuarDao();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.esf.backingbeans.util.BaseBean#searchRecord(javax.faces.event.ActionEvent
	 * )
	 */
	@Override
	public void searchRecord(ActionEvent e) {/* No hay busqueda */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.esf.backingbeans.util.BaseBean#validateFields(java.lang.String)
	 */
	@Override
	protected boolean validateFields(String action) {
		isError = false;
		// 1. Valida que el password actual sea el correcto
		// Encripta el password recibido para compararlo con el del usuario
		String currCryptPass = PasswordManager.encryptPassword(currentPassword);
		if (!sessionUser.getUsupassaf().equals(currCryptPass)) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0013"));
		}
		// 2. Valida la longitud del nuevo password
		if (newPassword.length() < minCharPassword) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0014", Constants.BLANKS
							+ minCharPassword));
		}
		// 3. Valida que el nuevo password sea diferente al actual
		if (currentPassword.equals(newPassword)) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0015"));
		}
		// 4. Valida que el nuevo password no sea igual al nombre de usuario o
		// lo contenga
		if (sessionUser.getUsuemaiak().toLowerCase()
				.contains(newPassword.toLowerCase())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0016"));
		}
		// Finaliza
		return !isError;
	}

	/****************************
	 * GETTERS Y SETTERS
	 ***************************/

	/**
	 * @return the currentPassword
	 */
	public String getCurrentPassword() {
		return currentPassword;
	}

	/**
	 * @param currentPassword
	 *            the currentPassword to set
	 */
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	/**
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @param newPassword
	 *            the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/**
	 * @return the minCharPassword
	 */
	public int getMinCharPassword() {
		return minCharPassword;
	}

	/**
	 * @param minCharPassword
	 *            the minCharPassword to set
	 */
	public void setMinCharPassword(int minCharPassword) {
		this.minCharPassword = minCharPassword;
	}

	/**
	 * @return the sessionUser
	 */
	public Usmusuar getSessionUser() {
		return sessionUser;
	}

	/**
	 * @param sessionUser
	 *            the sessionUser to set
	 */
	public void setSessionUser(Usmusuar sessionUser) {
		this.sessionUser = sessionUser;
	}

	/**
	 * @return the usmusuarDao
	 */
	public IUsmusuarDAO getUsmusuarDao() {
		return usmusuarDao;
	}

	/**
	 * @param usmusuarDao
	 *            the usmusuarDao to set
	 */
	public void setUsmusuarDao(IUsmusuarDAO usmusuarDao) {
		this.usmusuarDao = usmusuarDao;
	}

}
