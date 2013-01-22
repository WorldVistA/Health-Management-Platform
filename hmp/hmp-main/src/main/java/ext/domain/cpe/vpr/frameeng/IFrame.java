package EXT.DOMAIN.cpe.vpr.frameeng;

import EXT.DOMAIN.cpe.vpr.frameeng.Frame.FrameInitException;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameJob.FrameTask;
import EXT.DOMAIN.cpe.vpr.queryeng.ViewParam;

import java.net.URI;
import java.util.Map;
import java.util.Set;


public interface IFrame {
	public String getID();
	public String getName();
	public URI getResource();
	public Map<String, Object> getAppInfo();
	public Map<String, Object> getMeta();
	public Set<ViewParam> getParamDefs();
	public Map<String, Object> getParamDefaultVals();
	public IFrameTrigger<?> evalTriggers(IFrameEvent<?> event);
	
	// lifecycle methods
	public void validate(FrameTask task) throws FrameInitException;
	public void init(FrameTask task);
	public void exec(FrameTask ctx) throws Exception;
}
