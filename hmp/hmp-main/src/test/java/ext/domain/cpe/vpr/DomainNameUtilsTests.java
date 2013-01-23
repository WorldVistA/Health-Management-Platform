package org.osehra.cpe.vpr;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class DomainNameUtilsTests {

    @Test
    public void testGetDomainsForClass() throws Exception {
        assertThat(DomainNameUtils.getDomainsForClass(Allergy.class), hasItems("allergy"));
        assertThat(DomainNameUtils.getDomainsForClass(Document.class), hasItems("document"));
        assertThat(DomainNameUtils.getDomainsForClass(Encounter.class), hasItems("encounter"));
        assertThat(DomainNameUtils.getDomainsForClass(HealthFactor.class), hasItems("factor"));
        assertThat(DomainNameUtils.getDomainsForClass(Immunization.class), hasItems("immunization"));
        assertThat(DomainNameUtils.getDomainsForClass(Medication.class), hasItems("medication"));
        assertThat(DomainNameUtils.getDomainsForClass(Order.class), hasItems("order"));
        assertThat(DomainNameUtils.getDomainsForClass(Observation.class), hasItems("observation"));
        assertThat(DomainNameUtils.getDomainsForClass(Problem.class), hasItems("problem"));
        assertThat(DomainNameUtils.getDomainsForClass(Procedure.class), hasItems("procedure", "consult"));
        assertThat(DomainNameUtils.getDomainsForClass(Result.class), hasItems("laboratory"));
        assertThat(DomainNameUtils.getDomainsForClass(VitalSign.class), hasItems("vitalsign"));
        assertThat(DomainNameUtils.getDomainsForClass(Task.class), hasItems("task"));
    }

    @Test
    public void testGetClassForDomain() throws Exception {
        assertSame(Allergy.class, DomainNameUtils.getClassForDomain("allergy"));
        assertSame(Document.class, DomainNameUtils.getClassForDomain("document"));
        assertSame(Encounter.class, DomainNameUtils.getClassForDomain("encounter"));
        assertSame(HealthFactor.class, DomainNameUtils.getClassForDomain("factor"));
        assertSame(Immunization.class, DomainNameUtils.getClassForDomain("immunization"));
        assertSame(Medication.class, DomainNameUtils.getClassForDomain("medication"));
        assertSame(Order.class, DomainNameUtils.getClassForDomain("order"));
        assertSame(Observation.class, DomainNameUtils.getClassForDomain("observation"));
        assertSame(Problem.class, DomainNameUtils.getClassForDomain("problem"));
        assertSame(Procedure.class, DomainNameUtils.getClassForDomain("procedure"));
        assertSame(Procedure.class, DomainNameUtils.getClassForDomain("consult"));
        assertSame(Result.class, DomainNameUtils.getClassForDomain("laboratory"));
        assertSame(VitalSign.class, DomainNameUtils.getClassForDomain("vitalsign"));
        assertSame(Task.class, DomainNameUtils.getClassForDomain("task"));
    }
}
