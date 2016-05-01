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

import com.icesoft.faces.component.ext.HtmlDataTable;

import edu.upm.spbw.bbeans.base.AutenticationBean;
import edu.upm.spbw.bbeans.base.BaseBean;
import edu.upm.spbw.persistence.bo.Usdbloqu;
import edu.upm.spbw.persistence.bo.UsdbloquId;
import edu.upm.spbw.persistence.dao.IUsdbloquDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.DateUtils;
import edu.upm.spbw.utils.LogLogger;

/**
 * Bean para el servicio SE007 <br/>
 * SE007: Consulta Bloqueos de Usuario USDBLOQU
 * 
 * @author Juan Camilo
 */
public class SE007Bean extends BaseBean<Usdbloqu, UsdbloquId> {

	/**
	 * ID Serialización
	 */
	private static final long serialVersionUID = 1L;

	/***************************************
	 * ATRIBUTOS DEL BEAN
	 **************************************/

	/**
	 * Fecha Desde de busqueda
	 */
	private Date sinceDate;

	/**
	 * Fecha hasta de búsqueda
	 */
	private Date untilDate;

	/**
	 * Maximo de registros de query
	 */
	private int maxRecordsQuery;

	/**
	 * Lista de usuarios
	 */

	/**
	 * Constructor del Bean
	 */
	public SE007Bean() {
		super("SE007", false);
	}

	/**
	 * Inicia servicios
	 */
	@Override
	protected void initServices() {
		// DAO
		objectDao = appContext.getBean(IUsdbloquDAO.class);
		// Obtiene autentication bean
		AutenticationBean authBean = (AutenticationBean) this
				.getBackingBean(Constants.AUTENTICATION_BEAN);
		// Asigna maximo de registros por query
		if (authBean != null)
			this.maxRecordsQuery = authBean.getMaxRecordsQuery();
		// Lista de busqueda usuarios
		this.listDropdown = new ArrayList<SelectItem>();
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
			subfileList = ((IUsdbloquDAO) objectDao)
					.findByDateRange(
							sinceDate.getTime() - Constants.HOUR_MILIS,
							untilDate.getTime() + Constants.HOUR_MILIS,
							maxRecordsQuery);
			// Revisa si no hay nada
			if (subfileList.isEmpty()) {
				// No se encontraron registros
				this.processMessage(ApplicationMessages.getMessage("USR0009"));
			}
			// Lista de usuarios
			listDropdown.clear();
			// El seleccione
			listDropdown.add(new SelectItem(Constants.BLANKS,
					ApplicationMessages.getMessage(Constants.SELECCIONE)));
			// Obtiene usuarios registrados en la estructura
			List<Object[]> users = ((IUsdbloquDAO) objectDao).findUserCodes();
			// Recorre lista
			for (Object[] user : users) {
				// Adiciona objeto
				listDropdown.add(new SelectItem(user[0].toString(), user[1]
						.toString()
						+ Constants.BLANK_SPACE
						+ Constants.LEFT_PARENTHESIS
						+ user[0].toString()
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
		// Rango de fechas
		this.untilDate = DateUtils.getCurrentUtilDate();
		this.sinceDate = DateUtils.moveUtilDate(this.untilDate, (-1)
				* Constants.DAYS_IN_WEEK);
	}

	/**
	 * Realiza la validacion de los campos
	 */
	@Override
	protected boolean validateFields(String action) {
		// No se validan campos
		return true;
	}

	/**
	 * Busca registro
	 */
	@Override
	public void searchRecord(ActionEvent ev) {
		// Revisa el objeto a buscar
		if (!Constants.BLANKS.equals(searchField)) {
			// Limpiamos la lista
			subfileList.clear();
			// Adicionamos unicamente el registro buscado
			try {
				// Busca registros en la base de datos
				List<Usdbloqu> bloqus = ((IUsdbloquDAO) objectDao)
						.findByDateRangeAndUser(this.searchField,
								sinceDate.getTime() - Constants.HOUR_MILIS,
								untilDate.getTime() + Constants.HOUR_MILIS,
								maxRecordsQuery);
				// Si lo encuentra, adiciona en la lista
				if (bloqus.size() > 0) {/* Encontrado */
					// Adiciona en la lista
					for (Usdbloqu bloqu : bloqus) {
						subfileList.add(bloqu);
					}
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
		} else {/* No hay codigo de usuario */
			// Carga el subfile normal
			this.loadSubfile();
		}
		// Se posiciona en el seleccione
		this.searchField = Constants.BLANKS;
		// Rango de fechas
		this.untilDate = DateUtils.getCurrentUtilDate();
		this.sinceDate = DateUtils.moveUtilDate(this.untilDate, (-1)
				* Constants.DAYS_IN_WEEK);
		// Obtiene lista de subfile
		HtmlDataTable subfile = (HtmlDataTable) this
				.getComponentById(Constants.SUBFILE);
		// Pone en primera página
		subfile.setFirst(0);
	}

	/**
	 * @return the sinceDate
	 */
	public Date getSinceDate() {
		return sinceDate;
	}

	/**
	 * @param sinceDate
	 *            the sinceDate to set
	 */
	public void setSinceDate(Date sinceDate) {
		this.sinceDate = sinceDate;
	}

	/**
	 * @return the untilDate
	 */
	public Date getUntilDate() {
		return untilDate;
	}

	/**
	 * @param untilDate
	 *            the untilDate to set
	 */
	public void setUntilDate(Date untilDate) {
		this.untilDate = untilDate;
	}
}
