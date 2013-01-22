package EXT.DOMAIN.cpe.vpr.pom;

import EXT.DOMAIN.cpe.vpr.Patient;
import EXT.DOMAIN.cpe.vpr.frameeng.IFrameEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

/**
 */
public class PatientEvent<T extends IPatientObject> extends IFrameEvent<T> {
	private static final long serialVersionUID = 4146409780890124384L;
	public static enum Type {
		CREATE, UPDATE, EVAL
	}
	
	public static class Change implements Serializable {
		private static final long serialVersionUID = 1L;
		public String FIELD;
		public Serializable OLD_VALUE;
		public Serializable NEW_VALUE;
		
		public Change(String field, Serializable oldVal, Serializable newVal) {
			FIELD = field;
			OLD_VALUE = oldVal;
			NEW_VALUE = newVal;
		}
		
		@Override
		public String toString() {
			return FIELD + ": " + OLD_VALUE + " -> " + NEW_VALUE;
		}
	}

	private String pid;
	private String uid;
	private Type type;
	private List<Change> changes;
	private transient Patient patient;

	public PatientEvent(T source) {
		this(source, Type.EVAL, null);
	}
	
	public PatientEvent(T source, Type type, List<Change> changes) {
		super(source);
		this.pid = source.getPid();
		this.uid = source.getUid();
		this.type = type;
		this.changes = (changes == null) ? new ArrayList<Change>() : changes;
	}
	
	public void reconsitute(IGenericPatientObjectDAO dao, IPatientDAO patdao) {
		if (this.source == null) {
			this.source = dao.findByUID(getSourceClass(), getUID());
		}
		if (this.patient == null) {
			this.patient = patdao.findByAnyPid(getPID());
		}
	}
	
	public List<Change> getChanges() {
		return (changes == null) ? new ArrayList<Change>() : changes;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getPID() {
		return this.pid;
	}
	
	public String getUID() {
		return this.uid;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T getSource() {
		return (T) super.getSource();
	}
	
	public Patient getPatient() {
		return this.patient;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + "<" + getSourceClass().getSimpleName() + ">: " + ((getType() == Type.CREATE) ? "CREATE" : getChanges());
	}
}
