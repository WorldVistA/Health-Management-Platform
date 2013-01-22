package EXT.DOMAIN.cpe.vpr.dao.solr;

import grails.util.GrailsNameUtils;
import groovy.lang.Closure;
import groovy.lang.MissingMethodException;
import groovy.util.BuilderSupport;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty;
import org.hibernate.cfg.NamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * Builder used as a delegate against the closures in the solrMappings map passed to the constructor.
 *
 * @see DomainObjectToSolrInputDocument
 */
public class SearchablePropertyBuilder extends BuilderSupport {

    private static final Logger LOG = LoggerFactory.getLogger(SearchablePropertyBuilder.class);

    public static final String CONSTANT = "constant";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String ID = "id";
    public static final String COMPONENT = "component";
    public static final String PREFIX = "prefix";
    public static final String DATA = "data";
    public static final String EVENTS = "events";
    public static final String MODIFIED_FIELDS = "modifiedFields";

    private NamingStrategy namingStrategy;
    private Map<String, SearchableProperty> searchableProperties = new HashMap<String, SearchableProperty>();
    private Set<String> onlyProperties = new HashSet<String>();
    private Set<String> exceptProperties = new HashSet<String>();

    private BeanWrapper beanWrapper;
    private Map<Class, Closure> solrMappings;

    public SearchablePropertyBuilder(Object target, Map<Class, Closure> solrMappings, NamingStrategy namingStrategy) {
        Assert.notNull(target, "target must not be null");
        Assert.notNull(solrMappings, "solrMappings must not be null");
        Assert.notNull(namingStrategy, "namingStrategy must not be null");

        this.beanWrapper = new BeanWrapperImpl(target);
        this.solrMappings = solrMappings;
        this.namingStrategy = namingStrategy;

        determineDefaultSearchableProperties();
    }

    @Override
    protected Object createNode(Object name) {
        return createNode(name, Collections.EMPTY_MAP);
    }

    @Override
    protected void setParent(Object o, Object o1) {
        // NOOP
    }

