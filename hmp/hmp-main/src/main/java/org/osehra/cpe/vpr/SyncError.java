package org.osehra.cpe.vpr;

import java.util.Date;

public class SyncError {

    private String message;
    private String stackTrace;
    private String json;
    private String item;
    private String pid;
    private Date dateCreated;

    private String id;

    private Patient patient;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public String getStackTrace() {
        return stackTrace;
    }


    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }


    public String getJson() {
        return json;
    }


    public void setJson(String json) {
        this.json = json;
    }


    public String getItem() {
        return item;
    }


    public void setItem(String item) {
        this.item = item;
    }

    public String getPid() {
        return pid;
    }


    public void setPid(String pid) {
        this.pid = pid;
    }


    public Date getDateCreated() {
        return dateCreated;
    }


    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
