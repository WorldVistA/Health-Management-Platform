package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.ResultOrganizer;

public class AccessionImporter extends AbstractJsonImporter<ResultOrganizer> {
    @Override
    protected ResultOrganizer create() {
        return new ResultOrganizer();
    }
}
