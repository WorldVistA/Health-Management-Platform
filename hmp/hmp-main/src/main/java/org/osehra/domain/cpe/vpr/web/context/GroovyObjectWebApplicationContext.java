package org.osehra.cpe.vpr.web.context;

import groovy.lang.ExpandoMetaClass;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * An ApplicationContext that extends XmlWebApplicationContext and implements GroovyObject such that
 * beans can be retrieved with the dot de-reference syntax instead of using getBean('name').
 */
public class GroovyObjectWebApplicationContext extends XmlWebApplicationContext implements GroovyObject {

    protected MetaClass metaClass;
    private BeanWrapper ctxBean = new BeanWrapperImpl(this);

    public GroovyObjectWebApplicationContext() throws org.springframework.beans.BeansException {
        ExpandoMetaClass.enableGlobally();
        metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(getClass());
    }

    public MetaClass getMetaClass() {
        return metaClass;
    }

    public Object getProperty(String property) {
        if (containsBean(property)) {
            return getBean(property);
        }
        if (ctxBean.isReadableProperty(property)) {
            return ctxBean.getPropertyValue(property);
        }
        return null;
    }

    public Object invokeMethod(String name, Object args) {
        return metaClass.invokeMethod(this, name, args);
    }

    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    public void setProperty(String propertyName, Object newValue) {
        throw new UnsupportedOperationException();
    }
}
