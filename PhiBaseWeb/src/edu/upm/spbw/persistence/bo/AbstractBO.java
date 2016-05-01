/**
 * 
 */
package edu.upm.spbw.persistence.bo;

/**
 * Interfaz default de un Bussiness Object de la base de datos K: llave primaria
 * del objeto
 * 
 * @author Juan Camilo
 */
public interface AbstractBO<K> {

	/**
	 * Metodo para obtener la llave primaria del objeto
	 * 
	 * @return la llave primaria del objeto
	 */
	public K getPrimaryKey();

	/**
	 * Metodo para limpiar el objeto
	 */
	public void prepareObject();

	/**
	 * Preparar el objeto antes de actualizar
	 */
	public void prepareToUpdate();

}
