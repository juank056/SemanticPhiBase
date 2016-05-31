/**
 * 
 */
package edu.upm.spbw.bbeans.phi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.icefaces.util.JavaScriptRunner;

import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.selectinputtext.SelectInputText;

import edu.upm.spbw.bbeans.base.AutenticationBean;
import edu.upm.spbw.bbeans.base.BaseBean;
import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Sedconce;
import edu.upm.spbw.persistence.bo.Sedrelco;
import edu.upm.spbw.persistence.bo.Sepconce;
import edu.upm.spbw.persistence.bo.Seprelco;
import edu.upm.spbw.persistence.bo.SeprelcoId;
import edu.upm.spbw.persistence.bo.Usdlogqu;
import edu.upm.spbw.persistence.bo.UsdlogquId;
import edu.upm.spbw.persistence.dao.ISedconceDAO;
import edu.upm.spbw.persistence.dao.ISedrelcoDAO;
import edu.upm.spbw.persistence.dao.ISepconceDAO;
import edu.upm.spbw.persistence.dao.ISeprelcoDAO;
import edu.upm.spbw.persistence.dao.IUsdlogquDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.DateUtils;
import edu.upm.spbw.utils.LogLogger;

/**
 * Bean para el servicio SE011 <br/>
 * SE011: Consulta principal Semantic PHI-BASE
 * 
 * @author Juan Camilo
 */
public class SE011Bean extends BaseBean<Usdlogqu, UsdlogquId> {

	/**
	 * ID Serialización
	 */
	private static final long serialVersionUID = 1L;

	/***************************************
	 * ATRIBUTOS DEL BEAN
	 **************************************/

	/**
	 * DAO para leer SEPCONCE
	 */
	private ISepconceDAO sepconceDao;

	/**
	 * DAO para leer SEDCONCE
	 */
	private ISedconceDAO sedconceDao;

	/**
	 * DAO para leer SEPRELCO
	 */
	private ISeprelcoDAO seprelcoDao;

	/**
	 * DAO para leer SEDRELCO
	 */
	private ISedrelcoDAO sedrelcoDao;

	/**
	 * Arbol de conceptos y relaciones
	 */
	private DefaultTreeModel conceptsTree;

	/**
	 * Conceptos semanticos
	 */
	private List<Sepconce> conces;

	/**
	 * Lista de conceptos de busqueda
	 */
	private List<SearchConcept> searchConcepts;

	/**
	 * Lista de operadores logicos
	 */
	private List<LogicalOperatorConcept> operators;

	/**
	 * Lista de relaciones obtenidas con el autocompletar
	 */
	private List<SelectItem> relcos;

	/**
	 * Maximo de registros autocompletar
	 */
	private int maxRecordsAutocomplete;

	/**
	 * Maximo de registros de query
	 */
	private int maxRecordsQuery;

	/**
	 * Operador Logico de consulta inter-concepto
	 */
	private String interConceptOperator;

	/**
	 * Descripcion de la query
	 */
	private String queryDesc;

	/**
	 * Selected tab index
	 */
	private int selectedTabIndex;

	/**
	 * Lista de resultados de query
	 */
	private List<QueryResult> queryResults;

	/**
	 * Generador de Queries
	 */
	private QueryGenerator queryGenerator;

	/**
	 * Fecha Desde de busqueda
	 */
	private Date sinceDate;

	/**
	 * Fecha hasta de búsqueda
	 */
	private Date untilDate;

	/***********************************************
	 * INDICADORES PARA MOSTRAR O ESCONDER COLUMNAS
	 **********************************************/

	/**
	 * Show Interacciones
	 */
	private boolean show_interactions;

	/**
	 * Show Hosts
	 */
	private boolean show_hosts;

	/**
	 * Show Pathogens
	 */
	private boolean show_pathogens;

	/**
	 * Show Interaction Context Mutant Description
	 */
	private boolean show_intContextMutantDesc;

	/**
	 * Show Disease Names
	 */
	private boolean show_diseaseNames;

	/**
	 * Show Protocol Description
	 */
	private boolean show_protocolDescriptions;

	/**
	 * Show Citations
	 */
	private boolean show_citations;

	/**
	 * Show Alleles
	 */
	private boolean show_alleles;

	/**
	 * Show Invitro Growth
	 */
	private boolean show_invitroGrowths;

	/**
	 * Show Lethal Knockout
	 */
	private boolean show_lethalKnockouts;

	/**
	 * Show Genes
	 */
	private boolean show_genes;

	/**
	 * Show Gene - LocusId
	 */
	private boolean show_geneLocusIds;

	/**
	 * Show Gene - Function
	 */
	private boolean show_geneFunctions;

	/**
	 * Show Gene - Name
	 */
	private boolean show_geneNames;

	/**
	 * Show Gene - Accession
	 */
	private boolean show_geneAccessions;

	/**
	 * Show boton de retroceder
	 */
	private boolean show_backButton;

	/**
	 * URL del servicio de visualizacion
	 */
	private String visualizationService;

	/**
	 * Seleccionar columnas a desplegar
	 */
	private boolean selectColumns;

	/**
	 * Usuario en sesion
	 */
	private String usuemaiak;

	/**
	 * Tabs de seleccion
	 */
	private static final int TAB_SELECT = 0;
	// private static final int TAB_MY_QUERIES = 1;
	private static final int TAB_RESULTS = 2;
	private static final int TAB_GRAPH = 3;

	/**
	 * Indicador de visualizacion integrada
	 */
	private boolean visualizationIntegrated;

	/**
	 * Constructor del Bean
	 */
	public SE011Bean() {
		super("SE011", false);
	}

