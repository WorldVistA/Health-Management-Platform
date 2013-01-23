package org.osehra.cpe.vpr.frameeng;

import org.osehra.cpe.datetime.PointInTime;
import org.osehra.cpe.vpr.PatientAlert;
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO;
import org.osehra.cpe.vpr.queryeng.Table;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;
import org.osehra.cpe.vpr.queryeng.query.QueryDefCriteria;
import org.osehra.cpe.vpr.viewdef.RenderTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO: Should frameaction extend/implement IPOMObject?
 * @author brian
 */
public interface FrameAction {
	
	public class BaseFrameAction implements FrameAction {
		protected static ObjectMapper MAPPER = new ObjectMapper();
	}
	
	public abstract static class PatientAction extends BaseFrameAction {
		private String pid;
		public PatientAction(String pid) {
			this.pid = pid;
		}
		
		public String getType() {
			return getClass().getSimpleName();
		}
		
		public String getPid() {
			return this.pid;
		}
	}
	
	public static interface IPatientSerializableAction {
		public String getUid();
		public String getPid();
		public String toJSON() throws IOException;
	}
	
	public static interface IFrameActionExec {
		public void exec(FrameJob job);
	}

	
	public static class NewInstanceFrameAction extends PatientAction {
		public NewInstanceFrameAction(String pid) {
			super(pid);
		}
	}
	
	public static class ObsRequestAction extends PatientAction {
		private String title;
		private String value;
		
		@JsonCreator
		public ObsRequestAction(Map<String, Object> data) {
			this((String) data.get("pid"), (String) data.get("title"), (String) data.get("value"));
		}

		public ObsRequestAction(String pid, String title, String value) {
			super(pid);
			this.title = title;
			this.value = value;
		}
		
		public String getTitle() {
			return title;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	public static class ObsDateRequestAction extends PatientAction {
		private String title;
		private String value;

		@JsonCreator
		public ObsDateRequestAction(Map<String, Object> data) {
			this((String) data.get("pid"), (String) data.get("title"), (String) data.get("value"));
		}
		
		public ObsDateRequestAction(String pid, String title, String value) {
			super(pid);
			this.title = title;
			this.value = value;
		}
		
		public String getTitle() {
			return title;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	public static class ViewRenderAction extends BaseFrameAction {
		private RenderTask results;

		public ViewRenderAction(RenderTask results) {
			this.results = results;
		}
		
		public RenderTask getResults() {
			return results;
		}
	}
	
	/**
	 * This action represents an item that displays in the Action Menu
	 */
	public static class ActionMenuItem extends BaseFrameAction {
	}
	
	public static class URLActionMenuItem extends ActionMenuItem {
		private String title;
		private String url;
		private String heading;
		private String hint;

		public URLActionMenuItem(String url, String title) {
			this(url, title, null, null);
		}
		
		public URLActionMenuItem(String url, String title, String heading, String hint) {
			this.url = url;
			this.title = title;
			this.hint = hint;
			this.heading = heading;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getHint() {
			return hint;
		}
		
		public String getTitle() {
			return title;
		}
		
		public String getHeading() {
			return heading;
		}
	}
	
	public static class OrderActionMenuItem extends ActionMenuItem {
		public String orderDialogID;
		public String orderMessage;
		public String orderData;

		public OrderActionMenuItem(String orderDialogID, String orderMessage, String orderData) {
			this.orderDialogID = orderDialogID;
			this.orderMessage = orderMessage;
			this.orderData = orderData;
		}
	}
	
	public static class RetractAction extends PatientAction implements IFrameActionExec {
		private String frameID;

		// retract all alert for specified patient from specified frame
		public RetractAction(String pid, String frameID) {
			super(pid);
			assert frameID != null;
			this.frameID = frameID;
		}

		@Override
		public void exec(FrameJob job) {
			IGenericPatientObjectDAO dao = job.getResource(IGenericPatientObjectDAO.class);
			
			// query for alerts with the specified frame
			QueryDef qry = new QueryDef();
			qry.addCriteria(QueryDefCriteria.where("pid").is(getPid()));
			qry.addCriteria(QueryDefCriteria.where("frameID").is(frameID));
			List<PatientAlert> results = dao.findAllByQuery(PatientAlert.class, qry, null);
			for (PatientAlert a : results) {
				dao.deleteByUID(null, a.getUid());
			}
		}
	}
	
}
