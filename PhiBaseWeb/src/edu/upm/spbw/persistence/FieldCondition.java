/**
 * 
 */
package edu.upm.spbw.persistence;

/**
 * Condicion de Campo
 * 
 * @author Juan Camilo Mesa
 * 
 */
public class FieldCondition {

	/**
	 * Nombre del campo
	 */
	private String name;

	/**
	 * Valor del campo
	 */
	private Object value;

	/**
	 * Posibles valores
	 */
	private Object[] values;

	/**
	 * Condicion del campo
	 */
	private int contidion;

	/*****************************
	 * CONDICIONES DE LOS CAMPOS
	 ****************************/

	/**
	 * Igual
	 */
	public static final int EQ = 0;

	/**
	 * Menor o igual
	 */
	public static final int LE = 1;

	/**
	 * Mayor o igual
	 */
	public static final int GE = 2;

	/**
	 * Menor
	 */
	public static final int LT = 3;

	/**
	 * Mayor
	 */
	public static final int GT = 4;

	/**
	 * In
	 */
	public static final int IN = 5;

	/**
	 * Not Like
	 */
	public static final int ILIKE = 6;

	/**
	 * Like
	 */
	public static final int LIKE = 7;

	/**
	 * Diferente
	 */
	public static final int NE = 8;

	/**
	 * Constructor sin parametros
	 */
	public FieldCondition() {
	}

	/**
	 * @param name
	 *            Nombre del campo
	 * @param value
	 *            valor del campo
	 * @param contidion
	 *            condicion del campo
	 */
	public FieldCondition(String name, Object value, int contidion) {
		this.name = name;
		this.value = value;
		this.contidion = contidion;
	}

	/**
	 * @param name
	 *            Nombre del campo
	 * @param values
	 *            Posibles valores del campo
	 */
	public FieldCondition(String name, Object[] values) {
		this.name = name;
		this.values = values;
		this.contidion = IN;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @return the contidion
	 */
	public int getContidion() {
		return contidion;
	}

	/**
	 * @return the values
	 */
	public Object[] getValues() {
		return values;
	}
}
