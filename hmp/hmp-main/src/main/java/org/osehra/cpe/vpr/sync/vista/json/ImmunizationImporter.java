package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.Immunization;

public class ImmunizationImporter extends AbstractJsonImporter<Immunization> {
    @Override
    protected Immunization create() {
        return new Immunization();
    }
}