	/*************************************
	 * METODOS PROPIOS DEL BEAN
	 ************************************/

	/**
	 * Inicia servicios
	 */
	@Override
	protected void initServices() {
		// DAO
		objectDao = appContext.getBean(IUsdlogquDAO.class);
		sepconceDao = appContext.getBean(ISepconceDAO.class);
		sedconceDao = appContext.getBean(ISedconceDAO.class);
		seprelcoDao = appContext.getBean(ISeprelcoDAO.class);
		sedrelcoDao = appContext.getBean(ISedrelcoDAO.class);
		// Inicia lista de conceptos de busqueda
		this.searchConcepts = new ArrayList<>();
		// Lista de operadores
		this.operators = new ArrayList<>();
		// Lista de subfile
		this.subfileList = new ArrayList<>();
		// Autentication Bean
		AutenticationBean authBean = (AutenticationBean) this
				.getBackingBean(Constants.AUTENTICATION_BEAN);
		// Maximo de registros autocompletar
		this.maxRecordsAutocomplete = authBean.getMaxRecordsAutocomplete();
		// Maximo de registros query
		this.maxRecordsQuery = authBean.getMaxRecordsQuery();
		// Servicio de visualizacion
		this.visualizationService = authBean.getVisualizationService();
		// Visualizacion integrada
		this.visualizationIntegrated = authBean.isVisualizationIntegrated();
		// Usuario en sesion
		this.usuemaiak = authBean.getSessionUser().getUsuemaiak();
		// Operador Interconcepto en OR
		this.interConceptOperator = Constants.TWO;
		// Descripcion de Query
		this.queryDesc = Constants.BLANKS;
		// Tab el inicial
		selectedTabIndex = TAB_SELECT;
		// Lista de resultados de query
		this.queryResults = new ArrayList<>();
		// Indicadores de mostrar columnas
		this.show_interactions = true;
		this.show_hosts = true;
		this.show_pathogens = true;
		this.show_intContextMutantDesc = false;
		this.show_diseaseNames = false;
		this.show_protocolDescriptions = false;
		this.show_citations = false;
		this.show_alleles = false;
		this.show_invitroGrowths = false;
		this.show_lethalKnockouts = false;
		this.show_genes = false;
		this.show_geneLocusIds = false;
		this.show_geneFunctions = false;
		this.show_geneNames = false;
		this.show_geneAccessions = false;
		this.selectColumns = false;
		this.setShow_backButton(false);
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
			// Carga arbol de conceptos
			this.loadConceptsTree();
			// Carga lista de queries ejecutadas
			this.loadLastExecutedQueries();
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
		// Mostrar boton de retroceder
		this.setShow_backButton(false);
		// Revisa tamaño de la lista de autenticacion
		AutenticationBean authBean = (AutenticationBean) this
				.getBackingBean(Constants.AUTENTICATION_BEAN);
		if (authBean != null) {
			this.setShow_backButton(authBean.getExecutedServices().size() > 1);
		}
	}

	/**
	 * Realiza la validacion de los campos
	 */
	@Override
	protected boolean validateFields(String action) {
		// No hay validacion
		return true;
	}

	/**
	 * Busca registro
	 */
	@Override
	public void searchRecord(ActionEvent ev) {
		try {
			// Indicador de error en no
			this.isError = false;
			// Revisa que hayan conceptos
			if (this.searchConcepts.isEmpty()) {
				// Hay error
				this.isError = true;
				// Mensaje de error
				this.processErrorMessage(ApplicationMessages
						.getMessage("USR0035"));
			} else {/* Hay conceptos */
				// Recorre lista de conceptos de busqueda
				for (SearchConcept concept : this.searchConcepts) {
					// Revisa que haya un registro sedconce seleccionado
					if (concept.getConce() == null) {/* No seleccionado */
						if (Constants.BLANKS.equals(concept.getAutocomplete()
								.trim())) {
							// No hay texto autocomplete
							this.isError = true;
							this.processErrorMessage(ApplicationMessages
									.getMessage("USR0036",
											concept.getDescription()));
						} else {/* // Crea concepto ficticio */
							concept.setConce(new Sedconce(0, Constants.BLANKS,
									concept.getCosccosak(), concept
											.getAutocomplete().trim()));
						}
					}
				}
			}
			// Si hay error no continua
			if (this.isError) {
				// Asigna tab de seleccion filtro
				this.selectedTabIndex = TAB_SELECT;
				// Finaliza ejecución
				return;
			}
			// Lista de conceptos faltantes temporal
			List<LogicalOperatorConcept> tempConcepts = new ArrayList<>();
			// Valida que se encuentren todos los operadores logicos
			for (SearchConcept concept : this.searchConcepts) {
				// Revisa si se encuentra operador logico
				if (!this.operators.contains(new LogicalOperatorConcept(concept
						.getConce().getCosccosak(), concept.getConce()
						.getCosdcosaf(), Constants.ONE))) {
					// Busca el concepto original
					for (LogicalOperatorConcept operator : this.operators) {
						// Revisa si es el concepto
						if (operator.getCosccosak().equals(
								concept.getCosccosak())) {
							// Nuevo concepto
							tempConcepts.add(new LogicalOperatorConcept(concept
									.getConce().getCosccosak(), concept
									.getConce().getCosdcosaf(), operator
									.getOperator()));
						}
					}
				}
			}
			// Adiciona conceptos faltantes
			tempConcepts.addAll(operators);
			// Ejecuta query
			this.executeQuery(this.searchConcepts, tempConcepts,
					this.interConceptOperator);
			// Revisa si hay resultados
			if (this.queryResults.size() > 0) {
				// Finalmente Asigna tab de mostrar resultados
				this.selectedTabIndex = TAB_RESULTS;
			} else {/* No hay resultados */
				// No se encontraron registros
				this.processMessage(ApplicationMessages.getMessage("USR0009"));
			}
		} catch (Exception e) {/* Error Ejecutando query */
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			this.processErrorMessage(e.getMessage());
		}
	}

