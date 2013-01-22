package EXT.DOMAIN.cpe.vpr.web.view;

import grails.converters.XML;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class GrailsXMLView extends AbstractGrailsConverterView {

    public static final String DEFAULT_CONTENT_TYPE = "application/xml";

    public GrailsXMLView() {
        setContentType(DEFAULT_CONTENT_TYPE);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object o = locateToBeConverted(model);

        response.getWriter().write(new XML(o).toString());
    }
}
