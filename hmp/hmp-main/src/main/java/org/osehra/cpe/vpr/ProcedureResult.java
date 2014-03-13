package org.osehra.cpe.vpr;

import org.osehra.cpe.vpr.pom.AbstractPOMObject;
import org.osehra.cpe.vpr.pom.IGenericPatientObjectDAO;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProcedureResult extends AbstractPOMObject {
	@JsonCreator
	public ProcedureResult(Map<String, Object> vals) {
		super(vals);
	}
	
	public ProcedureResult()
	{
		super(null);
	}

	private Long id;
	private String interpretation;
	private String localTitle;
	private String nationalTitle;
	private String nationalTitleCode;
	private String subject;
	private String report; // TODO: how is this different from "document" field?
	private Document document;
	private Procedure procedure;

	public Long getId() {
		return id;
	}

    public String getInterpretation() {
		return interpretation;
	}

	public String getLocalTitle() {
		return localTitle;
	}

	public String getNationalTitle() {
		return nationalTitle;
	}

	public String getNationalTitleCode() {
		return nationalTitleCode;
	}

	public String getSubject() {
		return subject;
	}

	public String getReport() {
		return report;
	}

	public Document getDocument() {
		return document;
	}

	public Procedure getProcedure() {
		return procedure;
	}

    void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    @JsonIgnore
	public void loadDocumentBody(IGenericPatientObjectDAO dao)
	{
		Document doc = dao.findByUID(Document.class, uid);
		if(doc!=null)
		{
			document = doc;//doc.getContent();
		}
	}
	
	@Autowired
	IGenericPatientObjectDAO genericDao;
}
