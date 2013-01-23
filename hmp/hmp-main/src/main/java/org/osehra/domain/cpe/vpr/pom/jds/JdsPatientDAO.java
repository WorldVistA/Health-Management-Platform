package org.osehra.cpe.vpr.pom.jds;

import org.osehra.cpe.jsonc.JsonCCollection;
import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.pom.IPatientDAO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JdsPatientDAO extends JdsPatientObjectDAO<Patient> implements IPatientDAO {

    public JdsPatientDAO() {
        super(Patient.class);
    }

    @Override
    public Patient findByIcn(String icn) {
        return jdsTemplate.getForObject("/vpr/pid/" + icn, Patient.class);
    }

    @Override
    public Patient findByAnyPid(String pid) {
        return jdsTemplate.getForObject("/vpr/pid/" + pid, Patient.class);
    }

    @Override
    public Patient findByVprPid(String pid) {
        return jdsTemplate.getForObject("/vpr/" + pid.toString(), Patient.class);
    }

    @Override
    public Patient findByLocalID(String vistaIdOrFacilityCode, String dfn) {
    	return findByAnyPid(getQualifiedDfn(vistaIdOrFacilityCode, dfn));
    }

    private String getQualifiedDfn(String vistaIdOrFacilityCode, String dfn) {
        return vistaIdOrFacilityCode + ";" + dfn;
    }

    @Override
    public Page<Patient> findAll(Pageable pageable) {
    	//TODO: pageable need to be implemented in the db
    	//Will offset, page size and sort from pageable apply to list of pid or we need something better? 
    	List<String> pids = listPatientIds();
    	List<Patient> patients = new ArrayList<Patient>();
    	
    	for (String pid : pids) {
    		Patient patient = findByVprPid(pid);
    		patients.add(patient);
    	}
    	
		return new PageImpl<Patient>(patients, pageable, (pids!= null)?pids.size():0);		
    }

    @Override
    public List<String> listPatientIds() {
    	JsonNode json = jdsTemplate.getForJsonNode("/vpr/all/index/pid/pid");
    	JsonNode items = json.path("data").path("items");
        if (!items.isArray()) throw new DataRetrievalFailureException("expected data.items node in JSON response");
        ArrayList<String> rslt = new ArrayList<String>();
        for (JsonNode item : items) {
            rslt.add(item.asText());
        }
    	return rslt;
    }

    @Override
    public int count() {
        JsonCCollection<Map<String, Object>> jsonC = jdsTemplate.getForJsonC("/vpr/all/count/patient");
        if(jsonC.getItems().size()==0)
        {
        	return 0;
        }
        Map<String, Object> topicCount = jsonC.getItems().get(0);
        return (Integer) topicCount.get("count");
    }
}
