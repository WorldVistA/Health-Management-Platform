package org.osehra.cpe.vpr;

public class UserInterfaceRpcConstants {
    public static final String VPR_UI_CONTEXT = "VPR UI CONTEXT";

    public static final String FRONT_CONTROLLER_RPC = "VPRCRPC RPC";
    public static final String CONTROLLER_RPC_URI = "/" + VPR_UI_CONTEXT + "/" + FRONT_CONTROLLER_RPC;

    public static final String VPR_PUT_OBJECT_RPC = "VPR PUT OBJECT";
    public static final String VPR_PUT_OBJECT_RPC_URI = "/" + VPR_UI_CONTEXT + "/" + VPR_PUT_OBJECT_RPC;
    public static final String VPR_PUT_PATIENT_DATA_RPC = "VPR PUT PATIENT DATA";
    public static final String VPR_PUT_PATIENT_DATA_URI = "/" + VPR_UI_CONTEXT + "/" + VPR_PUT_PATIENT_DATA_RPC;
}
