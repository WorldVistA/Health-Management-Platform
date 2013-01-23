package org.osehra.cpe.hibernate;

import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Date;

public class DateCreatedPreInsertListener implements PreInsertEventListener {

    private BeanWrapperImpl beanWrapper = new BeanWrapperImpl();

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        beanWrapper.setWrappedInstance(event.getEntity());
        if (beanWrapper.isWritableProperty("dateCreated")) {
            beanWrapper.setPropertyValue("dateCreated", new Date());
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
