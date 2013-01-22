package EXT.DOMAIN.cpe.grails;

import grails.persistence.Entity;
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.*;

/**
 * Extension of {@link DefaultGrailsApplication} that automatically adds domain classes with the {@link @grails.persistence.Entity} annotation to the class list.
 */
public class GrailsApplication extends DefaultGrailsApplication {

    public GrailsApplication(Class<?>[] classes, ClassLoader classLoader, String[] basePackages) {
        super(appendEntityClasses(basePackages, classes, classLoader), classLoader);
    }

    public static Class<?>[] getEntityClasses(String... basePackages) {
       return appendEntityClasses(basePackages, new Class[0], GrailsApplication.class.getClassLoader());
    }

    private static Class<?>[] appendEntityClasses(String[] basePackages, Class<?>[] classes, ClassLoader classLoader) {
        Set<Class> all = new HashSet<Class>(Arrays.asList(classes));

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));

        for (String basePackage : basePackages) {
            Set<BeanDefinition> entities = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition entity : entities) {
                try {
                    all.add(classLoader.loadClass(entity.getBeanClassName()));
                } catch (ClassNotFoundException e) {
                    throw new BeanCreationException("Error creating GrailsApplication", e);
                }
            }
        }

        return all.toArray(new Class[all.size()]);
    }
}
