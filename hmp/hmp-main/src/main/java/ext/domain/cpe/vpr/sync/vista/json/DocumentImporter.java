package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.Document;

public class DocumentImporter extends EXT.DOMAIN.cpe.vpr.sync.vista.json.AbstractJsonImporter<Document> {
    @Override
    protected Document create() {
        return new Document();
    }
}
