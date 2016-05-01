/**
 * 
 */
package edu.upm.spbw.utils;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;

import edu.upm.spbw.persistence.bo.Cspparsi;
import edu.upm.spbw.persistence.dao.ICspparsiDAO;

/**
 * Clase que se encarga de ejecutar las queries sparql en el endpoint de
 * virtuoso
 * 
 * @author JuanCamilo
 * 
 */
public class SparqlQueryExecutor {

	/**
	 * DAO para leer CSPPARSI
	 */
	private ICspparsiDAO cspparsiDao;

	/**
	 * prefijo de las queries a ejecutar
	 */
	private String queryPrefix;

	/**
	 * Dirección endpoint
	 */
	private String endpointAddress;

	/**
	 * Constructor
	 */
	public SparqlQueryExecutor() throws Exception {
		// Inicia DAO
		cspparsiDao = SpringAppContext.getAppContext().getBean(
				ICspparsiDAO.class);
		// Obtiene parametro de endpoint
		Cspparsi parsi = cspparsiDao.findById(Constants.SE08);
		// Asigna Endpoint
		endpointAddress = parsi.getPsivalpaf().trim();
		// Archivo de Prefijos
		String prefixPath = ConfigApplicationManager
				.getParameter("edu.upm.spbw.sparql.prefix");
		// Carga prefix de las queries
		queryPrefix = Constants.BLANKS;
		// File reader
		FileReader fr = new FileReader(prefixPath);
		// Buffeded reader
		BufferedReader bf = new BufferedReader(fr);
		// Linea
		String line = null;
		// Bucle con todas las lineas.
		while ((line = bf.readLine()) != null) {
			// Concatena linea al prefijo
			queryPrefix += line + Constants.NEW_LINE;
		}
		// Cierra reader
		bf.close();
	}

	/**
	 * Se encarga de ejecutar una query en el endpoint virtuoso
	 * 
	 * @param query
	 *            Query Sparql a ejecutar
	 * @return Result set de la query ejecutada para que sea procesada
	 */
	public ResultSet executeQuery(String query) {
		LogLogger.getInstance(this.getClass()).logger(
				"Query to be Executed:\n\n " + query, LogLogger.DEBUG);
		// Concatena prefijos
		String fullQuery = queryPrefix + query;
		// Ejecucion de query
		QueryExecution qe = QueryExecutionFactory.sparqlService(
				endpointAddress, fullQuery);
		// Retorna resultados
		return qe.execSelect();
	}

}
