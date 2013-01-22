package EXT.DOMAIN.cpe.vpr;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonView;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject;
import EXT.DOMAIN.cpe.vpr.pom.JSONViews;


public class Telecom extends AbstractPOMObject {

	private Long id;
	private Long version;
//    IntervalOfTime period
    private String telecom;
//    private ContactUsage usageType;
    private String usageCode;
    private String usageName;
    
    public Telecom() {
    	super(null);
    }
    
    @JsonCreator
    public Telecom(Map<String, Object> vals) {
    	super(vals);
    }
    
    public Telecom(Map<String, Object> vals, Long id, Long version,
			String telecom, String usageCode, String usageName) {
		super(vals);
		this.id = id;
		this.version = version;
		this.telecom = telecom;
		this.usageCode = usageCode;
		this.usageName = usageName;
	}


	public String toString() {
        return telecom;
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

	public String getTelecom() {
		return telecom;
	}

	public void setTelecom(String telecom) {
		this.telecom = telecom;
	}

	public String getUsageCode() {
		return usageCode;
	}

	public void setUsageCode(String usageCode) {
		this.usageCode = usageCode;
	}

	public String getUsageName() {
		return usageName;
	}

	public void setUsageName(String usageName) {
		this.usageName = usageName;
	}
	
	@Override
	@JsonView(JSONViews.WSView.class) // dont store in DB
	public String getSummary() {
		return String.format("%s (%s)", this.telecom, this.usageName);
	}


}
