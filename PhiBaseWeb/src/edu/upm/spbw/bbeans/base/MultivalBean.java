/**
 * 
 */
package edu.upm.spbw.bbeans.base;

import java.io.Serializable;

import javax.faces.model.SelectItem;

import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;

/**
 * @author Juan Camilo Bean para obtener los multivalores de los diferentes
 *         campos del Sistema Los Campos Obtenidos seran un arreglo de MenuItem
 *         siempre con el Seleccione en la Primera Posicion. El nombre del
 *         Metodo empieza por get seguido del Nombre de la tabla seguido del
 *         Nombre del campo multivalor
 */
public class MultivalBean implements Serializable {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor del Bean de multivalores
	 */
	public MultivalBean() {
	}

	/**
	 * 
	 * Multivalor de Tipo de Parametro de la tabla de parametros del sistema
	 */
	public SelectItem[] getCspparsi_F_psitparvf() {
		return new SelectItem[] {
				new SelectItem(Constants.BLANKS,
						ApplicationMessages.getMessage("seleccione")),
				new SelectItem(Constants.ONE,
						ApplicationMessages.getMessage("cspparsi_psitparvf_1")),
				new SelectItem(Constants.TWO,
						ApplicationMessages.getMessage("cspparsi_psitparvf_2")) };
	}

	/**
	 * 
	 * Multivalor de estado del usuario
	 */
	public SelectItem[] getUsmusuar_F_usuestavf() {
		return new SelectItem[] {
				new SelectItem(Constants.BLANKS,
						ApplicationMessages.getMessage("seleccione")),
				new SelectItem(Constants.ONE,
						ApplicationMessages.getMessage("usmusuar_usuestavf_1")),
				new SelectItem(Constants.TWO,
						ApplicationMessages.getMessage("usmusuar_usuestavf_2")) };
	}

	/**
	 * Obtiene un valor para multivalor
	 * 
	 * @param val
	 *            Valor del multivalor
	 * @return Valor obtenido del multivalor
	 */
	public String value(int val) {
		return Constants.BLANKS + val;
	}
}