    @Override
    protected Object createNode(Object name, Object value) {
        return createNode(name, Collections.EMPTY_MAP, value);
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        try {
            String property = (String) name;
            if (CONSTANT.equals(property)) {
                String fieldName = (String) attributes.get(NAME);
                SearchableProperty sp;
                if (searchableProperties.containsKey(fieldName)) {
                    sp = searchableProperties.get(fieldName);
                } else {
                    sp = new SearchableProperty(beanWrapper.getWrappedInstance(), fieldName, fieldName, (String) attributes.get("value"));
                    searchableProperties.put(fieldName, sp);
                }
                return sp;
            }

            SearchableProperty sp;
            if (searchableProperties.containsKey(property)) {
                sp = searchableProperties.get(property);
            } else {
                sp = new SearchableProperty(beanWrapper.getWrappedInstance(), property, getFieldName(property));
                searchableProperties.put(property, sp);
            }

            if (sp.getPropertyType() == null) {
                LOG.warn("Property [" + sp.getPropertyName() + "] not found in domain class " + beanWrapper.getWrappedClass().getName() + "; cannot apply solr mapping: " + attributes);
                return sp;
            }

            if (attributes.containsKey(NAME)) sp.setFieldName(attributes.get(NAME).toString());
            if (attributes.containsKey(VALUE)) sp.setValueClosure((Closure) attributes.get(VALUE));

            if (GrailsClassUtils.getBooleanFromMap(COMPONENT, attributes)) {
                String prefix = (String) attributes.get(PREFIX);
                if (StringUtils.hasText(prefix)) sp.setFieldName(prefix);
            }

            return sp;
        } catch (InvalidPropertyException ipe) {
            throw new MissingMethodException((String) name, beanWrapper.getWrappedClass(), new Object[]{attributes});
        }
    }

    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        throw new MissingMethodException((String) name, beanWrapper.getWrappedClass(), new Object[]{attributes, value});
    }

    private void determineDefaultSearchableProperties() {
        PropertyDescriptor[] propertyDescriptors;
        if (beanWrapper.getWrappedInstance() instanceof Collection) {
            Class elementType = CollectionUtils.findCommonElementType((Collection) beanWrapper.getWrappedInstance());
            propertyDescriptors = BeanUtils.getPropertyDescriptors(elementType);
        } else {
            propertyDescriptors = beanWrapper.getPropertyDescriptors();
        }
        for (PropertyDescriptor p : propertyDescriptors) {
            if (isPropertySearchable(p)) {
                addDefaultSearchableProperty(p);
            }
        }
    }

    private SearchableProperty addDefaultSearchableProperty(PropertyDescriptor p) {
        SearchableProperty sp = new SearchableProperty(beanWrapper.getWrappedInstance(), p.getName());

        if (ID.equals(p.getName())) {
            sp.setFieldName(ID);
            sp.setValueClosure(new Closure(this) {
                @Override
                public Object call(Object[] args) {
                    return getAlias() + "-" + args[0];
                }
            });
        } else {
            sp.setFieldName(getFieldName(p));

            if (isAssociationMultiple(p)) {
                addChildSearchableProperties(sp);
            }
        }

        searchableProperties.put(p.getName(), sp);
        return sp;
    }

    private void addChildSearchableProperties(SearchableProperty sp) {
        Map<String, SearchableProperty> componentProps = null;
        Object value = sp.getFieldValue();
        componentProps = evaluateSearchableProperties(value);
        if (componentProps != null) sp.setChildren(componentProps);
    }

    private String getFieldName(String propertyName) {
        return namingStrategy.columnName(propertyName);
    }

    private String getFieldName(PropertyDescriptor p) {
        if (isAssociationMultiple(p)) {
            String fieldName = namingStrategy.columnName(p.getName());
            if (fieldName.endsWith("s")) fieldName = fieldName.substring(0, fieldName.length() - 1) + "_";
            return fieldName;
        } else {
            return namingStrategy.columnName(p.getName());
        }
    }

    private Object getDefaultFieldValue(PropertyDescriptor p) {
        if (beanWrapper.getWrappedInstance() instanceof Collection) {
            return null;
        } else {
            return beanWrapper.getPropertyValue(p.getName());
        }
    }

    private boolean isAssociationMultiple(PropertyDescriptor p) {
        Object value = getDefaultFieldValue(p);
        if (value instanceof Collection) {
            Class elementType = CollectionUtils.findCommonElementType((Collection) value);
            return solrMappings.containsKey(elementType);
        } else if (ObjectUtils.isArray(value)) {
            TypeDescriptor td = TypeDescriptor.forObject(value);
            Class elementType = td.getElementType();
            return solrMappings.containsKey(elementType);
        } else {
            return false;
        }
    }

    private String getAlias() {
        Class elementType = beanWrapper.getWrappedClass();
        if (Collection.class.isAssignableFrom(elementType)) {
            elementType = CollectionUtils.findCommonElementType((Collection) beanWrapper.getWrappedInstance());
        }
        return this.namingStrategy.tableName(GrailsNameUtils.getShortName(elementType));
    }

    public Map<String, SearchableProperty> getSearchableProperties() {
        if (!onlyProperties.isEmpty()) {
            Map<String, SearchableProperty> searchablesOnly = new HashMap<String, SearchableProperty>(onlyProperties.size());
            for (String only : onlyProperties) {
                if (searchableProperties.containsKey(only))
                    searchablesOnly.put(only, searchableProperties.get(only));
            }
            return Collections.unmodifiableMap(searchablesOnly);
        }
        if (!exceptProperties.isEmpty()) {
            Map<String, SearchableProperty> searchablesExcept = new HashMap<String, SearchableProperty>(searchableProperties);
            for (String except : exceptProperties) {
                searchablesExcept.remove(except);
            }
            return Collections.unmodifiableMap(searchablesExcept);
        }
        return Collections.unmodifiableMap(searchableProperties);
    }

    public Set<String> getExceptProperties() {
        return Collections.unmodifiableSet(exceptProperties);
    }

    public Set<String> getExcept() {
        return Collections.unmodifiableSet(exceptProperties);
    }

    public void setExcept(Set<String> except) {
        exceptProperties.addAll(except);
    }

    public void setExcept(List<String> except) {
        exceptProperties.addAll(except);
    }

    public Set<String> getOnlyProperties() {
        return Collections.unmodifiableSet(onlyProperties);
    }

    public Set<String> getOnly() {
        return Collections.unmodifiableSet(onlyProperties);
    }

    public void setOnly(Set<String> only) {
        onlyProperties.addAll(only);
    }

    public void setOnly(List<String> only) {
        onlyProperties.addAll(only);
    }

    private boolean isPropertySearchable(PropertyDescriptor p) {
        return !p.getName().equals(DATA) &&
                !p.getName().equals(EVENTS) &&
                !p.getName().equals(MODIFIED_FIELDS) &&
                !p.getName().equals(GrailsDomainClassProperty.META_CLASS) &&
                !p.getName().equals(GrailsDomainClassProperty.CLASS) &&
                !p.getName().equals(GrailsDomainClassProperty.DATE_CREATED) &&
                !p.getName().equals(GrailsDomainClassProperty.VERSION) &&
                !p.getName().equals(GrailsDomainClassProperty.LAST_UPDATED) &&
                !p.getName().equals(GrailsDomainClassProperty.CONSTRAINTS) &&
                !p.getName().equals(GrailsDomainClassProperty.HAS_MANY) &&
                !p.getName().equals("solr");
    }

    private Map<String, SearchableProperty> evaluateSearchableProperties(Object entity) {
        if (entity == null) return null;

        Class elementType = null;
        if (entity instanceof Collection) {
            elementType = CollectionUtils.findCommonElementType((Collection) entity);
        } else {
            elementType = entity.getClass();
        }
        if (!solrMappings.containsKey(elementType)) return null;

        SearchablePropertyBuilder builder = new SearchablePropertyBuilder(entity, solrMappings, namingStrategy);
        Closure c = solrMappings.get(elementType);
        c.setDelegate(builder);
        c.call();

        return builder.getSearchableProperties();
    }

}
