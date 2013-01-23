package org.osehra.cpe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.osehra.cpe.hub.VistaAccount;

public class SetupCommand {

    String serverId;

    String serverHost;

    Integer httpPort;

    Integer httpsPort;

    int demo = 0;

    String databaseDriverClassName;

    String databaseUrl;

    String databaseUsername;

    public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public Integer getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(Integer httpPort) {
		this.httpPort = httpPort;
	}

	public Integer getHttpsPort() {
		return httpsPort;
	}

	public void setHttpsPort(Integer httpsPort) {
		this.httpsPort = httpsPort;
	}

	public int getDemo() {
		return demo;
	}

	public void setDemo(int demo) {
		this.demo = demo;
	}

	public String getDatabaseDriverClassName() {
		return databaseDriverClassName;
	}

	public void setDatabaseDriverClassName(String databaseDriverClassName) {
		this.databaseDriverClassName = databaseDriverClassName;
	}

	public String getDatabaseUrl() {
		return databaseUrl;
	}

	public void setDatabaseUrl(String databaseUrl) {
		this.databaseUrl = databaseUrl;
	}

	public String getDatabaseUsername() {
		return databaseUsername;
	}

	public void setDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	public String getDatasourceName() {
		return datasourceName;
	}

	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}

	public VistaAccount getVista() {
		return vista;
	}

	public void setVista(VistaAccount vista) {
		this.vista = vista;
	}

	String databasePassword;

    String datasourceName;

    VistaAccount vista = new VistaAccount();
    
    /*
     * Yes everything below should be inside of a wired-in or otherwise centralized encryption utility, 
     * but brute force gets us thru round 1 of ATO.
     */

	public boolean isHmpPropertiesEncrypted() {
		return hmpPropertiesEncrypted;
	}

	public void setHmpPropertiesEncrypted(boolean encrypted) {
		this.hmpPropertiesEncrypted = encrypted;
	}

	private boolean hmpPropertiesEncrypted;
    
    public void encrypt() throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException {
    	if(!hmpPropertiesEncrypted) {
    		//this.serverHost = serverHost!=null?VprEncryption.getInstance().encrypt(serverHost):serverHost;
    		this.databaseUsername = databaseUsername!=null?VprEncryption.getInstance().encrypt(databaseUsername):databaseUsername;
    		this.databasePassword = databasePassword!=null?VprEncryption.getInstance().encrypt(databasePassword):databasePassword;
    		this.databaseUrl = databaseUrl!=null?VprEncryption.getInstance().encrypt(databaseUrl):databaseUrl;
    		vista.encrypt();
    		hmpPropertiesEncrypted = true;
    	}
    }
    
    public void decrypt() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException {
    	if(hmpPropertiesEncrypted) {
    		//this.serverHost = serverHost!=null?VprEncryption.getInstance().decrypt(serverHost):serverHost;
    		this.databaseUsername = databaseUsername!=null?VprEncryption.getInstance().decrypt(databaseUsername):databaseUsername;
    		this.databasePassword = databasePassword!=null?VprEncryption.getInstance().decrypt(databasePassword):databasePassword;
    		this.databaseUrl = databaseUrl!=null?VprEncryption.getInstance().decrypt(databaseUrl):databaseUrl;
    		vista.decrypt();
    		hmpPropertiesEncrypted = false;
    	}
    }
}
