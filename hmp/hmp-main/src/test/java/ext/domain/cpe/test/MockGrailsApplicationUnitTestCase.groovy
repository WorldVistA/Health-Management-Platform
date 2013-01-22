package EXT.DOMAIN.cpe.test;
import grails.test.*
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.converters.ConverterUtil
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.commons.DefaultArtefactInfo
import org.springframework.context.ApplicationContext
import org.springframework.context.support.StaticApplicationContext

abstract class MockGrailsApplicationUnitTestCase extends GrailsUnitTestCase {

    private GrailsApplication grailsApplication

    protected void setUp() {
        super.setUp()

        ConverterUtil.grailsApplication = getGrailsApplication()
    }

    protected void tearDown() {
        super.tearDown()
    }

    final GrailsApplication getGrailsApplication() {
        if (!grailsApplication) {
            grailsApplication = createGrailsApplication(domainClassesInfo)
        }
        return grailsApplication
    }

    static GrailsApplication createGrailsApplication(DefaultArtefactInfo domainClassesInfo) {
        ApplicationContext mainContext = new StaticApplicationContext()
        return [
                getArtefact: {artefactType, name -> domainClassesInfo.getGrailsClass(name)},
                getArtefactByLogicalPropertyName: {artefactType, name -> domainClassesInfo.getGrailsClassByLogicalPropertyName(name)},
                getArtefacts: {artefactType -> domainClassesInfo.updateComplete(); domainClassesInfo.getGrailsClasses()},
                getArtefactInfo: {artefactType -> domainClassesInfo},
                isArtefactOfType: {artefactType, name -> domainClassesInfo.getGrailsClass(name) != null},
                isDomainClass: {clazz -> domainClassesInfo.getGrailsClass(clazz.name) != null},
                getClassLoader: { MockGrailsApplicationUnitTestCase.classLoader },
                getConfig: {ConfigurationHolder.config},
                getMainContext: { mainContext },
                toString: {'mockGrailsApplication'}
        ] as GrailsApplication
    }
}
