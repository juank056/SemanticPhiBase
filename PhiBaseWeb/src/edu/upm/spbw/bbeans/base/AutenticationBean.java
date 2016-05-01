/**
 * 
 */
package edu.upm.spbw.bbeans.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.Timer;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;

import edu.upm.spbw.persistence.bo.Cspparsi;
import edu.upm.spbw.persistence.bo.Cspserpf;
import edu.upm.spbw.persistence.bo.CspserpfId;
import edu.upm.spbw.persistence.bo.Cspsersi;
import edu.upm.spbw.persistence.bo.Usdbloqu;
import edu.upm.spbw.persistence.bo.UsdbloquId;
import edu.upm.spbw.persistence.bo.Usdlogin;
import edu.upm.spbw.persistence.bo.UsdloginId;
import edu.upm.spbw.persistence.bo.Usmusuar;
import edu.upm.spbw.persistence.dao.ICspparsiDAO;
import edu.upm.spbw.persistence.dao.ICspserpfDAO;
import edu.upm.spbw.persistence.dao.ICspsersiDAO;
import edu.upm.spbw.persistence.dao.IUsdbloquDAO;
import edu.upm.spbw.persistence.dao.IUsdloginDAO;
import edu.upm.spbw.persistence.dao.IUsmusuarDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.DateUtils;
import edu.upm.spbw.utils.LogLogger;
import edu.upm.spbw.utils.PasswordManager;
import edu.upm.spbw.utils.UnlockUserProcess;

/**
 * Bean que se encarga de manejar la autenticación en el sistema
 * 
 * @author JuanCamilo
 * 
 */
public class AutenticationBean extends BaseBean<Usmusuar, String> {

	/**
	 * SerialUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Nombre de Usuario
	 */
	private String userName;

	/**
	 * Contrasena del Usuario
	 */
	private String userPassword;

	/**
	 * Nombre del usuario (descripcion)
	 */
	private String userDescription;

	/**
	 * Fecha de Login
	 */
	private Date loginDate;

	/**
	 * Pila de servicios ejecutados
	 */
	private Stack<Cspsersi> executedServices;

	/**
	 * Maximo intentos login
	 */
	private int maxFailedTries;

	/**
	 * Minimo caracteres passwords
	 */
	private int minCharsPassword;

	/**
	 * Maximo registros a mostrar en el subfile
	 */
	private int maxRegSubfile;

	/**
	 * Maximo de paginas del subfile
	 */
	private int maxPagesSubfile;

	/**
	 * Nombre del Subfile principal
	 */
	private String nameSubfile;

	/**
	 * Dias para cambio de contraseña
	 */
	private int daysToChangePass;

	/**
	 * Tiempo de bloqueo de usuario
	 */
	private int timeToUnlockUser;

	/**
	 * Maximo de registros de consulta
	 */
	private int maxRecordsQuery;

	/**
	 * Maximo de registros autocompletar
	 */
	private int maxRecordsAutocomplete;

	/**
	 * Indicador de que hay usuario autenticado
	 */
	private boolean userAutenticated;

	/******************************************
	 * DAOS PARA LEER ESTRUCTURAS
	 *****************************************/

	/**
	 * DAO para leer la estructura de CSPPARSI
	 */
	private ICspparsiDAO cspparsiDao;

	/**
	 * DAO para leer la estructura de CSPSERSI
	 */
	private ICspsersiDAO cspsersiDao;

	/**
	 * DAO para leer la estructura de USMUSUAR
	 */
	private IUsmusuarDAO usmusuarDao;

	/**
	 * DAO para leer la estructura de UDSLOGIN
	 */
	private IUsdloginDAO usdloginDao;

	/**
	 * DAO para leer la estructura de USDBLOQU
	 */
	private IUsdbloquDAO usdbloquDao;

	/**
	 * DAO para leer la estructura de CSPSERPF
	 */
	private ICspserpfDAO cspserpfDao;

	/**
	 * Usuario en sesion
	 */
	private Usmusuar sessionUser;

	/**
	 * Lista de servicios a los que el usuario tiene permiso
	 */
	private List<Cspsersi> authorizedServices;

