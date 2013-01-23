package org.osehra.cpe.vpr;

import org.osehra.cpe.vpr.pom.AbstractPOMObject;

import java.util.Map;

public class LastViewed extends AbstractPOMObject {
	private String userId;

    public LastViewed() {
        super(null);
    }

    public LastViewed(Map<String, Object> vals) {
        super(vals);
    }

    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		//this.setData("userId", userId);
		this.userId = userId;
	}
}
