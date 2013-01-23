package org.osehra.cpe.team;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.osehra.cpe.vpr.pom.AbstractPOMObject;
import org.osehra.cpe.vpr.pom.JSONViews;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Team extends AbstractPOMObject {

    private String displayName;
    private String ownerUid;
    private String ownerName;
    private Integer rosterId;
    private List<StaffAssignment> staff;

    public Team() {
        super(null);
    }

    public Team(Map<String, Object> vals) {
        super(vals);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Integer getRosterId() {
        return rosterId;
    }

    public List<StaffAssignment> getStaff() {
        return Collections.unmodifiableList(staff);
    }

    @Override
    public String getSummary() {
        return getDisplayName();
    }

    public static class StaffAssignment {
        private TeamPosition position = new TeamPosition();
        private Person person = new Person();

        public StaffAssignment(TeamPosition position, Person person) {
            this.position = position;
            this.person = person;
        }

        @JsonCreator
        public StaffAssignment(Map<String, Object> vals) {
            this.position.setData("uid", vals.get("positionUid"));
            this.position.setData("name", vals.get("positionName"));
            this.person.setData("uid", vals.get("personUid"));
            this.person.setData("name", vals.get("personName"));
        }

        @JsonIgnore
        public TeamPosition getPosition() {
            return position;
        }

        @JsonIgnore
        public Person getPerson() {
            return person;
        }

        public String getPositionUid() {
            return position.getUid();
        }

        public String getPositionName() {
            return position.getName();
        }

        public String getPersonUid() {
            return person.getUid();
        }

        public String getPersonName() {
            return person.getName();
        }

        @JsonView(JSONViews.WSView.class)
        public String getPersonPhotoHref() {
            if (!StringUtils.hasText(person.getUid())) return null;
            return "/person/v1/"+ person.getUid() + "/photo"; // TODO: use link generator or some other decorator during serialization?
        }
    }
}
