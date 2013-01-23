package org.osehra.cpe.datetime.propertyeditors;

import org.osehra.cpe.datetime.IntervalOfTime;
import org.osehra.cpe.datetime.format.IntervalOfTimeFormat;

import java.beans.PropertyEditorSupport;

/**
 * Editor for IntervalOfTime.  IntervalsOfTime instances will be converted to their string representations and vice-versa
 * with IntervalOfTimeFormat.
 *
 * @see org.osehra.cpe.datetime.IntervalOfTime
 * @see org.osehra.cpe.datetime.format.IntervalOfTimeFormat
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
