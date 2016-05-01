/**
 * 
 */
package edu.upm.spbw.bbeans.crud;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.validator.EmailValidator;

import com.icesoft.faces.component.ext.HtmlDataTable;

import edu.upm.spbw.bbeans.base.AutenticationBean;
import edu.upm.spbw.bbeans.base.BaseBean;
import edu.upm.spbw.persistence.bo.Cspperfu;
import edu.upm.spbw.persistence.bo.Usdbloqu;
import edu.upm.spbw.persistence.bo.UsdbloquId;
import edu.upm.spbw.persistence.bo.Usmusuar;
import edu.upm.spbw.persistence.dao.ICspperfuDAO;
import edu.upm.spbw.persistence.dao.IUsdbloquDAO;
import edu.upm.spbw.persistence.dao.IUsmusuarDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.ConfigApplicationManager;
import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.DateUtils;
import edu.upm.spbw.utils.LogLogger;
import edu.upm.spbw.utils.MailException;
import edu.upm.spbw.utils.MailManager;
import edu.upm.spbw.utils.PasswordManager;
import edu.upm.spbw.utils.exception.MissingConfigurationParameterException;

/**
 * Bean para el servicio SE005 <br/>
 * SE005: Mantenimiento de los Usuarios del sistema
 * 
 * @author Juan Camilo
 */
public class SE005Bean extends BaseBean<Usmusuar, String> {

	/**
	 * ID Serialización
	 */
	private static final long serialVersionUID = 1L;

	/***************************************
	 * ATRIBUTOS DEL BEAN
	 **************************************/

	/**
	 * DAO para USDBLOQU
	 */
	private IUsdbloquDAO usdbloquDao;

	/**
	 * DAO para CSPPERFU
	 */
	private ICspperfuDAO cspperfuDao;

	/**
	 * Lista de perfiles de usuario
	 */
	protected List<SelectItem> lsPerfu;

	/**
	 * Constructor del Bean
	 */
	public SE005Bean() {
		super("SE005", false);
	}

