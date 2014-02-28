package org.osehra.cpe.vpr.queryeng.dynamic;

import org.osehra.cpe.vpr.queryeng.ViewDef;

public interface IDynamicViewDefService {
	public ViewDef getViewDefByName(String name);
	public void setViewDefDef(ViewDefDef def);
}
