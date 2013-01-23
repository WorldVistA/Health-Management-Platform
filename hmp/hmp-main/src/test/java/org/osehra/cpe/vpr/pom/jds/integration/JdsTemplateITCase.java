package org.osehra.cpe.vpr.pom.jds.integration;

import org.osehra.cpe.jsonc.JsonCCollection;
import org.osehra.cpe.vpr.pom.jds.JdsTemplate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class JdsTemplateITCase {

    static JdsTemplate t;

    @BeforeClass
    public static void init() {
        t = new JdsTemplate();
        t.setRestTemplate(new RestTemplate());
        t.setJdsUrl("http://localhost:9080");
    }

    @Test
    public void testGetForJsonC() throws Exception {
        JsonCCollection r = t.getForJsonC("/vpr/1/index/immunization");
    }
}
