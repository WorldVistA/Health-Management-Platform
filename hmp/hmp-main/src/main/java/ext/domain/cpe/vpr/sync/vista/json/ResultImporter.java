package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.Result;

public class ResultImporter extends AbstractJsonImporter<Result> {
    @Override
    protected Result create() {
        return new Result();
    }
}
