package EXT.DOMAIN.cpe.vpr.dao.solr

import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.common.SolrInputDocument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.ConversionService
import EXT.DOMAIN.cpe.vpr.dao.ISolrDao
import EXT.DOMAIN.cpe.vpr.VitalSign
import EXT.DOMAIN.cpe.vpr.VitalSignOrganizer
import EXT.DOMAIN.cpe.vpr.Result
import EXT.DOMAIN.cpe.vpr.ResultOrganizer
import EXT.DOMAIN.cpe.vpr.pom.IPatientObject

class DefaultSolrDao implements ISolrDao {

    private ConversionService conversionService
    private SolrServer solrServer

    @Autowired
    void setSolrServer(SolrServer solrServer) {
        this.solrServer = solrServer
    }

    @Autowired
    void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService
    }

    SolrServer getServer() {
        return solrServer
    }

    QueryResponse search(String query) {
        return search(new SolrQuery(query))
    }

    QueryResponse search(SolrQuery solrQuery) {
        return getServer().query(solrQuery);
    }

    void save(Object entity) {
        index(entity, false)
    }

    def <T extends IPatientObject> void save(T entity) {
        index(entity, false)
    }

    void index(Object entity, boolean commit = true) {
        if (entity instanceof ResultOrganizer) {
            ResultOrganizer o = (ResultOrganizer) entity;
            // index each individual result
            o.results.each { Result r ->
                indexInternal(r, false);
            }
            if (commit) this.commit()
        } else if (entity instanceof VitalSignOrganizer) {
            VitalSignOrganizer o = (VitalSignOrganizer) entity;
            // index each individual vital sign
            o.vitalSigns.each { VitalSign vs ->
                indexInternal(vs, false);
            }
            if (commit) this.commit()
        } else {
            indexInternal(entity, commit);
        }
    }

    private void indexInternal(entity, boolean commit) {
        SolrInputDocument doc = conversionService.convert(entity, SolrInputDocument)
        if (!doc) return // TODO: log warning or error or sommat

        SolrServer server = getServer()
        server.add(doc)
        if (commit) server.commit()
    }

    def <T extends IPatientObject> void delete(T entity) {
        delete(entity, true)
    }

    void delete(Object entity, boolean commit = true) {
        String uid = entity.uid
        if (!uid) return; // TODO: log warning or error or sommat
        SolrServer server = getServer()
        server.deleteById(uid)
        if (commit) server.commit()
    }

    void deleteByQuery(String query) {
        getServer().deleteByQuery(query)
    }

    void commit() {
        getServer().commit()
    }
}
