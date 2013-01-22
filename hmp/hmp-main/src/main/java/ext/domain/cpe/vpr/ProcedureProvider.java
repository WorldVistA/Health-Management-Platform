package EXT.DOMAIN.cpe.vpr;

import java.util.Map;

import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject;

public class ProcedureProvider extends AbstractPOMObject {
	public ProcedureProvider(Map<String, Object> vals) {
		super(vals);
	}
	
	public ProcedureProvider()
	{
		super(null);
	}

	private Long id;
	private Clinician provider;
	private String name;

	public Long getId() {
		return id;
	}

	public Clinician getProvider() {
		return provider;
	}

	public String getName() {
		return name;
	}
}
