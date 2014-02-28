package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.Procedure;

public class ProcedureImporter extends AbstractJsonImporter<Procedure> {
    @Override
    protected Procedure create() {
        return new Procedure();
    }
}
