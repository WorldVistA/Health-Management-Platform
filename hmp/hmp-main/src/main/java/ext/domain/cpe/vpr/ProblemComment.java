package EXT.DOMAIN.cpe.vpr;

import java.util.Map;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.vpr.pom.AbstractPOMObject;

public class ProblemComment extends AbstractPOMObject{

	private Long id;

	/**
	 * Timestamp for when the comment was entered (required for VistA)
	 */
	private PointInTime entered;
	/**
	 * Display value for the person who entered the comment
	 */
//	private String enteredBy;
	private String enteredByName;
	private String enteredByCode;
	/**
	 * Text of the comment. For VistA, this is
	 */
	private String comment;

	public ProblemComment() {
		super(null);
	}
	
	public ProblemComment(Map<String, Object> vals) {
		super(vals);
	}

	public Long getId() {
		return id;
	}

	public PointInTime getEntered() {
		return entered;
	}

	public String getEnteredByName() {
		return enteredByName;
	}

	public String getEnteredByCode() {
		return enteredByCode;
	}

	public String getComment() {
		return comment;
	}
}
