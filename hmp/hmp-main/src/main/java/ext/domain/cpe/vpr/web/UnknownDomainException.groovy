package EXT.DOMAIN.cpe.vpr.web

class UnknownDomainException extends BadRequestException {

    String domain

    UnknownDomainException(String domain) {
        super("Unknown domain '${domain}'".toString())
        this.domain = domain
    }
}
