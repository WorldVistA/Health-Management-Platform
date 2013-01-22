package EXT.DOMAIN.cpe.vpr;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Task extends AbstractPatientObject implements IPatientObject {

    private static final String TASK = "Task";

    private String facilityCode;
    private String facilityName;
    private String type;
    private String taskName;
    private String assignToName;
    private String assignToCode;
    private String ownerName;
    private String ownerCode;
    private String description;
    private Boolean completed;
    private PointInTime dueDate;

    public Task() {
        super(null);
    }

    @JsonCreator
    public Task(Map<String, Object> vals) {
        super(vals);
    }

	public String getTaskName() {
        return taskName;
    }

    public String getAssignToName() {
		return assignToName;
	}

	public String getAssignToCode() {
		return assignToCode;
	}

    public String getOwnerName() {
		return ownerName;
	}

	public String getOwnerCode() {
		return ownerCode;
	}

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getCompleted() {
        return completed;
    }


    public String getFacilityCode() {
        return facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getKind() {
        return TASK;
    }

    public PointInTime getDueDate() {
        return dueDate;
    }

    @Override
    public String getSummary() {
        return taskName;
    }

}
