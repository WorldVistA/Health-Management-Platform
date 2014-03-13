package org.osehra.cpe;

import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class HmpProperties {
    public static final String HMP_HOME_ENVIRONMENT_VARIABLE_NAME = "HMP_HOME";
    public static final String HMP_HOME_SYSTEM_PROPERTY_NAME = "hmp.home";
    public static final String HMP_PROPERTIES_FILE_NAME = "hmp.properties";

    public static final String VERSION = "hmp.version";
    public static final String BUILD_DATE = "hmp.build.date";
    public static final String SETUP_COMPLETE = "hmp.setup.complete";
    public static final String SERVER_ID = "hmp.server.id";
    public static final String PROPERTIES_ENCRYPTED = "hmp.properties.encrypted";
    public static final String SERVER_HOST = "hmp.server.host";
    public static final String SERVER_PORT_HTTP = "hmp.server.port.http";
    public static final String SERVER_PORT_HTTPS = "hmp.server.port.https";
    public static final String SERVER_URL = "hmp.server.url";
    public static final String DATABASE_DRIVER_CLASS = "database.driverClassName";
    public static final String DATABASE_URL = "database.url";
    public static final String DATABASE_USERNAME = "database.username";
    public static final String DATABASE_PASSWORD = "database.password";
    public static final String DATASOURCE_NAME = "datasource.name";
    public static final String HIBERNATE_DIALECT = "hibernate.dialect";
    public static final String ACTIVEMQ_BROKER_URL = "activemq.broker.url";
    public static final String ACTIVEMQ_DATA_DIR = "activemq.data.dir";
    public static final String EHCACHE_DATA_DIR = "ehcache.disk.store.dir";
    public static final String TERM_DB_DIR = "termdb.dir";
    public static final String LOGS_DIR = "logs.dir";
    public static final String JDS_URL = "jds.url";
    public static final String SOLR_URL = "solr.url";
    public static final String INFO_BUTTON_URL = "openinfobutton.url";

    public static Map<String, String> getProperties(Environment environment) {
        return getProperties(environment, false);
    }

    public static Map<String, String> getProperties(Environment environment, boolean includeSensitive) {
        Map<String, String> props = new HashMap<String, String>();
        for (String prop : HmpProperties.getPropertyNames(includeSensitive)) {
            props.put(prop, environment.getProperty(prop));
        }
        return Collections.unmodifiableMap(props);
    }

    public static Set<String> getPropertyNames() {
        return getPropertyNames(false);
    }

    public static Set<String> getPropertyNames(boolean includeSensitive) {
        HashSet<String> s = new HashSet<String>();
        Field[] fields = HmpProperties.class.getFields();
        for (Field f : fields) {
            if (Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()) && String.class.isAssignableFrom(f.getType())) {
                try {
                    String propertyName = f.get(null).toString();
                    if (isSensitive(propertyName)) {
                        if (includeSensitive) s.add(propertyName);
                    } else {
                        s.add(propertyName);
                    }
                } catch (IllegalAccessException e) {
                    // NOOP: shouldn't ever happen because we're only examining public fields
                }
            }
        }
        return Collections.unmodifiableSet(s);
    }


    private static boolean isSensitive(String propertyName) {
        String[] disallowed = new String[]{"password", "username", "credent", "passwd", "usrname"};
        for (String s : disallowed) {
            if (propertyName.toLowerCase().indexOf(s) >= 0) {
                return true;
            }
        }
        return false;
    }
}
