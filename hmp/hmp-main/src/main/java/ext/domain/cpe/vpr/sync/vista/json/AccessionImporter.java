package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.ResultOrganizer;

public class AccessionImporter extends AbstractJsonImporter<ResultOrganizer> {
    @Override
    protected ResultOrganizer create() {
        return new ResultOrganizer();
    }
}
