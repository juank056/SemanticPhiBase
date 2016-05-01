/**
 * 
 */
package edu.upm.spbw.bbeans.phi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Sepconce;
import edu.upm.spbw.persistence.bo.Usdlogqu;
import edu.upm.spbw.persistence.bo.UsdlogquId;
import edu.upm.spbw.persistence.dao.IUsdlogquDAO;
import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.DateUtils;
import edu.upm.spbw.utils.SparqlQueryExecutor;
import edu.upm.spbw.utils.SpringAppContext;

/**
 * Se encarga de generar y ejecutar las Queries de consulta a Semantic PHI-BASE
 * 
 * @author JuanCamilo
 * 
 */
public class QueryGenerator {

	/**
	 * DAO par trabajar con USDLOGQU
	 */
	private IUsdlogquDAO usdlogquDao;

	/**
	 * Mapa de conceptos semanticos
	 */
	private Map<String, Sepconce> conceptsMap;

	/**
	 * Nombres de variables
	 */
	// Separador punto
	private static final String A = " a ";
	// Separador punto
	private static final String DOT = " . \n";
	// And
	private static final String AND = " && ";
	// Or
	private static final String OR = " || ";
	// Igual
	private static final String EQ = " = ";
	// Interaction
	private static final String INT = " ?int";
	private static final String INTL = " ?int_l";
	private static final String INTRDF = "PHIO:PHIBO_00022";
	// Host
	private static final String HOST = " ?host_";
	private static final String HOSTL = " ?host_l_";
	// Pathogen
	private static final String PATHCX = " ?path_context ";
	private static final String PATH = " ?path_";
	private static final String PATHL = " ?path_l_";
	// Interaction Context Mutant (Ddescription)
	private static final String INTCX = " ?intcont ";
	private static final String INCON = " ?incon_";
	private static final String INCONL = " ?incon_l_";
	// Disease Name
	private static final String DISN = " ?disn_";
	private static final String DISNL = " ?disn_l_";
	// Protocol Description
	private static final String PROT = " ?prot ";
	private static final String PROTD = " ?protd_";
	private static final String PROTDL = " ?protd_l_";
	// Citation
	private static final String CITA = " ?cita_";
	private static final String CITAL = " ?cita_l_";
	// Allele
	private static final String ALLE = " ?alle_";
	private static final String ALLEL = " ?alle_l_";
	// Lethal Knockout
	private static final String LETH = " ?leth_";
	private static final String LETHL = " ?leth_l_";
	// Invitro Growth
	private static final String INVG = " ?invg_";
	private static final String INVGL = " ?invg_l_";
	// Gene
	private static final String GENE = " ?gene_";
	private static final String GENEL = " ?gene_l_";
	// Locus ID
	private static final String LOCU = " ?locu_";
	private static final String LOCUL = " ?locu_l_";
	// Gene Function
	private static final String GFUN = " ?gfun_";
	private static final String GFUNL = " ?gfun_l_";
	// Gene Name
	private static final String GNAM = " ?gnam_";
	private static final String GNAML = " ?gnam_l_";
	// Gene Accession
	private static final String GACC = " ?gacc_";
	private static final String GACCL = " ?gacc_l_";

	/**
	 * Relaciones
	 */
	private static final String HAS_PARTICIPANT = " PHIO:has_participant ";
	private static final String HAS_QUALITY = " PHIO:has_quality ";
	private static final String HAS_VALUE = " PHIO:has_value ";
	private static final String IS_DESCRIBED_BY = " PHIO:is_described_by ";
	private static final String HAS_UNIQUE_ID = " PHIO:has_unique_identifier ";
	private static final String RDFS_LABEL = " rdfs:label ";
	private static final String IS_VARIANT_OF = " PHIO:is_variant_of ";
	private static final String CITATION = " <http://schema.org/citation> ";

	/**
	 * Opional y corchetes
	 */
	private static final String OPTIONAL = "OPTIONAL{ \n";
	private static final String L_PAR = "(";
	private static final String R_PAR = ")";
	private static final String R_KEY = "}\n";
	private static final String SPACE = "\n";

	/**
	 * Para el Contains
	 */
	private static final String CONTAINS = " CONTAINS(?variable, \"?autocomplete\") ";
	private static final String VARIABLE = "?variable";
	private static final String AUTOCOMPLETE = "?autocomplete";

	/**
	 * Ejecutor de Query sparql
	 */
	private SparqlQueryExecutor queryExecutor;

	/**
	 * Constructor
	 * 
	 * @param concepts
	 *            Lista de los conceptos semanticos parametrizados
	 * @throws Exception
	 */
	public QueryGenerator(List<Sepconce> concepts) throws Exception {
		// Inicia DAO
		this.usdlogquDao = SpringAppContext.getAppContext().getBean(
				IUsdlogquDAO.class);
		// Inicia mapa de conceptos
		conceptsMap = new HashMap<>();
		// Recorre lista recibida
		for (Sepconce concept : concepts) {
			// Adiciona concepto a mapa
			conceptsMap.put(concept.getCosccosak(), concept);
		}
		// Query Executor
		this.queryExecutor = new SparqlQueryExecutor();
	}

