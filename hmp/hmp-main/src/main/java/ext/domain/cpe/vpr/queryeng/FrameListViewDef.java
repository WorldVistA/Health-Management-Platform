package EXT.DOMAIN.cpe.vpr.queryeng;

import EXT.DOMAIN.cpe.vpr.frameeng.FrameRegistry;
import EXT.DOMAIN.cpe.vpr.frameeng.FrameRegistry.FrameStats;
import EXT.DOMAIN.cpe.vpr.frameeng.IFrame;
import EXT.DOMAIN.cpe.vpr.queryeng.ColDef.TemplateColDef;
import EXT.DOMAIN.cpe.vpr.viewdef.RenderTask;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "EXT.DOMAIN.cpe.vpr.queryeng.FrameListViewDef") 
@HMPAppInfo(value="EXT.DOMAIN.cpe.nonpatientviewdef", title="Frame List")
@Scope("prototype")
public class FrameListViewDef extends ViewDef {
	
	@Autowired
	public FrameListViewDef(final FrameRegistry registry) {
		declareParam(new ViewParam.SortParam("type", false));
        // list of fields that are not displayable as columns and a default user column set/order
        String displayCols = "id,name,stats";
        String requireCols = "id,name";
        String hideCols = "class,resource,runCount,runAvg";
        String sortCols = "type";
        String groupCols = "type";
        declareParam(new ViewParam.ColumnsParam(this, displayCols, requireCols, hideCols, sortCols, groupCols));
		
		// fetch the frames from the frame registry
        Query primary = addQuery(new Query("id", null) {
				@Override
				public void exec(RenderTask task) throws Exception {
					Iterator<IFrame> itr = registry.iterator();
					while (itr.hasNext()) {
						IFrame frame = itr.next();
						FrameStats stats = registry.getFrameStats(frame);
						task.add(Table.buildRow("id", frame.getID(), "name", frame.getName(), "class", frame.getClass(), "meta", frame.getMeta(),
								"resource", frame.getResource(), "type", frame.getAppInfo().get("type"),
								"runCount", stats.RUN_COUNT, "runAvg", stats.RUNTIME_AVG_MS, 
								"selfLink", "/frame/info?uid=" + frame.getID()));
					}
					
					
				}
			}
        );
        addColumns(primary, "id", "name", "class", "resource", "type", "runCount");
        getColumn("id").setMetaData("width", 250);
        getColumn("name").setMetaData("width", 250);
        getColumn("class").setMetaData("text","Implementation Class").setMetaData("width", 250);
        
        addColumn(new TemplateColDef("stats", "{runCount} <tpl if='runAvg &gt; 1'>(avg: {runAvg}ms)</tpl>"));
    }
}
