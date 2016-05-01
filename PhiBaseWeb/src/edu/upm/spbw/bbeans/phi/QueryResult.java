/**
 * 
 */
package edu.upm.spbw.bbeans.phi;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un registro de resultado de ejecutar una query en Semantic
 * PHI-BASE
 * 
 * @author JuanCamilo
 * 
 */
public class QueryResult {

	/*********************************
	 * DEFINICIÓN DE ATRIBUTOS
	 ********************************/

	/**
	 * Interacciones
	 */
	private List<QueryValue> interactions;

	/**
	 * Hosts
	 */
	private List<QueryValue> hosts;

	/**
	 * Pathogens
	 */
	private List<QueryValue> pathogens;

	/**
	 * Interaction Context Mutant Description
	 */
	private List<QueryValue> intContextMutantDesc;

	/**
	 * Disease Names
	 */
	private List<QueryValue> diseaseNames;

	/**
	 * Protocol Description
	 */
	private List<QueryValue> protocolDescriptions;

	/**
	 * Citations
	 */
	private List<QueryValue> citations;

	/**
	 * Alleles
	 */
	private List<QueryValue> alleles;

	/**
	 * Invitro Growth
	 */
	private List<QueryValue> invitroGrowths;

	/**
	 * Lethal Knockout
	 */
	private List<QueryValue> lethalKnockouts;

	/**
	 * Genes
	 */
	private List<QueryValue> genes;

	/**
	 * Gene - LocusId
	 */
	private List<QueryValue> geneLocusIds;

	/**
	 * Gene - Function
	 */
	private List<QueryValue> geneFunctions;

	/**
	 * Gene - Name
	 */
	private List<QueryValue> geneNames;

	/**
	 * Gene - Accession
	 */
	private List<QueryValue> geneAccessions;

	/**
	 * Constructor
	 */
	public QueryResult() {
		// Interacciones
		interactions = new ArrayList<>();
		// Hosts
		hosts = new ArrayList<>();
		// Pathogens
		pathogens = new ArrayList<>();
		// Interaction Context Mutant Description
		intContextMutantDesc = new ArrayList<>();
		// Disease Names
		diseaseNames = new ArrayList<>();
		// Protocol Description
		protocolDescriptions = new ArrayList<>();
		// Citations
		citations = new ArrayList<>();
		// Alleles
		alleles = new ArrayList<>();
		// Invitro Growth
		invitroGrowths = new ArrayList<>();
		// Lethal Knockout
		lethalKnockouts = new ArrayList<>();
		// Genes
		genes = new ArrayList<>();
		// Gene - LocusId
		geneLocusIds = new ArrayList<>();
		// Gene - Function
		geneFunctions = new ArrayList<>();
		// Gene - Name
		geneNames = new ArrayList<>();
		// Gene - Accession
		geneAccessions = new ArrayList<>();
	}

	/**
	 * @return the interactions
	 */
	public List<QueryValue> getInteractions() {
		return interactions;
	}

	/**
	 * @param interactions
	 *            the interactions to set
	 */
	public void setInteractions(List<QueryValue> interactions) {
		this.interactions = interactions;
	}

	/**
	 * @return the hosts
	 */
	public List<QueryValue> getHosts() {
		return hosts;
	}

	/**
	 * @param hosts
	 *            the hosts to set
	 */
	public void setHosts(List<QueryValue> hosts) {
		this.hosts = hosts;
	}

	/**
	 * @return the pathogens
	 */
	public List<QueryValue> getPathogens() {
		return pathogens;
	}

	/**
	 * @param pathogens
	 *            the pathogens to set
	 */
	public void setPathogens(List<QueryValue> pathogens) {
		this.pathogens = pathogens;
	}

	/**
	 * @return the intContextMutantDesc
	 */
	public List<QueryValue> getIntContextMutantDesc() {
		return intContextMutantDesc;
	}

	/**
	 * @param intContextMutantDesc
	 *            the intContextMutantDesc to set
	 */
	public void setIntContextMutantDesc(List<QueryValue> intContextMutantDesc) {
		this.intContextMutantDesc = intContextMutantDesc;
	}

	/**
	 * @return the diseaseNames
	 */
	public List<QueryValue> getDiseaseNames() {
		return diseaseNames;
	}

	/**
	 * @param diseaseNames
	 *            the diseaseNames to set
	 */
	public void setDiseaseNames(List<QueryValue> diseaseNames) {
		this.diseaseNames = diseaseNames;
	}

	/**
	 * @return the protocolDescriptions
	 */
	public List<QueryValue> getProtocolDescriptions() {
		return protocolDescriptions;
	}

