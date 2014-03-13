package org.osehra.cpe.vpr.web.view;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

abstract public class AbstractGrailsConverterView extends AbstractView {

    public static final String DEFAULT_MODEL_KEY = "response";

    private String modelKey = DEFAULT_MODEL_KEY;

    /**
     * Set the name of the model key that represents the object to be converted. If not specified, the entire model map will be
     * converted.
     */
    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        super.prepareResponse(request, response);

        response.setContentType(getContentType());
    }

    /**
     * Locates the object to be converted. The default implementation first attempts to look under the configured
     * {@linkplain #setModelKey(String) model key}, if any, before return the model Map itself for conversion.
     *
     * @param model the model Map
     * @return the Object to be converted (or <code>model</code> if none found)

     * @see #setModelKey(String)
     */
    protected Object locateToBeConverted(Map model) {
        if (StringUtils.hasText(modelKey) && model.containsKey(modelKey)) {
            return model.get(modelKey);
        } else {
            return model;
        }
    }
}
