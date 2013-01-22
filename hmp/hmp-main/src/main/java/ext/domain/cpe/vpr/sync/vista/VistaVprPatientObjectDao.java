package EXT.DOMAIN.cpe.vpr.sync.vista;

import com.fasterxml.jackson.databind.JsonNode;
import EXT.DOMAIN.cpe.auth.UserContext;
import EXT.DOMAIN.cpe.vista.rpc.RpcResponse;
import EXT.DOMAIN.cpe.vpr.Patient;
import EXT.DOMAIN.cpe.vpr.pom.IGenericPatientObjectDAO;
import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.POMUtils;
import EXT.DOMAIN.cpe.vpr.web.PatientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.util.Assert;

import java.util.Map;

import static EXT.DOMAIN.cpe.vpr.UserInterfaceRpcConstants.VPR_PUT_PATIENT_DATA_URI;

public class VistaVprPatientObjectDao extends VistaVprObjectDaoSupport implements IVistaVprPatientObjectDao {

    private IPatientDAO jdsPatientDao;
    private IGenericPatientObjectDAO jdsGenericDao;
    private UserContext userContext;

    @Required
    public void setJdsPatientDao(IPatientDAO jdsPatientDao) {
        this.jdsPatientDao = jdsPatientDao;
    }

    @Required
    public void setJdsGenericDao(IGenericPatientObjectDAO jdsGenericDao) {
        this.jdsGenericDao = jdsGenericDao;
    }

    @Autowired
    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    public <T extends IPatientObject> T save(T entity) {
        Assert.hasText(entity.getPid(), "[Assertion failed] - 'pid' must have text; it must not be null, empty, or blank");
        String localPatientId = getLocalPatientId(entity.getPid(), userContext.getCurrentUser().getDivision());
        String requestJsonString = POMUtils.toJSON(entity);
        Map<String, Object> data = doSave(entity.getClass(), localPatientId, requestJsonString);
        entity.setData(data);
        return saveToJds(entity);
    }

    @Override
    public <T extends IPatientObject> T save(Class<T> entityType, Map<String, Object> data) {
        String pid = (String) data.get("pid");
        Assert.hasText(pid, "[Assertion failed] - 'pid' must have text; it must not be null, empty, or blank");
        String localPatientId = getLocalPatientId(pid, userContext.getCurrentUser().getDivision());
        String requestJsonString = POMUtils.toJSON(data);
        Map<String, Object> vals = doSave(entityType, localPatientId, requestJsonString);
        T entity = POMUtils.newInstance(entityType, data);
        entity.setData(vals);
        return saveToJds(entity);
    }

    private <T extends IPatientObject> Map<String, Object> doSave(Class<T> entityType, String localPatientId, String requestJsonString) {
        JsonNode responseJson = executeForJsonAndSplitLastArg(VPR_PUT_PATIENT_DATA_URI, localPatientId, getCollectionName(entityType), requestJsonString);
        if (!responseJson.path("success").booleanValue()) {
            throw new DataRetrievalFailureException("Unable to save " + entityType.getName() + " to VPR PATIENT OBJECT file.");
        }
        return POMUtils.convertNodeToMap(responseJson.path("data"));
    }

    private String getLocalPatientId(String pid, String division) {
        Patient pt = jdsPatientDao.findByVprPid(pid);
        if (pt == null) throw new PatientNotFoundException(pid);
        return pt.getLocalPatientIdForFacility(division);
    }

    private <T extends IPatientObject> T saveToJds(T entity) {
        Assert.hasText(entity.getUid(), "[Assertion failed] - 'uid' must have text; it must not be null, empty, or blank");
        this.jdsGenericDao.save(entity);
        return entity;
    }
}
