package EXT.DOMAIN.cpe.vpr.termeng;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractTermDataSource implements ITermDataSource {
	
	@Override
	public List<String> search(String text) {
		return null;
	}
	
	@Override
	public boolean contains(String urn) {
		return getConceptData(urn) != null;
	}

	@Override
	public String getDescription(String urn) {
		Map<String, Object> data = getConceptData(urn);
		if (data != null && data.containsKey("description")) {
			return (String) data.get("description");
		}
		return null;
	}
	
	@Override
	public Set<String> getAncestorSet(String urn) {
		Map<String, Object> data = getConceptData(urn);
		if (data != null && data.containsKey("ancestors")) {
			return (Set<String>) data.get("ancestors");
		}
		return new HashSet<String>();
	}

	@Override
	public Set<String> getEquivalentSet(String urn) {
		Map<String, Object> data = getConceptData(urn);
		if (data != null && data.containsKey("sameas")) {
			return (Set<String>) data.get("sameas");
		}
		return new HashSet<String>();
	}

	@Override
	public Set<String> getParentSet(String urn) {
		Map<String, Object> data = getConceptData(urn);
		if (data != null && data.containsKey("parents")) {
			return (Set<String>) data.get("parents");
		}
		return new HashSet<String>();
	}
	
	@Override
	public Map<String, String> getRelMap(String urn) {
		Map<String, Object> data = getConceptData(urn);
		if (data != null && data.containsKey("rels")) {
			return (Map<String,String>) data.get("rels");
		}
		return new HashMap<String,String>();
	}

}
