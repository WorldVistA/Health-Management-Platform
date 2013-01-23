package org.osehra.cpe.vpr;

import java.util.Map;

import org.osehra.cpe.vpr.pom.AbstractPOMObject;

public class ObservationQualifier extends AbstractPOMObject{
	
	private Long id;
	private Long version;
	private String type;
	private String code;
	private String name;
	
	public ObservationQualifier() {
		super(null);
	}
	
	public ObservationQualifier(Map<String, Object> vals) {
		super(vals);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// TODO - delete when checked
	// static belongsTo = [Observation]
	//
	// static constraints = {
	// type(nullable: false)
	// code(nullable: true)
	// name(nullable: false)
	// }
}
