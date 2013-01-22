package EXT.DOMAIN.cpe.vpr.sync.vista.json.integration;

import EXT.DOMAIN.cpe.test.junit4.runners.*;
import EXT.DOMAIN.cpe.vpr.Order;
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk;
import EXT.DOMAIN.cpe.vpr.sync.vista.json.OrderImporter;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

@RunWith(ImporterIntegrationTestRunner.class)
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = {"1", "236", "8", "40"})
@VprExtract(domain = "order")
@Importer(OrderImporter.class)
public class ImportOrdersITCase extends AbstractImporterITCaseWithDB<Order> {

    public ImportOrdersITCase(VistaDataChunk chunk) {
        super(chunk);
    }

    @Test
    public void testOrderImporter() {
        assertThat(getDomainInstance(), not(nullValue()));
        assertSave(getDomainInstance());
    }
}
