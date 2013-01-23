package org.osehra.cpe.vpr.viewdef;

import org.osehra.cpe.vpr.queryeng.ViewDef;

public class ViewDefRenderException extends Exception {
	private static final long serialVersionUID = 4151010493860383366L;
	private ViewDef vd;

	public ViewDefRenderException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public ViewDefRenderException(String msg, ViewDef vd) {
		super(msg);
		this.vd = vd;
	}
	
	public ViewDefRenderException(String msg, Throwable cause, ViewDef vd) {
		this(msg, cause);
		this.vd = vd;
	}
}
