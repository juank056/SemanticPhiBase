/**
 * 
 */
package edu.upm.spbw.bbeans.phi;

import javax.swing.tree.DefaultMutableTreeNode;

import com.icesoft.faces.component.tree.IceUserObject;

/**
 * Clase para manejar la representacion de los nodos de un arbol
 * 
 * @author Juan Camilo
 * 
 */
public class TreeUserObject extends IceUserObject {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param wrapper
	 *            El tree node que lo encapsula
	 */
	public TreeUserObject(DefaultMutableTreeNode wrapper) {
		super(wrapper);
	}

	/****************************
	 * ATRIBUTOS ADICIONALES
	 ***************************/

	/**
	 * Identificador para poder saber de que objeto se trata
	 */
	private String nodeId;

	/**
	 * Tipo de nodo '1' Concepto, '2' Relacion
	 */
	private String nodeType;

	/**
	 * Obtiene el Id del nodo
	 * 
	 * @return Id del nodo
	 */
	public String getNodeId() {
		return nodeId;
	}

	/**
	 * Asigna Id del nodo
	 * 
	 * @param nodeId
	 *            Id del nodo
	 */
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * @param nodeType
	 *            the nodeType to set
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

}
