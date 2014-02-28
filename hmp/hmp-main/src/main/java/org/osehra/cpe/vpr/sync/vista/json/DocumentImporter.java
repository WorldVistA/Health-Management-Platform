package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.Document;

public class DocumentImporter extends org.osehra.cpe.vpr.sync.vista.json.AbstractJsonImporter<Document> {
    @Override
    protected Document create() {
        return new Document();
    }
}