	/**
	 * Reinicia criterios de busqueda
	 * 
	 * @param ev
	 *            Evento
	 */
	public void restartCriteria(ActionEvent ev) {
		// Reinicia listas
		searchConcepts.clear();
		operators.clear();
		this.interConceptOperator = Constants.TWO;
		this.queryDesc = Constants.BLANKS;
	}

	/**
	 * Metodo para adicionar un concepto a la lista de busqueda
	 * 
	 * @param event
	 *            el evento a manejar
	 */
	public void addConceptToList(DragEvent event) {
		try {
			// Revisamos que tipo de evento es
			if (event.getEventType() == DndEvent.DROPPED) {
				if (((HtmlPanelGroup) event.getComponent()).getDragValue() != null) {
					// Obtiene nodo
					TreeUserObject node = ((TreeUserObject) ((HtmlPanelGroup) event
							.getComponent()).getDragValue());
					// Identificador dentro de la lista
					int identifier = searchConcepts.size() + 1;
					// Obtiene detalle de acuerdo al tipo
					String description = Constants.BLANKS;
					// Objetos
					Sepconce conce = null;
					String cosccosak = Constants.BLANKS, rcocrcoak = Constants.BLANKS;
					switch (node.getNodeType()) {
					case Constants.ONE:/* Concepto */
						// Busca concepto
						cosccosak = node.getNodeId();
						conce = sepconceDao.findById(cosccosak);
						// Asigna descripcion
						description = conce.getCosdcosaf();
						break;
					case Constants.TWO:
						// Llaves
						cosccosak = node.getNodeId().split(
								Constants.UNDER_SCORE)[0];
						rcocrcoak = node.getNodeId().split(
								Constants.UNDER_SCORE)[1];
						// Busca concepto
						conce = sepconceDao.findById(cosccosak);
						// Relacion
						Seprelco relco = seprelcoDao.findById(new SeprelcoId(
								cosccosak, rcocrcoak));
						// Descripcion
						description = conce.getCosdcosaf()
								+ Constants.BLANK_SPACE
								+ Constants.LEFT_PARENTHESIS
								+ relco.getRcodrcoaf()
								+ Constants.RIGHT_PARENTHESIS;
						break;
					}
					// Crea un nuevo search concept
					SearchConcept searchConcept = new SearchConcept(identifier,
							cosccosak, rcocrcoak, description,
							Constants.BLANKS, null);
					// Adiciona registro a la lista
					this.searchConcepts.add(searchConcept);
					// Adiciona concepto a los operadores
					this.addConceptToOperators(conce);
				}
			}
		} catch (Exception e) {/* Error cargando registros */
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			this.processErrorMessage(e.getMessage());
		}
	}

	/**
	 * Se encarga de mostrar el grafo de interaccion con un elemento
	 * seleccionado
	 * 
	 * @param ev
	 *            Evento
	 */
	public void showGraph(ActionEvent ev) {
		// Obtiene Query Result
		QueryResult queryResult = (QueryResult) ev.getComponent()
				.getAttributes().get(Constants.QUERY_RESULT);
		// Obtiene Query Value
		QueryValue queryValue = (QueryValue) ev.getComponent().getAttributes()
				.get(Constants.QUERY_VALUE);
		// Revisa si hay iri
		if (Constants.BLANKS.equals(queryValue.getObjectIri())) {
			// No hay IRI (No hay visualizacion)
			return;
		}
		// String de visualizacion
		String visCommand = "showPhiBaseGraph('&ServerURL', '&interactionType', '&interactionIRI','&elementType', '&elementIRI', &integrated);";
		// Reemplaza valores
		// Server URL
		visCommand = visCommand.replace("&ServerURL", visualizationService);
		// interactionType
		visCommand = visCommand.replace("&interactionType", queryResult
				.getInteractions().get(0).getType());
		// interactionIRI
		visCommand = visCommand.replace("&interactionIRI", queryResult
				.getInteractions().get(0).getObjectIri());
		// Server URL
		visCommand = visCommand.replace("&elementType", queryValue.getType());
		// Server URL
		visCommand = visCommand.replace("&elementIRI",
				queryValue.getObjectIri());
		// Visualizacion integrada
		visCommand = visCommand.replace("&integrated",
				visualizationIntegrated ? "true" : "false");
		// Llama funcion para visualizar el grafo
		JavaScriptRunner.runScript(FacesContext.getCurrentInstance(),
				visCommand);
		// Muestra tab de grafo
		this.selectedTabIndex = TAB_GRAPH;
	}

	/**
	 * Se encarga de expandir o contraer un nodo
	 * 
	 * @param event
	 *            Evento
	 */
	public void expandTree(ActionEvent event) {
		// Obtiene Node
		TreeUserObject node = (TreeUserObject) event.getComponent()
				.getAttributes().get(Constants.PARAMETER);
		if (!node.isLeaf())/* Solo lo cambia si no es hoja */
			// Asigna expanded
			node.setExpanded(!node.isExpanded());
	}

