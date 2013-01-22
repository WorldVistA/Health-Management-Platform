package EXT.DOMAIN.cpe.vpr;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject;

import java.util.Map;

public class AllergyComment extends AbstractPOMObject {
	private Long id;
	private Long version;
	/**
	 * Timestamp for when the comment was entered (required for VistA)
	 */
	private PointInTime entered;
	/**
	 * Display value for the person who entered the comment
	 */
	private String enteredByName;
	/**
	 * Text of the comment. For VistA, this is
	 */
	private String comment;

    public AllergyComment() {
        super(null);
    }

    public AllergyComment(Map<String, Object> vals) {
        super(vals);
    }

    public PointInTime getEntered() {
		return entered;
	}

	public String getEnteredByName() {
		return enteredByName;
	}

	public String getComment() {
		return comment;
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

}
