/**
 * 
 */
package edu.upm.spbw.bbeans.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.context.ApplicationContext;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.AbstractBO;
import edu.upm.spbw.persistence.dao.GenericDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.LogLogger;
import edu.upm.spbw.utils.SpringAppContext;

/**
 * Bean Base que todos deben extenders
 * 
 * @author Juan Camilo
 */
public abstract class BaseBean<T extends AbstractBO<PK>, PK extends Serializable>
		implements Serializable {

	/**
	 * Id serializacion
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Codigo de Servicio que representa este bean
	 */
	private String serviceCode;

	/******************************************
	 * ATRIBUTOS PARA VISUALIZACION DE PANTALLA
	 *****************************************/

	/**
	 * Indicador para determinar si se esta en un add
	 */
	private boolean add;

	/**
	 * Indicador para determinar si se esta en un Change
	 */
	private boolean change;

	/**
	 * Indicador para determinar si se esta en un Delete
	 */
	private boolean delete;

	/**
	 * Indicador para determinar si se esta en un Display
	 */
	private boolean display;

	/**
	 * Indicador para determinar si hubo error en las validaciones
	 */
	protected boolean isError;

	/**
	 * Lista de mensajes de error que se puedan presentar
	 */
	private List<String> errorMessages;

	/**
	 * Application context para instanciar Servicios de Spring
	 */
	protected ApplicationContext appContext;

	/**
	 * DAO para manejar operaciones de base de datos
	 */
	protected GenericDAO<T, PK> objectDao;

	/**
	 * Lista del Subfile
	 */
	protected List<T> subfileList;

	/**
	 * Objeto de trabajo (add, change, delete, display)
	 */
	protected T object;

	/**
	 * Campo de busqueda
	 */
	protected String searchField;

	/**
	 * /** Lista para el buscador
	 */
	protected List<SelectItem> listDropdown;

	/**
	 * Constructor del Bean. Recibe el codigo del Servicio en el que se esta
	 * como parametro
	 * 
	 * @param serviceCode
	 *            identifica el codigo del servicio de este bean
	 * @param hasParameters
	 *            booleano para determinar si el Bean a crear debe recibir
	 *            parametros a partir de otro servicio
	 */
	public BaseBean(String serviceCode, boolean hasParameters) {
		// Inicia el app context para Spring
		this.initAppContext();
		// Asigna el codigo del Servicio
		this.serviceCode = serviceCode;
		// Inicia Lista para manejo de errores
		errorMessages = new ArrayList<String>();
		// Iniciamos los servicios del Bean
		this.initServices();
		// Inicia los objetos
		this.initObjects();
		/*
		 * Revisamos si el bean recibe parametros para llamar el cargue del
		 * subfile y tambien el inicio de los objetos del bean. Si no recibe
		 * parametros, este cargue se sebe realizar en el metodo de recibir
		 * parametros
		 */
		if (!hasParameters) {
			// Carga el subfile
			this.loadSubfile();
		}
	}

	/**
	 * Inicia el Application Context para instanciar servicios de Spring
	 */
	private void initAppContext() {
		appContext = SpringAppContext.getAppContext();
	}

	/**
	 * Metodo para determinar si el usuario se encuentra autenticado y
	 * autorizado para usar el servicio
	 * 
	 * @return true si el usuario no se encuentra autorizado o autenticado para
	 *         usar el servicio
	 */
	public boolean isUserNOTAuthenticatedAndAuthorized() {
		// Obtenemos el bean de Autenticacion para asignar el servicio actual
		HttpSession s = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		AutenticationBean ab = (AutenticationBean) s
				.getAttribute(Constants.AUTENTICATION_BEAN);
		// Revisa que exista el bean de autenticacion
		if (ab == null) {
			LogLogger.getInstance(this.getClass()).logger(
					"Autentication Bean was Null", LogLogger.WARN);
			return true;
		}
		// Realizamos peticion de autorizacion al servicio
		try {
			// Revisa si el codigo de servicio es del mismo auth bean
			if (this.serviceCode.equals(Constants.AUTENTICATION_BEAN)) {
				// Se trata del menu principal
				// Revisa si el usuario esta autenticado
				return !ab.isUserAutenticated();
			} else {/* Es un servicio convencional */
				// Revisa si el usuario esta autorizado
				return !ab.isUserAuthorized(this.serviceCode);
			}
		} catch (Exception e) {
			LogLogger.getInstance(this.getClass()).logger(
					"Error in Autentication: "
							+ ExceptionUtils.getFullStackTrace(e),
					LogLogger.ERROR);
			// Procesamos mensaje
			this.processErrorMessage(e.getMessage());
		}
		// Autenticado correctamente
		return false;
	}

	/**
	 * Metodo para obtener un Backing Bean (de Session o Aplicacion)
	 * 
	 * @param backingBeanName
	 *            el nombre del Backing Bean a Obtener
	 * @return el Backing Bean obtenido. Retorna null si no se encuentra el
	 *         objeto
	 */
	protected Object getBackingBean(String backingBeanName) {
		// Obtenemos el bean de Autenticacion para asignar el servicio actual
		HttpSession s = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		return s.getAttribute(backingBeanName);
	}

	/******************************************
	 * METODOS BASICOS DE UN MANTENIMIENTO
	 *****************************************/

	/**
	 * Método para adicionar un registro
	 * 
	 * @param e
	 *            action event
	 */
	public boolean addRecord(ActionEvent ev) {
		// Obtenemos parametro para saber si apenas se va a iniciar el add
		String stdfunreq = (String) ev.getComponent().getAttributes()
				.get(Constants.STDFUNREQ);
		if (Constants.INIT_ADD.equals(stdfunreq)) {
			// Visualizacion add
			this.setAdd(true);
			this.initObjects();/* Inicia Objetos */
		} else {
			// Guardamos el servicio en la base de datos
			try {
				// Realizamos las validaciones correspondientes de los campos
				if (!this.validateFields(Constants.ADD)) {
					// Procesa lista de errores
					this.processErrorListMessages();
					// Finaliza
					return false;
				}
				// Guarda registro
				this.saveRecord();
				// Se ha adicionado el registro Satisfactoriamente.
				this.processMessage(ApplicationMessages.getMessage("USR0005"));
				// Carga nuevamente el Subfile
				this.loadSubfile();
				// Inicia Objetos
				this.initObjects();
				// Visualizacion add
				this.setAdd(false);
			} catch (Exception e) {
				LogLogger.getInstance(this.getClass()).logger(
						ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
				this.processErrorMessage(e.getMessage());
			}
		}
		// Finaliza
		return true;
	}

	/**
	 * Método para modificar un registro
	 * 
	 * @param ev
	 *            action event
	 */
	public boolean changeRecord(ActionEvent ev) {
		// Obtenemos parametros del request
		String stdfunreq = (String) ev.getComponent().getAttributes()
				.get(Constants.STDFUNREQ);
		// Revisa si se esta iniciando el change
		if (Constants.INIT_CHANGE.equals(stdfunreq)) {
			// Visualizacion Change
			this.setChange(true);
			// Obtiene el objeto a modificar
			if (!this.getObject(ev)) {
				// Procesa lista de errores
				this.processErrorMessage(ApplicationMessages
						.getMessage("USR0024"));
				// Visualizacion Change
				this.setChange(false);
				// Objeto no encontrado
				return false;
			}
		} else {/* Ya se va a actualizar el registro */
			// Guardamos el servicio en la base de datos
			try {
				// Realizamos las validaciones correspondientes de los campos
				if (!this.validateFields(Constants.CHANGE)) {
					// Procesa lista de errores
					this.processErrorListMessages();
					// Finaliza
					return false;
				}
				// Llama al servicio encargado de la actualizacion
				this.updateRecord();
				// Se ha Actualizado el Registro satisfactoriamente
				this.processMessage(ApplicationMessages.getMessage("USR0006"));

				// Carga nuevamente el Subfile
				this.loadSubfile();
				// Inicia Objetos
				this.initObjects();
				// Visualizacion Change
				this.setChange(false);
			} catch (Exception e) {/* Ocurrio error */
				LogLogger.getInstance(this.getClass()).logger(
						ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
				this.processErrorMessage(e.getMessage());
			}
		}
		// Finaliza
		return true;
	}

	/**
	 * Método para eliminar un registro
	 * 
	 * @param ev
	 *            action event
	 */
	public boolean deleteRecord(ActionEvent ev) {
		// Eliminamos el registro
		try {
			// Obtiene el objeto a eliminar
			if (!this.getObject(ev)) {
				// Procesa lista de errores
				this.processErrorMessage(ApplicationMessages
						.getMessage("USR0024"));
				// Objeto no encontrado
				return false;
			}
			// Realiza validaciones para saber si el objeto se puede eliminar
			if (!this.validateFields(Constants.DELETE)) {
				// Procesa lista de errores
				this.processErrorListMessages();
				// Finaliza
				return false;
			}
			// Llama al servicio que se encarga de la eliminacion
			this.deleteRecord();
			// Carga nuevamente el subfile
			this.loadSubfile();
			// Inicia objetos
			this.initObjects();/* Inicia Objetos */
			// Se ha Eliminado el Registro satisfactoriamente
			this.processMessage(ApplicationMessages.getMessage("USR0007"));
			// Visualizacion Delete
			this.setDelete(false);
		} catch (Exception e) {/* Ocurrio error eliminando registro */
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			this.processErrorMessage(e.getMessage());
		}
		// Finaliza ok
		return true;
	}

	/**
	 * Método para visualizar un registro
	 * 
	 * @param e
	 *            action event
	 */
	public void displayRecord(ActionEvent e) {
		// Visualizacion Display
		this.setDisplay(true);
		// Obtiene el objeto a visualizar
		if (!this.getObject(e)) {
			this.processErrorMessage(ApplicationMessages.getMessage("USR0024"));
			this.setDisplay(false);
		}
	}

	/**
	 * Cancela las acciones de Adicionar, Modificar o Eliminar un registro
	 * 
	 * @param e
	 *            el Evento
	 */
	public void cancel(ActionEvent e) {
		// Desactiva vistas
		add = false;
		change = false;
		delete = false;
		display = false;
		// Reinicia los objetos nuevamente
		this.initObjects();
	}

	/**
	 * Obtiene un parametro que llega del Request
	 * 
	 * @param paramCode
	 *            el codigo del parametro a obtener
	 * @return el parametro obtenido, null si no se encuentra el parametro
	 */
	public Object getRequestParameter(String paramCode) {
		return FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get(paramCode);
	}

	/***************************************************************
	 * METODOS ABSTRACTOS QUE DEBE IMPLEMENTAR CADA BEAN A SU MANERA
	 **************************************************************/

	/**
	 * Metodo para recibir los parametros que puedan llegar.
	 * 
	 * @param e
	 *            el evento
	 */
	public abstract void receiveParameters(ActionEvent e);

	/**
	 * Inica los servicios del Bean (DAOs o cualquier clase que se necesite)
	 * Adicionalmente este metodo puede cargar listas o lo que necesite que solo
	 * deban ser cargadas una vez
	 */
	protected abstract void initServices();

	/**
	 * Metodo para cargar la lista del Servicio
	 */
	protected abstract void loadSubfile();

	/**
	 * Inicia los objetos del Bean
	 */
	protected abstract void initObjects();

	/**
	 * Metodo para realizar las operaciones de buscador
	 * 
	 * @param e
	 *            el evento
	 */
	public abstract void searchRecord(ActionEvent e);

	/**
	 * Obtiene un metodo del Subfile y lo asigna al objeto de trabajo
	 */
	@SuppressWarnings("unchecked")
	protected boolean getObject(ActionEvent e) {
		// Obtiene objeto
		object = (T) e.getComponent().getAttributes().get(Constants.PARAMETER);
		// Revisa si lo encontro para retornar
		return object != null;
	}

	/**
	 * Metodo para adicionar un registro Este metodo debe llamar al servicio o
	 * al DAO encargado de la operacion
	 */
	protected void saveRecord() throws DAOException {
		// Prepara para actualizar
		object.prepareToUpdate();
		objectDao.save(object);
	}

	/**
	 * Metodo para actualizar un registro Este metodo debe llamar al servicio o
	 * al DAO encargado de la operacion
	 */
	protected void updateRecord() throws DAOException {
		// Prepara para actualizar
		object.prepareToUpdate();
		objectDao.update(object);
	}

	/**
	 * Metodo para eliminar un registro Este metodo debe llamar al servicio o al
	 * DAO encargado de la operacion
	 */
	protected void deleteRecord() throws DAOException {
		objectDao.delete(object);
	};

	/**
	 * Metodo que se debe implementar para realizar las validaciones de los
	 * campos de un formulario
	 * 
	 * @return Indicador de validacion de campos
	 * @param action
	 *            la accion en la que se estan realizando las validaciones
	 *            (add,change,delete)
	 */
	protected abstract boolean validateFields(String action);

	/**
	 * Obtiene un componente de la pantalla dado su ID
	 * 
	 * @param componentId
	 *            Id del componente de la pantalla
	 * @return Componente encontrado o null
	 */
	protected UIComponent getComponentById(String componentId) {
		// Root
		FacesContext context = FacesContext.getCurrentInstance();
		UIViewRoot root = context.getViewRoot();
		// Busca componente
		return findComponent(root, componentId);
	}

	/**
	 * Busca componente por ID
	 * 
	 * @param c
	 *            Componente padre
	 * @param id
	 *            Id del componente a buscar
	 * @return Componente encontrado o null
	 */
	private UIComponent findComponent(UIComponent c, String id) {
		if (id.equals(c.getId())) {
			return c;
		}
		Iterator<UIComponent> kids = c.getFacetsAndChildren();
		while (kids.hasNext()) {
			UIComponent found = findComponent(kids.next(), id);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	/**************************
	 * PROCESAMIENTO DE MENSAJES
	 *************************/
	/**
	 * Se encarga de mostrar un mensaje en la pantalla
	 * 
	 * @param message
	 *            el contenido del mensaje
	 */
	protected void processMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
	}

	/**
	 * Se encarga de mostrar un mensaje de error en la pantalla
	 * 
	 * @param message
	 *            el contenido del mensaje
	 */
	protected void processErrorMessage(String message) {
		FacesContext.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, message,
								message));
	}

	/**
	 * Lee toda la lista de errores para mostrar en pantalla
	 */
	protected void processErrorListMessages() {
		// Recorre para mostrar errores
		for (String message : errorMessages) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, message,
							message));
		}
		// Limpia la lista de errores
		errorMessages.clear();
	}

	/*************************************
	 * GETTERS Y SETTERS
	 ************************************/

	/**
	 * Obtiene codigo de servicio del sistema
	 * 
	 * @return Codigo de servicio del sistema
	 */
	public String getServiceCode() {
		return serviceCode;
	}

	/**
	 * Retorna indicador de pantalla de Add
	 * 
	 * @return Indicador de pantalla de Add
	 */
	public boolean isAdd() {
		return add;
	}

	/**
	 * Asigna indicador de pantalla de Add
	 * 
	 * @param add
	 *            Indicador de pantalla de Add
	 */
	public void setAdd(boolean add) {
		this.add = add;
	}

	/**
	 * Obtiene indicador de que se esta en change
	 * 
	 * @return indicador de si se esta en un change
	 */
	public boolean isChange() {
		return change;
	}

	/**
	 * Asigna indicador de que se esta en change
	 * 
	 * @param change
	 *            Indicador de change
	 */
	public void setChange(boolean change) {
		this.change = change;
	}

	/**
	 * Obtiene indicador de si se esta en delete
	 * 
	 * @return Indicador de delete
	 */
	public boolean isDelete() {
		return delete;
	}

	/**
	 * Asigna indicador de si se esta en display
	 * 
	 * @param delete
	 *            Indicador de display
	 */
	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	/**
	 * Obtiene indicador de si se esta en display
	 * 
	 * @return Indicador de display
	 */
	public boolean isDisplay() {
		return display;
	}

	/**
	 * Asigna indicador de si se esta en display
	 * 
	 * @param display
	 *            Indicador de display
	 */
	public void setDisplay(boolean display) {
		this.display = display;
	}

	/*************************
	 * VALIDACIONES DE CAMPOS
	 ************************/

	/**
	 * Metodo para validar que un campo no sea blancos
	 * 
	 * @param field
	 *            el campo a validar
	 * @return true si el campo es blancos, false de lo contrario
	 */
	public boolean isBlanks(String field) {
		return Constants.BLANKS.equals(field.trim());
	}

	/**
	 * Metodo para determinar si un campo es nulo
	 * 
	 * @param field
	 *            el campo a validar
	 * @return true si el campo es null,false de lo contrario
	 */
	public boolean isNull(Object field) {
		return field == null;
	}

	/**
	 * Obtiene lista de mensajes de error
	 * 
	 * @return Lista de mensajes de error
	 */
	public List<String> getErrorMessages() {
		return errorMessages;
	}

	/**
	 * Asigna lista de mensajes de error
	 * 
	 * @param errorMessages
	 *            Lista de mensajes de error
	 */
	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

	/**
	 * @return the isError
	 */
	public boolean isError() {
		return isError;
	}

	/**
	 * @param isError
	 *            the isError to set
	 */
	public void setError(boolean isError) {
		this.isError = isError;
	}

	/**
	 * @return the subfileList
	 */
	public List<T> getSubfileList() {
		return subfileList;
	}

	/**
	 * @param subfileList
	 *            the subfileList to set
	 */
	public void setSubfileList(List<T> subfileList) {
		this.subfileList = subfileList;
	}

	/**
	 * @return the objectDao
	 */
	public GenericDAO<T, PK> getObjectDao() {
		return objectDao;
	}

	/**
	 * @param objectDao
	 *            the objectDao to set
	 */
	public void setObjectDao(GenericDAO<T, PK> objectDao) {
		this.objectDao = objectDao;
	}

	/**
	 * @return the object
	 */
	public T getObject() {
		return object;
	}

	/**
	 * @param object
	 *            the object to set
	 */
	public void setObject(T object) {
		this.object = object;
	}

	/**
	 * @return the listDropdown
	 */
	public List<SelectItem> getListDropdown() {
		return listDropdown;
	}

	/**
	 * @param listDropdown
	 *            the listDropdown to set
	 */
	public void setListDropdown(List<SelectItem> listDropdown) {
		this.listDropdown = listDropdown;
	}

	/**
	 * @return the searchField
	 */
	public String getSearchField() {
		return searchField;
	}

	/**
	 * @param searchField
	 *            the searchField to set
	 */
	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}
}