	/**
	 * Se encarga de eliminar registro de la busqueda
	 * 
	 * @param ev
	 *            Evento
	 */
	public void removeConcept(ActionEvent ev) {
		try {
			// Obtiene registro a eliminar
			SearchConcept concept = (SearchConcept) ev.getComponent()
					.getAttributes().get(Constants.PARAMETER);
			SearchConcept toDelete = null;
			// Busca concepto en la lista
			for (SearchConcept sconce : this.searchConcepts) {
				// Revisa si es
				if (concept.getIdentifier() == sconce.getIdentifier()) {
					toDelete = sconce;
					break;

				}
			}
			// Elimina elemento de la lista
			this.searchConcepts.remove(toDelete);
			// Elimina de la lista de operadores
			this.removeConceptFromOperators(toDelete.getCosccosak());
		} catch (Exception e) {/* Error cargando registros */
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			this.processErrorMessage(e.getMessage());
		}
	}

	/**
	 * Se encarga del evento de seleccionar un valor con el autocompletar
	 * 
	 * @param event
	 *            Evento
	 */
	public void selectAutocompleteValue(ValueChangeEvent event) {
		try {
			// Obtiene el concepto de busqueda
			SearchConcept concept = (SearchConcept) event.getComponent()
					.getAttributes().get(Constants.PARAMETER);
			// Obtiene el campo
			SelectInputText autoComplete = (SelectInputText) event
					.getComponent();
			// Obtiene el nuevo valor
			String newValue = (String) event.getNewValue();
			// Regenera lita de autocompletado
			relcos = generateAutocompleteList(concept, newValue);
			// Revisa si se ha seleccionado algun objeto
			if (autoComplete.getSelectedItem() != null) {
				Sedrelco relco = (Sedrelco) autoComplete.getSelectedItem()
						.getValue();
				// Busca registro de Sedconce padre
				Sedconce dconce = sedconceDao.findById(relco.getId()
						.getDconsecnk());
				// Adiciona concepto
				concept.setConce(dconce);
				// Icono de encontrado
				concept.setIcon(Constants.ICON_FOUND);
			} else {/* No seleccionado nada */
				concept.setConce(null);
				concept.setIcon(Constants.ICON_NOTFOUND);
			}
		} catch (Exception e) {/* Error cargando registros */
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			this.processErrorMessage(e.getMessage());
		}
	}

	/**
	 * Muestra popup de seleccion de columnas
	 * 
	 * @param ev
	 *            Evento
	 */
	public void showColumnSelection(ActionEvent ev) {
		this.selectColumns = true;
	}

	/**
	 * Esconde popup de seleccion de columnas
	 * 
	 * @param ev
	 *            Evento
	 */
	public void hideColumnSelection(ActionEvent ev) {
		this.selectColumns = false;
	}

	/**
	 * Se encarga de ejecutar la busqueda de queries
	 * 
	 * @param ev
	 *            Evento
	 */
	public void searchQueries(ActionEvent ev) {
		// Ejecuta busqueda
		try {
			// Busqueda
			this.loadLastExecutedQueries();
			// Rango de fechas
			this.untilDate = DateUtils.getCurrentUtilDate();
			this.sinceDate = DateUtils.moveUtilDate(this.untilDate, (-1)
					* Constants.DAYS_IN_WEEK);
		} catch (DAOException e) {/* Ocurrio error */
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			this.processErrorMessage(e.getMessage());
		}
	}

	/**
	 * Evento de cancelar
	 */
	@Override
	public void cancel(ActionEvent e) {
		// Super
		super.cancel(e);
		// Esconde columnas
		this.hideColumnSelection(e);
	}

	/**
	 * Se encarga de cargar los parametros de ejecucion de una query
	 * 
	 * @param ev
	 *            Evento
	 */
	public void loadQueryParameters(ActionEvent ev) {
		try {
			// Tab de seleccion de filtro
			this.selectedTabIndex = TAB_SELECT;
			// Obtiene registro USDLOGQU
			this.object = (Usdlogqu) ev.getComponent().getAttributes()
					.get(Constants.BEAN_PARAM01);
			// Ejecuta evento de restart criteria
			this.restartCriteria(ev);
			// Descripcion de query
			this.queryDesc = this.object.getLqudescaf();
			// Obtiene parametros de ejecucion de query
			String[] params = this.object.getLquparqaf().split(
					Constants.PARAM_SEP);
			// 1.InterOperator
			this.interConceptOperator = params[0];
			// 2.Operadores
			String[] operators = params[1].split(Constants.COMMA);
			// Recorre operadores
			for (int i = 0; i < operators.length; i++) {
				// Codigo de operador
				String code = operators[i].split(Constants.EQUAL)[0];
				// Operador
				String operator = operators[i].split(Constants.EQUAL)[1];
				// Objeto sepconce
				Sepconce conce = sepconceDao.findById(code);
				// Crea Logical operator concept y lo adiciona
				this.operators.add(new LogicalOperatorConcept(code, conce
						.getCosdcosaf(), operator));
			}
			// 3. Conceptos
			String[] concepts = params[2].split(Constants.MAIL_SEP);
			// Recorre conceptos
			for (int i = 0; i < concepts.length; i++) {
				// Contador
				int cont = 0;
				// Cosccosak
				String cosccosak = concepts[i].split(Constants.PARAM_SEP_2)[cont++];
				// rcocrcoak
				String rcocrcoak = concepts[i].split(Constants.PARAM_SEP_2)[cont++];
				// description
				String description = concepts[i].split(Constants.PARAM_SEP_2)[cont++];
				// autocomplete
				String autocomplete = concepts[i].split(Constants.PARAM_SEP_2)[cont++];
				// IRI
				String iri = concepts[i].split(Constants.PARAM_SEP_2)[cont++];
				// Icon
				String icon = concepts[i].split(Constants.PARAM_SEP_2)[cont++];
				// Conce
				Sedconce conce = null;
				// Revisa si hay IRI
				if (!Constants.BLANKS.equals(iri)) {
					// Busca objeto por IRI
					conce = sedconceDao.findByIRI(iri);
				}
				// SearchConcept
				SearchConcept sc = new SearchConcept(i + 1, cosccosak,
						rcocrcoak, description, autocomplete, conce);
				// Icono
				sc.setIcon(icon);
				// Adiciona concepto de busqueda
				searchConcepts.add(sc);
			}
		} catch (DAOException e) {/* Ocurrio error */
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			this.processErrorMessage(e.getMessage());
		}
	}

