# Edit this file to CATALINA_BASE/bin/setenv.sh to set custom options
# Tomcat accepts two parameters JAVA_OPTS and CATALINA_OPTS
# JAVA_OPTS are used during START/STOP/RUN
# CATALINA_OPTS are used during START/RUN

# JVM memory settings - general
GENERAL_JVM_OPTS="-server -Xmx1024m -Xss192k"

# JVM Sun specific settings
# For a complete list http://blogs.sun.com/watt/resource/jvm-options-list.html
SUN_JVM_OPTS="-XX:MaxPermSize=256m \
              -XX:MaxGCPauseMillis=500"

# Set any custom application options here
APPLICATION_OPTS="-Dcom.sun.management.jmxremote \
    -Dcom.sun.management.jmxremote.port=8189 \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Dsolr.solr.home=$CATALINA_BASE/solr-home/ \
    -Dsolr.data.dir=$CATALINA_BASE/hmp-home/solr/data \
	-Dhmp.home=$CATALINA_BASE/hmp-home"

JVM_OPTS="$GENERAL_JVM_OPTS $SUN_JVM_OPTS"

CATALINA_OPTS="$JVM_OPTS $APPLICATION_OPTS"
