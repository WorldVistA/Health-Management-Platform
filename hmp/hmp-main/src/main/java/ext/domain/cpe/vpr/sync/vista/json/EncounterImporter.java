package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.Encounter;

public class EncounterImporter extends AbstractJsonImporter<Encounter> {
    @Override
    protected Encounter create() {
        return new Encounter();
    }
}
