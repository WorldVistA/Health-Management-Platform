package org.osehra.cpe;

import org.junit.Test;

import java.util.Set;

import static org.osehra.cpe.HmpProperties.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class HmpPropertiesTests {

    @Test
    public void testGetPropertyNames() throws Exception {
        Set<String> props = HmpProperties.getPropertyNames();

        assertThat(props.isEmpty(), is(false)); // 22 props at time of writing, arbitrarily testing for someTHING
        assertThat(props, hasItems(VERSION, BUILD_DATE, INFO_BUTTON_URL, DATABASE_DRIVER_CLASS)); // picked a few arbitrary ones
        assertThat(props, not(hasItems(DATABASE_PASSWORD, DATABASE_USERNAME)));

    }

    @Test
    public void testGetPropertyNamesIncludingSensitive() throws Exception {
        Set<String> props = HmpProperties.getPropertyNames(true);

        assertThat(props.isEmpty(), is(false)); // 22 props at time of writing, arbitrarily testing for someTHING
        assertThat(props, hasItems(VERSION, BUILD_DATE, INFO_BUTTON_URL, DATABASE_DRIVER_CLASS, DATABASE_PASSWORD, DATABASE_USERNAME)); // picked a few arbitrary ones
    }
}
