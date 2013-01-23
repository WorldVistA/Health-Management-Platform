package org.osehra.cpe.datetime.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;

public class HealthTimeModuleTestBase {
    protected ObjectMapper jsonMapper;

    @Before
    public void setUp() throws Exception {
        jsonMapper = new ObjectMapper();
        jsonMapper.registerModule(new HealthTimeModule());

    }
}
