package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.Encounter;

public class EncounterImporter extends AbstractJsonImporter<Encounter> {
    @Override
    protected Encounter create() {
        return new Encounter();
    }
}
