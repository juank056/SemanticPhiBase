/**
 * 
 */
package edu.upm.spbw.bbeans.phi;

import edu.upm.spbw.persistence.bo.Sedconce;
import edu.upm.spbw.utils.Constants;

/**
 * Objeto que representa un concepto de busqueda
 * 
 * @author JuanCamilo
 * 
 */
public class SearchConcept {

	/**
	 * Identificador
	 */
	private int identifier;

	/**
	 * Codigo de concepto seleccionado
	 */
	private String cosccosak;

	/**
	 * Codigo de relacion de concepto seleccionada
	 */
	private String rcocrcoak;

	/**
	 * Descripcion concepto-relacion
	 */
	private String description;

	/**
	 * Texto de autocompletar
	 */
	private String autocomplete;

	/**
	 * Registro Sedconce seleccionado
	 */
	private Sedconce conce;

	/**
	 * Icono de registro seleccionado
	 */
	private String icon;

	/**
	 * Constructor
	 */
	public SearchConcept() {

	}

	/**
	 * Constructor de un concepto de busqueda
	 * 
	 * @param identifier
	 *            Identificador del registro
	 * @param cosccosak
	 *            Codigo de concepto
	 * @param rcocrcoak
	 *            Codigo de relacion
	 * @param description
	 *            Descripcion de COncepto-Relacion
	 * @param autocomplete
	 *            Texto de Autocompletar ingresado
	 * @param relco
	 *            Registro sedrelco seleccionado
	 * @param conce
	 *            Registro sedconce seleccionado
	 */
	public SearchConcept(int identifier, String cosccosak, String rcocrcoak,
			String description, String autocomplete, Sedconce conce) {
		this.identifier = identifier;
		this.cosccosak = cosccosak;
		this.rcocrcoak = rcocrcoak;
		this.description = description;
		this.autocomplete = autocomplete;
		this.conce = conce;
		// Icono
		this.icon = Constants.ICON_NOTFOUND;
	}

	/**
	 * @return the cosccosak
	 */
	public String getCosccosak() {
		return cosccosak;
	}

	/**
	 * @param cosccosak
	 *            the cosccosak to set
	 */
	public void setCosccosak(String cosccosak) {
		this.cosccosak = cosccosak;
	}

	/**
	 * @return the rcocrcoak
	 */
	public String getRcocrcoak() {
		return rcocrcoak;
	}

	/**
	 * @param rcocrcoak
	 *            the rcocrcoak to set
	 */
	public void setRcocrcoak(String rcocrcoak) {
		this.rcocrcoak = rcocrcoak;
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
	 * @return the autocomplete
	 */
	public String getAutocomplete() {
		return autocomplete;
	}

	/**
	 * @param autocomplete
	 *            the autocomplete to set
	 */
	public void setAutocomplete(String autocomplete) {
		this.autocomplete = autocomplete;
	}

	/**
	 * @return the conce
	 */
	public Sedconce getConce() {
		return conce;
	}

	/**
	 * @param conce
	 *            the conce to set
	 */
	public void setConce(Sedconce conce) {
		this.conce = conce;
	}

	/**
	 * @return the identifier
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + identifier;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SearchConcept)) {
			return false;
		}
		SearchConcept other = (SearchConcept) obj;
		if (identifier != other.identifier) {
			return false;
		}
		return true;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchConcept [identifier="
				+ identifier
				+ ", "
				+ (cosccosak != null ? "cosccosak=" + cosccosak + ", " : "")
				+ (rcocrcoak != null ? "rcocrcoak=" + rcocrcoak + ", " : "")
				+ (description != null ? "description=" + description + ", "
						: "")
				+ (autocomplete != null ? "autocomplete=" + autocomplete + ", "
						: "") + (conce != null ? "conce=" + conce + ", " : "")
				+ (icon != null ? "icon=" + icon : "") + "]";
	}

}
