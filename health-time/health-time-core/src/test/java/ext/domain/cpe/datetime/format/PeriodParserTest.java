package org.osehra.cpe.datetime.format;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PeriodParserTest {

    private PeriodFormatter formatter;

    @Before
    public void setUp() {
        formatter = new PeriodFormatter(null, new PeriodParser());
        assertThat(formatter.isParser(), is(true));
    }

    @Test
    public void testParsePeriodWithNoUnits() throws Exception {
        Period p = formatter.parsePeriod("9");
        assertThat(p.getDays(), is(9));
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));
    }

    @Test
    public void testParsePeriodOfYears() throws Exception {
        Period p = formatter.parsePeriod("9Y");
        assertThat(p.getYears(), is(9));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));

        p = formatter.parsePeriod("2y");
        assertThat(p.getYears(), is(2));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));
    }

    @Test
    public void testParsePeriodOfMonths() throws Exception {
        Period p = formatter.parsePeriod("23M");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(23));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));

        p = formatter.parsePeriod("2MO");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(2));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));

        p = formatter.parsePeriod("42mo");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(42));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));
    }

    @Test
    public void testParsePeriodOfWeeks() throws Exception {
        Period p = formatter.parsePeriod("9W");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(9));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));

        p = formatter.parsePeriod("2w");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(2));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));
    }

    @Test
    public void testParsePeriodOfDays() throws Exception {
        Period p = formatter.parsePeriod("9D");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(9));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));

        p = formatter.parsePeriod("2d");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(2));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));
    }

    @Test
    public void testParsePeriodOfHours() throws Exception {
        Period p = formatter.parsePeriod("9H");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(9));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));

        p = formatter.parsePeriod("2h");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(2));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));
    }

    @Test
    public void testParsePeriodOfMinutes() throws Exception {
        Period p = formatter.parsePeriod("9m");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(9));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));

        p = formatter.parsePeriod("2MI");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(2));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));

        p = formatter.parsePeriod("23mi");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(23));
        assertThat(p.getSeconds(), is(0));
        assertThat(p.getMillis(), is(0));
    }

    @Test
    public void testParsePeriodOfSeconds() throws Exception {
        Period p = formatter.parsePeriod("9S");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(9));
        assertThat(p.getMillis(), is(0));

        p = formatter.parsePeriod("2s");
        assertThat(p.getYears(), is(0));
        assertThat(p.getMonths(), is(0));
        assertThat(p.getWeeks(), is(0));
        assertThat(p.getDays(), is(0));
        assertThat(p.getHours(), is(0));
        assertThat(p.getMinutes(), is(0));
        assertThat(p.getSeconds(), is(2));
        assertThat(p.getMillis(), is(0));
    }
}
