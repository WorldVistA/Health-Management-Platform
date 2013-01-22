package EXT.DOMAIN.cpe.vista.rpc.broker.conn;

import EXT.DOMAIN.cpe.vista.rpc.conn.SystemInfo;
import EXT.DOMAIN.cpe.vista.util.VistaStringUtils;

/**
 * TODOC: Provide summary documentation of class EXT.DOMAIN.cpe.vista.impl.SystemInfo
 */
public class VistaSystemInfo implements SystemInfo {
    private String server;
    private String volume;
    private String UCI;
    private String device;
    private String domainName;
    private boolean productionAccount;
    private String introText;
    private int activityTimeoutSeconds;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Override
    public String getVistaId() {
        return VistaStringUtils.crc16Hex(getDomainName());
    }

    public String getIntroText() {
        return introText;
    }

    public void setIntroText(String introText) {
        this.introText = introText;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public boolean isProductionAccount() {
        return productionAccount;
    }

    public void setProductionAccount(boolean productionAccount) {
        this.productionAccount = productionAccount;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUCI() {
        return UCI;
    }

    public void setUCI(String UCI) {
        this.UCI = UCI;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public int getActivityTimeoutSeconds() {
        return activityTimeoutSeconds;
    }

    public void setActivityTimeoutSeconds(int activityTimeoutSeconds) {
        this.activityTimeoutSeconds = activityTimeoutSeconds;
    }
}