	/**
	 * @param protocolDescriptions
	 *            the protocolDescriptions to set
	 */
	public void setProtocolDescriptions(List<QueryValue> protocolDescriptions) {
		this.protocolDescriptions = protocolDescriptions;
	}

	/**
	 * @return the citations
	 */
	public List<QueryValue> getCitations() {
		return citations;
	}

	/**
	 * @param citations
	 *            the citations to set
	 */
	public void setCitations(List<QueryValue> citations) {
		this.citations = citations;
	}

	/**
	 * @return the alleles
	 */
	public List<QueryValue> getAlleles() {
		return alleles;
	}

	/**
	 * @param alleles
	 *            the alleles to set
	 */
	public void setAlleles(List<QueryValue> alleles) {
		this.alleles = alleles;
	}

	/**
	 * @return the invitroGrowths
	 */
	public List<QueryValue> getInvitroGrowths() {
		return invitroGrowths;
	}

	/**
	 * @param invitroGrowths
	 *            the invitroGrowths to set
	 */
	public void setInvitroGrowths(List<QueryValue> invitroGrowths) {
		this.invitroGrowths = invitroGrowths;
	}

	/**
	 * @return the lethalKnockouts
	 */
	public List<QueryValue> getLethalKnockouts() {
		return lethalKnockouts;
	}

	/**
	 * @param lethalKnockouts
	 *            the lethalKnockouts to set
	 */
	public void setLethalKnockouts(List<QueryValue> lethalKnockouts) {
		this.lethalKnockouts = lethalKnockouts;
	}

	/**
	 * @return the genes
	 */
	public List<QueryValue> getGenes() {
		return genes;
	}

	/**
	 * @param genes
	 *            the genes to set
	 */
	public void setGenes(List<QueryValue> genes) {
		this.genes = genes;
	}

	/**
	 * @return the geneLocusIds
	 */
	public List<QueryValue> getGeneLocusIds() {
		return geneLocusIds;
	}

	/**
	 * @param geneLocusIds
	 *            the geneLocusIds to set
	 */
	public void setGeneLocusIds(List<QueryValue> geneLocusIds) {
		this.geneLocusIds = geneLocusIds;
	}

	/**
	 * @return the geneFunctions
	 */
	public List<QueryValue> getGeneFunctions() {
		return geneFunctions;
	}

	/**
	 * @param geneFunctions
	 *            the geneFunctions to set
	 */
	public void setGeneFunctions(List<QueryValue> geneFunctions) {
		this.geneFunctions = geneFunctions;
	}

	/**
	 * @return the geneNames
	 */
	public List<QueryValue> getGeneNames() {
		return geneNames;
	}

	/**
	 * @param geneNames
	 *            the geneNames to set
	 */
	public void setGeneNames(List<QueryValue> geneNames) {
		this.geneNames = geneNames;
	}

	/**
	 * @return the geneAccessions
	 */
	public List<QueryValue> getGeneAccessions() {
		return geneAccessions;
	}

	/**
	 * @param geneAccessions
	 *            the geneAccessions to set
	 */
	public void setGeneAccessions(List<QueryValue> geneAccessions) {
		this.geneAccessions = geneAccessions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QueryResult ["
				+ (interactions != null ? "interactions=" + interactions + ", "
						: "")
				+ (hosts != null ? "hosts=" + hosts + ", " : "")
				+ (pathogens != null ? "pathogens=" + pathogens + ", " : "")
				+ (intContextMutantDesc != null ? "intContextMutantDesc="
						+ intContextMutantDesc + ", " : "")
				+ (diseaseNames != null ? "diseaseNames=" + diseaseNames + ", "
						: "")
				+ (protocolDescriptions != null ? "protocolDescriptions="
						+ protocolDescriptions + ", " : "")
				+ (citations != null ? "citations=" + citations + ", " : "")
				+ (alleles != null ? "alleles=" + alleles + ", " : "")
				+ (invitroGrowths != null ? "invitroGrowths=" + invitroGrowths
						+ ", " : "")
				+ (lethalKnockouts != null ? "lethalKnockouts="
						+ lethalKnockouts + ", " : "")
				+ (genes != null ? "genes=" + genes + ", " : "")
				+ (geneLocusIds != null ? "geneLocusIds=" + geneLocusIds + ", "
						: "")
				+ (geneFunctions != null ? "geneFunctions=" + geneFunctions
						+ ", " : "")
				+ (geneNames != null ? "geneNames=" + geneNames + ", " : "")
				+ (geneAccessions != null ? "geneAccessions=" + geneAccessions
						: "") + "]";
	}

}
