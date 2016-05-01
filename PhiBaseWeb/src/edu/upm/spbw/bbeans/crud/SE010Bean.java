/**
 * 
 */
package edu.upm.spbw.bbeans.crud;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.icesoft.faces.component.ext.HtmlDataTable;

import edu.upm.spbw.bbeans.base.AutenticationBean;
import edu.upm.spbw.bbeans.base.BaseBean;
import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Sedconce;
import edu.upm.spbw.persistence.bo.Sedrelco;
import edu.upm.spbw.persistence.bo.Sepconce;
import edu.upm.spbw.persistence.bo.Seprelco;
import edu.upm.spbw.persistence.dao.ISedconceDAO;
import edu.upm.spbw.persistence.dao.ISedrelcoDAO;
import edu.upm.spbw.persistence.dao.ISepconceDAO;
import edu.upm.spbw.persistence.dao.ISeprelcoDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.LogLogger;

/**
 * Bean para el servicio SE010 <br/>
 * SE010: Mantenimiento del detalle de los conceptos semánticos
 * 
 * @author Juan Camilo
 */
public class SE010Bean extends BaseBean<Sedconce, Integer> {

	/**
	 * ID Serialización
	 */
	private static final long serialVersionUID = 1L;

	/***************************************
	 * ATRIBUTOS DEL BEAN
	 **************************************/

	/**
	 * DAO para leer Sedrelco
	 */
	private ISedrelcoDAO sedrelcoDao;

	/**
	 * DAO para leer Sepconce
	 */
	private ISepconceDAO sepconceDao;

	/**
	 * DAO para leer Seprelco
	 */
	private ISeprelcoDAO seprelcoDao;

	/**
	 * Lista de conceptos semanticos
	 */
	private List<SelectItem> lsConcepts;

	/**
	 * Codigo de concepto para busqueda
	 */
	private String codeConceptSearch;

	/**
	 * Constructor del Bean
	 */
	public SE010Bean() {
		super("SE010", false);
	}

	/**
	 * Inicia servicios
	 */
	@Override
	protected void initServices() {
		// DAO
		objectDao = appContext.getBean(ISedconceDAO.class);
		sedrelcoDao = appContext.getBean(ISedrelcoDAO.class);
		seprelcoDao = appContext.getBean(ISeprelcoDAO.class);
		sepconceDao = appContext.getBean(ISepconceDAO.class);
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
			// Auth bean
			AutenticationBean authBean = (AutenticationBean) this
					.getBackingBean(Constants.AUTENTICATION_BEAN);
			// Busca registros
			subfileList = ((ISedconceDAO) objectDao).findByConceptAndIRI(
					this.codeConceptSearch, this.searchField,
					authBean.getMaxRecordsQuery());
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
		this.codeConceptSearch = Constants.BLANKS;
		// Inicia el objeto de trabajo
		object = new Sedconce();
		// Prepara objeto
		object.prepareObject();
		// Lista de conceptos semanticos
		lsConcepts = new ArrayList<>();
		// Adiciona el Seleccione
		lsConcepts.add(new SelectItem(Constants.BLANKS, ApplicationMessages
				.getMessage(Constants.SELECCIONE)));
		// Obtiene lista de conceptos
		try {
			List<Sepconce> conces = sepconceDao.findAll();
			// Adiciona objetos
			for (Sepconce conce : conces) {
				// Adiciona objeto
				lsConcepts.add(new SelectItem(conce.getCosccosak(), conce
						.getCosdcosaf()));
			}
		} catch (DAOException e) {/* Error cargando conceptos */
			// Muestra mensaje
			this.processErrorMessage(e.getMessage());
			// Log
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
		}
	}

	/**
	 * Realiza la validacion de los campos
	 */
	@Override
	protected boolean validateFields(String action) {
		// Booleano para indicar que hubo error
		isError = false;
		// IRI Concepto
		if (this.isBlanks(object.getDcoiricaf())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0008",
							ApplicationMessages.getMessage("se010_iriDetail")));
		}
		// Concepto Semántico
		if (this.isBlanks(object.getCosccosak())) {
			isError = true;
			this.getErrorMessages().add(
					ApplicationMessages.getMessage("USR0008",
							ApplicationMessages
									.getMessage("se010_semanticConcept")));
		}
		// Valida relaciones
		for (Sedrelco relco : object.getRelcos()) {
			// Valor Relacion
			if (this.isBlanks(relco.getDrcvalraf())) {
				isError = true;
				this.getErrorMessages().add(
						ApplicationMessages.getMessage("USR0008",
								ApplicationMessages
										.getMessage("se010_valueRelation")));
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
		// Carga el subfile
		this.loadSubfile();
		// Concepto de busqueda
		this.codeConceptSearch = Constants.BLANKS;
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
		for (Sedrelco relco : object.getRelcos()) {
			// Asigna Id padre
			relco.getId().setDconsecnk(object.getDconsecnk());
			sedrelcoDao.save(relco);
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
		for (Sedrelco relco : object.getRelcos()) {
			sedrelcoDao.update(relco);
		}
	}

	/**
	 * Metodo para eliminar un registro Este metodo debe llamar al servicio o al
	 * DAO encargado de la operacion
	 */
	protected void deleteRecord() throws DAOException {
		// Elimina relaciones
		for (Sedrelco relco : object.getRelcos()) {
			sedrelcoDao.delete(relco);
		}
		// Elimina objeto
		objectDao.delete(object);
	};

	/**
	 * Inicia relaciones de concepto semantico
	 * 
	 * @param ev
	 *            Evento
	 */
	public void initRelations(ValueChangeEvent ev) {
		try {
			// Limpia lista de relaciones
			object.getRelcos().clear();
			// Obtiene valor del concepto seleccionado
			String cosccosak = ev.getNewValue().toString();
			// Obtiene valores de acuerdo al concepto
			List<Seprelco> relcos = seprelcoDao.findByConcept(cosccosak);
			// Recorre lista
			for (Seprelco relco : relcos) {
				// Inicia nuevo registro de sedrelco
				Sedrelco drelco = new Sedrelco();
				// Prepara objeto
				drelco.prepareObject();
				// Concepto
				drelco.setCosccosak(relco.getId().getCosccosak());
				drelco.setRcocrcoak(relco.getId().getRcocrcoak());
				// Descripcion relacion
				drelco.setRcodrcoaf(relco.getRcodrcoaf());
				// Adiciona objeto a la lista
				object.getRelcos().add(drelco);
			}
		} catch (Exception e) {/* Error */
			// Muestra mensaje
			this.processErrorMessage(e.getMessage());
			// Log
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
		}
	}

	/**
	 * @return the lsConcepts
	 */
	public List<SelectItem> getLsConcepts() {
		return lsConcepts;
	}

	/**
	 * @param lsConcepts
	 *            the lsConcepts to set
	 */
	public void setLsConcepts(List<SelectItem> lsConcepts) {
		this.lsConcepts = lsConcepts;
	}

	/**
	 * @return the codeConceptSearch
	 */
	public String getCodeConceptSearch() {
		return codeConceptSearch;
	}

	/**
	 * @param codeConceptSearch
	 *            the codeConceptSearch to set
	 */
	public void setCodeConceptSearch(String codeConceptSearch) {
		this.codeConceptSearch = codeConceptSearch;
	}

}
