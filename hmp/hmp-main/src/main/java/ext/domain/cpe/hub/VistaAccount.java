package EXT.DOMAIN.cpe.hub;


import com.fasterxml.jackson.annotation.JsonIgnore;
import EXT.DOMAIN.cpe.VprEncryption;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class VistaAccount {
    static final int DEFAULT_PORT = 9200;

    private Integer id;
    private String vistaId;
    private String division;
    private String name;
    private String host;
    private int port = DEFAULT_PORT;
    private Integer region;
    private boolean production = false;
    private String vprUserCredentials;
    private boolean vprAutoUpdate = true;

    private boolean encrypted;

    @Override
    public String toString() {
        //return "${name} (vrpcb://${division}@${host}:${port})";
        StringBuffer buff = new StringBuffer();
        buff.append(name);
        buff.append(" ");
        buff.append("(vrpcb://");
        if (division != null) {
            buff.append(division);
            buff.append("@");
        }
        try {
			buff.append((encrypted || host==null)?host:VprEncryption.getInstance().encrypt(host));
		} catch (Exception e) {
			e.printStackTrace();
		}
        buff.append(":");
        buff.append(port);
        buff.append(")");
        return buff.toString();
    }

    @JsonIgnore
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVistaId() {
        return vistaId;
    }

    public void setVistaId(String vistaId) {
        this.vistaId = vistaId;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Integer getRegion() {
        return region;
    }

    public void setRegion(Integer region) {
        this.region = region;
    }

    public boolean isProduction() {
        return production;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }

    public String getVprUserCredentials() {
        return vprUserCredentials;
    }

    public boolean isEncrypted() {
		return encrypted;
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

	public void setVprUserCredentials(String vprUserCredentials) {
        this.vprUserCredentials = vprUserCredentials;
    }

    public boolean isVprAutoUpdate() {
        return vprAutoUpdate;
    }

    public void setVprAutoUpdate(boolean vprAutoUpdate) {
        this.vprAutoUpdate = vprAutoUpdate;
    }

    public void encrypt() throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
    	if(!encrypted) {
    		//this.host = host!=null?VprEncryption.getInstance().encrypt(host):host;
    		this.vprUserCredentials = vprUserCredentials!=null?VprEncryption.getInstance().encrypt(vprUserCredentials):vprUserCredentials;
    		encrypted = true;
    	}
    }
    
    public void decrypt() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException {
    	if(encrypted) {
    		//this.host = host!=null?VprEncryption.getInstance().decrypt(host):host;
    		this.vprUserCredentials = vprUserCredentials!=null?VprEncryption.getInstance().decrypt(vprUserCredentials):vprUserCredentials;
    		encrypted = false;
    	}
    }
}
