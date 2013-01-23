package org.osehra.cpe.vpr.queryeng

import org.osehra.cpe.vpr.ws.link.PatientRelatedSelfLinkGenerator


class DomainClassSelfLinkColDef extends ColDef {

    private Class domainClass;

    DomainClassSelfLinkColDef(String key, Class domainClass) {
        super(key, null)
        this.domainClass = domainClass;
    }

    @Override
    void render(ViewDefRenderer renderer) {
        String pid = renderer.results.getCell(renderer.resultspkval, "pid") ?: renderer.getParamStr("pid");
        String uid = renderer.results.getCell(renderer.resultspkval, "uid");
        if (pid && uid)
            renderer.results.setCell(renderer.resultspkval, getKey(), PatientRelatedSelfLinkGenerator.getSelfHref(pid, domainClass, uid));
    }

}
