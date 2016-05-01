/**
 * 
 */
package edu.upm.spbw.bbeans.phi;

/**
 * Clase que representa un concepto semantico con su operador logico de intra
 * concepto
 * 
 * @author JuanCamilo
 * 
 */
public class LogicalOperatorConcept {

	/**
	 * Codigo de concepto
	 */
	private String cosccosak;

	/**
	 * Descripcion de concepto
	 */
	private String cosdcosaf;

	/**
	 * Operador Logico '1' AND, '2' OR
	 */
	private String operator;

	/**
	 * Constructor
	 */
	public LogicalOperatorConcept() {
	}

	/**
	 * COnstructor
	 * 
	 * @param cosccosak
	 *            Codigo de concepto
	 * @param cosdcosaf
	 *            Descripcion de concepto
	 * @param operator
	 *            Operador logico
	 */
	public LogicalOperatorConcept(String cosccosak, String cosdcosaf,
			String operator) {
		this.cosccosak = cosccosak;
		this.cosdcosaf = cosdcosaf;
		this.operator = operator;
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
	 * @return the cosdcosaf
	 */
	public String getCosdcosaf() {
		return cosdcosaf;
	}

	/**
	 * @param cosdcosaf
	 *            the cosdcosaf to set
	 */
	public void setCosdcosaf(String cosdcosaf) {
		this.cosdcosaf = cosdcosaf;
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
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
		result = prime * result
				+ ((cosccosak == null) ? 0 : cosccosak.hashCode());
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
		if (!(obj instanceof LogicalOperatorConcept)) {
			return false;
		}
		LogicalOperatorConcept other = (LogicalOperatorConcept) obj;
		if (cosccosak == null) {
			if (other.cosccosak != null) {
				return false;
			}
		} else if (!cosccosak.equals(other.cosccosak)) {
			return false;
		}
		return true;
	}

}
