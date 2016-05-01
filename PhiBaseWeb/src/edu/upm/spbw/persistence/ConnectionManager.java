/**
 * 
 */
package edu.upm.spbw.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.upm.spbw.utils.ConfigApplicationManager;
import edu.upm.spbw.utils.DBDataChiper;
import edu.upm.spbw.utils.exception.MissingConfigurationParameterException;

/**
 * Clase para manejar las conexiones con la base de datos Esta clase sera usada
 * para los DAOs cuya implementacion no se dé por hibernate.
 * 
 * @author Juan Camilo Mesa
 * 
 */
public class ConnectionManager {

	/**
	 * Instancia del ConnectionManager
	 */
	private static ConnectionManager instance;

	/**
	 * Url hacia la base de datos
	 */
	private String urlDb;

	/**
	 * Nombre de usuario de la base de datos
	 */
	private String userName;

	/**
	 * Password del usuario de la base de datos
	 */
	private String password;

	/**
	 * Obtiene la instancia del Connection Manager de la aplicacion
	 * 
	 * @return el connection manager de la aplicacion
	 */
	public static ConnectionManager getInstance() {
		if (instance == null)
			instance = new ConnectionManager();
		return instance;
	}

	/**
	 * Constructor
	 */
	private ConnectionManager() {
		// Registra el driver a utilizar
		String driverClass = null;
		try {
			driverClass = ConfigApplicationManager
					.getParameter("com.esf.db.driver");
			Class.forName(driverClass);
			// Carga la url de la base de datos
			urlDb = ConfigApplicationManager.getParameter("com.esf.db.url");
			// Carga el nombre de usuario de la base de datos
			userName = DBDataChiper.decrypt(ConfigApplicationManager
					.getParameter("com.esf.db.username").trim());
			// Carga el password del usuario de la base de datos
			password = DBDataChiper.decrypt(ConfigApplicationManager
					.getParameter("com.esf.db.passwd").trim());
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found: " + driverClass
					+ ".\nThe application will terminate now.");
			System.out.println("Stack Trace:");
			e.printStackTrace();
			System.exit(-1);
		} catch (MissingConfigurationParameterException e) {
			System.out.println(e.getMessage()
					+ ".\nThe application will terminate now.");
			System.out.println("Stack Trace:");
			e.printStackTrace();
			System.exit(-1);
		}

	}

	/**
	 * Obtiene una conexion a la base de datos
	 * 
	 * @return la conexion con la base de datos
	 * @throws SQLException
	 *             En caso de que no pueda obtener la conexion
	 */
	public Connection getConnection() throws SQLException {
		Connection c = DriverManager.getConnection(urlDb, userName, password);
		return c;
	}

	/**
	 * Metodo para cerrar las conexiones con la base de datos
	 * 
	 * @param rs
	 *            el resultSet a cerrar o null si no se desea cerrar ninguno
	 * @param ps
	 *            el preparedStatement a cerrar o null si no se desea cerrar
	 *            ninguno
	 * @param conn
	 *            la conexion a cerrar o null si no se va a cerrar ninguna
	 * @throws SQLException
	 *             En caso de no poder cerrar la conexion con la base de datos
	 */
	public static void closeConnections(ResultSet rs, PreparedStatement ps,
			Connection conn) throws SQLException {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) {
				rs = null;
			}
			rs = null;
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException ex) {
				ps = null;
			}
			ps = null;
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception ex) {
				throw new SQLException(ex.getMessage());
			}
			conn = null;
		}
	}

}