	/**
	 * Se encarga de ejecutar una query y visualizar los resultados obtenidos
	 * 
	 * @param ev
	 *            Evento
	 */
	public void executeQueryAndViewResults(ActionEvent ev) {
		try {
			// Obtiene registro USDLOGQU
			this.object = (Usdlogqu) ev.getComponent().getAttributes()
					.get(Constants.BEAN_PARAM01);
			// Contador
			int cont = 0;
			// Numeros de query
			String[] numbers = this.object.getLqurespaf()
					.split(Constants.COMMA);
			// Lanza ejecucion de query
			this.queryResults = queryGenerator.executeQuery(usuemaiak,
					this.object.getLqutextaf(), this.object.getLquparqaf(),
					this.object.getLqudescaf(),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]),
					Integer.valueOf(numbers[cont++]));
			// Revisa si hay resultados
			if (this.queryResults.size() > 0) {
				// Finalmente Asigna tab de mostrar resultados
				this.selectedTabIndex = TAB_RESULTS;
			} else {/* No hay resultados */
				// No se encontraron registros
				this.processMessage(ApplicationMessages.getMessage("USR0009"));
			}
		} catch (DAOException e) {/* Ocurrio error */
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
			this.processErrorMessage(e.getMessage());
		}
	}

	/*************************************
	 * METODOS PRIVADOS
	 ************************************/

	/**
	 * Carga lista de ultimas queries ejecutadas
	 * 
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	private void loadLastExecutedQueries() throws DAOException {
		// Limpia lista
		this.subfileList.clear();
		// DAO USDLOGQU
		IUsdlogquDAO usdlogquDao = (IUsdlogquDAO) this.objectDao;
		// Lista de queries ejecutadas
		this.subfileList = usdlogquDao.findByDateRangeAndUser(usuemaiak,
				sinceDate, untilDate, maxRecordsQuery);
	}

	/**
	 * Carga arbol de conceptos
	 * 
	 * @throws Exception
	 *             Error cargando arbol
	 */
	private void loadConceptsTree() throws Exception {
		/*
		 * Se encarga de cargar el arbol de conceptos para la seleccion del
		 * filtro de busqueda
		 */
		// Inicia Raiz del arbol
		DefaultMutableTreeNode rootTreeNode = this.createNode(Constants.ZERO,
				Constants.ROOT_NODE, Constants.ROOT_NODE, Constants.ONE, true);
		this.setConceptsTree(new DefaultTreeModel(rootTreeNode));
		// Obtiene todos los registros de Sepconce
		conces = sepconceDao.findAll();
		// Query Generator
		this.queryGenerator = new QueryGenerator(conces);
		// Recorre y procesa los registros que no tienen padre
		for (Sepconce conce : conces) {
			// Revisa si tiene padre
			if (!Constants.BLANKS.equals(conce.getCosccopak())) {
				// Continua con siguiente registro
				continue;
			}
			// Crea el nodo del arbol
			DefaultMutableTreeNode node = this.createNode(conce.getCosccosak(),
					conce.getCosdcosaf(), conce.getCosdocuaf(), Constants.ONE,
					false);
			// Crea nodos hijos del nodo padre
			createChildrenNodes(node, conce, conces);
			// Adicionamos el nodo a la raiz del arbol
			rootTreeNode.add(node);
		}
		this.setConceptsTree(new DefaultTreeModel(rootTreeNode));
	}

	/**
	 * Adiciona un nuevo elemento de operador en la lista de elementos
	 * 
	 * @param conce
	 *            Concepto a adicionar en la lista
	 */
	private void addConceptToOperators(Sepconce conce) {
		// Registro de Operador Logico
		LogicalOperatorConcept logical = new LogicalOperatorConcept(
				conce.getCosccosak(), conce.getCosdcosaf(), Constants.TWO);
		// Lo adiciona si no estaba ya
		if (!this.operators.contains(logical)) {/* No existe */
			// Adiciona elemento
			this.operators.add(logical);
		}
	}

	/**
	 * Elimina concepto de la lista de operadores si es posible
	 * 
	 * @param cosccosak
	 *            Codigo de concepto a eliminar
	 */
	private void removeConceptFromOperators(String cosccosak) {
		// Registro de Operador Logico
		LogicalOperatorConcept logical = new LogicalOperatorConcept(cosccosak,
				Constants.BLANKS, Constants.TWO);
		// Busca en la lista de conceptos de busqueda por alguno con el mismo
		// concepto
		// Indicador de existe
		boolean exists = false;
		for (SearchConcept searchConcept : this.searchConcepts) {
			// Revisa si tiene el mismo concepto
			if (cosccosak.equals(searchConcept.getCosccosak())) {
				// Existe el concepto
				exists = true;
				break;
			}
		}
		// Si no existe, lo elimina
		if (!exists) {
			this.operators.remove(logical);
		}
	}

	/**
	 * Genera lista de autocompletar para un concepto dado
	 * 
	 * @param concept
	 *            Concepto semantico dado
	 * @param value
	 *            Valor de String de busqueda
	 * @return Lista de conceptos generados
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	private List<SelectItem> generateAutocompleteList(SearchConcept concept,
			String value) throws DAOException {
		// Obtiene registros hijos del concepto
		List<String> childConcepts = new ArrayList<>();
		// Llena lista recursivamente
		findChildConcepts(concept.getCosccosak(), childConcepts);
		// Ejecuta query de cargue de conceptos
		List<Sedrelco> lsRelcos = sedrelcoDao.findByConceptAndDescription(
				childConcepts, concept.getRcocrcoak(), value,
				maxRecordsAutocomplete);
		// Lista de relcos
		relcos = new ArrayList<>();
		// Adiciona elementos en la lista
		for (Sedrelco relco : lsRelcos) {
			// Prepara objeto
			relco.prepareObject();
			// Crea nuevo select Item
			relcos.add(new SelectItem(relco, relco.getDrcvalraf()));
		}
		// Retorna lista
		return relcos;
	}

	/**
	 * Busca registros hijos de un concepto dado
	 * 
	 * @param cosccosak
	 *            Concepto dado
	 * @param childConcepts
	 *            Lita en donde se iran cargando los registros
	 */
	private void findChildConcepts(String cosccosak, List<String> childConcepts) {
		// Se adiciona el mismo a la lista
		childConcepts.add(cosccosak);
		// Recorre lista en busca de los hijos
		for (Sepconce conce : conces) {
			// Revisa si es hijo
			if (cosccosak.equals(conce.getCosccopak())) {
				// Procesa hijo
				findChildConcepts(conce.getCosccosak(), childConcepts);
			}
		}
	}

	/**
	 * Se encarga de crear los nodos hijos de un nodo padre
	 * 
	 * @param pnode
	 *            Nodo Enviado de parametro
	 * @param pconce
	 *            Registro de concepto al que se le adicionaran los hijos
	 * @param conces
	 *            Lista con todos los conceptos cargados
	 */
	private void createChildrenNodes(DefaultMutableTreeNode pnode,
			Sepconce pconce, List<Sepconce> conces) {
		// Recorre lista para obtener los conceptos hijos
		for (Sepconce conce : conces) {
			// Revisa si el concepto es hijo del recibido
			if (!pconce.getCosccosak().equals(conce.getCosccopak())) {
				// No es hijo (continua con siguiente nodo)
				continue;
			}
			// Crea el nodo del arbol
			DefaultMutableTreeNode node = this.createNode(conce.getCosccosak(),
					conce.getCosdcosaf(), conce.getCosdocuaf(), Constants.ONE,
					false);
			// Crea nodos hijos del nodo padre
			createChildrenNodes(node, conce, conces);
			// Adicionamos el nodo al nodo recibido
			pnode.add(node);
		}
		// Adiciona nodos de relaciones
		for (Seprelco relco : pconce.getRelcos()) {
			// Crea el nodo del arbol
			DefaultMutableTreeNode leaf = this.createNode(relco.getId()
					.getCosccosak()
					+ Constants.UNDER_SCORE
					+ relco.getId().getRcocrcoak(), relco.getRcodrcoaf(), relco
					.getRcodocuaf(), Constants.TWO, false);
			// Se adiciona nodo al padre
			pnode.add(leaf);
		}
	}

	/**
	 * Metodo para crear un nodo de un arbol
	 * 
	 * @param id
	 *            el id del nodo del arbol
	 * @param name
	 *            Nombre del nodo
	 * @param toolTip
	 *            Tooltip del nodo
	 * @param typeNode
	 *            el tipo de nodo a crear: '1' Concepto '2' Relación Concepto
	 * @param expanded
	 *            Indicador de nodo expandido o no
	 * @return El TreeNode
	 */
	private DefaultMutableTreeNode createNode(String id, String name,
			String toolTip, String typeNode, boolean expanded) {
		// Crea el tree node
		DefaultMutableTreeNode branchNode = new DefaultMutableTreeNode();
		// Crea el objeto
		TreeUserObject branchObject = new TreeUserObject(branchNode);
		// Expanded
		branchObject.setExpanded(expanded);
		// Si es relacion es hoja
		if (Constants.TWO.equals(typeNode)) {
			branchObject.setLeaf(true);
			branchNode.setAllowsChildren(false);
		} else {/* Es concepto o Root */
			branchObject.setLeaf(false);
			branchNode.setAllowsChildren(true);
		}
		// Asigna identificador del nodo
		branchObject.setNodeId(id);
		// Tipo de nodo
		branchObject.setNodeType(typeNode);
		// Texto del servicio o menu
		branchObject.setText(name);
		// Tooltip
		branchObject.setTooltip(toolTip);
		// Imagen para la hoja
		if (typeNode.equals(Constants.ONE)) {/* Concepto */
			branchObject.setLeafIcon(Constants.PATH_FOLDER_CLOSE);
			branchObject.setBranchContractedIcon(Constants.PATH_FOLDER_CLOSE);
			branchObject.setBranchExpandedIcon(Constants.PATH_FOLDER_OPEN);
		} else {/* Relacion concepto */
			branchObject.setLeafIcon(Constants.PATH_LEAF);
			branchObject.setBranchContractedIcon(Constants.PATH_LEAF);
			branchObject.setBranchExpandedIcon(Constants.PATH_LEAF);
		}
		// Asigna el userObject creado a la rama del arbol
		branchNode.setUserObject(branchObject);
		return branchNode;
	}

	/**
	 * Genera y Ejecuta Query SPARQL de acuerdo al criterio de busqueda
	 * seleccionado
	 * 
	 * @param concepts
	 *            Conceptos de busqueda a tener en cuenta
	 * @param operators
	 *            Relación entre los conceptos de búsqueda
	 * @param interOperator
	 *            Operador interconceptos
	 * @throws DAOException
	 *             Error ejecutando query
	 */
	private void executeQuery(List<SearchConcept> concepts,
			List<LogicalOperatorConcept> operators, String interOperator)
			throws DAOException {
		// Bean de autenticacion
		AutenticationBean authBean = (AutenticationBean) this
				.getBackingBean(Constants.AUTENTICATION_BEAN);
		// Ejecuta Query
		this.queryResults = queryGenerator.executeQuery(authBean
				.getSessionUser().getUsuemaiak(), concepts, operators,
				this.operators, interOperator, !Constants.BLANKS
						.equals(this.queryDesc) ? this.queryDesc
						: ApplicationMessages.getMessage("USR0037"));
	}

	/*************************************
	 * GETTERS Y SETTERS
	 ************************************/

	/**
	 * @return the conceptsTree
	 */
	public DefaultTreeModel getConceptsTree() {
		return conceptsTree;
	}

	/**
	 * @param conceptsTree
	 *            the conceptsTree to set
	 */
	public void setConceptsTree(DefaultTreeModel conceptsTree) {
		this.conceptsTree = conceptsTree;
	}

	/**
	 * @return the searchConcepts
	 */
	public List<SearchConcept> getSearchConcepts() {
		return searchConcepts;
	}

	/**
	 * @param searchConcepts
	 *            the searchConcepts to set
	 */
	public void setSearchConcepts(List<SearchConcept> searchConcepts) {
		this.searchConcepts = searchConcepts;
	}

	/**
	 * @return the relcos
	 */
	public List<SelectItem> getRelcos() {
		return relcos;
	}

	/**
	 * @param relcos
	 *            the relcos to set
	 */
	public void setRelcos(List<SelectItem> relcos) {
		this.relcos = relcos;
	}

	/**
	 * Obtiene operadores logicos
	 * 
	 * @return Lita de operadores logicos
	 */
	public SelectItem[] getLogicalOperators() {
		return new SelectItem[] {
				new SelectItem(Constants.ONE,
						ApplicationMessages.getMessage("se011_andOperator")),
				new SelectItem(Constants.TWO,
						ApplicationMessages.getMessage("se011_orOperator")) };
	}

	/**
	 * @return the interConceptOperator
	 */
	public String getInterConceptOperator() {
		return interConceptOperator;
	}

	/**
	 * @param interConceptOperator
	 *            the interConceptOperator to set
	 */
	public void setInterConceptOperator(String interConceptOperator) {
		this.interConceptOperator = interConceptOperator;
	}

	/**
	 * @return the operators
	 */
	public List<LogicalOperatorConcept> getOperators() {
		return operators;
	}

	/**
	 * @param operators
	 *            the operators to set
	 */
	public void setOperators(List<LogicalOperatorConcept> operators) {
		this.operators = operators;
	}

	/**
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * @param selectedTabIndex
	 *            the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	/**
	 * @return the queryResults
	 */
	public List<QueryResult> getQueryResults() {
		return queryResults;
	}

	/**
	 * @param queryResults
	 *            the queryResults to set
	 */
	public void setQueryResults(List<QueryResult> queryResults) {
		this.queryResults = queryResults;
	}

	/**
	 * @return the show_interactions
	 */
	public boolean isShow_interactions() {
		return show_interactions;
	}

	/**
	 * @param show_interactions
	 *            the show_interactions to set
	 */
	public void setShow_interactions(boolean show_interactions) {
		this.show_interactions = show_interactions;
	}

	/**
	 * @return the show_hosts
	 */
	public boolean isShow_hosts() {
		return show_hosts;
	}

	/**
	 * @param show_hosts
	 *            the show_hosts to set
	 */
	public void setShow_hosts(boolean show_hosts) {
		this.show_hosts = show_hosts;
	}

	/**
	 * @return the show_pathogens
	 */
	public boolean isShow_pathogens() {
		return show_pathogens;
	}

	/**
	 * @param show_pathogens
	 *            the show_pathogens to set
	 */
	public void setShow_pathogens(boolean show_pathogens) {
		this.show_pathogens = show_pathogens;
	}

	/**
	 * @return the show_intContextMutantDesc
	 */
	public boolean isShow_intContextMutantDesc() {
		return show_intContextMutantDesc;
	}

	/**
	 * @param show_intContextMutantDesc
	 *            the show_intContextMutantDesc to set
	 */
	public void setShow_intContextMutantDesc(boolean show_intContextMutantDesc) {
		this.show_intContextMutantDesc = show_intContextMutantDesc;
	}

	/**
	 * @return the show_diseaseNames
	 */
	public boolean isShow_diseaseNames() {
		return show_diseaseNames;
	}

	/**
	 * @param show_diseaseNames
	 *            the show_diseaseNames to set
	 */
	public void setShow_diseaseNames(boolean show_diseaseNames) {
		this.show_diseaseNames = show_diseaseNames;
	}

	/**
	 * @return the show_protocolDescriptions
	 */
	public boolean isShow_protocolDescriptions() {
		return show_protocolDescriptions;
	}

	/**
	 * @param show_protocolDescriptions
	 *            the show_protocolDescriptions to set
	 */
	public void setShow_protocolDescriptions(boolean show_protocolDescriptions) {
		this.show_protocolDescriptions = show_protocolDescriptions;
	}

	/**
	 * @return the show_citations
	 */
	public boolean isShow_citations() {
		return show_citations;
	}

	/**
	 * @param show_citations
	 *            the show_citations to set
	 */
	public void setShow_citations(boolean show_citations) {
		this.show_citations = show_citations;
	}

	/**
	 * @return the show_alleles
	 */
	public boolean isShow_alleles() {
		return show_alleles;
	}

	/**
	 * @param show_alleles
	 *            the show_alleles to set
	 */
	public void setShow_alleles(boolean show_alleles) {
		this.show_alleles = show_alleles;
	}

	/**
	 * @return the show_invitroGrowths
	 */
	public boolean isShow_invitroGrowths() {
		return show_invitroGrowths;
	}

	/**
	 * @param show_invitroGrowths
	 *            the show_invitroGrowths to set
	 */
	public void setShow_invitroGrowths(boolean show_invitroGrowths) {
		this.show_invitroGrowths = show_invitroGrowths;
	}

	/**
	 * @return the show_lethalKnockouts
	 */
	public boolean isShow_lethalKnockouts() {
		return show_lethalKnockouts;
	}

	/**
	 * @param show_lethalKnockouts
	 *            the show_lethalKnockouts to set
	 */
	public void setShow_lethalKnockouts(boolean show_lethalKnockouts) {
		this.show_lethalKnockouts = show_lethalKnockouts;
	}

	/**
	 * @return the show_genes
	 */
	public boolean isShow_genes() {
		return show_genes;
	}

	/**
	 * @param show_genes
	 *            the show_genes to set
	 */
	public void setShow_genes(boolean show_genes) {
		this.show_genes = show_genes;
	}

	/**
	 * @return the show_geneLocusIds
	 */
	public boolean isShow_geneLocusIds() {
		return show_geneLocusIds;
	}

	/**
	 * @param show_geneLocusIds
	 *            the show_geneLocusIds to set
	 */
	public void setShow_geneLocusIds(boolean show_geneLocusIds) {
		this.show_geneLocusIds = show_geneLocusIds;
	}

	/**
	 * @return the show_geneFunctions
	 */
	public boolean isShow_geneFunctions() {
		return show_geneFunctions;
	}

	/**
	 * @param show_geneFunctions
	 *            the show_geneFunctions to set
	 */
	public void setShow_geneFunctions(boolean show_geneFunctions) {
		this.show_geneFunctions = show_geneFunctions;
	}

	/**
	 * @return the show_geneNames
	 */
	public boolean isShow_geneNames() {
		return show_geneNames;
	}

	/**
	 * @param show_geneNames
	 *            the show_geneNames to set
	 */
	public void setShow_geneNames(boolean show_geneNames) {
		this.show_geneNames = show_geneNames;
	}

	/**
	 * @return the show_geneAccessions
	 */
	public boolean isShow_geneAccessions() {
		return show_geneAccessions;
	}

	/**
	 * @param show_geneAccessions
	 *            the show_geneAccessions to set
	 */
	public void setShow_geneAccessions(boolean show_geneAccessions) {
		this.show_geneAccessions = show_geneAccessions;
	}

	/**
	 * @return the selectColumns
	 */
	public boolean isSelectColumns() {
		return selectColumns;
	}

	/**
	 * @param selectColumns
	 *            the selectColumns to set
	 */
	public void setSelectColumns(boolean selectColumns) {
		this.selectColumns = selectColumns;
	}

	/**
	 * @return the sinceDate
	 */
	public Date getSinceDate() {
		return sinceDate;
	}

	/**
	 * @return the untilDate
	 */
	public Date getUntilDate() {
		return untilDate;
	}

	/**
	 * @param sinceDate
	 *            the sinceDate to set
	 */
	public void setSinceDate(Date sinceDate) {
		this.sinceDate = sinceDate;
	}

	/**
	 * @param untilDate
	 *            the untilDate to set
	 */
	public void setUntilDate(Date untilDate) {
		this.untilDate = untilDate;
	}

	/**
	 * @return the queryDesc
	 */
	public String getQueryDesc() {
		return queryDesc;
	}

	/**
	 * @param queryDesc
	 *            the queryDesc to set
	 */
	public void setQueryDesc(String queryDesc) {
		this.queryDesc = queryDesc;
	}

	/**
	 * @return the show_backButton
	 */
	public boolean isShow_backButton() {
		// Ejecuta autenticacion de usuario
		this.isUserNOTAuthenticatedAndAuthorized();
		// Mostrar boton de retroceder
		this.show_backButton = false;
		// Revisa tamaño de la lista de autenticacion
		AutenticationBean authBean = (AutenticationBean) this
				.getBackingBean(Constants.AUTENTICATION_BEAN);
		if (authBean != null) {
			this.show_backButton = authBean.getExecutedServices().size() > 1;
		}
		return show_backButton;
	}

	/**
	 * @param show_backButton
	 *            the show_backButton to set
	 */
	public void setShow_backButton(boolean show_backButton) {
		this.show_backButton = show_backButton;
	}

	/**
	 * @return the visualizationIntegrated
	 */
	public boolean isVisualizationIntegrated() {
		return visualizationIntegrated;
	}

	/**
	 * @param visualizationIntegrated
	 *            the visualizationIntegrated to set
	 */
	public void setVisualizationIntegrated(boolean visualizationIntegrated) {
		this.visualizationIntegrated = visualizationIntegrated;
	}
}
