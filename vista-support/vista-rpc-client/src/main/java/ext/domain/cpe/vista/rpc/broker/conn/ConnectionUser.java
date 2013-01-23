package org.osehra.cpe.vista.rpc.broker.conn;

import org.osehra.cpe.vista.rpc.conn.ConnectionUserDetails;

import java.util.Collections;
import java.util.Map;

/**
 * TODO: Document org.osehra.cpe.vista.protocol
 */
public class ConnectionUser implements ConnectionUserDetails {
    private String DUZ;
    private String accessCode;
    private String verifyCode;
    private String name;
    private String standardName;
    private String division;
    private Map<String, String> divisionNames;
    private boolean verifyCodeChanged;
    private String title;
    private String serviceSection;
    private String language;
    private String dTime;
    private String vpid;

    public String getDUZ() {
        return DUZ;
    }

    public void setDUZ(String DUZ) {
        this.DUZ = DUZ;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStandardName() {
        return standardName;
    }

    public void setStandardName(String standardName) {
        this.standardName = standardName;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public Map<String, String> getDivisionNames() {
        return divisionNames;
    }

    public void setDivisionNames(Map<String, String> divisionNames) {
        this.divisionNames = Collections.unmodifiableMap(divisionNames);
    }

    public boolean isVerifyCodeChanged() {
        return verifyCodeChanged;
    }

    public void setVerifyCodeChanged(boolean verifyCodeChanged) {
        this.verifyCodeChanged = verifyCodeChanged;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getServiceSection() {
        return serviceSection;
    }

    public void setServiceSection(String serviceSection) {
        this.serviceSection = serviceSection;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDTime() {
        return dTime;
    }

    public void setDTime(String dTime) {
        this.dTime = dTime;
    }

    public String getVPID() {
        return vpid;
    }

    public void setVpid(String vpid) {
        this.vpid = vpid;
    }
}
/*
TVistaUser = class(TObject)
private
  FDUZ: string;
  FName: string;
  FStandardName: string;
  FDivision: String;
  FVerifyCodeChngd: Boolean;
  FTitle: string;
  FServiceSection: string;
  FLanguage: string;
  FDtime: string;
  FVpid: String;
  procedure SetDivision(const Value: String);
  procedure SetDUZ(const Value: String);
  procedure SetName(const Value: String);
  procedure SetVerifyCodeChngd(const Value: Boolean);
  procedure SetStandardName(const Value: String);
  procedure SetTitle(const Value: string);
  procedure SetDTime(const Value: string);
  procedure SetLanguage(const Value: string);
  procedure SetServiceSection(const Value: string);
public
  property DUZ: String read FDUZ write SetDUZ;
  property Name: String read FName write SetName;
  property StandardName: String read FStandardName write SetStandardName;
  property Division: String read FDivision write SetDivision;
  property VerifyCodeChngd: Boolean read FVerifyCodeChngd write SetVerifyCodeChngd;
  property Title: string read FTitle write SetTitle;
  property ServiceSection: string read FServiceSection write SetServiceSection;
  property Language: string read FLanguage write SetLanguage;
  property DTime: string read FDTime write SetDTime;
  property Vpid: string read FVpid write FVpid;
end;
 */
