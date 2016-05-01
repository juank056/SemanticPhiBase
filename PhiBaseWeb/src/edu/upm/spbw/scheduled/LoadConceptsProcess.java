/**
 * 
 */
package edu.upm.spbw.scheduled;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.bo.Sedconce;
import edu.upm.spbw.persistence.bo.Sedrelco;
import edu.upm.spbw.persistence.bo.SedrelcoId;
import edu.upm.spbw.persistence.bo.Sepconce;
import edu.upm.spbw.persistence.dao.ISedconceDAO;
import edu.upm.spbw.persistence.dao.ISedrelcoDAO;
import edu.upm.spbw.persistence.dao.ISepconceDAO;
import edu.upm.spbw.utils.Constants;
import edu.upm.spbw.utils.LogLogger;
import edu.upm.spbw.utils.SparqlQueryExecutor;
import edu.upm.spbw.utils.SpringAppContext;

/**
 * Proceso que se ejecuta todos los días para mantener actualizadas las
 * estructuras de conceptos semánticos y sus relaciones
 * 
 * @author JuanCamilo
 * 
 */
public class LoadConceptsProcess extends QuartzJobBean {

	/**
	 * DAO para leer SEPCONCE
	 */
	private ISepconceDAO sepconceDao;

	/**
	 * DAO para leer SEDCONCE
	 */
	private ISedconceDAO sedconceDao;

	/**
	 * DAO para leer SEDRELCO
	 */
	private ISedrelcoDAO sedrelcoDao;

	/**
	 * Ejecutor de queries
	 */
	private SparqlQueryExecutor queryExecutor;

	/**
	 * Ejecuta el proceso de carga de estructuras de acuerdo a la
	 * parametrización
	 */
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		try {
			LogLogger.getInstance(this.getClass()).logger(
					"Init LoadConceptsProcess...", LogLogger.DEBUG);
			// Inicia DAOs
			sepconceDao = SpringAppContext.getAppContext().getBean(
					ISepconceDAO.class);
			sedconceDao = SpringAppContext.getAppContext().getBean(
					ISedconceDAO.class);
			sedrelcoDao = SpringAppContext.getAppContext().getBean(
					ISedrelcoDAO.class);
			// Inicia ejecutor de queries
			queryExecutor = new SparqlQueryExecutor();
			// Elimina registros SEDRELCO
			LogLogger.getInstance(this.getClass()).logger(
					"Deleting records on table SEDRELCO...", LogLogger.DEBUG);
			sedrelcoDao.deleteAllRecords();
			LogLogger.getInstance(this.getClass()).logger(
					"records on table SEDRELCO deleted successfully...",
					LogLogger.DEBUG);
			// Elimina registros SEDCONCE
			LogLogger.getInstance(this.getClass()).logger(
					"Deleting records on table SEDCONCE...", LogLogger.DEBUG);
			sedconceDao.deleteAllRecords();
			LogLogger.getInstance(this.getClass()).logger(
					"records on table SEDCONCE deleted successfully...",
					LogLogger.DEBUG);
			// Obtiene todos los registros de Sepconce
			List<Sepconce> conces = sepconceDao.findAll();
			// Recorre registros
			for (Sepconce conce : conces) {
				LogLogger.getInstance(this.getClass()).logger(
						"Processing Concept: " + conce.getCosdcosaf(),
						LogLogger.DEBUG);
				// Ejecuta proceso de carga de registro
				this.loadConcept(conce);
				LogLogger.getInstance(this.getClass()).logger(
						"Concept: " + conce.getCosdcosaf()
								+ " processed successfully", LogLogger.DEBUG);
			}
			LogLogger.getInstance(this.getClass()).logger(
					"LoadConceptsProcess executed successfully...",
					LogLogger.DEBUG);
		} catch (Exception e) {/* Error en proceso */
			LogLogger.getInstance(this.getClass()).logger(
					ExceptionUtils.getFullStackTrace(e), LogLogger.ERROR);
		}
	}

	/**
	 * Se encarga de cargar datos de los detalles de un concepto
	 * 
	 * @param conce
	 *            Concepto semantico
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	private void loadConcept(Sepconce conce) throws DAOException {
		// Arma query a ejecutar
		String query = "SELECT DISTINCT ?object ";
		// Atributos a obtener
		for (int i = 0; i < conce.getCosnrelnf(); i++) {
			// Concatena atributo
			query += Constants.REL + i + Constants.BLANK_SPACE;
		}
		// Where
		query += " WHERE { ";
		// Iri del objeto
		query += "?object a " + conce.getCosrdftaf() + Constants.BLANK_SPACE
				+ Constants.DOT + Constants.BLANK_SPACE;
		// Atributos
		for (int i = 0; i < conce.getCosnrelnf(); i++) {
			query += "?object " + conce.getRelcos().get(i).getRcordfraf()
					+ Constants.BLANK_SPACE + Constants.REL + i
					+ Constants.BLANK_SPACE + Constants.DOT
					+ Constants.BLANK_SPACE;
		}
		// Cierra query
		query += " }";
		// Ejecuta query
		ResultSet results = queryExecutor.executeQuery(query);
		// Recorre resultados
		for (; results.hasNext();) {
			// Solucion query
			QuerySolution solution = results.nextSolution();
			// Objeto
			String object = solution.get(Constants.OBJECT).toString();
			// Revisa si se encuentra el concepto por IRI
			Sedconce dconce = sedconceDao.findByIRI(object);
			if (dconce != null) {/* Ya estaba registrado */
				// Continua con siguiente registro
				continue;
			}
			// Crea registro SEDCONCE
			dconce = new Sedconce(Constants.MINOR + object + Constants.MAJOR,
					conce.getCosccosak());
			// Guarda registro
			sedconceDao.save(dconce);
			// Atributos a crear
			for (int i = 0; i < conce.getCosnrelnf(); i++) {
				// Obtiene atributo
				String attribute = prepareAttribute(solution.get(Constants.REL
						+ i));
				// Crea registro SEDRELCO
				Sedrelco relco = new Sedrelco(new SedrelcoId(
						dconce.getDconsecnk(), 0), attribute,
						conce.getCosccosak(), conce.getRelcos().get(i).getId()
								.getRcocrcoak());
				// Guarda registro
				sedrelcoDao.save(relco);
			}
		}
	}

	/**
	 * Prepara atributo a guardar
	 * 
	 * @param attribute
	 * @return Atributo preparado
	 */
	private String prepareAttribute(RDFNode attribute) {
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
