package EXT.DOMAIN.cpe.grails.plugins.datetime

import org.springframework.beans.PropertyEditorRegistrar
import org.springframework.beans.PropertyEditorRegistry
import EXT.DOMAIN.cpe.datetime.PointInTime
import EXT.DOMAIN.cpe.datetime.propertyeditors.PointInTimeEditor
import EXT.DOMAIN.cpe.datetime.IntervalOfTime
import EXT.DOMAIN.cpe.datetime.propertyeditors.IntervalOfTimeEditor

public class HL7DateTimePropertyEditorRegistrar implements PropertyEditorRegistrar {

    public void registerCustomEditors(PropertyEditorRegistry propertyEditorRegistry) {
        propertyEditorRegistry.registerCustomEditor(PointInTime.class, new PointInTimeEditor());
        propertyEditorRegistry.registerCustomEditor(IntervalOfTime.class, new IntervalOfTimeEditor());
        
        // TODO: investigate editors for joda date/time types and java.util.Date editors to/from HL7 timestamp strings
    }

}
