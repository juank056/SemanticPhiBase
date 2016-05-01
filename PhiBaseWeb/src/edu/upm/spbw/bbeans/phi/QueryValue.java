/**
 * 
 */
package edu.upm.spbw.bbeans.phi;

import edu.upm.spbw.utils.ApplicationMessages;
import edu.upm.spbw.utils.Constants;

/**
 * Clase que representa un valor de resultado de una query
 * 
 * @author JuanCamilo
 * 
 */
public class QueryValue {

	/**
	 * Tipo de valor
	 */
	private String type;

	/**
	 * Descripción del valor
	 */
	private String description;

	/**
	 * IRI del objeto
	 */
	private String objectIri;

	/**
	 * Clase del objeto
	 */
	private String styleClass;

	/**
	 * Constructor
	 */
	public QueryValue() {

	}

	/**
	 * Construye un nuevo Query Value
	 * 
	 * @param type
	 *            Tipo de valor (RDF:TYPE)
	 * @param description
	 *            Descripción del valor
	 * @param objectIri
	 *            Iri del objeto representado
	 */
	public QueryValue(String type, String description, String objectIri) {
		this.type = type;
		this.description = description;
		// Si la Iri es sin informacion se blanquea
		if (!ApplicationMessages.getMessage("USR0037").equals(objectIri))
			this.objectIri = objectIri;
		else
			/* No hay IRI del objeto */
			this.objectIri = Constants.BLANKS;
		// Revisa si hay IRI
		if (!Constants.BLANKS.equals(this.objectIri)) {
			// Asigna class link
			this.styleClass = Constants.LINK;
		} else {/* No hay IRI */
			// Blancos
			this.styleClass = Constants.BLANKS;
		}
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the objectIri
	 */
	public String getObjectIri() {
		return objectIri;
	}

	/**
	 * @param objectIri
	 *            the objectIri to set
	 */
	public void setObjectIri(String objectIri) {
		this.objectIri = objectIri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QueryValue ["
				+ (type != null ? "type=" + type + ", " : "")
				+ (description != null ? "description=" + description + ", "
						: "")
				+ (objectIri != null ? "objectIri=" + objectIri : "") + "]";
	}

	/**
	 * @return the styleClass
	 */
	public String getStyleClass() {
		return styleClass;
	}

	/**
	 * @param styleClass
	 *            the styleClass to set
	 */
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

}
