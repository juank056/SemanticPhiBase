package edu.upm.spbw.utils;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Clase Encargada de realizar la desencripción de los datos para realizar la
 * conexion con la base de datos
 * 
 * @author Juan Camilo Mesa P
 */
public class DBDataChiper {

	/**
	 * Clave Simetrica a usar
	 */
	private static final String KEY_STR = "ESFSASLINCE_JCMP";

	/**
	 * Algoritmo a usar DES
	 */
	private static final String ALGORITHM = "DES";

	/**
	 * Codificacion de Caracteres a usar
	 */
	private static final String ENCODING = "UTF8";

	/**
	 * Encargado de Encriptar
	 */
	private static Cipher encrypt;

	/**
	 * Encargado de Desencriptar
	 */
	private static Cipher decrypt;

	static {/* Inicializacion Estatica de Atributos */
		try {
			// Inicia encargado de Encriptar
			encrypt = Cipher.getInstance(ALGORITHM);
			// Inicia encargado de Desencriptar
			decrypt = Cipher.getInstance(ALGORITHM);
			// Obtiene llave
			KeySpec ks = new DESKeySpec(KEY_STR.getBytes(ENCODING));
			// Fabrica de Llaves
			SecretKeyFactory kf = SecretKeyFactory.getInstance(ALGORITHM);
			// Trae llave secreta de la fabrica
			SecretKey ky = kf.generateSecret(ks);
			// Inicia al encargado de encriptar
			encrypt.init(Cipher.ENCRYPT_MODE, ky);
			// Inicia al encargado de Desencriptar
			decrypt.init(Cipher.DECRYPT_MODE, ky);
		} catch (Exception ex) { /* Error Inesperado */
			// Termina Aplicacion
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Se encarga de Encriptar una cadena
	 * 
	 * @param str
	 *            Encripta una cadena
	 * @return Cadena encriptada utilizando la llave y el Algoritmo DES
	 */
	public static String encrypt(String str) {
		try {
			// Encode the string into bytes using utf-8
			byte[] utf8 = str.getBytes(ENCODING);
			// Encrypt
			byte[] enc = encrypt.doFinal(utf8);
			// Encode bytes to base64 to get a string
			return new sun.misc.BASE64Encoder().encode(enc);
		} catch (Exception e) { /* Error Inesperado */
			// Termina Aplicacion
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}

	/**
	 * Se Encarga de desencriptar una cadena
	 * 
	 * @param str
	 *            Cadena a desencriptar
	 * @return Cadena desencriptada utilizando la llave y el Algoritmo DES
	 */
	public static String decrypt(String str) {
		try {
			// Decode base64 to get bytes
			byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
			// Decrypt
			byte[] utf8 = decrypt.doFinal(dec);
			// Decode using utf-8
			return new String(utf8, ENCODING);
		} catch (Exception e) { /* Error Inesperado */
			// Termina Aplicacion
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
}