	/**
	 * Inicia servicios
	 */
	@Override
	protected void initServices() {
		// DAO
		objectDao = appContext.getBean(IUsmusuarDAO.class);
		usdbloquDao = appContext.getBean(IUsdbloquDAO.class);
		cspperfuDao = appContext.getBean(ICspperfuDAO.class);
		// Lista de perfiles de usuario
		lsPerfu = new ArrayList<>();
		// Lista de Dropdown de busqueda
		listDropdown = new ArrayList<SelectItem>();
		// Obtiene los perfiles de usuario
		try {
			List<Cspperfu> perfus = cspperfuDao.findActive();
			// El seleccione
			lsPerfu.add(new SelectItem(Constants.BLANKS, ApplicationMessages
					.getMessage(Constants.SELECCIONE)));
			// Recorre registros
			for (Cspperfu perfu : perfus) {
				// Adiciona registro
				lsPerfu.add(new SelectItem(perfu.getPfucpfuak(), perfu
						.getPfudpfuaf()));
			}
		} catch (Exception e) {/* Ocurrio error cargando lista */
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			this.processErrorMessage(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.esf.backingbeans.util.BaseBean#receiveParameters(javax.faces.event
	 * .ActionEvent)
	 */
	@Override
	public void receiveParameters(ActionEvent e) {/* No recibe parametros */
	}

	/**
	 * Carga subfile
	 */
	@Override
	protected void loadSubfile() {
		try {
			// Carga todos los objetos
			subfileList = objectDao.findAll();
			// Revisa si no hay nada
			if (subfileList.isEmpty()) {
				// No se encontraron registros
				this.processMessage(ApplicationMessages.getMessage("USR0009"));
			}
			// Limpia lista de dropdown
			listDropdown.clear();
			// Adiciona el Seleccione
			listDropdown.add(new SelectItem(Constants.BLANKS,
					ApplicationMessages.getMessage(Constants.SELECCIONE)));
			// Adiciona objetos
			for (Usmusuar usuar : subfileList) {
				// Adiciona objeto
				listDropdown.add(new SelectItem(usuar.getUsuemaiak(), usuar
						.getUsunusuaf()
						+ Constants.BLANK_SPACE
						+ Constants.LEFT_PARENTHESIS
						+ usuar.getUsuemaiak()
						+ Constants.RIGHT_PARENTHESIS));
			}
		} catch (Exception e) {/* Error cargando registros */
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			this.processErrorMessage(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.esf.backingbeans.util.BaseBean#initObjects()
	 */
	@Override
	protected void initObjects() {
		// Campo de busqueda en Seleccione
		this.searchField = Constants.BLANKS;
		// Inicia el Usuario del Sistema de Trabajo
		object = new Usmusuar();
		// Prepara objeto
		object.prepareObject();
	}

	/**
	 * Realiza la validacion de los campos
	 */
	@Override
	protected boolean validateFields(String action) {
		// Booleano para indicar que hubo error
		isError = false;
		// Email Usuario
		if (this.isBlanks(object.getUsuemaiak())
				|| !this.isValidEmail(object.getUsuemaiak())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0008",
							ApplicationMessages.getMessage("se005_emailUser")));
		}
		// Nombre Usuario
		if (this.isBlanks(object.getUsunusuaf())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0008",
							ApplicationMessages.getMessage("se005_nameUser")));
		}
		// Perfil de usuario
		if (this.isBlanks(object.getPfucpfuak())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages
							.getMessage("USR0008", ApplicationMessages
									.getMessage("se005_profileUser")));
		}
		// Revisa si no hay error y se esta adicionando usuario
		if (!isError && Constants.ADD.equals(action)) {
			// Genera contraseña inicial
			String newPassword = generateNewPassword(object.getUsunusuaf());
			System.out.println("NUEVA CONTRASEÑA USUARIO: " + newPassword);
			// Actualiza contraseña en el usuario
			object.setUsupassaf(PasswordManager.encryptPassword(newPassword));
			// Password Caducado
			object.setUsupascsf(Constants.ONE);
			object.setBusupascsf(true);
			// Estado activo
			object.setUsuestavf(Constants.ONE);
			// Intentos Login
			object.setUsuilognf(0);
			// Tiempo de bloqueo
			object.setUsutblonf(0);
			// Fecha ultimo cambio contraseña
			object.setUsufulcff(DateUtils.getCurrentUtilDate());
			// Envia correo electronico al usuario con su contraseña
			try {
				// Intenta envío
				this.sendEmailNewPassword(newPassword);
			} catch (MailException | MissingConfigurationParameterException e) {
				this.processErrorMessage(ApplicationMessages
						.getMessage("USR0010"));
				this.processErrorMessage(e.getMessage());
				LogLogger.getInstance(this.getClass()).logger(
						ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			}
		}
		// Retorna
		return !isError;
	}

	/**
	 * Busca registro
	 */
	@Override
	public void searchRecord(ActionEvent ev) {
		// Revisa el objeto a buscar
		if (!Constants.BLANKS.equals(this.searchField)) {
			// Limpiamos la lista
			subfileList.clear();
			// Adicionamos unicamente el registro buscado
			try {
				// Busca registro en la base de datos
				object = objectDao.findById(this.searchField);
				// Si lo encuentra, adiciona en la lista
				if (object != null) {/* Encontrado */
					// Adiciona en la lista
					subfileList.add(object);
				} else {/* Objeto no encontrado */
					this.processMessage(ApplicationMessages
							.getMessage("USR0024"));
				}
			} catch (Exception e) {/* Error */
				// Muestra mensaje
				this.processMessage(e.getMessage());
				// Log
				LogLogger.getInstance(this.getClass()).logger(
						ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			}
		} else {/* No hay busqueda */
			// Carga el subfile normal
			this.loadSubfile();
		}
		// Campo de busqueda en Seleccione
		this.searchField = Constants.BLANKS;
		// Obtiene lista de subfile
		HtmlDataTable subfile = (HtmlDataTable) this
				.getComponentById(Constants.SUBFILE);
		// Pone en primera página
		subfile.setFirst(0);
	}

	/**
	 * Método para regenerar la contraseña del usuario
	 * 
	 * @param ev
	 *            Action event que activa el evento de regeneracion de password
	 */
	public void regeneratePassword(ActionEvent ev) {
		// Obtiene el usuario para regeneracion de contraseña
		if (!this.getObject(ev)) {
			// Procesa lista de errores
			this.processErrorMessage(ApplicationMessages.getMessage("USR0024"));
			// No se puede regenerar la contraseña
			return;
		}
		// Inicia proceso de generacion de contraseña
		try {
			String newPassword = generateNewPassword(object.getUsunusuaf());
			// Actualiza contraseña en el usuario
			object.setUsupassaf(PasswordManager.encryptPassword(newPassword));
			// Password Caducado
			object.setUsupascsf(Constants.ONE);
			// Actualiza objeto
			objectDao.update(object);
			// Envia correo electronico con nueva contraseña
			this.sendEmailNewPassword(newPassword);
			// Contraseña actualizada satisfactoriamente
			this.processMessage(ApplicationMessages.getMessage("USR0012"));
		} catch (Exception e) {/* Error */
			// Muestra mensaje
			this.processMessage(e.getMessage());
			// Log
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
		}
	}

	/**
	 * Se encarga de desbloquear al usuario por intentos fallidos de ingreso
	 * 
	 * @param ev
	 *            Action event
	 * @return Indicador de accion realizada
	 */
	public void unlockUser(ActionEvent ev) {
		// Obtiene el usuario para desbloquearlo
		if (!this.getObject(ev)) {
			// Procesa lista de errores
			this.processErrorMessage(ApplicationMessages.getMessage("USR0024"));
			// No se puede desbloquear al usuario
			return;
		}
		try {
			// Revisa que el usuario tenga el estado '3' bloqueado
			if (!Constants.THREE.equals(object.getUsuestavf())) {
				// Usuario no se encuentra bloqueado
				this.processErrorMessage(ApplicationMessages
						.getMessage("USR0028"));
				// No se puede desbloquear al usuario
				return;
			}
			// Obtiene el registro de USDBLOQU del bloqueo del usuario
			Usdbloqu bloqu = usdbloquDao.findById(new UsdbloquId(object
					.getUsuemaiak(), object.getUsutblonf()));
			// Revisa que lo encuentra
			if (bloqu == null) {
				// Usuario no se encuentra bloqueado
				this.processErrorMessage(ApplicationMessages
						.getMessage("USR0028"));
				// No se puede desbloquear al usuario
				return;
			}
			// Obtiene bean de autenticacion
			AutenticationBean authBean = (AutenticationBean) this
					.getBackingBean(Constants.AUTENTICATION_BEAN);
			// Asigna datos del desbloqueo
			// Fecha actual
			Date current = DateUtils.getCurrentUtilDate();
			// Tiempo de desbloqueo
			bloqu.setBlutdesnf(current.getTime());
			// Usuario que realiza desbloqueo
			bloqu.setUsuemadaf(authBean.getSessionUser().getUsuemaiak());
			// Desbloqueo automatico
			bloqu.setBludautsf(Constants.ZERO);
			// Actualiza registro en la base de datos
			usdbloquDao.update(bloqu);
			// Actualiza usuario desbloqueandolo
			// Estado
			object.setUsuestavf(Constants.ONE);
			// Tiempo de bloqueo usuario
			object.setUsutblonf(0);
			// Intentos fallidos a cero
			object.setUsuilognf(0);
			// Actualiza registro en la base de datos
			objectDao.update(object);
			// Usuario Desbloqueado
			this.processMessage(ApplicationMessages.getMessage("USR0017"));
		} catch (Exception e) {/* Error */
			// Muestra mensaje
			this.processMessage(e.getMessage());
			// Log
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
		}
	}

	/**
	 * Genera una contraseña nueva para usuario
	 * 
	 * @param usunusuaf
	 *            Nombre del usuario
	 * @return Contraseña nueva generada
	 */
	private String generateNewPassword(String usunusuaf) {
		// Separa nombre del usuario
		String[] s_name = object.getUsunusuaf().split(Constants.BLANK_SPACE);
		// Cadenas de nombres
		String name = Constants.BLANKS, lastName = Constants.BLANKS;
		// Revisa longitud
		if (s_name.length > 3) {/* Tiene nombre completo */
			name = s_name[0] + s_name[1];
			lastName = s_name[2] + s_name[3];
		} else if (s_name.length == 3) {/* Tres Posiciones */
			name = s_name[0];
			lastName = s_name[1] + s_name[2];
		} else if (s_name.length == 2) {/* Dos Posiciones */
			name = s_name[0];
			lastName = s_name[1];
		} else {/* Una sola posicion */
			name = s_name[0];
			lastName = s_name[0];
		}
		// Genera contraseña
		return PasswordManager.generatePassword(name, lastName);
	}

	/**
	 * Se encarga de enviar mensaje de correo electronico con la nueva
	 * contraseña para el usuario del sistema
	 * 
	 * @param newPassword
	 *            Nueva contraseña del usuario
	 * @throws MailException
	 *             Error enviando correo electronico
	 * @throws MissingConfigurationParameterException
	 *             Falta parametro de configuracion
	 */
	private void sendEmailNewPassword(String newPassword) throws MailException,
			MissingConfigurationParameterException {
		String header = "<html><head></head><body>";
		String footer = "<br/><br/>Universidad Politécnica de Madrid.<br/><br/><b>"
				+ "Semantic PHI-BASE Web Interface.</b><br/><img src='cid:logo'/></body></html>";
		// Buenos días
		String goodMorning = ApplicationMessages.getMessage("USR0025");
		// Su nombre de usuario y contraseña para el sistema son
		String info = ApplicationMessages.getMessage("USR0026");
		// Nombre de usuario
		String userName = ApplicationMessages.getMessage("index_username");
		// Link de ingreso
		String link = ApplicationMessages.getMessage("USR0032")
				+ ConfigApplicationManager
						.getParameter("edu.upm.spbw.login.link");
		// Contraseña
		String passwd = ApplicationMessages.getMessage("index_password");
		String content = goodMorning + "<br/><br/>" + info + "<br/>" + userName
				+ ": <b>" + object.getUsuemaiak() + "</b>" + "<br/>" + passwd
				+ ": <b>" + newPassword + "</b>" + "<br/><br/>" + link
				+ "<br/>";
		content = header + content + footer;
		// Envía correo electrónico
		MailManager.getInstance().sendMessage(content, object.getUsuemaiak(),
				ApplicationMessages.getMessage("USR0027"));
	}

	/**
	 * Valida Que la cadena recibida sea un email valido
	 * 
	 * @param usuemaiak
	 *            Email a validar
	 * @return True si es un email valido, false de lo contrario
	 */
	private boolean isValidEmail(String usuemaiak) {
		// Obtiene validador de Email
		EmailValidator validator = EmailValidator.getInstance();
		// Valida
		return validator.isValid(usuemaiak);
	}

	/**
	 * @return the lsPerfu
	 */
	public List<SelectItem> getLsPerfu() {
		return lsPerfu;
	}

	/**
	 * @param lsPerfu
	 *            the lsPerfu to set
	 */
	public void setLsPerfu(List<SelectItem> lsPerfu) {
		this.lsPerfu = lsPerfu;
	}
}
