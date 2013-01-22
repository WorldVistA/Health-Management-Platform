package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.Procedure;

public class ProcedureImporter extends AbstractJsonImporter<Procedure> {
    @Override
    protected Procedure create() {
        return new Procedure();
    }
}
