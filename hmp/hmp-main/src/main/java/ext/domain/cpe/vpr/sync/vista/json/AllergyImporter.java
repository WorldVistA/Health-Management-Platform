package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.Allergy;

public class AllergyImporter extends AbstractJsonImporter<Allergy> {
    @Override
    protected Allergy create() {
        return new Allergy();
    }
}
