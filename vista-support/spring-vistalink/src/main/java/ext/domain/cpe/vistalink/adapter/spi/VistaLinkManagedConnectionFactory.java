package org.osehra.cpe.vistalink.adapter.spi;

import org.osehra.vistalink.adapter.spi.EMAdapterEnvironment;
import org.springframework.beans.factory.InitializingBean;

public class VistaLinkManagedConnectionFactory extends org.osehra.vistalink.adapter.spi.VistaLinkManagedConnectionFactory implements InitializingBean {

    private String stationNumber;

    public void afterPropertiesSet() throws Exception {
        setPrimaryStation(stationNumber);
        setAdapterEnvironment(EMAdapterEnvironment.J2EE);
    }

    public void setStationNumber(String stationNumber) {
        this.stationNumber = stationNumber;
    }
}
