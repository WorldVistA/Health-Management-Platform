package EXT.DOMAIN.cpe.vpr;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject;

public class DocumentClinician extends AbstractPOMObject {
	
	@JsonCreator
	public DocumentClinician(Map<String, Object> vals) {
		super(vals);
	}
	
	public DocumentClinician()
	{
		super(null);
	}

	private Long id;
	private Clinician clinician;
	private Document document;
	private String name;
	private String localId;
	private String role;
	private PointInTime signedDateTime;
	private String signature;

	public Long getId() {
		return id;
	}

	public Clinician getClinician() {
		return clinician;
	}

	public Document getDocument() {
		return document;
	}

    public String getLocalId() {
		return localId;
	}

	public String getRole() {
		return role;
	}

	public PointInTime getSignedDateTime() {
		return signedDateTime;
	}

	public String getSignature() {
		return signature;
	}

	public String getName() {
		return name;
	}
}
