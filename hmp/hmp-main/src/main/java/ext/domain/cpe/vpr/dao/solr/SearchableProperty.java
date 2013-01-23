package org.osehra.cpe.vpr.dao.solr;

import groovy.lang.Closure;
import org.springframework.beans.BeanWrapperImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SearchableProperty {

    private BeanWrapperImpl beanWrapper;
    private String propertyName;
    private String fieldName;
    private Object fieldValue;
    private BigDecimal boost;
    private Closure valueClosure;
    private Map<String, SearchableProperty> children = new HashMap<String, SearchableProperty>();

    public SearchableProperty(Object target, String propertyName) {
        this(target, propertyName, null, null, null, null);
    }

    public SearchableProperty(Object target, String propertyName, String fieldName) {
        this(target, propertyName, fieldName, null, null, null);
    }

    public SearchableProperty(Object target, String propertyName, String fieldName, String fieldValue) {
        this(target, propertyName, fieldName, fieldValue, null, null);
    }

    public SearchableProperty(Object target, String propertyName, String fieldName, String fieldValue, BigDecimal boost, Closure valueClosure) {
        this.beanWrapper = new BeanWrapperImpl(target);
        this.propertyName = propertyName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.boost = boost;
        this.valueClosure = valueClosure;
    }

    public Class<?> getOwningClass() {
        return beanWrapper.getWrappedClass();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getPropertyType() {
        return beanWrapper.getPropertyType(propertyName);
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getFieldValue() {
        if (beanWrapper.getWrappedInstance() instanceof Collection) {
            ArrayList value = new ArrayList();
            Collection items = (Collection) beanWrapper.getWrappedInstance();
            for (Object item : items) {
                Object itemFieldValue = getFieldValue(item);
                if (itemFieldValue != null)
                    value.add(itemFieldValue);
            }
            if (value.isEmpty())
                return null;
            else
                return value;
        } else {
            return getFieldValue(beanWrapper.getWrappedInstance());
        }
    }

    private Object getFieldValue(Object target) {
        final Object oldTarget = beanWrapper.getWrappedInstance();
        beanWrapper.setWrappedInstance(target);
        try {
            if (valueClosure != null) {
                Object it = fieldValue != null ? fieldValue : beanWrapper.getPropertyValue(propertyName);
                getValueClosure().setDelegate(beanWrapper.getWrappedInstance());
                Object value = getValueClosure().call(it);
                return value;
            }

            if (fieldValue != null)
                return fieldValue;

            return beanWrapper.getPropertyValue(propertyName);
        } finally {
            beanWrapper.setWrappedInstance(oldTarget);
        }
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public Closure getValueClosure() {
        return valueClosure;
    }

    public void setValueClosure(Closure valueClosure) {
        this.valueClosure = valueClosure;
    }

    public BigDecimal getBoost() {
        return boost;
    }

    public void setBoost(BigDecimal boost) {
        this.boost = boost;
    }

    public Map<String, SearchableProperty> getChildren() {
        return children;
    }

    public void setChildren(Map<String, SearchableProperty> children) {
        this.children = children;
    }
}
