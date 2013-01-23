package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.Medication;

public class MedicationImporter extends AbstractJsonImporter<Medication> {
    @Override
    protected Medication create() {
		return new Medication();
    }
}
