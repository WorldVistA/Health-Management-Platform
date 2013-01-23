package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.Allergy;

public class AllergyImporter extends AbstractJsonImporter<Allergy> {
    @Override
    protected Allergy create() {
        return new Allergy();
    }
}
