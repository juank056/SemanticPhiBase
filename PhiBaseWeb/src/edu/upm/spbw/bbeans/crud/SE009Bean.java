/**
 * 
 */
package edu.upm.spbw.bbeans.crud;

import java.util.ArrayList;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.icesoft.faces.component.ext.HtmlDataTable;

import edu.upm.spbw.bbeans.base.BaseBean;
import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Sepconce;
import edu.upm.spbw.persistence.bo.Seprelco;
import edu.upm.spbw.persistence.dao.ISepconceDAO;
import edu.upm.spbw.persistence.dao.ISeprelcoDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.LogLogger;

/**
 * Bean para el servicio SE009 <br/>
 * SE009: Mantenimiento de los Conceptos Semánticos
 * 
 * @author Juan Camilo
 */
public class SE009Bean extends BaseBean<Sepconce, String> {

	/**
	 * ID Serialización
	 */
	private static final long serialVersionUID = 1L;

	/***************************************
	 * ATRIBUTOS DEL BEAN
	 **************************************/

	/**
	 * DAO para leer Seprelco
	 */
	private ISeprelcoDAO seprelcoDao;

	/**
	 * Constructor del Bean
	 */
	public SE009Bean() {
		super("SE009", false);
	}

	/**
	 * Inicia servicios
	 */
	@Override
	protected void initServices() {
		// DAO
		objectDao = appContext.getBean(ISepconceDAO.class);
		seprelcoDao = appContext.getBean(ISeprelcoDAO.class);
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
			for (Sepconce conce : subfileList) {
				// Adiciona objeto
				listDropdown.add(new SelectItem(conce.getCosccosak(), conce
						.getCosdcosaf()));
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
		// Inicia el objeto de trabajo
		object = new Sepconce();
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
		// Código de concepto
		if (this.isBlanks(object.getCosccosak())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages
							.getMessage("USR0008", ApplicationMessages
									.getMessage("se009_codeConcept")));
		}
		// Descripción concepto
		if (this.isBlanks(object.getCosdcosaf())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages
							.getMessage("USR0008", ApplicationMessages
									.getMessage("se009_descConcept")));
		}
		// RDF Concepto
		if (this.isBlanks(object.getCosrdftaf())) {
			isError = true;
			this.getErrorMessages()
					.add(ApplicationMessages.getMessage("USR0008",
							ApplicationMessages.getMessage("se009_rdfConcept")));
		}
		// Relaciones concepto
		if (object.getCosnrelnf() <= 0) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0018",
							ApplicationMessages
									.getMessage("se009_relationsConcept")));
		}
		// Documentación
		if (this.isBlanks(object.getCosdocuaf())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0008",
							ApplicationMessages
									.getMessage("se009_documentation")));
		}
		// Concepto padre
		if (!this.isBlanks(object.getCosccopak())
				&& object.getCosccosak().equals(object.getCosccopak())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0033"));
		}
		// Valida relaciones
		for (Seprelco relco : object.getRelcos()) {
			// Codigo relacion
			if (this.isBlanks(relco.getId().getRcocrcoak())) {
				isError = true;
				this.getErrorMessages().add(
						ApplicationMessages.getMessage("USR0008",
								ApplicationMessages
										.getMessage("se009_codeRelation")));
			}
			// Descripción relacion
			if (this.isBlanks(relco.getRcodrcoaf())) {
				isError = true;
				this.getErrorMessages().add(
						ApplicationMessages.getMessage("USR0008",
								ApplicationMessages
										.getMessage("se009_descRelation")));
			}
			// RDF Relación
			if (this.isBlanks(relco.getRcordfraf())) {
				isError = true;
				this.getErrorMessages().add(
						ApplicationMessages.getMessage("USR0008",
								ApplicationMessages
										.getMessage("se009_rdfRelation")));
			}
			// Documentación
			if (this.isBlanks(relco.getRcodocuaf())) {
				isError = true;
				this.getErrorMessages().add(
						ApplicationMessages.getMessage("USR0008",
								ApplicationMessages
										.getMessage("se009_documentation")));
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
	 * Metodo para adicionar un registro Este metodo debe llamar al servicio o
	 * al DAO encargado de la operacion
	 */
	protected void saveRecord() throws DAOException {
		// Prepara para actualizar
		object.prepareToUpdate();
		// Guarda objecto
		objectDao.save(object);
		// Guarda registros de relaciones
		for (Seprelco relco : object.getRelcos()) {
			// Asigna Id padre
			relco.getId().setCosccosak(object.getCosccosak());
			seprelcoDao.save(relco);
		}
	}

	/**
	 * Metodo para actualizar un registro Este metodo debe llamar al servicio o
	 * al DAO encargado de la operacion
	 */
	protected void updateRecord() throws DAOException {
		// Prepara para actualizar
		object.prepareToUpdate();
		// Actualiza objeto
		objectDao.update(object);
		// Actualiza relaciones
		for (Seprelco relco : object.getRelcos()) {
			seprelcoDao.update(relco);
		}
	}

	/**
	 * Metodo para eliminar un registro Este metodo debe llamar al servicio o al
	 * DAO encargado de la operacion
	 */
	protected void deleteRecord() throws DAOException {
		// Elimina relaciones
		for (Seprelco relco : object.getRelcos()) {
			seprelcoDao.delete(relco);
		}
		// Elimina objeto
		objectDao.delete(object);
	};

	/**
	 * Inicia relaciones de concepto semantico
	 * 
	 * @param ev
	 */
	public void initRelations(ValueChangeEvent ev) {
		// Limpia lista de relaciones
		object.getRelcos().clear();
		// Inicia tantos objetos como numero de relaciones sean
		for (int i = 0; i < Integer.valueOf(ev.getNewValue().toString()); i++) {
			// Inicia objeto de relacion
			Seprelco relco = new Seprelco();
			// Prepara objeto
			relco.prepareObject();
			// Adiciona objeto en la lista
			object.getRelcos().add(relco);
		}
	}

}
