/**
 * 
 */
package edu.upm.spbw.bbeans.crud;

import java.util.ArrayList;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.icesoft.faces.component.ext.HtmlDataTable;

import edu.upm.spbw.bbeans.base.BaseBean;
import edu.upm.spbw.persistence.bo.Cspparsi;
import edu.upm.spbw.persistence.dao.ICspparsiDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.LogLogger;

/**
 * Bean para el servicio SE004 <br/>
 * SE004: Mantenimiento de los parametros del sistema
 * 
 * @author Juan Camilo
 */
public class SE004Bean extends BaseBean<Cspparsi, String> {

	/**
	 * ID Serialización
	 */
	private static final long serialVersionUID = 1L;

	/***************************************
	 * ATRIBUTOS DEL BEAN
	 **************************************/

	/**
	 * Constructor del Bean
	 */
	public SE004Bean() {
		super("SE004", false);
	}

	/**
	 * Inicia servicios
	 */
	@Override
	protected void initServices() {
		// DAO
		objectDao = appContext.getBean(ICspparsiDAO.class);
		// Carga el Dropdown de Busqueda
		listDropdown = new ArrayList<SelectItem>();
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
			for (Cspparsi parsi : subfileList) {
				// Adiciona objeto
				listDropdown.add(new SelectItem(parsi.getPsicparak(), parsi
						.getPsidparaf()));
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
		// Inicia el parametro del Sistema de Trabajo
		object = new Cspparsi();
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
		// Valida codigo del Parametro
		if (this.isBlanks(object.getPsicparak())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0008",
							ApplicationMessages
									.getMessage("se004_codeParameter")));
		}
		// Valida Nombre del Parametro
		if (this.isBlanks(object.getPsidparaf())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0008",
							ApplicationMessages
									.getMessage("se004_nameParameter")));
		}
		// Valida el Tipo de Parametro
		if (this.isBlanks(object.getPsitparvf())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0008",
							ApplicationMessages
									.getMessage("se004_typeParameter")));
		}
		// Valida el valor del Parametro (Dependiendo de numerico o alfa)
		switch (object.getPsitparvf()) {
		case Constants.ONE:/* Alfanumerico */
			// Valida que no sea blancos
			if (this.isBlanks(object.getPsivalpaf())) {
				isError = true;
				this.getErrorMessages().add(
						ApplicationMessages.getMessage("USR0008",
								ApplicationMessages
										.getMessage("se004_valueParameter")));
			}
			break;
		case Constants.TWO: /* Numérico */
			// Revisa que sea numerico
			try {
				// Trata de convertir valor
				Double.valueOf(object.getPsivalpaf());
			} catch (Exception e) {/* No pudo convertir */
				isError = true;
				this.getErrorMessages().add(
						ApplicationMessages.getMessage("USR0008",
								ApplicationMessages
										.getMessage("se004_valueParameter")));
			}
			break;
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
}
