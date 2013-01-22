package EXT.DOMAIN.cpe.vistalink.adapter.spi;

import EXT.DOMAIN.vistalink.adapter.spi.EMAdapterEnvironment;
import org.springframework.beans.factory.InitializingBean;

public class VistaLinkManagedConnectionFactory extends EXT.DOMAIN.vistalink.adapter.spi.VistaLinkManagedConnectionFactory implements InitializingBean {

    private String stationNumber;

    public void afterPropertiesSet() throws Exception {
        setPrimaryStation(stationNumber);
        setAdapterEnvironment(EMAdapterEnvironment.J2EE);
    }

    public void setStationNumber(String stationNumber) {
        this.stationNumber = stationNumber;
    }
}
