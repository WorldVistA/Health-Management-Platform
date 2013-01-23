package org.osehra.cpe.vpr;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AllergyTests  {
	@Test
    public void testSummary() {
        Allergy a = new Allergy();
        AllergyProduct product = new AllergyProduct();
        product.setData("name","BBQ SAUCE");
        a.addToProducts(product);
        assertEquals("BBQ SAUCE", a.getSummary());
    }

	@Test
    public void testKind() {
        Allergy a = new Allergy();
        assertEquals("Allergy / Adverse Reaction", a.getKind());
    }
}
