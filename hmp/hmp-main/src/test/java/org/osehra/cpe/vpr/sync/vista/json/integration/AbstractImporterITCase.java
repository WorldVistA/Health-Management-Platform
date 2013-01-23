package org.osehra.cpe.vpr.sync.vista.json.integration;

import org.osehra.cpe.test.junit4.runners.AnnotationFinder;
import org.osehra.cpe.test.junit4.runners.Importer;
import org.osehra.cpe.vpr.pom.IPatientObject;
import org.osehra.cpe.vpr.sync.vista.ImportException;
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk;
import org.junit.runners.model.InitializationError;
import org.springframework.core.convert.converter.Converter;

public abstract class AbstractImporterITCase<T extends IPatientObject> {

    private VistaDataChunk chunk;

    private Class<? extends Converter<VistaDataChunk, T>> importerClass;

    private T domainInstance;

    public AbstractImporterITCase(VistaDataChunk chunk) {
        this.chunk = chunk;
        try {
            this.importerClass = getImporterClass(this.getClass());
        } catch (InitializationError initializationError) {
            throw new RuntimeException(initializationError);
        }
    }

    protected T getDomainInstance() {
        if (domainInstance == null) {
            domainInstance = importDomainInstance();
        }
        return domainInstance;
    }

    public VistaDataChunk getChunk() {
        return this.chunk;
    }

    private T importDomainInstance() {
        try {
            Converter<VistaDataChunk, T> importer = importerClass.newInstance();
            domainInstance = importer.convert(chunk);
            return domainInstance;
        } catch (ImportException e) {
            throw e;
        } catch (Throwable e) {
            throw new ImportException(chunk, e);
        }
    }

    private Class<? extends Converter<VistaDataChunk, T>> getImporterClass(Class<?> testClass) throws InitializationError {
        AnnotationFinder annotationFinder = new AnnotationFinder(testClass);
        Importer importerClass = annotationFinder.find(Importer.class);
        if (importerClass == null)
            throw new InitializationError(Importer.class.getSimpleName() + " annotation must be placed over your test class for this runner: "
                    + testClass.getSimpleName());
        return (Class<? extends Converter<VistaDataChunk, T>>) importerClass.value();
    }
}
