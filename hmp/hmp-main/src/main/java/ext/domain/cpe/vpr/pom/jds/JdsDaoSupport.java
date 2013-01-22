package EXT.DOMAIN.cpe.vpr.pom.jds;

import EXT.DOMAIN.cpe.vpr.pom.POMObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;

public class JdsDaoSupport extends DaoSupport {

    protected final Logger logger;

    protected JdsOperations jdsTemplate;

    protected POMObjectMapper jsonMapper = new POMObjectMapper();

    public JdsDaoSupport() {
        logger = LoggerFactory.getLogger(getClass());
    }

    public void setJdsTemplate(JdsOperations jdsTemplate) {
        this.jdsTemplate = jdsTemplate;
    }

    @Override
    protected void checkDaoConfig() throws IllegalArgumentException {
        Assert.notNull(this.jdsTemplate, "jdsTemplate must not be null");
    }
}
