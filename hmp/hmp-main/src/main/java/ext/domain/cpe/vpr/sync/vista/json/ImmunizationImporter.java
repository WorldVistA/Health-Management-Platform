package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.Immunization;

public class ImmunizationImporter extends AbstractJsonImporter<Immunization> {
    @Override
    protected Immunization create() {
        return new Immunization();
    }
}
