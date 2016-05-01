/**
 * 
 */
package edu.upm.spbw.utils;

/**
 * Encripta datos para ser usados en los archivos de configuracion
 * 
 * @author Juan Camilo
 * 
 */
public class DbDataCreator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String passwd = "1";
		String crypted = DBDataChiper.encrypt(passwd);
		System.out.println("Crypted: " + crypted);
		String clear = DBDataChiper.decrypt(crypted);
		System.out.println("Clear: " + clear);

	}

}
