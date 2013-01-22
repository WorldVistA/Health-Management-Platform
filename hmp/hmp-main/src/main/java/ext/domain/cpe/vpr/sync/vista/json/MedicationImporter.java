package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.Medication;

public class MedicationImporter extends AbstractJsonImporter<Medication> {
    @Override
    protected Medication create() {
		return new Medication();
    }
}
