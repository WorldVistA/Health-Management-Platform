package org.osehra.cpe.vpr.dao.solr

import org.codehaus.groovy.grails.commons.GrailsClassUtils


public class SolrUtil {

    public static final String PROPERTY_NAME = "solr";

    public static Map<Class, Closure> getSolrMappingsForClass(Class classWithSolrMapping) {
        return getSolrMappingsForClasses(classWithSolrMapping);
    }

    public static Map<Class, Closure> getSolrMappingsForClasses(Class... classesWithSolrMappings) {
        Map<Class, Closure> mappings = new HashMap<Class, Closure>();
        for (Class classWithSolrMapping: classesWithSolrMappings) {
            def solr = GrailsClassUtils.getStaticPropertyValue(classWithSolrMapping, PROPERTY_NAME)
            if (solr instanceof Boolean) {
                if (solr)
                    mappings.put(classWithSolrMapping, DEFAULT_SOLR_MAPPING);
            } else if (solr instanceof Closure) {
                Closure c = (Closure) solr;
                mappings.put(classWithSolrMapping, c);
            }
        }
        return mappings;
    }

    private static DEFAULT_SOLR_MAPPING = {

    };
}
