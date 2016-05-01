/**
 * 
 */
package edu.upm.spbw.bbeans.base;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import edu.upm.spbw.utils.Constants;

/**
 * Bean para invocar los metodos de otros bean que heredan de BaseBean o que
 * Implementan Interfaces especificas para acciones especificas.
 * 
 * @author Juan Camilo
 * 
 */
public class InvokeMethodBean implements Serializable {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor del Bean
	 */
	public InvokeMethodBean() {/* No hace nada */
	}

	/**
	 * Metodo para iniciar la visualizacion de un display de un Bean
	 * 
	 * @param e
	 *            Evento
	 */
	public void initDisplay(ActionEvent e) {
		// Obtiene parametro del Nombre del Bean a obtener
		String beanName = (String) e.getComponent().getAttributes()
				.get(Constants.BEAN_NAME);
		// Obtiene el Backing Bean con ese nombre
		BaseBean<?, ?> bean = this.getBackingBaseBean(beanName);
		// Llama al metodo de Display del Bean
		bean.displayRecord(e);
	}

	/**
	 * Metodo para iniciar la visualizacion de un add de un Bean Tambien se usa
	 * para realizar ya el guardado del registro Comportamiento depende del
	 * valor del Campo stdfunreq
	 * 
	 * @param e
	 *            Evento
	 */
	public void initAdd(ActionEvent e) {
		// Obtiene parametro del Nombre del Bean a obtener
		String beanName = (String) e.getComponent().getAttributes()
				.get(Constants.BEAN_NAME);
		// Obtiene el Backing Bean con ese nombre
		BaseBean<?, ?> bean = this.getBackingBaseBean(beanName);
		// Llama al metodo de Add del Bean enviado
		bean.addRecord(e);
	}

	/**
	 * Metodo para iniciar la visualizacion de un change de un Bean Tambien se
	 * usa para ya realizar la actualizacion del registro Comportamiento depende
	 * del valor del Campo stdfunreq
	 * 
	 * @param e
	 *            Evento
	 */
	public void initChange(ActionEvent e) {
		// Obtiene parametro del Nombre del Bean a obtener
		String beanName = (String) e.getComponent().getAttributes()
				.get(Constants.BEAN_NAME);
		// Obtiene el Backing Bean con ese nombre
		BaseBean<?, ?> bean = this.getBackingBaseBean(beanName);
		// Llama al metodo de Change del Bean enviado
		bean.changeRecord(e);
	}

	/**
	 * Metodo para iniciar el Delete de un Backing Bean
	 * 
	 * @param e
	 *            Evento
	 */
	public void initDelete(ActionEvent e) {
		// Obtiene parametro del Nombre del Bean a obtener
		String beanName = (String) e.getComponent().getAttributes()
				.get(Constants.BEAN_NAME);
		// Obtiene el Backing Bean con ese nombre
		BaseBean<?, ?> bean = this.getBackingBaseBean(beanName);
		// Llama al metodo de Delete del Bean Enviado
		bean.deleteRecord(e);
	}

	/**
	 * Metodo para cancelar alguna accion que se realice de un Bean
	 * 
	 * @param e
	 *            el evento
	 */
	public void cancelAction(ActionEvent e) {
		// Obtiene parametro del Nombre del Bean a obtener
		String beanName = (String) e.getComponent().getAttributes()
				.get(Constants.BEAN_NAME);
		// Obtiene el Backing Bean con ese nombre
		BaseBean<?, ?> bean = this.getBackingBaseBean(beanName);
		// Llama al metodo de Cancel del Bean Enviado
		bean.cancel(e);
	}

	/**
	 * Metodo para manejar el receiveParameters de un Bean
	 * 
	 * @param e
	 *            el evento
	 */
	public void receiveParameters(ActionEvent e) {
		// Obtiene parametro del Nombre del Bean a obtener
		String beanName = (String) e.getComponent().getAttributes()
				.get(Constants.BEAN_NAME);
		// Obtiene el Backing Bean con ese nombre
		BaseBean<?, ?> bean = this.getBackingBaseBean(beanName);
		if (bean != null) {
			// Llama al metodo de ReceiveParameters del Bean
			bean.receiveParameters(e);
		}
	}

	/**
	 * Metodo para obtener un Backing Bean (de Session o Aplicacion) que herede
	 * de BaseBean
	 * 
	 * @param backingBeanName
	 *            el nombre del Backing Bean a Obtener
	 * @return el Backing Bean obtenido. Retorna null si no se encuentra el
	 *         objeto
	 */
	private BaseBean<?, ?> getBackingBaseBean(String backingBeanName) {
		// Obtiene la session actual
		HttpSession s = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		return (BaseBean<?, ?>) s.getAttribute(backingBeanName);
	}

}
