package EXT.DOMAIN.cpe.vpr.queryeng.dynamic;

import EXT.DOMAIN.cpe.vpr.queryeng.ViewDef;

public interface IDynamicViewDefService {
	public ViewDef getViewDefByName(String name);
	public void setViewDefDef(ViewDefDef def);
}
