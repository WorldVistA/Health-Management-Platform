package org.osehra.cpe.vpr.termeng;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for defining data sources for the terminology engine.
 * 
 * There are currently two major types of data 1) Mapping Sets, 2) Code Systems
 * 
 * Implementors may choose how do do many of the following things: 1) Data
 * Source (file, DB, WebService, etc.) 2) Source Data Format (XML, CSV, Tabular,
 * etc.) 3) Caching/Memory Mapping: Can the entire dataset fit into memory or
 * should it be paged to disk? 4) Lazy/Eager loading: Should the data set be
 * loaded all at once at the beginning? Or gradually as needed?
 * 
 * TODO: Should this interface be broken down into two seperate interfaces?
 * (Probably) TODO: CodeSystem needs an object and a registry.
 * 
 */
public interface ITermDataSource {

	public boolean contains(String urn);
	public Set<String> getCodeSystemList();
	public Map<String,Object> getCodeSystemMap();
	public List<String> search(String text); // optional, may return null?

	// Relationship methods ---------------------------------------------------

	public Set<String> getAncestorSet(String urn);
	public Set<String> getEquivalentSet(String urn);
	public Set<String> getParentSet(String urn);
	public Map<String, String> getRelMap(String urn);

	public String getDescription(String urn);
	public Map<String, Object> getConceptData(String urn);

}
