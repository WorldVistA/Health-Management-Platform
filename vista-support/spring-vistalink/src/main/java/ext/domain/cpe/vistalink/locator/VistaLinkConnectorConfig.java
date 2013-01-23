package org.osehra.cpe.vistalink.locator;

import org.osehra.cpe.vista.util.RpcUriUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class VistaLinkConnectorConfig {
    public static int DEFAULT_PORT = 9200;

    private String id;
    private String name;
    private URI uri;
    private int region;

    private String accessCode;
    private String verifyCode;


    public VistaLinkConnectorConfig() {
    }

    public VistaLinkConnectorConfig(String primaryStation, String name, String host, int port, String accessCode, String verifyCode) {
        setId(primaryStation);
        setName(name);
        setUri(RpcUriUtils.VISTALINK_SCHEME + "://" + host + ":" + port);
        this.accessCode = accessCode;
        this.verifyCode = verifyCode;
    }

    public String getId() {
        return id;
    }

    public String getStationNumber() {
        return getId();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStationNumber(String stationNumber) {
        setId(stationNumber);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public URI getUri() {
        return uri;
    }

    public String getHost() {
        return uri.getHost();
    }

    public int getPort() {
        if (uri == null) return DEFAULT_PORT;
        return uri.getPort();
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setUri(String uri) {
        try {
            setUri(new URI(uri));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("unable to set uri", e);
        }
    }

    public void setUri(String host, int port) {
        setUri(RpcUriUtils.VISTA_RPC_BROKER_SCHEME + "://" + host + ":" + port);
    }

    public void setHost(String host) {
        setUri(host, getPort());
    }

    public void setPort(int port) {
        setUri(getHost(), port);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VistaLinkConnectorConfig that = (VistaLinkConnectorConfig) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getName();
    }


    public String getPrimaryStation() {
        return getStationNumber();
    }

    public String getAccessCode() {
        return accessCode;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setPrimaryStation(String primaryStation) {
        setId(primaryStation);
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
