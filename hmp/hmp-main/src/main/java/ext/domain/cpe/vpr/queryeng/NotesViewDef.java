package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.vpr.VprConstants;
import org.osehra.cpe.vpr.pom.jds.JdsOperations;
import org.osehra.cpe.vpr.ws.link.OpenInfoButtonLinkGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component(value = "org.osehra.cpe.vpr.queryeng.NotesViewDef")
@Scope("prototype")
public class NotesViewDef extends MergedDocumentsViewDef {
	
	@Autowired 
	public NotesViewDef(OpenInfoButtonLinkGenerator linkgen, JdsOperations jdsTemplate, Environment environ) {
		super(linkgen, environ);
		declareParam(new ViewParam.ViewInfoParam(this, "Notes"));
	}
	protected String[][] getInFilter() {
		String[][] rslt = {
			{(env.acceptsProfiles(VprConstants.JSON_DATASTORE_PROFLE, VprConstants.MONGO_DATASTORE_PROFLE)?"kind":"TYPE"),
				"Progress Note", "Consult Report", "Laboratory Report", "Discharge Summary", "Consult", "Unknown", "Advance Directive", "Crisis Note", "Allergy/Adverse Reaction", "Clinical Warning"}
		};
		return rslt;
	}
}
