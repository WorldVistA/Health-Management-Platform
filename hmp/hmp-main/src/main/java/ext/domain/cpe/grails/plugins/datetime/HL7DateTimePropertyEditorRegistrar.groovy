package org.osehra.cpe.grails.plugins.datetime

import org.springframework.beans.PropertyEditorRegistrar
import org.springframework.beans.PropertyEditorRegistry
import org.osehra.cpe.datetime.PointInTime
import org.osehra.cpe.datetime.propertyeditors.PointInTimeEditor
import org.osehra.cpe.datetime.IntervalOfTime
import org.osehra.cpe.datetime.propertyeditors.IntervalOfTimeEditor

public class HL7DateTimePropertyEditorRegistrar implements PropertyEditorRegistrar {

    public void registerCustomEditors(PropertyEditorRegistry propertyEditorRegistry) {
        propertyEditorRegistry.registerCustomEditor(PointInTime.class, new PointInTimeEditor());
        propertyEditorRegistry.registerCustomEditor(IntervalOfTime.class, new IntervalOfTimeEditor());
        
        // TODO: investigate editors for joda date/time types and java.util.Date editors to/from HL7 timestamp strings
    }

}
