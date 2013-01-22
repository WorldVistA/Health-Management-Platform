package EXT.DOMAIN.cpe.vpr.queryeng;

import EXT.DOMAIN.cpe.vpr.VprConstants;
import EXT.DOMAIN.cpe.vpr.pom.jds.JdsOperations;
import EXT.DOMAIN.cpe.vpr.ws.link.OpenInfoButtonLinkGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "EXT.DOMAIN.cpe.vpr.queryeng.StudiesViewDef")
@Scope("prototype")
public class StudiesViewDef extends MergedDocumentsViewDef {
	@Autowired 
	public StudiesViewDef(OpenInfoButtonLinkGenerator linkgen, JdsOperations jdsTemplate, Environment environ) {
		super(linkgen, environ);
		declareParam(new ViewParam.ViewInfoParam(this, "Studies"));
	}
	protected String[][] getInFilter() {
		String[][] rslt = {
				{(env.acceptsProfiles(VprConstants.JSON_DATASTORE_PROFLE, VprConstants.MONGO_DATASTORE_PROFLE)?"kind":"TYPE"),
					"Procedure", "Imaging", "Radiology Report", "Surgery", "Surgery Report"}
		};
		return rslt;
	}
}