	/**
	 * Se encarga de generar y de ejecutar una query
	 * 
	 * @param usuemaiak
	 *            Usuario que esta ejecutando la query
	 * @param concepts
	 *            Conceptos de busqueda de la query
	 * @param operators
	 *            Operadores de intra query
	 * @param operatorsOriginal
	 *            Lista de operadores original (para generar parametros de
	 *            ejecucion)
	 * @param interOperator
	 *            Operador de interQuery
	 * @param queryDesc
	 *            Descripcion de la query
	 * @return Lista de resultados de ejecucion
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<QueryResult> executeQuery(String usuemaiak,
			List<SearchConcept> concepts,
			List<LogicalOperatorConcept> operators,
			List<LogicalOperatorConcept> operatorsOriginal,
			String interOperator, String queryDesc) throws DAOException {
		// Numero de variables por concepto
		int nHost = 0, nPath = 0, nIncon = 0, nDisn = 0, nProtd = 0, nCita = 0, nAlle = 0;
		int nLeth = 0, nInvg = 0, nGene = 0, nLocu = 0, nGfun = 0, nGnam = 0, nGacc = 0;
		// Indicadores de opcional
		boolean opCita = true, opLeth = true, opInvg = true, opLocu = true, opGfun = true;
		boolean opGnam = true, opGacc = true;
		// InterOperador
		String interOpValue = Constants.ONE.equals(interOperator) ? AND : OR;
		// Variable de intra operator
		String intraOp;
		// Mapa de operadores
		Map<String, LogicalOperatorConcept> opeMap = new HashMap<>();
		// Poblacion del mapa
		for (LogicalOperatorConcept operator : operators) {
			// Revisa que encuentre el concepto en la lista de busqueda
			boolean found = false;
			for (SearchConcept concept : concepts) {
				// Revisa si es el mismo concepto
				if (concept.getConce().getCosccosak()
						.equals(operator.getCosccosak())) {
					// Encontrado
					found = true;
				}
			}
			// Adiciona elemento si ha sido encontrado
			if (found)
				opeMap.put(operator.getCosccosak(), operator);
		}
		// Variables
		String variables = "SELECT DISTINCT" + INT + INTL;
		// Query content
		String content = " WHERE { \n ?int a PHIO:PHIBO_00022 . "
				+ "\n ?int rdfs:label ?int_l . "
				+ "\n ?intcont a PHIO:PHIBO_00076 . "
				+ "\n ?int PHIO:is_manifested_as ?intcont . "
				+ "\n ?prot a SIO:SIO_000747 . "
				+ "\n ?intcont PHIO:is_output_of ?prot . "
				+ "\n ?intcont PHIO:depends_on ?path_context . \n\n";
		// Filter
		String filter = " FILTER(";
		// Variable de si ya hay filtro
		boolean isFilter = false;
		// Contador
		int cont = 0;
		/**
		 * 1. HOST
		 */
		// Revisa si hay operador
		LogicalOperatorConcept operator = opeMap.get(Constants.K01);
		if (operator != null) {/* Operador encontrado */
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K01.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nHost++;
						// Variables
						variables += HOST + nHost + HOSTL + nHost;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ HOST + nHost + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, HOSTL + nHost)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un host
			nHost = 1;
			variables += HOST + Constants.ONE + HOSTL + Constants.ONE;
		}
		// Contenido de acuerdo al numero de hosts
		for (int i = 1; i <= nHost; i++) {
			// Rdf type
			content += HOST + i + A
					+ conceptsMap.get(Constants.K01).getCosrdftaf() + DOT;
			// Label
			content += HOST + i + RDFS_LABEL + HOSTL + i + DOT;
			// Has Participant
			content += INT + HAS_PARTICIPANT + HOST + i + DOT;
		}
		// Space
		content += SPACE;
		/**
		 * 2. PATHOGEN
		 */
		// Revisa si hay operador
		operator = opeMap.get(Constants.K02);
		if (operator != null) {/* Operador encontrado */
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K02.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nPath++;
						// Variables
						variables += PATH + nPath + PATHL + nPath;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ PATH + nPath + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, PATHL + nPath)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Pathogen
			nPath = 1;
			variables += PATH + Constants.ONE + PATHL + Constants.ONE;
		}
		// Contenido de acuerdo al numero de Pathogens
		for (int i = 1; i <= nPath; i++) {
			// Rdf type
			content += PATH + i + A
					+ conceptsMap.get(Constants.K02).getCosrdftaf() + DOT;
			// Label
			content += PATH + i + RDFS_LABEL + PATHL + i + DOT;
			// Has Participant
			content += INT + HAS_PARTICIPANT + PATH + i + DOT;
		}
		/**
		 * 3. INTERACTION CONTEXT DESCRIPTION
		 */
		// Space
		content += SPACE;
		// Revisa si hay operador
		operator = opeMap.get(Constants.K04);
		if (operator != null) {/* Operador encontrado */
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K04.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nIncon++;
						// Variables
						variables += INCON + nIncon + INCONL + nIncon;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ INCON + nIncon + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, INCONL + nIncon)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Interaction Context Description
			nIncon = 1;
			variables += INCON + Constants.ONE + INCONL + Constants.ONE;
		}
		// Contenido de acuerdo al numero de Interaction Context Description
		for (int i = 1; i <= nIncon; i++) {
			// Rdf type
			content += INCON + i + A
					+ conceptsMap.get(Constants.K04).getCosrdftaf() + DOT;
			// Has Value
			content += INCON + i + HAS_VALUE + INCONL + i + DOT;
			// Has Quality
			content += INTCX + HAS_QUALITY + INCON + i + DOT;
		}
		/**
		 * 4. PROTOCOL DESCRIPTION
		 */
		// Space
		content += SPACE;
		// Revisa si hay operador
		operator = opeMap.get(Constants.K03);
		if (operator != null) {/* Operador encontrado */
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K03.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nProtd++;
						// Variables
						variables += PROTD + nProtd + PROTDL + nProtd;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ PROTD + nProtd + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, PROTDL + nProtd)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Protocol Description
			nProtd = 1;
			variables += PROTD + Constants.ONE + PROTDL + Constants.ONE;
		}
		// Contenido de acuerdo al numero de Protocol Description
		for (int i = 1; i <= nProtd; i++) {
			// Rdf type
			content += PROTD + i + A
					+ conceptsMap.get(Constants.K03).getCosrdftaf() + DOT;
			// Has Value
			content += PROTD + i + HAS_VALUE + PROTDL + i + DOT;
			// Has Quality
			content += PROT + HAS_QUALITY + PROTD + i + DOT;
		}
		/**
		 * 5. CITATION
		 */
		// Space
		content += SPACE;
		// Revisa si hay operador
		operator = opeMap.get(Constants.K13);
		if (operator != null) {/* Operador encontrado */
			// Opcional false
			opCita = false;
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K13.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nCita++;
						// Variables
						variables += CITA + nCita + CITAL + nCita;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CITA + nCita + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, CITAL + nCita)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Citation
			nCita = 1;
			variables += CITA + Constants.ONE + CITAL + Constants.ONE;
		}
		// Contenido de acuerdo al numero de Citation
		// Revisa si es opcional
		content += opCita ? OPTIONAL : Constants.BLANKS;
		for (int i = 1; i <= nCita; i++) {
			// Rdf type
			content += CITA + i + A
					+ conceptsMap.get(Constants.K13).getCosrdftaf() + DOT;
			// Has Unique Identifier
			content += CITA + i + HAS_UNIQUE_ID + CITAL + i + DOT;
			// Has Quality
			content += PROT + CITATION + CITA + i + DOT;
		}
		// Cierre de opcional
		content += opCita ? R_KEY : Constants.BLANKS;
		/**
		 * 6. DISEASE NAME
		 */
		// Space
		content += SPACE;
		// Revisa si hay operador
		operator = opeMap.get(Constants.K05);
		if (operator != null) {/* Operador encontrado */
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K05.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nDisn++;
						// Variables
						variables += DISN + nDisn + DISNL + nDisn;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ DISN + nDisn + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, DISNL + nDisn)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Disease Name
			nDisn = 1;
			variables += DISN + Constants.ONE + DISNL + Constants.ONE;
		}
		// Contenido de acuerdo al numero de Disease Name
		for (int i = 1; i <= nDisn; i++) {
			// Rdf type
			content += DISN + i + A
					+ conceptsMap.get(Constants.K05).getCosrdftaf() + DOT;
			// Has Value
			content += DISN + i + HAS_VALUE + DISNL + i + DOT;
			// Is described by (Relacion con Int Cont Des)
			for (int j = 1; j <= nIncon; j++) {
				content += INCON + j + IS_DESCRIBED_BY + DISN + i + DOT;
			}
		}
		/**
		 * 7. ALLELE
		 */
		// Space
		content += SPACE;
		// Revisa si hay operador
		operator = opeMap.get(Constants.K09);
		if (operator != null) {/* Operador encontrado */
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K09.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nAlle++;
						// Variables
						variables += ALLE + nAlle + ALLEL + nAlle;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ ALLE + nAlle + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, ALLEL + nAlle)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Allele
			nAlle = 1;
			variables += ALLE + Constants.ONE + ALLEL + Constants.ONE;
		}
		// Contenido de acuerdo al numero de Alleles
		for (int i = 1; i <= nAlle; i++) {
			// Rdf type
			content += ALLE + i + A
					+ conceptsMap.get(Constants.K09).getCosrdftaf() + DOT;
			// Label
			content += ALLE + i + RDFS_LABEL + ALLEL + i + DOT;
			// Path context has quality allele
			content += PATHCX + HAS_QUALITY + ALLE + i + DOT;
		}
		/**
		 * 8. LETHAL KNOCKOUT
		 */
		// Space
		content += SPACE;
		// Revisa si hay operador
		operator = opeMap.get(Constants.K11);
		if (operator != null) {/* Operador encontrado */
			// Opcional false
			opLeth = false;
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K11.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nLeth++;
						// Variables
						variables += LETH + nLeth + LETHL + nLeth;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ LETH + nLeth + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, LETHL + nLeth)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Lethal Knockout
			nLeth = 1;
			variables += LETH + Constants.ONE + LETHL + Constants.ONE;
		}
		// Revisa si es opcional
		content += opLeth ? OPTIONAL : Constants.BLANKS;
		for (int i = 1; i <= nLeth; i++) {
			// Rdf type
			content += LETH + i + A
					+ conceptsMap.get(Constants.K11).getCosrdftaf() + DOT;
			// Has Value
			content += LETH + i + HAS_VALUE + LETHL + i + DOT;
			// Allele has quality Lethal Knockout
			for (int j = 1; j <= nAlle; j++) {
				content += ALLE + j + HAS_QUALITY + LETH + i + DOT;
			}
		}
		// Cierre de opcional
		content += opLeth ? R_KEY : Constants.BLANKS;
		/**
		 * 9. INVITRO GROWTH
		 */
		// Space
		content += SPACE;
		// Revisa si hay operador
		operator = opeMap.get(Constants.K10);
		if (operator != null) {/* Operador encontrado */
			// Opcional false
			opInvg = false;
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K10.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nInvg++;
						// Variables
						variables += INVG + nInvg + INVGL + nInvg;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ INVG + nInvg + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, INVGL + nInvg)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Invitro Growth
			nInvg = 1;
			variables += INVG + Constants.ONE + INVGL + Constants.ONE;
		}
		// Revisa si es opcional
		content += opInvg ? OPTIONAL : Constants.BLANKS;
		for (int i = 1; i <= nInvg; i++) {
			// Rdf type
			content += INVG + i + A
					+ conceptsMap.get(Constants.K10).getCosrdftaf() + DOT;
			// Has Value
			content += INVG + i + HAS_VALUE + INVGL + i + DOT;
			// Allele has quality Invitro Growth
			for (int j = 1; j <= nAlle; j++) {
				content += ALLE + j + HAS_QUALITY + INVG + i + DOT;
			}
		}
		// Cierre de opcional
		content += opInvg ? R_KEY : Constants.BLANKS;
		/**
		 * 10. GEN
		 */
		// Space
		content += SPACE;
		// Revisa si hay operador
		operator = opeMap.get(Constants.K00);
		if (operator != null) {/* Operador encontrado */
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K00.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nGene++;
						// Variables
						variables += GENE + nGene + GENEL + nGene;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ GENE + nGene + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, GENEL + nGene)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Gen
			nGene = 1;
			variables += GENE + Constants.ONE + GENEL + Constants.ONE;
		}
		for (int i = 1; i <= nGene; i++) {
			// Rdf type
			content += GENE + i + A
					+ conceptsMap.get(Constants.K00).getCosrdftaf() + DOT;
			// Label
			content += GENE + i + RDFS_LABEL + GENEL + i + DOT;
			// Allele is variant of gen
			for (int j = 1; j <= nAlle; j++) {
				content += ALLE + j + IS_VARIANT_OF + GENE + i + DOT;
			}
		}
		/**
		 * 11. Gene Locus Id
		 */
		// Space
		content += SPACE;
		// Revisa si hay operador
		operator = opeMap.get(Constants.K06);
		if (operator != null) {/* Operador encontrado */
			// Opcional false
			opLocu = false;
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K06.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nLocu++;
						// Variables
						variables += LOCU + nLocu + LOCUL + nLocu;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ LOCU + nLocu + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, LOCUL + nLocu)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Locus Id
			nLocu = 1;
			variables += LOCU + Constants.ONE + LOCUL + Constants.ONE;
		}
		// Revisa si es opcional
		content += opLocu ? OPTIONAL : Constants.BLANKS;
		for (int i = 1; i <= nLocu; i++) {
			// Rdf type
			content += LOCU + i + A
					+ conceptsMap.get(Constants.K06).getCosrdftaf() + DOT;
			// Has Value
			content += LOCU + i + HAS_VALUE + LOCUL + i + DOT;
			// Gene Has unique identifier Locus Id
			for (int j = 1; j <= nGene; j++) {
				content += GENE + j + HAS_UNIQUE_ID + LOCU + i + DOT;
			}
		}
		// Cierre de opcional
		content += opLocu ? R_KEY : Constants.BLANKS;
		/**
		 * 12. Gene Function
		 */
		// Space
		content += SPACE;
		// Revisa si hay operador
		operator = opeMap.get(Constants.K07);
		if (operator != null) {/* Operador encontrado */
			// Opcional false
			opGfun = false;
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K07.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nGfun++;
						// Variables
						variables += GFUN + nGfun + GFUNL + nGfun;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ GFUN + nGfun + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, GFUNL + nGfun)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Locus Id
			nGfun = 1;
			variables += GFUN + Constants.ONE + GFUNL + Constants.ONE;
		}
		// Revisa si es opcional
		content += opGfun ? OPTIONAL : Constants.BLANKS;
		for (int i = 1; i <= nGfun; i++) {
			// Rdf type
			content += GFUN + i + A
					+ conceptsMap.get(Constants.K07).getCosrdftaf() + DOT;
			// Has Value
			content += GFUN + i + HAS_VALUE + GFUNL + i + DOT;
			// Gene Has Quality Locus Id
			for (int j = 1; j <= nGene; j++) {
				content += GENE + j + HAS_QUALITY + GFUN + i + DOT;
			}
		}
		// Cierre de opcional
		content += opGfun ? R_KEY : Constants.BLANKS;
		/**
		 * 13. Gene Name
		 */
		// Space
		content += SPACE;
		// Revisa si hay operador
		operator = opeMap.get(Constants.K08);
		if (operator != null) {/* Operador encontrado */
			// Opcional false
			opGnam = false;
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K08.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nGnam++;
						// Variables
						variables += GNAM + nGnam + GNAML + nGnam;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ GNAM + nGnam + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, GNAML + nGnam)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Gene Name
			nGnam = 1;
			variables += GNAM + Constants.ONE + GNAML + Constants.ONE;
		}
		// Revisa si es opcional
		content += opGnam ? OPTIONAL : Constants.BLANKS;
		for (int i = 1; i <= nGnam; i++) {
			// Rdf type
			content += GNAM + i + A
					+ conceptsMap.get(Constants.K08).getCosrdftaf() + DOT;
			// Has Value
			content += GNAM + i + HAS_VALUE + GNAML + i + DOT;
			// Gene Has Unique Identifier Gene Name
			for (int j = 1; j <= nGene; j++) {
				content += GENE + j + HAS_UNIQUE_ID + GNAM + i + DOT;
			}
		}
		// Cierre de opcional
		content += opGnam ? R_KEY : Constants.BLANKS;
		/**
		 * 14. Gene Accession
		 */
		// Space
		content += SPACE;
		// Revisa si hay operador
		operator = opeMap.get(Constants.K12);
		if (operator != null) {/* Operador encontrado */
			// Opcional false
			opGacc = false;
			// Contador
			cont = 0;
			// Intra op
			intraOp = Constants.ONE.equals(operator.getOperator()) ? AND : OR;
			// Inicia filter
			filter += (isFilter ? interOpValue : Constants.BLANKS) + L_PAR;
			// Recorre elementos para filter y variables
			for (SearchConcept conce : concepts) {
				// Revisa si es del tipo
				if (Constants.K12.equals(conce.getConce().getCosccosak())) {
					// Revisa si incrementa contador y variables
					if (cont == 0
							|| Constants.ONE.equals(operator.getOperator())) {
						// Incrementa numero
						nGacc++;
						// Variables
						variables += GACC + nGacc + GACCL + nGacc;
					}
					// Filter (Dependiendo de si hay IRI o no)
					if (!Constants.BLANKS.equals(conce.getConce()
							.getDcoiricaf())) {
						// Hay IRI
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ GACC + nGacc + EQ
								+ conce.getConce().getDcoiricaf();
					} else {/* No hay IRI */
						// Filter con contains
						filter += (cont == 0 ? Constants.BLANKS : intraOp)
								+ CONTAINS.replace(VARIABLE, GACCL + nGacc)
										.replace(AUTOCOMPLETE,
												conce.getAutocomplete());
					}
					// Contador de registros
					cont++;
				}
			}
			// Cierra filter
			filter += R_PAR;
			// Hay filter
			isFilter = true;
		} else {/* Operador no encontrado */
			// Solo un Gene Name
			nGacc = 1;
			variables += GACC + Constants.ONE + GACCL + Constants.ONE;
		}
		// Revisa si es opcional
		content += opGacc ? OPTIONAL : Constants.BLANKS;
		for (int i = 1; i <= nGacc; i++) {
			// Rdf type
			content += GACC + i + A
					+ conceptsMap.get(Constants.K12).getCosrdftaf() + DOT;
			// Has Value
			content += GACC + i + HAS_VALUE + GACCL + i + DOT;
			// Gene Has Unique Identifier Gene Accession
			for (int j = 1; j <= nGene; j++) {
				content += GENE + j + HAS_UNIQUE_ID + GACC + i + DOT;
			}
		}
		// Cierre de opcional
		content += opGacc ? R_KEY : Constants.BLANKS;
		// Cierra filter y content de query
		filter += R_PAR;
		// Contenido de la query
		content += filter + R_KEY;
		// Query a ejecutar
		String query = variables + SPACE + content;
		// Crea parametros de ejecucion de query
		String queryParameters = this.createQueryParameters(operatorsOriginal,
				concepts, interOperator);
		// Ejecuta Query
		return this.executeQuery(usuemaiak, query, queryParameters, queryDesc,
				nHost, nPath, nIncon, nDisn, nProtd, nCita, nAlle, nLeth,
				nInvg, nGene, nLocu, nGfun, nGnam, nGacc);
	}

	/**
	 * Se encarga de ejecutar Query recibiendola ya armada
	 * 
	 * @param usuemaiak
	 *            Usuario que ejecuta la query
	 * @param query
	 *            Query a ejecutar
	 * @param queryParameters
	 *            Parametros de ejecución de queries
	 * @param queryDesc
	 *            Descripcion de la query
	 * @param nHost
	 *            Numero de Hosts a recibir
	 * @param nPath
	 *            Numero de Pathogen a recibir
	 * @param nIncon
	 *            Numero de Interaction context a recibir
	 * @param nDisn
	 *            Numero de disease name a recibir
	 * @param nProtd
	 *            Numero de Protocol description a recibir
	 * @param nCita
	 *            numero de Citations a recibir
	 * @param nAlle
	 *            Numero de allelos a recibir
	 * @param nLeth
	 *            Numero de Lethal Knockout a recibir
	 * @param nInvg
	 *            Numero de Invitro Growth a recibir
	 * @param nGene
	 *            Numero de Gene a recibir
	 * @param nLocu
	 *            Numero de Locus Id a recibir
	 * @param nGfun
	 *            Numero de Gene Function a recibir
	 * @param nGnam
	 *            Numero de Gene Name a recibir
	 * @param nGacc
	 *            Numero de Gene Accession a recibir
	 * @return Resultado de ejecucion de la query
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<QueryResult> executeQuery(String usuemaiak, String query,
			String queryParameters, String queryDesc, int nHost, int nPath,
			int nIncon, int nDisn, int nProtd, int nCita, int nAlle, int nLeth,
			int nInvg, int nGene, int nLocu, int nGfun, int nGnam, int nGacc)
			throws DAOException {
		// Inicia lista de resultados
		List<QueryResult> results = new ArrayList<QueryResult>();
		// Genera registro USDLOGQU
		this.generateLogquRecord(usuemaiak, query, queryParameters, queryDesc,
				nHost, nPath, nIncon, nDisn, nProtd, nCita, nAlle, nLeth,
				nInvg, nGene, nLocu, nGfun, nGnam, nGacc);
		// Ejecuta Query
		ResultSet spresult = this.queryExecutor.executeQuery(query);
		// Itera sobre resultados
		// Recorre resultados
		for (; spresult.hasNext();) {
			// Solucion query
			QuerySolution solution = spresult.nextSolution();
			// Registro QueryResult
			QueryResult result = new QueryResult();
			/*
			 * Inicia obtenicion de variables
			 */
			// Interacción
			result.getInteractions().add(
					new QueryValue(INTRDF, prepareAttribute(solution.get(INTL
							.trim())),
							prepareAttribute(solution.get(INT.trim()))));
			// Host
			for (int i = 1; i <= nHost; i++) {
				// Adiciona registro
				result.getHosts()
						.add(new QueryValue(conceptsMap.get(Constants.K01)
								.getCosrdftaf(), prepareAttribute(solution
								.get(HOSTL.trim() + i)),
								prepareAttribute(solution.get(HOST.trim() + i))));
			}
			// Pathogen
			for (int i = 1; i <= nPath; i++) {
				// Adiciona registro
				result.getPathogens()
						.add(new QueryValue(conceptsMap.get(Constants.K02)
								.getCosrdftaf(), prepareAttribute(solution
								.get(PATHL.trim() + i)),
								prepareAttribute(solution.get(PATH.trim() + i))));
			}
			// Interaction Context Description
			for (int i = 1; i <= nIncon; i++) {
				// Adiciona registro
				result.getIntContextMutantDesc()
						.add(new QueryValue(
								conceptsMap.get(Constants.K04).getCosrdftaf(),
								prepareAttribute(solution.get(INCONL.trim() + i)),
								prepareAttribute(solution.get(INCON.trim() + i))));
			}
			// Protocol Description
			for (int i = 1; i <= nProtd; i++) {
				// Adiciona registro
				result.getProtocolDescriptions()
						.add(new QueryValue(
								conceptsMap.get(Constants.K03).getCosrdftaf(),
								prepareAttribute(solution.get(PROTDL.trim() + i)),
								prepareAttribute(solution.get(PROTD.trim() + i))));
			}
			// Citation
			for (int i = 1; i <= nCita; i++) {
				// Adiciona registro
				result.getCitations()
						.add(new QueryValue(conceptsMap.get(Constants.K13)
								.getCosrdftaf(), prepareAttribute(solution
								.get(CITAL.trim() + i)),
								prepareAttribute(solution.get(CITA.trim() + i))));
			}
			// Disease Name
			for (int i = 1; i <= nDisn; i++) {
				// Adiciona registro
				result.getDiseaseNames()
						.add(new QueryValue(conceptsMap.get(Constants.K05)
								.getCosrdftaf(), prepareAttribute(solution
								.get(DISNL.trim() + i)),
								prepareAttribute(solution.get(DISN.trim() + i))));
			}
			// Allele
			for (int i = 1; i <= nAlle; i++) {
				// Adiciona registro
				result.getAlleles()
						.add(new QueryValue(conceptsMap.get(Constants.K09)
								.getCosrdftaf(), prepareAttribute(solution
								.get(ALLEL.trim() + i)),
								prepareAttribute(solution.get(ALLE.trim() + i))));
			}
			// Lethal Knockout
			for (int i = 1; i <= nLeth; i++) {
				// Adiciona registro
				result.getLethalKnockouts()
						.add(new QueryValue(conceptsMap.get(Constants.K11)
								.getCosrdftaf(), prepareAttribute(solution
								.get(LETHL.trim() + i)),
								prepareAttribute(solution.get(LETH.trim() + i))));
			}
			// Invitro Growth
			for (int i = 1; i <= nInvg; i++) {
				// Adiciona registro
				result.getInvitroGrowths()
						.add(new QueryValue(conceptsMap.get(Constants.K10)
								.getCosrdftaf(), prepareAttribute(solution
								.get(INVGL.trim() + i)),
								prepareAttribute(solution.get(INVG.trim() + i))));
			}
			// Gene
			for (int i = 1; i <= nGene; i++) {
				// Adiciona registro
				result.getGenes()
						.add(new QueryValue(conceptsMap.get(Constants.K00)
								.getCosrdftaf(), prepareAttribute(solution
								.get(GENEL.trim() + i)),
								prepareAttribute(solution.get(GENE.trim() + i))));
			}
			// Gene - Locus Id
			for (int i = 1; i <= nLocu; i++) {
				// Adiciona registro
				result.getGeneLocusIds()
						.add(new QueryValue(conceptsMap.get(Constants.K06)
								.getCosrdftaf(), prepareAttribute(solution
								.get(LOCUL.trim() + i)),
								prepareAttribute(solution.get(LOCU.trim() + i))));
			}
			// Gene - Function
			for (int i = 1; i <= nGfun; i++) {
				// Adiciona registro
				result.getGeneFunctions()
						.add(new QueryValue(conceptsMap.get(Constants.K07)
								.getCosrdftaf(), prepareAttribute(solution
								.get(GFUNL.trim() + i)),
								prepareAttribute(solution.get(GFUN.trim() + i))));
			}
			// Gene - Name
			for (int i = 1; i <= nGnam; i++) {
				// Adiciona registro
				result.getGeneNames()
						.add(new QueryValue(conceptsMap.get(Constants.K08)
								.getCosrdftaf(), prepareAttribute(solution
								.get(GNAML.trim() + i)),
								prepareAttribute(solution.get(GNAM.trim() + i))));
			}
			// Gene - Accession
			for (int i = 1; i <= nGacc; i++) {
				// Adiciona registro
				result.getGeneAccessions()
						.add(new QueryValue(conceptsMap.get(Constants.K12)
								.getCosrdftaf(), prepareAttribute(solution
								.get(GACCL.trim() + i)),
								prepareAttribute(solution.get(GACC.trim() + i))));
			}
			// Adiciona registro resultado
			results.add(result);
		}
		// Retorna resultado
		return results;
	}

	/**
	 * Se encarga de generar un String para almacenar los parametros de
	 * ejecucion de una query
	 * 
	 * @param operators
	 *            Operadores involucrados en la query
	 * @param concepts
	 *            Lista de conceptos de busqueda
	 * @param interOperator
	 *            Operador inter-conceptos
	 * @return Cadena que representa los parametros de ejecucion de la query
	 */
	private String createQueryParameters(
			List<LogicalOperatorConcept> operators,
			List<SearchConcept> concepts, String interOperator) {
		// String a retornar
		String queryParameters = Constants.BLANKS;
		// Contador
		int cont = 0;
		// InterOperator
		queryParameters += interOperator + Constants.PARAM_SEP;
		// Lista de operadores
		for (LogicalOperatorConcept op : operators) {
			// Concatena operador
			queryParameters += (cont++ == 0 ? Constants.BLANKS
					: Constants.COMMA)
					+ op.getCosccosak()
					+ Constants.EQUAL
					+ op.getOperator();
		}
		// Contador a cero
		cont = 0;
		// Separador
		queryParameters += Constants.PARAM_SEP;
		// Lista de conceptos
		for (SearchConcept sc : concepts) {
			// Concatena concepto
			queryParameters += (cont++ == 0 ? Constants.BLANKS
					: Constants.MAIL_SEP)
					+ sc.getCosccosak()
					+ Constants.PARAM_SEP_2
					+ sc.getRcocrcoak()
					+ Constants.PARAM_SEP_2
					+ sc.getDescription()
					+ Constants.PARAM_SEP_2
					+ sc.getAutocomplete()
					+ Constants.PARAM_SEP_2
					+ (sc.getConce() != null ? sc.getConce().getDcoiricaf()
							: Constants.BLANKS)
					+ Constants.PARAM_SEP_2
					+ sc.getIcon();
		}
		// Operadores
		return queryParameters;
	}

	/**
	 * Genera Log de Ejecución de la query
	 * 
	 * @param usuemaiak
	 *            Usuario que ejecuta la query
	 * @param query
	 *            Query a ejecutar
	 * @param queryParameters
	 *            Parametros de ejecución de queries
	 * @param queryDesc
	 *            Descripcion de la query
	 * @param nHost
	 *            Numero de Hosts a recibir
	 * @param nPath
	 *            Numero de Pathogen a recibir
	 * @param nIncon
	 *            Numero de Interaction context a recibir
	 * @param nDisn
	 *            Numero de disease name a recibir
	 * @param nProtd
	 *            Numero de Protocol description a recibir
	 * @param nCita
	 *            numero de Citations a recibir
	 * @param nAlle
	 *            Numero de allelos a recibir
	 * @param nLeth
	 *            Numero de Lethal Knockout a recibir
	 * @param nInvg
	 *            Numero de Invitro Growth a recibir
	 * @param nGene
	 *            Numero de Gene a recibir
	 * @param nLocu
	 *            Numero de Locus Id a recibir
	 * @param nGfun
	 *            Numero de Gene Function a recibir
	 * @param nGnam
	 *            Numero de Gene Name a recibir
	 * @param nGacc
	 *            Numero de Gene Accession a recibir
	 * @return Resultado de ejecucion de la query
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	private void generateLogquRecord(String usuemaiak, String query,
			String queryParameters, String queryDesc, int nHost, int nPath,
			int nIncon, int nDisn, int nProtd, int nCita, int nAlle, int nLeth,
			int nInvg, int nGene, int nLocu, int nGfun, int nGnam, int nGacc)
			throws DAOException {
		// Fecha actual
		Date current = DateUtils.getCurrentUtilDate();
		Date time = DateUtils.getCurrentSQLTime();
		// Numeros de la query
		String dataExpected = nHost + Constants.COMMA + nPath + Constants.COMMA
				+ nIncon + Constants.COMMA + nDisn + Constants.COMMA + nProtd
				+ Constants.COMMA + nCita + Constants.COMMA + nAlle
				+ Constants.COMMA + nLeth + Constants.COMMA + nInvg
				+ Constants.COMMA + nGene + Constants.COMMA + nLocu
				+ Constants.COMMA + nGfun + Constants.COMMA + nGnam
				+ Constants.COMMA + nGacc;
		// Inicia registro
		Usdlogqu logqu = new Usdlogqu(new UsdlogquId(usuemaiak, current, 0),
				time, query.length() > 8192 ? query.substring(0, 8191) : query,
				dataExpected, queryParameters, queryDesc);
		// Guarda registro en la base de datos
		usdlogquDao.save(logqu);
	}

	/**
	 * Prepara atributo a guardar
	 * 
	 * @param attribute
	 * @return Atributo preparado
	 */
	private String prepareAttribute(RDFNode attribute) {
		// Revisa que no sea null el atributo
		if (attribute == null)
			return ApplicationMessages.getMessage("USR0037");
		// Revisa si es un resource
		if (attribute.isURIResource())
			return Constants.MINOR + attribute.toString() + Constants.MAJOR;
		// Trim
		String s_attribute = attribute.toString().trim();
		// Separa por @
		String[] attribute_s = s_attribute.split(Constants.MAIL_SEP);
		// Retorna
		return attribute_s[0];
	}
}
