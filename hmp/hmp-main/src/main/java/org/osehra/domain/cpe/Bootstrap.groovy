package org.osehra.cpe

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextInitializer
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.ResourcePropertySource
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import org.springframework.web.context.ConfigurableWebApplicationContext

import static org.osehra.cpe.HmpProperties.*
import net.sf.ehcache.config.DiskStoreConfiguration
import net.sf.ehcache.CacheManager
import net.sf.ehcache.config.Configuration
import net.sf.ehcache.config.ConfigurationFactory

/**
 * Configures Spring 3.1 Environment based on contents of hmp.properties
 */
class Bootstrap implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

    /** HMP version properties property source name: {@value} */
    public static final String HMP_VERSION_PROPERTIES_PROPERTY_SOURCE_NAME = "hmpVersionProperties";

    /** HMP default properties property source name: {@value} */
    public static final String HMP_DEFAULT_PROPERTIES_PROPERTY_SOURCE_NAME = "hmpDefaultProperties";

    /** HMP compound properties property source name: {@value} */
    public static final String HMP_CALCULATED_PROPERTIES_PROPERTY_SOURCE_NAME = "hmpCalculatedProperties";

    /** HMP properties property source name: {@value} */
    public static final String HMP_PROPERTIES_PROPERTY_SOURCE_NAME = "hmpProperties";

    public static final String DEVELOPMENT_PROFILE = "dev";
    public static final String JNDI_DATASOURCE_PROFILE = "jndi-datasource";
    public static final String PROPERTIES_DATASOURCE_PROFILE = "properties-datasource";

    private static Logger LOG = LoggerFactory.getLogger(Bootstrap.class)

    public static boolean isSetupComplete(Environment env) {
        return Boolean.parseBoolean(env.getProperty(SETUP_COMPLETE));
    }

    public static Resource getHmpHomeDirectory(ApplicationContext applicationContext) {
        Environment environment = applicationContext.getEnvironment();

        // check for HMP_HOME
        String home = environment.getProperty(HMP_HOME_ENVIRONMENT_VARIABLE_NAME);

        // check for hmp.home
        if (environment.containsProperty(HMP_HOME_SYSTEM_PROPERTY_NAME)) {
            home = environment.getProperty(HMP_HOME_SYSTEM_PROPERTY_NAME)
        }

        // otherwise use current working directory
        if (!StringUtils.hasText(home)) home = "."

        if (!home.endsWith(File.separator)) home += File.separator;

        Resource homeDir = applicationContext.getResource("file:" + home)
        return homeDir
    }

    public static Resource getHmpPropertiesResource(ApplicationContext applicationContext) {
        Resource homeDir = getHmpHomeDirectory(applicationContext);

        Resource hmpProps = homeDir.createRelative(HMP_PROPERTIES_FILE_NAME);

        return hmpProps;
    }

    void initialize(ConfigurableWebApplicationContext applicationContext) {
        ConfigurableEnvironment environment = (ConfigurableEnvironment) applicationContext.getEnvironment();

        Resource homeDir = getHmpHomeDirectory(applicationContext);
        Resource hmpProps = getHmpPropertiesResource(applicationContext);
		
        // add version info
        environment.getPropertySources().addFirst(new ResourcePropertySource(HMP_VERSION_PROPERTIES_PROPERTY_SOURCE_NAME, applicationContext.getResource("classpath:/version.properties")));

        // add hmp-defaults with low precedence
        environment.getPropertySources().addLast(createHmpDefaultsPropertySource(applicationContext));

        if (hmpProps.exists()) {
			Map map = new ResourcePropertySource(HMP_PROPERTIES_PROPERTY_SOURCE_NAME, hmpProps).getProperties().get("source");
			if(map.get("hmp.properties.encrypted")) {
				map.put(HmpProperties.DATABASE_URL, VprEncryption.getInstance().decrypt(map.get(HmpProperties.DATABASE_URL)));
				map.put(HmpProperties.DATABASE_PASSWORD, VprEncryption.getInstance().decrypt(map.get(HmpProperties.DATABASE_PASSWORD)));
				map.put(HmpProperties.DATABASE_USERNAME, VprEncryption.getInstance().decrypt(map.get(HmpProperties.DATABASE_USERNAME)));
			}
            LOG.info("loading configuration from ${hmpProps.file?.absolutePath}");
            environment.getPropertySources().addFirst(new MapPropertySource(HMP_PROPERTIES_PROPERTY_SOURCE_NAME, map));
		} else {
            LOG.info("no configuration found at ${hmpProps.file?.absolutePath}, using defaults");
            environment.getPropertySources().addFirst(createHmpRuntimeDefaultsPropertySource(homeDir));
        }

        // add calculated/compound hmp properties
        environment.getPropertySources().addLast(createHmpCalculatedPropertySource(homeDir, environment));

        environment.activeProfiles // this initializes the activeProfiles list
        if (StringUtils.hasText(environment.getProperty(DATASOURCE_NAME))) {
            LOG.info("activating profile: ${JNDI_DATASOURCE_PROFILE}");
            environment.addActiveProfile(JNDI_DATASOURCE_PROFILE);
        } else {
            LOG.info("activating profile: ${PROPERTIES_DATASOURCE_PROFILE}");
            environment.addActiveProfile(PROPERTIES_DATASOURCE_PROFILE);
        }

        initializeEhcacheDiskStore(environment);

        // dump all hmp environment settings to log
        if (LOG.isDebugEnabled()) {
            Map props = HmpProperties.getProperties(environment, true);

            String template = "%s=%s\n";
            StringBuilder sb = new StringBuilder();
            for (Map.Entry e : props.entrySet()) {
                sb.append(String.format(template, e.getKey(), e.getValue()));
            }

            LOG.debug("active profiles: {}", environment.activeProfiles)
            LOG.debug("environment properties:\n{}", sb.toString())
        }
    }

    private void initializeEhcacheDiskStore(Environment environment) {
        DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
        diskStoreConfiguration.setPath(environment.getProperty(EHCACHE_DATA_DIR));

        Configuration configuration = ConfigurationFactory.parseConfiguration();
        configuration.addDiskStore(diskStoreConfiguration);

        CacheManager.newInstance(configuration);
    }

    /**
     * Property defaults that are "baked" in to the HMP, but can still be overridden.
     * @param resourceLoader
     * @return
     */
    protected PropertySource createHmpDefaultsPropertySource(ResourceLoader resourceLoader) {
        return new ResourcePropertySource(HMP_DEFAULT_PROPERTIES_PROPERTY_SOURCE_NAME, resourceLoader.getResource("classpath:/hmp-defaults.properties"));
    }

    /**
     * Property defaults used when no "hmp.properties" file is found.  In other words, these are intended to be set in hmp.properties.
     * @param homeDir
     * @return
     */
    protected PropertySource createHmpRuntimeDefaultsPropertySource(Resource homeDir) {
        Map runtimeProps = new HashMap();
        runtimeProps.put(SERVER_ID, getDefaultServerId())
        runtimeProps.put(SERVER_HOST, getDefaultServerHost())
        runtimeProps.put(DATABASE_DRIVER_CLASS, "org.h2.Driver")
        runtimeProps.put(DATABASE_URL, "jdbc:h2:file:${homeDir.filename}/db/vpr;AUTO_SERVER=TRUE;MVCC=TRUE")
        runtimeProps.put(DATABASE_USERNAME, "sa")
        runtimeProps.put(DATABASE_PASSWORD, "")
        return new MapPropertySource(HMP_PROPERTIES_PROPERTY_SOURCE_NAME, runtimeProps);
    }

    /**
     * Property values that are calculated from the values of other existing properties ("compound"), but that can still be overridden in hmp.properties.
     * @param environment
     * @return
     */
    protected PropertySource createHmpCalculatedPropertySource(Resource homeDir, Environment environment) {
        String homeDirCanonicalPath = homeDir.file.canonicalPath;

        Map calculatedProps = new HashMap();

        calculatedProps.put(SERVER_URL, getDefaultServerUrl(environment))
        calculatedProps.put(ACTIVEMQ_BROKER_URL, "vm://hmp-${environment.getProperty(HmpProperties.SERVER_ID)}");
        calculatedProps.put(ACTIVEMQ_DATA_DIR, "${homeDirCanonicalPath}"+File.separator+"activemq-data");
        calculatedProps.put(EHCACHE_DATA_DIR, "${homeDirCanonicalPath}"+File.separator+"ehcache");
        calculatedProps.put(TERM_DB_DIR, homeDirCanonicalPath);
        calculatedProps.put(LOGS_DIR, "${homeDirCanonicalPath}"+File.separator+"logs");
        calculatedProps.put(SOLR_URL, getDefaultSolrUrl(environment));

        if (StringUtils.hasText(environment.getProperty(HmpProperties.DATABASE_DRIVER_CLASS)) && environment.getProperty(HmpProperties.DATABASE_DRIVER_CLASS).contains("h2")) {
            calculatedProps.put(HmpProperties.HIBERNATE_DIALECT, "org.hibernate.dialect.H2Dialect")
        } else if (StringUtils.hasText(environment.getProperty(HmpProperties.DATABASE_DRIVER_CLASS)) && environment.getProperty(HmpProperties.DATABASE_DRIVER_CLASS).contains("intersys")) {
            calculatedProps.put(HmpProperties.HIBERNATE_DIALECT, "org.hibernate.dialect.Cache71Dialect")
        }

        return new MapPropertySource(HMP_CALCULATED_PROPERTIES_PROPERTY_SOURCE_NAME, calculatedProps);
    }

    private String getDefaultServerUrl(Environment environment) {
        Assert.hasText(environment.getProperty(SERVER_HOST));
        Assert.hasText(environment.getProperty(SERVER_PORT_HTTPS));

        return "https://" + environment.getProperty(SERVER_HOST) + ":" + environment.getProperty(SERVER_PORT_HTTPS) + "/"
    }

    private String getDefaultSolrUrl(Environment environment) {
        Assert.hasText(environment.getProperty(SERVER_HOST));
        Assert.hasText(environment.getProperty(SERVER_PORT_HTTP));

        return "http://" + environment.getProperty(SERVER_HOST) + ":" + environment.getProperty(SERVER_PORT_HTTP) + "/solr/"
    }

    private String getDefaultServerId() {
        return UUID.randomUUID().toString().toUpperCase()
    }

    private String getDefaultServerHost() {
        String host = InetAddress.localHost.hostName
        return host;
    }
}
