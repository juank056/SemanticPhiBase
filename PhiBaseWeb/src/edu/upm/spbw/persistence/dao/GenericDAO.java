/**
 * 
 */
package edu.upm.spbw.persistence.dao;

import java.io.Serializable;
import java.util.List;

import edu.upm.spbw.persistence.DAOException;
import edu.upm.spbw.persistence.FieldCondition;

/**
 * DAO Genérico que todos deben implementar
 * 
 * @author Juan Camilo
 */
public interface GenericDAO<T, PK extends Serializable> extends Serializable {

	/**
	 * Guarda un objeto en la base de datos
	 * 
	 * @param o
	 *            el objeto a guardar
	 * @throws DAOException
	 *             error guardando el objeto
	 */
	public void save(T o) throws DAOException;

	/**
	 * Actualiza un object
	 * 
	 * @param o
	 *            el objeto a actualizar
	 * @throws DAOException
	 *             error actualizando el objeto
	 */
	public void update(T o) throws DAOException;

	/**
	 * Actualiza o guarda un object
	 * 
	 * @param o
	 *            el objeto a actualizar o guardar
	 * @throws DAOException
	 *             error actualizando o guardando el objeto
	 */
	public void saveOrUpdate(T o) throws DAOException;

	/**
	 * Carga un objeto de la base de datos dado su id. De ser encontrado el
	 * objeto, se llama su metodo cleanObject para que lo devuelva limpio
	 * 
	 * @param id
	 *            el id del objeto a cargar
	 * @return el objeto si se pudo cargar, null de lo contrario
	 * @throws DAOException
	 *             error en la base de datos
	 */
	public T findById(PK id) throws DAOException;

	/**
	 * Elimina un objeto de la base de datos
	 * 
	 * @param o
	 *            el objeto a eliminar
	 * @throws DAOException
	 *             si no se puede eliminar el objeto
	 */
	public void delete(T o) throws DAOException;

	/**
	 * Carga todos los objetos de una tabla de la base de datos
	 * 
	 * @return lista de todos los objetos cargados
	 * @throws DAOException
	 *             error cargando los objetos
	 */
	public List<T> findAll() throws DAOException;

	/**
	 * Asigna Indicador para re-usar session
	 * 
	 * @param reuseSession
	 *            Booleano indicando si va a reusar sesion
	 */
	public void setReuseSession(boolean reuseSession);

	/**
	 * Obtiene Indicador de Reutilizacion de la session de la base de datos
	 * 
	 * @return Indicador de reuso de Session
	 */
	public boolean getReuseSession();

	/**
	 * Inicia Operación de Base de datos
	 * 
	 * @exception DAOException
	 *                Error Iniciando Operacion
	 */
	public void initDbOperation() throws DAOException;

	/**
	 * Finaliza Operacion de Base de datos
	 * 
	 * @exception DAOException
	 *                Error Finalizando Operacion
	 */
	public void finishDBOperation() throws DAOException;

	/**
	 * Encuentra Objetos dadas condiciones sobre sus campos
	 * 
	 * @param conditions
	 *            Condiciones de los campos
	 * @return Lista de objetos que cumplen las condiciones
	 * @throws DAOException
	 *             Error en la base de datos
	 */
	public List<T> findByConditions(FieldCondition[] conditions)
			throws DAOException;

}
