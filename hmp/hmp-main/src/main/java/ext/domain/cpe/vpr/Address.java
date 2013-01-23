package org.osehra.cpe.vpr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonView;
import org.osehra.cpe.vpr.pom.AbstractPOMObject;
import org.osehra.cpe.vpr.pom.JSONViews;

import java.util.Map;

public class Address extends AbstractPOMObject{

	private Long id;
	private Long version;
	private String city;
	private String country;
	private String postalCode;
	private String stateProvince;
	private String streetLine1;
	private String streetLine2;
	
	public Address() {
		super(null);
	}
	
	@JsonCreator
	public Address(Map<String, Object> vals) {
		super(vals);
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getStateProvince() {
		return stateProvince;
	}

	public void setStateProvince(String stateProvince) {
		this.stateProvince = stateProvince;
	}

	public String getStreetLine1() {
		return streetLine1;
	}

	public void setStreetLine1(String streetLine1) {
		this.streetLine1 = streetLine1;
	}

	public String getStreetLine2() {
		return streetLine2;
	}

	public void setStreetLine2(String streetLine2) {
		this.streetLine2 = streetLine2;
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
	
	@Override
	@JsonView(JSONViews.WSView.class) // dont store in DB
	public String getSummary() {
		return String.format("%s %s, %s %s", streetLine1, city, stateProvince, postalCode);
	}


}
