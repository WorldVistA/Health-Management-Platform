package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.pom.IGenericPatientObjectDAO;
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject;
import EXT.DOMAIN.cpe.vpr.pom.POMUtils;
import EXT.DOMAIN.cpe.vpr.sync.vista.ImportException;
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

public abstract class AbstractJsonImporter<T extends IPatientObject> implements Converter<VistaDataChunk, T> {

	@Autowired
	IGenericPatientObjectDAO dao;
	
    public AbstractJsonImporter() {
    }

    @Override
    public T convert(VistaDataChunk chunk) {
        try {
            Map<String, Object> data = POMUtils.parseJSONtoMap(chunk.getContent());
            if (data == null) throw new ImportException("reading chunk JSON resulted in a null Map", chunk);
            if (StringUtils.hasText(chunk.getPatientId()))
                data.put("pid", chunk.getPatientId());
            
            // let the converter transform the data if it wants to
            transform(data);
            
            // Look for existing object (create a new one if none)
            String uid = (String) data.get("uid"); 
            if (uid == null || uid.length() == 0) throw new ImportException("no UID found in import chunk", chunk);
            T obj = null;
            if (dao != null) obj = dao.findByUID(uid);  // TODO: How to avoid warnings when/if VPR returns 404?
            if (obj == null) {
            	obj = create(); // TODO: take the data param out of the create method...
            }
            
            // update the object with the new data
            obj.setData(data);
            return obj;
        } catch (Exception e) {
            throw new ImportException(chunk, e);
        }
    }
    
    protected void transform(Map<String, Object> data) {
    	// transform the data as needed, if needed.
    }

    abstract protected T create();
}
