package org.osehra.cpe.vpr.ws.link

enum LinkRelation {
    SELF("self"),
    NEXT("next"),
    PREVIOUS("previous"),
    PATIENT("http://vaww.cpe.DOMAIN.EXT/rels/patient"),
    TREND("http://vaww.cpe.DOMAIN.EXT/rels/trend"),
    OPEN_INFO_BUTTON("http://vaww.cpe.DOMAIN.EXT/rels/openinfobutton"),

    private String rel

    private LinkRelation(String rel) {
        this.rel = rel
    }

    @Override
    String toString() {
        return rel
    }
}
