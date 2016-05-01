/**
 * 
 */
package edu.upm.spbw.bbeans.base;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.LogLogger;

/**
 * Clase para manejar la navegacion de la aplicacion
 * 
 * @author Juan Camilo
 * 
 */
public class NavigationHandler implements Serializable {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * instancia
	 */
	private static NavigationHandler instance;

	/**
	 * Obtiene instancia singleton
	 * 
	 * @return Instancia singleton
	 */
	public static NavigationHandler getInstance() {
		if (instance == null)
			instance = new NavigationHandler();
		return instance;
	}

	/**
	 * Constructor
	 */
	public NavigationHandler() {
	}

	/**
	 * Va de la pantalla de inicio a la pantalla principal de la aplicacion
	 * 
	 * @return cadena con el valor de ingreso exitoso
	 */
	public String loginSuccesfull() {
		return "index_main";
	}

	/**
	 * Ingreso no valido a la aplicacion
	 * 
	 * @return cadena con el valor de ingreso no valido
	 */
	public String loginBad() {
		return "index_index";
	}

	/**
	 * Ingreso valido pero con cambio de password
	 * 
	 * @return cadena con el valor para ir al a pagina de nuevo password
	 */
	public String newPassword() {
		return "newPassword";
	}

	/**
	 * Para ir de la actualizacion de contraseña al menú princiapl
	 * 
	 * @return String de navegacion
	 */
	public String newPasswordOK() {
		return "newPasswd_main";
	}

	/**
	 * Para permanecer en la actualizacion de contraseña
	 * 
	 * @return String de navegacion
	 */
	public String newPasswordBad() {
		return "newPasswd_newPasswd";
	}

	/**
	 * Para ir al inicio de la aplicación
	 * 
	 * @return String de navegacion
	 */
	public String newPasswordCancel() {
		return "newPasswd_index";
	}

	/**
	 * Para ir del menu principal a la actualizacion de contraseña
	 * 
	 * @return String de navegacion
	 */
	public String mainNewPassword() {
		return "main_newPasswd";
	}

	/**
	 * Logout de la aplicacion
	 * 
	 * @return cadena de logout
	 */
	public String logout() {
		return "main_index";
	}

	/**
	 * Calcula el String para ir en navegacion hacia atras
	 * 
	 * @return la cadena con la navegacion hacia atras
	 */
	public String goServiceBack() {
		String serviceBack = Constants.BLANKS;
		// Obtenemos el bean de Autenticacion para obtener al usuario
		HttpSession s = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		AutenticationBean ab = (AutenticationBean) s
				.getAttribute(Constants.AUTENTICATION_BEAN);
		if (ab != null) {
			// Revisamos el servicio donde se encuentra actualmente y lo
			// removemos
			String currentService = ab.getExecutedServices().pop()
					.getSsicssiak();
			// Obtenemos el servicio anterior
			String lastService = ab.getExecutedServices().lastElement()
					.getSsicssiak();
			// Armamos cadena de retorno (De donde estoy a donde voy)
			serviceBack = currentService + Constants.UNDER_SCORE + lastService;
		} else {
			LogLogger.getInstance(this.getClass()).logger(
					"Autentication Bean was null", LogLogger.ERROR);
		}
		return serviceBack;
	}

}
