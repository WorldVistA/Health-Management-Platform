package EXT.DOMAIN.cpe.datetime.propertyeditors;

import EXT.DOMAIN.cpe.datetime.IntervalOfTime;
import EXT.DOMAIN.cpe.datetime.format.IntervalOfTimeFormat;

import java.beans.PropertyEditorSupport;

/**
 * Editor for IntervalOfTime.  IntervalsOfTime instances will be converted to their string representations and vice-versa
 * with IntervalOfTimeFormat.
 *
 * @see EXT.DOMAIN.cpe.datetime.IntervalOfTime
 * @see EXT.DOMAIN.cpe.datetime.format.IntervalOfTimeFormat
 */
public class IntervalOfTimeEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        IntervalOfTime i = (IntervalOfTime) getValue();
        if (i == null)
            return super.getAsText();
        else
            return IntervalOfTimeFormat.print(i);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        IntervalOfTime i = IntervalOfTimeFormat.parse(text);
        setValue(i);
    }
}