	/**
	 * URL del servicio de visualizacion
	 */
	private String visualizationService;

	/**
	 * Constructor del bean de autenticacion
	 */
	public AutenticationBean() {
		// LLama constructor padre
		super(Constants.AUTENTICATION_BEAN, false);
		// Usuario autenticado false
		this.setUserAutenticated(false);
	}

	/***************************************
	 * METODOS PROPIOS DEL BEAN
	 *************************************/

	/**
	 * Metodo para autenticar a un usuario
	 * 
	 * @return cadena indicando la navegacion del usuario
	 */
	public String autenticateUser() {
		// Inicia pagina a continuar como login fallido
		String pageForward = NavigationHandler.getInstance().loginBad();
		try {
			// Realiza validacion de campos
			if (!validateFields(Constants.BLANKS)) {
				// Procesa mensajes
				this.processErrorListMessages();
				// Finaliza
				return pageForward;
			}
			// Realiza Hash a la contraseña
			String hashPasswd = PasswordManager.encryptPassword(userPassword);
			// Busca usuario en la base de datos
			sessionUser = usmusuarDao.findById(userName);
			// Revisa si lo encuentra
			if (sessionUser == null) {/* Usuario no registrado */
				// Nombre de usuario o contraseña invalido
				this.processErrorMessage(ApplicationMessages
						.getMessage("USR0004"));
				// Finaliza
				return pageForward;
			}
			// Limpia usuario
			sessionUser.prepareObject();
			// Nombre de usuario
			this.userDescription = sessionUser.getUsunusuaf();
			// Fecha de ingreso
			this.loginDate = DateUtils.getCurrentUtilDate();
			// Revisa el estado del usuario
			if (!Constants.ONE.equals(sessionUser.getUsuestavf())) {
				// Usuario no activo
				this.processErrorMessage(ApplicationMessages
						.getMessage("USR0001"));
				// Finaliza
				return pageForward;
			}
			// Revisa si coinciden las contraseñas
			if (!hashPasswd.equals(sessionUser.getUsupassaf())) {
				// Nombre de usuario o contraseña invalido
				this.processErrorMessage(ApplicationMessages
						.getMessage("USR0004"));
				// Ingreso fallido
				this.insertLogLogin(Constants.ZERO);
				// Incrementa ingresos fallidos del usuario
				sessionUser.setUsuilognf(sessionUser.getUsuilognf() + 1);
				// Revisa si ya excede el valor del parametro
				if (sessionUser.getUsuilognf() >= maxFailedTries) {
					// Estado '3' Bloqueado
					sessionUser.setUsuestavf(Constants.THREE);
					// Timestamp de bloqueo
					sessionUser.setUsutblonf(this.loginDate.getTime());
					// Usuario bloqueado
					this.processErrorMessage(ApplicationMessages
							.getMessage("USR0003", Constants.BLANKS
									+ this.timeToUnlockUser));
					// Inicia proceso para desbloqueo de usuario
					this.initUnlockProcess();
				}
				// Actualiza usuario
				usmusuarDao.update(sessionUser);
				// Finaliza
				return pageForward;
			}
			/* Contraseña correcta en este punto */
			// Usuario esta autenticado
			this.setUserAutenticated(true);
			// Ingreso exitoso de login
			this.insertLogLogin(Constants.ONE);
			// Actualiza intentos no correctos
			sessionUser.setUsuilognf(0);
			// Actualiza usuario
			usmusuarDao.update(sessionUser);
			// Carga servicios permitidos del usuario
			this.loadAuthorizedServices();
			// Revisa si su contraseña esta caducada
			if (Constants.ONE.equals(sessionUser.getUsupascsf())) {
				// Contraseña caducada (se debe actualizar)
				this.processErrorMessage(ApplicationMessages
						.getMessage("USR0021"));
				// Pagina
				pageForward = NavigationHandler.getInstance().newPassword();
				// Finaliza
				return pageForward;
			}
			/*
			 * Ejecuta validaciones del usuario ingresado
			 */
			// Obtiene fecha de cambio de contraseña
			Date dateToChange = DateUtils.moveUtilDate(loginDate,
					daysToChangePass);
			// Revisa si la fecha es anterior a la actual
			if (dateToChange.before(loginDate)) {/* Se debe cambiar contraseña */
				// Contraseña caducada (se debe actualizar)
				this.processErrorMessage(ApplicationMessages
						.getMessage("USR0021"));
				// Pagina
				pageForward = NavigationHandler.getInstance().newPassword();
				// Finaliza
				return pageForward;
			}
			// Login ok
			pageForward = NavigationHandler.getInstance().loginSuccesfull();
		} catch (Exception e) {/* Ocurrio Error */
			// Log
			LogLogger.getInstance(getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			// Login Bad
			pageForward = NavigationHandler.getInstance().loginBad();
			// Adiciona mensaje de lo ocurrido
			this.processErrorMessage(e.getMessage());
		}
		// Finaliza
		return pageForward;
	}

	/**
	 * Se encarga de validar si un usuario esta autorizado a un servicio
	 * 
	 * @param serviceCode
	 *            Codigo de servicio a validar
	 * @return True si el usuario esta autorizado al servicio, false de lo
	 *         contrario
	 */
	public boolean isUserAuthorized(String serviceCode) {
		try {
			// Revisa si hay usuario del sistema
			if (sessionUser == null) {/* No hay usuario */
				// No autenticado
				return false;
			}
			// Busca servicio del sistema
			Cspsersi sersi = cspsersiDao.findById(serviceCode);
			if (sersi == null) {/* No encontrado servicio */
				return false;
			}
			// Revisa si el servicio esta activo
			if (!Constants.ONE.equals(sersi.getSsiiactsf())) {
				// Servicio inactivo
				return false;
			}
			// Obtiene registro de CSPSERPF
			Cspserpf serpf = cspserpfDao.findById(new CspserpfId(sessionUser
					.getPfucpfuak(), sersi.getSsicssiak()));
			if (serpf == null) {/* Usuario no autorizado */
				return false;
			}
			// Revisa si esta activo el servicio dentro del perfil
			if (!Constants.ONE.equals(serpf.getSvpiactsf())) {
				// Servicio inactivo
				return false;
			}
			// Revisa si la pila de llamadas contiene el servicio
			if (!executedServices.contains(sersi)) {
				// Incluye registro en la pila
				executedServices.push(sersi);
			}
		} catch (Exception e) {/* Error */
			// Log
			LogLogger.getInstance(getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			// No autorizado
			return false;
		}
		// Usuario autorizado
		return true;
	}

	/**
	 * Realiza el logout del usuario
	 */
	public void logout(ActionEvent e) {
		// Reinicia todos los valores
		userName = Constants.BLANKS;
		userPassword = Constants.BLANKS;
		userDescription = Constants.BLANKS;
		userAutenticated = false;
		// Limpia servicios
		authorizedServices.clear();
		sessionUser = null;
		executedServices.clear();
	}

	/**
	 * Acciones de ir al menu principal
	 */
	public void gotoMainMenu(ActionEvent e) {
		// Limpia pila de llamadas
		executedServices.clear();
	}

	/****************************************
	 * METODOS PRIVADOS DEL BEAN
	 ***************************************/

	/**
	 * Inserta registro de USDLOGIN
	 * 
	 * @param liuiexisf
	 *            Indicador de ingreso exitoso o fallido
	 */
	private void insertLogLogin(String liuiexisf) {
		try {
			// Request para obtener direccion ip
			HttpServletRequest req = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			// Registro de login
			Usdlogin login = new Usdlogin(new UsdloginId(
					sessionUser.getUsuemaiak(), DateUtils.getCurrentUtilDate(),
					0), DateUtils.getCurrentSQLTime(), req.getRemoteAddr(),
					liuiexisf);
			// Inserta registro
			usdloginDao.save(login);
		} catch (Exception e) {/* Error */
			// Log
			LogLogger.getInstance(getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
		}
	}

	/**
	 * Inicia proceso para desbloqueo del usuario
	 */
	private void initUnlockProcess() {
		try {
			// Creaa registro de USDBLOQU
			Usdbloqu bloqu = new Usdbloqu(new UsdbloquId(
					sessionUser.getUsuemaiak(), sessionUser.getUsutblonf()),
					sessionUser.getUsuilognf(), 0, Constants.BLANKS,
					Constants.ZERO);
			// Inserta registro de bloqueo
			usdbloquDao.save(bloqu);
			// Crea Timer y tarea a ejecutar
			Timer timer = new Timer(Constants.TIMER
					+ sessionUser.getUsuemaiak());
			// Programa tarea para ejecutar
			timer.schedule(new UnlockUserProcess(sessionUser, bloqu,
					usmusuarDao, usdbloquDao), 60000 * this.timeToUnlockUser);
		} catch (Exception e) {/* Error */
			// Log
			LogLogger.getInstance(getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
		}
	}

	/**
	 * Carga lista de servicios autorizados para el usuario
	 */
	private void loadAuthorizedServices() {
		try {
			// Limpia lista
			authorizedServices.clear();
			// Obtiene servicios del perfil de usuario
			List<Cspserpf> serpfs = cspserpfDao.findByPerfilActive(sessionUser
					.getPfucpfuak());
			// Recorre lista de servicios
			for (Cspserpf serpf : serpfs) {
				// Obtiene registro CSPSERSI
				Cspsersi sersi = cspsersiDao.findById(serpf.getId()
						.getSsicssiak());
				// Revisa si esta activo para adicionar en la lista
				if (Constants.ONE.equals(sersi.getSsiiactsf())) {
					// Adiciona registro en lista
					authorizedServices.add(sersi);
				}
			}
		} catch (Exception e) {/* Error */
			// Log
			LogLogger.getInstance(getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
		}

	}

	/***************************************
	 * METODOS HEREDADOS DEL BEAN
	 *************************************/

	/**
	 * Inicia servicios (DAOs)
	 */
	@Override
	protected void initServices() {
		// Obtiene daos
		cspparsiDao = appContext.getBean(ICspparsiDAO.class);
		cspsersiDao = appContext.getBean(ICspsersiDAO.class);
		usmusuarDao = appContext.getBean(IUsmusuarDAO.class);
		usdloginDao = appContext.getBean(IUsdloginDAO.class);
		usdbloquDao = appContext.getBean(IUsdbloquDAO.class);
		cspserpfDao = appContext.getBean(ICspserpfDAO.class);
	}

	@Override
	protected void loadSubfile() {
		/* No hay subfile para este bean */
	}

	@Override
	protected void initObjects() {
		// Nombre de usuario
		userName = Constants.BLANKS;
		// Contraseña
		userPassword = Constants.BLANKS;
		// Pila de servicios ejecutados
		executedServices = new Stack<>();
		// Servicios permitidos
		authorizedServices = new ArrayList<>();
		// Nombre del subfile
		nameSubfile = Constants.NAME_SUBFILE;
		// Obtiene parametros del sistema necesarios
		Cspparsi parsi = null;
		// Asigna valores por defecto inicialmente
		this.maxFailedTries = 3;
		this.minCharsPassword = 8;
		this.maxRegSubfile = 8;
		this.maxPagesSubfile = 3;
		this.daysToChangePass = 60;
		this.timeToUnlockUser = 5;
		this.setMaxRecordsQuery(100);
		try {
			// Maximo intentos inicio de sesion
			parsi = cspparsiDao.findById(Constants.SE01);
			if (parsi != null) {
				this.maxFailedTries = Integer.valueOf(parsi.getPsivalpaf());
			}
			// Numero de registros a mostrar en subfile
			parsi = cspparsiDao.findById(Constants.SE02);
			if (parsi != null) {
				this.maxRegSubfile = Integer.valueOf(parsi.getPsivalpaf());
			}
			// Numero de paginas del subfile
			parsi = cspparsiDao.findById(Constants.SE03);
			if (parsi != null) {
				this.maxPagesSubfile = Integer.valueOf(parsi.getPsivalpaf());
			}
			// Minimo de caracteres de password
			parsi = cspparsiDao.findById(Constants.SE04);
			if (parsi != null) {
				this.maxPagesSubfile = Integer.valueOf(parsi.getPsivalpaf());
			}
			// Dias para cambio de password
			parsi = cspparsiDao.findById(Constants.SE05);
			if (parsi != null) {
				this.daysToChangePass = Integer.valueOf(parsi.getPsivalpaf());
			}
			// Minutos para desbloquear usuario
			parsi = cspparsiDao.findById(Constants.SE06);
			if (parsi != null) {
				this.timeToUnlockUser = Integer.valueOf(parsi.getPsivalpaf());
			}
			// Maximo de registros de query
			parsi = cspparsiDao.findById(Constants.SE07);
			if (parsi != null) {
				this.setMaxRecordsQuery(Integer.valueOf(parsi.getPsivalpaf()));
			}
			// Maximo de registros de autocompletar
			parsi = cspparsiDao.findById(Constants.SE09);
			if (parsi != null) {
				this.setMaxRecordsAutocomplete(Integer.valueOf(parsi
						.getPsivalpaf()));
			}
			// URL Servicio de visualizacio
			parsi = cspparsiDao.findById(Constants.SE10);
			if (parsi != null) {
				this.visualizationService = parsi.getPsivalpaf().trim();
			}
		} catch (Exception e) {/* Ocurrio error obteniendo parametros */
			LogLogger.getInstance(getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
		}
	}

	@Override
	public void searchRecord(ActionEvent e) {
		/* No hay busqueda de registro */
	}

	@Override
	protected boolean validateFields(String action) {
		// Booleano para indicar que hubo error
		setError(false);
		// Valida nombre de usuario
		if (this.isBlanks(this.userName)) {
			setError(true);
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0008",
							ApplicationMessages.getMessage("index_username")));
		}
		// Valida Contraseña
		if (this.isBlanks(this.userPassword)) {
			setError(true);
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0008",
							ApplicationMessages.getMessage("index_password")));
		}
		// Finaliza
		return !isError();
	}

	@Override
	public void receiveParameters(ActionEvent e) {
		/* No recibe parametros */
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the userPassword
	 */
	public String getUserPassword() {
		return userPassword;
	}

	/**
	 * @param userPassword
	 *            the userPassword to set
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	/**
	 * @return the maxRegSubfile
	 */
	public int getMaxRegSubfile() {
		return maxRegSubfile;
	}

	/**
	 * @param maxRegSubfile
	 *            the maxRegSubfile to set
	 */
	public void setMaxRegSubfile(int maxRegSubfile) {
		this.maxRegSubfile = maxRegSubfile;
	}

	/**
	 * @return the maxPagesSubfile
	 */
	public int getMaxPagesSubfile() {
		return maxPagesSubfile;
	}

	/**
	 * @param maxPagesSubfile
	 *            the maxPagesSubfile to set
	 */
	public void setMaxPagesSubfile(int maxPagesSubfile) {
		this.maxPagesSubfile = maxPagesSubfile;
	}

	/**
	 * @return the nameSubfile
	 */
	public String getNameSubfile() {
		return nameSubfile;
	}

	/**
	 * @param nameSubfile
	 *            the nameSubfile to set
	 */
	public void setNameSubfile(String nameSubfile) {
		this.nameSubfile = nameSubfile;
	}

	/**
	 * @return the userDescription
	 */
	public String getUserDescription() {
		return userDescription;
	}

	/**
	 * @param userDescription
	 *            the userDescription to set
	 */
	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}

	/**
	 * @return the loginDate
	 */
	public Date getLoginDate() {
		return loginDate;
	}

	/**
	 * @param loginDate
	 *            the loginDate to set
	 */
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	/**
	 * @return the executedServices
	 */
	public Stack<Cspsersi> getExecutedServices() {
		return executedServices;
	}

	/**
	 * @return the userAutenticated
	 */
	public boolean isUserAutenticated() {
		return userAutenticated;
	}

	/**
	 * @param userAutenticated
	 *            the isUserAutenticated to set
	 */
	public void setUserAutenticated(boolean userAutenticated) {
		this.userAutenticated = userAutenticated;
	}

	/**
	 * @return the maxFailedTries
	 */
	public int getMaxFailedTries() {
		return maxFailedTries;
	}

	/**
	 * @param maxFailedTries
	 *            the maxFailedTries to set
	 */
	public void setMaxFailedTries(int maxFailedTries) {
		this.maxFailedTries = maxFailedTries;
	}

	/**
	 * @return the minCharsPassword
	 */
	public int getMinCharsPassword() {
		return minCharsPassword;
	}

	/**
	 * @param minCharsPassword
	 *            the minCharsPassword to set
	 */
	public void setMinCharsPassword(int minCharsPassword) {
		this.minCharsPassword = minCharsPassword;
	}

	/**
	 * @return the daysToChangePass
	 */
	public int getDaysToChangePass() {
		return daysToChangePass;
	}

	/**
	 * @param daysToChangePass
	 *            the daysToChangePass to set
	 */
	public void setDaysToChangePass(int daysToChangePass) {
		this.daysToChangePass = daysToChangePass;
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
	 * @return the timeToUnlockUser
	 */
	public int getTimeToUnlockUser() {
		return timeToUnlockUser;
	}

	/**
	 * @param timeToUnlockUser
	 *            the timeToUnlockUser to set
	 */
	public void setTimeToUnlockUser(int timeToUnlockUser) {
		this.timeToUnlockUser = timeToUnlockUser;
	}

	/**
	 * @return the authorizedServices
	 */
	public List<Cspsersi> getAuthorizedServices() {
		// Revisa que no sea null
		if (this.authorizedServices == null)
			this.authorizedServices = new ArrayList<>();
		return authorizedServices;
	}

	/**
	 * @param authorizedServices
	 *            the authorizedServices to set
	 */
	public void setAuthorizedServices(List<Cspsersi> authorizedServices) {
		this.authorizedServices = authorizedServices;
	}

	/**
	 * @return the cspparsiDao
	 */
	public ICspparsiDAO getCspparsiDao() {
		return cspparsiDao;
	}

	/**
	 * @return the cspsersiDao
	 */
	public ICspsersiDAO getCspsersiDao() {
		return cspsersiDao;
	}

	/**
	 * @return the usmusuarDao
	 */
	public IUsmusuarDAO getUsmusuarDao() {
		return usmusuarDao;
	}

	/**
	 * @return the usdloginDao
	 */
	public IUsdloginDAO getUsdloginDao() {
		return usdloginDao;
	}

	/**
	 * @return the usdbloquDao
	 */
	public IUsdbloquDAO getUsdbloquDao() {
		return usdbloquDao;
	}

	/**
	 * @return the cspserpfDao
	 */
	public ICspserpfDAO getCspserpfDao() {
		return cspserpfDao;
	}

	/**
	 * @return the maxRecordsQuery
	 */
	public int getMaxRecordsQuery() {
		return maxRecordsQuery;
	}

	/**
	 * @param maxRecordsQuery
	 *            the maxRecordsQuery to set
	 */
	public void setMaxRecordsQuery(int maxRecordsQuery) {
		this.maxRecordsQuery = maxRecordsQuery;
	}

	/**
	 * @return the maxRecordsAutocomplete
	 */
	public int getMaxRecordsAutocomplete() {
		return maxRecordsAutocomplete;
	}

	/**
	 * @param maxRecordsAutocomplete
	 *            the maxRecordsAutocomplete to set
	 */
	public void setMaxRecordsAutocomplete(int maxRecordsAutocomplete) {
		this.maxRecordsAutocomplete = maxRecordsAutocomplete;
	}

	/**
	 * @return the visualizationService
	 */
	public String getVisualizationService() {
		return visualizationService;
	}

	/**
	 * @param visualizationService
	 *            the visualizationService to set
	 */
	public void setVisualizationService(String visualizationService) {
		this.visualizationService = visualizationService;
	}
}
