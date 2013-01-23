package org.osehra.cpe.vpr.sync.convert;

import org.osehra.cpe.hub.dao.json.JsonAssert;
import org.osehra.cpe.vpr.SyncError;
import org.junit.Test;

import javax.jms.MapMessage;

import static org.osehra.cpe.vpr.sync.SyncMessageConstants.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapMessageToSyncErrorTests {
    @Test
    public void testConvertErrorMessageDuringChunkImport() throws Exception {
        long t = System.currentTimeMillis();

        MapMessage msg = mock(MapMessage.class);
        when(msg.itemExists(VPR_DOMAIN)).thenReturn(true);
        when(msg.getJMSMessageID()).thenReturn("34");
        when(msg.getJMSTimestamp()).thenReturn(t);
        when(msg.getString(PATIENT_ID)).thenReturn("23");
        when(msg.getString(VPR_DOMAIN)).thenReturn("foo");
        when(msg.getInt(RPC_ITEM_INDEX)).thenReturn(3);
        when(msg.getInt(RPC_ITEM_COUNT)).thenReturn(5);
        when(msg.getString(RPC_URI)).thenReturn("vrpcb://foobar");
        when(msg.getString(RPC_ITEM_CONTENT)).thenReturn("{\"foo\":\"bar\",\"baz\":" + false + "}");
        when(msg.getString(EXCEPTION_MESSAGE)).thenReturn("bar");
        when(msg.getString(EXCEPTION_STACK_TRACE)).thenReturn("baz");

        MapMessageToSyncError c = new MapMessageToSyncError();
        SyncError e = c.convert(msg);

        assertThat(e.getId(), is(equalTo(msg.getJMSMessageID())));
        assertThat(e.getDateCreated().getTime(), is(equalTo(t)));
        assertThat(e.getPid(), is(equalTo(msg.getString(PATIENT_ID))));
        JsonAssert.assertJsonEquals(e.getJson(), msg.getString(RPC_ITEM_CONTENT));
        assertThat(e.getMessage(), is(equalTo(msg.getString(EXCEPTION_MESSAGE))));
        assertThat(e.getItem(), is(equalTo("'foo' chunk 4 of 5 returned from vrpcb://foobar")));
        assertThat(e.getStackTrace(), is(equalTo(msg.getString(EXCEPTION_STACK_TRACE))));
    }

    @Test
    public void testConvertErrorMessage() throws Exception {
        long t = System.currentTimeMillis();

        MapMessage msg = mock(MapMessage.class);
        when(msg.itemExists(VPR_DOMAIN)).thenReturn(false);
        when(msg.getJMSMessageID()).thenReturn("34");
        when(msg.getJMSTimestamp()).thenReturn(t);
        when(msg.getString(EXCEPTION_NAME)).thenReturn("foo");
        when(msg.getString(EXCEPTION_MESSAGE)).thenReturn("bar");
        when(msg.getString(EXCEPTION_STACK_TRACE)).thenReturn("baz");

        MapMessageToSyncError c = new MapMessageToSyncError();
        SyncError e = c.convert(msg);

        assertThat(e.getId(), is(equalTo(msg.getJMSMessageID())));
        assertThat(e.getDateCreated().getTime(), is(equalTo(t)));
        assertThat(e.getPid(), nullValue());
        assertThat(e.getJson(), nullValue());
        assertThat(e.getMessage(), is(equalTo(msg.getString(EXCEPTION_MESSAGE))));
        assertThat(e.getItem(), is(equalTo(msg.getString(EXCEPTION_NAME))));
        assertThat(e.getStackTrace(), is(equalTo(msg.getString(EXCEPTION_STACK_TRACE))));
    }
}
