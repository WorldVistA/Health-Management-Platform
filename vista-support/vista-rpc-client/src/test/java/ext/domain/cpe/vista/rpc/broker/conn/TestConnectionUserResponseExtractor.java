package org.osehra.cpe.vista.rpc.broker.conn;

import org.osehra.cpe.vista.rpc.RpcResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestConnectionUserResponseExtractor {

    @Test
    public void extractVistaSystemInfo() {
        ConnectionUserResponseExtractor e = new ConnectionUserResponseExtractor();

        ConnectionUser user = e.extractData(new RpcResponse("20012\r\nVEHU,TEN\r\nTen Vehu\r\n21787^SLC-FO EDIS DEV^960\r\nScholar Extraordinaire\r\nMEDICINE\r\n\r\n5400\r\n\r\n"));
        assertEquals("20012", user.getDUZ());
        assertEquals("VEHU,TEN", user.getName());
        assertEquals("Ten Vehu", user.getStandardName());
        assertEquals("960", user.getDivision());
        assertEquals("Scholar Extraordinaire", user.getTitle());
        assertEquals("MEDICINE", user.getServiceSection());
        assertEquals("", user.getLanguage());
        assertEquals("5400", user.getDTime());
        assertEquals("SLC-FO EDIS DEV", user.getDivisionNames().get(user.getDivision()));

//          user.setDUZ(r.toLines()[0]);
//        user.setName(r.toLines()[1]);
//        user.setStandardName(r.toLines()[2]);
//        user.setDivision(r.toLines()[3]);
//        user.setTitle(r.toLines()[4]);
//        user.setServiceSection(r.toLines()[5]);
//        user.setLanguage(r.toLines()[6]);
//        user.setDTime(r.toLines()[7]);

//        assertEquals("EDIS-DEV.FO-SLC.DOMAIN.EXT", systemInfo.getVolume());
//        assertEquals("DEV", systemInfo.getUCI());
//        assertEquals("/dev/null:26294", systemInfo.getDevice());
//        assertFalse(systemInfo.isProductionAccount());

//        20012[\r][\n]"
//16:28:27.449 [main] DEBUG org.osehra.cpe.vista.broker.wire - << "VEHU,TEN[\r][\n]"
//16:28:27.449 [main] DEBUG org.osehra.cpe.vista.broker.wire - << "Ten Vehu[\r][\n]"
//16:28:27.449 [main] DEBUG org.osehra.cpe.vista.broker.wire - << "21787^SLC-FO EDIS DEV^960[\r][\n]"
//16:28:27.449 [main] DEBUG org.osehra.cpe.vista.broker.wire - << "Scholar Extraordinaire[\r][\n]"
//16:28:27.449 [main] DEBUG org.osehra.cpe.vista.broker.wire - << "MEDICINE[\r][\n]"
//16:28:27.449 [main] DEBUG org.osehra.cpe.vista.broker.wire - << "[\r][\n]"
//16:28:27.449 [main] DEBUG org.osehra.cpe.vista.broker.wire - << "5400[\r][\n]"
//16:28:27.449 [main] DEBUG org.osehra.cpe.vista.broker.wire - << "[\r][\n]

        // these properties set by BrokerConnection
        assertNull(user.getAccessCode());
        assertNull(user.getVerifyCode());
    }
}
