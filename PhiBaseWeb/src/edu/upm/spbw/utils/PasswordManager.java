/**
 * 
 */
package edu.upm.spbw.utils;

import java.util.Random;

/**
 * @author Juan Camilo Mesa P Esta clase va a manejar todo lo relacionado con
 *         los passwords
 */
public class PasswordManager {

	private static String[] characters = { "A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "0", "!", "@", "#", "$", "+", "%", "^", "&", "*", "(",
			"<", "?", "~", ")", ">", "[", "]", "{", "}", "?" };

	/**
	 * Constructor
	 */
	public PasswordManager() {
	}

	/**
	 * Se encarga de generar una contraseña por defecto
	 */
	public static String generatePassword(String fName, String lName) {
		String passwd = Constants.BLANKS;
		passwd += fName.substring((new Random()).nextInt(fName.length()));
		passwd += generateTrash(4);
		passwd += lName.substring((new Random()).nextInt(lName.length()));
		passwd += generateTrash(4);
		return passwd.trim();
	}

	/**
	 * Genera una cadena aleatoria de Longitud i
	 * 
	 * @param i
	 *            Longitud de la cadena a generar
	 * @return
	 */
	private static String generateTrash(int i) {
		String trash = Constants.BLANKS;
		for (int j = 0; j < i; j++) {
			trash += characters[(new Random()).nextInt(characters.length)];
		}
		return trash;
	}

	/**
	 * Este metodo encripta un password
	 * 
	 * @param passw
	 *            el password en texto claro
	 * @return el password encriptado
	 */
	public static String encryptPassword(String passw) {
		String p1 = Constants.BLANKS, p2 = Constants.BLANKS, pc = Constants.BLANKS;
		p1 = Crypto.getStringMessageDigest(
				passw.substring(0, passw.length() / 2), Crypto.MD5);
		p2 = Crypto
				.getStringMessageDigest(
						passw.substring(passw.length() / 2, passw.length()),
						Crypto.MD5);
		pc = p1 + p2;
		return pc;
	}
}
