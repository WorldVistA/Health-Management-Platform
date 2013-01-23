package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.PatientFacility;
import org.osehra.cpe.vpr.pom.POMUtils;
import org.osehra.cpe.vpr.sync.vista.ImportException;
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.util.Map;

public class PatientImporter implements Converter<VistaDataChunk, Patient> {
    @Override
    public Patient convert(VistaDataChunk chunk) {
        try {
            Map<String, Object> data = POMUtils.parseJSONtoMap(chunk.getContent());
            if (data == null) throw new ImportException("reading chunk JSON resulted in a null Map", chunk);
            Patient patient = new Patient(data);

            return patient;
        } catch (Exception e) {
            throw new ImportException(chunk, e);
        }
    }
}



