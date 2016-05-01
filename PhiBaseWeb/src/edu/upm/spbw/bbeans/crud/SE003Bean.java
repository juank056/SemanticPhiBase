/**
 * 
 */
package edu.upm.spbw.bbeans.crud;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.icesoft.faces.component.ext.HtmlDataTable;

import edu.upm.spbw.bbeans.base.BaseBean;
import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Cspperfu;
import edu.upm.spbw.persistence.bo.Cspserpf;
import edu.upm.spbw.persistence.bo.CspserpfId;
import edu.upm.spbw.persistence.bo.Cspsersi;
import edu.upm.spbw.persistence.dao.ICspserpfDAO;
import edu.upm.spbw.persistence.dao.ICspsersiDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.LogLogger;

/**
 * Bean para el servicio SE003 <br/>
 * SE003: Asociar Servicios a Perfiles de Usuario
 * 
 * @author Juan Camilo
 */
public class SE003Bean extends BaseBean<Cspserpf, CspserpfId> {

	/**
	 * ID Serialización
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Perfil de usuario padre
	 */
	private Cspperfu parentPerfu;

	/**
	 * DAO para leer servicios del sistema
	 */
	private ICspsersiDAO cspsersiDao;

	/**
	 * Lista para el adicionar
	 */
	protected List<SelectItem> lsSersi;

	/***************************************
	 * ATRIBUTOS DEL BEAN
	 **************************************/

	/**
	 * Constructor del Bean
	 */
	public SE003Bean() {
		// Recibe parametros
		super("SE003", true);
	}

	/**
	 * Inicia servicios
	 */
	@Override
	protected void initServices() {
		// DAO
		objectDao = appContext.getBean(ICspserpfDAO.class);
		// DAO sersi
		cspsersiDao = appContext.getBean(ICspsersiDAO.class);
		// Lista de servicios del sistema
		lsSersi = new ArrayList<>();
		// Carga el Dropdown de Busqueda
		listDropdown = new ArrayList<SelectItem>();
		// Obtiene todos los servicios del sistema activos
		try {
			List<Cspsersi> sersis = cspsersiDao.findActive();
			// El seleccione
			lsSersi.add(new SelectItem(Constants.BLANKS, ApplicationMessages
					.getMessage(Constants.SELECCIONE)));
			// Recorre registros
			for (Cspsersi sersi : sersis) {
				// Adiciona registro
				lsSersi.add(new SelectItem(sersi.getSsicssiak(), sersi
						.getSsinssiaf()));
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
	public void receiveParameters(ActionEvent e) {
		// Recibe el Codigo del Servicio Padre
		parentPerfu = (Cspperfu) e.getComponent().getAttributes()
				.get(Constants.BEAN_PARAM01);
		this.loadSubfile();/* Carga lista del Subfile */
		// Asigna Id al objeto de trabajo
		object.getId().setPfucpfuak(parentPerfu.getPfucpfuak());
	}

	/**
	 * Carga subfile
	 */
	@Override
	protected void loadSubfile() {
		try {
			// Carga todos los objetos
			subfileList = ((ICspserpfDAO) objectDao).findByProfile(parentPerfu
					.getPfucpfuak());
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
			for (Cspserpf serpf : subfileList) {
				// Adiciona objeto
				listDropdown.add(new SelectItem(serpf.getId().getSsicssiak(),
						serpf.getSsinssiaf()));
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
		// Inicia el Objeto de trabajo
		object = new Cspserpf(
				new CspserpfId(Constants.BLANKS, Constants.BLANKS),
				Constants.ZERO);
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
		// Valida Servicio del sistema
		if (this.isBlanks(object.getId().getSsicssiak())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0008",
							ApplicationMessages
									.getMessage("se003_systemService")));
		}
		// Retorna
		return !isError;
	}

	/**
	 * Se encarga de guardar registro
	 */
	@Override
	protected void saveRecord() throws DAOException {
		// Asigna perfil
		object.getId().setPfucpfuak(parentPerfu.getPfucpfuak());
		// Guarda registro
		super.saveRecord();
	}

	/**
	 * Actualiza registro
	 */
	@Override
	protected void updateRecord() throws DAOException {
		// Asigna perfil
		object.getId().setPfucpfuak(parentPerfu.getPfucpfuak());
		// Actualiza registro
		super.updateRecord();
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
				// perfil de usuario
				object.getId().setPfucpfuak(parentPerfu.getPfucpfuak());
				// Servicio del sistema
				object.getId().setSsicssiak(this.searchField);
				// Busca registro en la base de datos
				object = objectDao.findById(this.object.getId());
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
	 * @return the lsSersi
	 */
	public List<SelectItem> getLsSersi() {
		return lsSersi;
	}

	/**
	 * @param lsSersi
	 *            the lsSersi to set
	 */
	public void setLsSersi(List<SelectItem> lsSersi) {
		this.lsSersi = lsSersi;
	}
}
