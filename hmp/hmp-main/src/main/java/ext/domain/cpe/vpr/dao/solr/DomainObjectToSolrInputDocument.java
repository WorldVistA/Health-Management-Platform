package EXT.DOMAIN.cpe.vpr.dao.solr;

import groovy.lang.Closure;
import org.apache.solr.common.SolrInputDocument;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.util.*;

public class DomainObjectToSolrInputDocument implements Converter<Object, SolrInputDocument> {

    private Map<Class, Closure> solrMappings;
    private ImprovedNamingStrategy namingStrategy;

    public DomainObjectToSolrInputDocument(Map<Class, Closure> solrMappings) {
        this.solrMappings = solrMappings;
        this.namingStrategy = new ImprovedNamingStrategy();
    }

    @Override
    public synchronized SolrInputDocument convert(Object entity) {
        if (entity == null) return null;

        Closure c = solrMappings.get(entity.getClass());
        if (c == null) return null;

        SearchablePropertyBuilder builder = new SearchablePropertyBuilder(entity, solrMappings, namingStrategy);
        c.setDelegate(builder);
        c.call();

        Collection<SearchableProperty> searchables = builder.getSearchableProperties().values();
        if (searchables.isEmpty()) return null;

        SolrInputDocument solrInputDocument = new SolrInputDocument();
        for (SearchableProperty sp : searchables) {
            if (sp.getValueClosure() == null && sp.getChildren() != null && !sp.getChildren().isEmpty()) {
                for (SearchableProperty child : sp.getChildren().values()) {
                    String fieldName = sp.getFieldName() + child.getFieldName();
                    BigDecimal boost = child.getBoost() != null ? child.getBoost() : sp.getBoost();
                    Object value = child.getFieldValue();
                    if (boost != null)
                        solrInputDocument.addField(fieldName, value, boost.floatValue());
                    else
                        solrInputDocument.addField(fieldName, value);
                }
            } else {
                Object value = sp.getFieldValue();
                if (value != null) {
                    if (sp.getBoost() != null)
                        solrInputDocument.addField(sp.getFieldName(), value, sp.getBoost().floatValue());
                    else
                        solrInputDocument.addField(sp.getFieldName(), value);
                }
            }

        }
        return solrInputDocument;
    }
}
