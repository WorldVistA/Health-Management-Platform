package org.osehra.cpe.vpr.sync.vista;

import com.fasterxml.jackson.databind.JsonNode;
import org.osehra.cpe.vpr.pom.IGenericPOMObjectDAO;
import org.osehra.cpe.vpr.pom.IPOMObject;
import org.osehra.cpe.vpr.pom.IPatientObject;
import org.osehra.cpe.vpr.pom.POMUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.util.Assert;

import java.util.Map;

import static org.osehra.cpe.vpr.UserInterfaceRpcConstants.VPR_PUT_OBJECT_RPC_URI;
import static org.osehra.cpe.vpr.UserInterfaceRpcConstants.VPR_PUT_PATIENT_DATA_URI;

public class VistaVprObjectDao extends VistaVprObjectDaoSupport implements IVistaVprObjectDao {

    private IGenericPOMObjectDAO jdsDao;

    @Required
    public void setJdsDao(IGenericPOMObjectDAO jdsDao) {
        this.jdsDao = jdsDao;
    }

    @Override
    public <T extends IPOMObject> T save(T entity) {
        String requestJsonString = POMUtils.toJSON(entity);
        Map<String,Object> data = doSave(entity.getClass(), requestJsonString);
        entity.setData(data);
        return saveToJds(entity);
    }

    @Override
    public <T extends IPOMObject> T save(Class<T> entityType, Map<String, Object> data) {
        String requestJsonString = POMUtils.toJSON(data);
        Map<String,Object> vals = doSave(entityType, requestJsonString);
        T entity = POMUtils.newInstance(entityType, data);
        entity.setData(vals);
        return saveToJds(entity);
    }

    private <T extends IPOMObject> T saveToJds(T entity) {
        Assert.hasText(entity.getUid(), "[Assertion failed] - 'uid' must have text; it must not be null, empty, or blank\"");
        entity = jdsDao.save(entity);
        return entity;
    }

    private <T extends IPOMObject> Map<String, Object> doSave(Class<T> entityType, String requestJsonString) {
        JsonNode responseJson = executeForJsonAndSplitLastArg(VPR_PUT_OBJECT_RPC_URI, getCollectionName(entityType), requestJsonString);
        if (!responseJson.path("success").booleanValue()) {
            throw new DataRetrievalFailureException("Unable to save " + entityType.getName() + " to VPR OBJECT file.");
        }
        return POMUtils.convertNodeToMap(responseJson.path("data"));
    }
}
