package org.osehra.cpe.vpr.pom;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DefaultNamingStrategyTests {

    private DefaultNamingStrategy namingStrategy = new DefaultNamingStrategy();

    @Test
    public void testCollectionName() throws Exception {
       assertThat(namingStrategy.collectionName(TestPatientObject.class), is("testpatientobject"));
    }
}
