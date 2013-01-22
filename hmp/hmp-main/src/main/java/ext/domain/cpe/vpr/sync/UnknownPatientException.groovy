package EXT.DOMAIN.cpe.vpr.sync

class UnknownPatientException extends Exception {
	static String MESSAGE = "Patient with localPatientId '%1s' from system '%2s' is currently unknown to the VPR. This is likely due to old subscriptions notifying this VPR about new data for patient's that are no longer in it."
    UnknownPatientException(String systemId, String localPatientId) {
        super(sprintf(MESSAGE,[localPatientId,systemId]))
    }
}
