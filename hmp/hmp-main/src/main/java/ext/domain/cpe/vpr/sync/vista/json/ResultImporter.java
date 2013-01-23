package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.Result;

public class ResultImporter extends AbstractJsonImporter<Result> {
    @Override
    protected Result create() {
        return new Result();
    }
}
