package org.osehra.cpe.vpr.sync.vista.json.integration

import org.osehra.cpe.vpr.pom.POMUtils;
import org.osehra.cpe.vpr.pom.hibernate.GenericHibMapDAO;
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk

import org.hibernate.HibernateException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test
import org.junit.runner.RunWith
import org.osehra.cpe.test.junit4.runners.*

import static org.hamcrest.CoreMatchers.nullValue
import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat
import org.osehra.cpe.vpr.Document
import org.osehra.cpe.vpr.sync.vista.json.*

@RunWith(ImporterIntegrationTestRunner)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["711", "129"])
@VprExtract(domain = "document")
@Importer(DocumentImporter.class)
class ImportDocumentsITCase extends AbstractImporterITCaseWithDB<Document> {

    ImportDocumentsITCase(VistaDataChunk chunk) {
        super(chunk)
    }

    @Test
    void testDocumentImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
        assertSave(getDomainInstance());
    }
}
