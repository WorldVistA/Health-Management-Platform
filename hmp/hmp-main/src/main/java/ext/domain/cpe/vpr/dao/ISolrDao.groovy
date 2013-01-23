package org.osehra.cpe.vpr.dao

import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.response.QueryResponse

import org.osehra.cpe.vpr.pom.IDataStoreDAO

public interface ISolrDao extends IDataStoreDAO {
    QueryResponse search(String query);
    QueryResponse search(SolrQuery solrQuery);
    void index(Object entity, boolean commit);
    void delete(Object entity, boolean commit);
    void deleteByQuery(String query);
    void commit();
}
