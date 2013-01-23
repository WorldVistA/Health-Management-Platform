package org.osehra.cpe.vpr.sync.vista

import org.springframework.beans.factory.InitializingBean
import org.springframework.util.Assert
import org.springframework.core.convert.converter.Converter
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment

/**
 * Examines the {@link VistaDataChunk} element name and forwards the conversion on to an importer in the configured importers (a map of domains
 * to converter beans).
 */
class CentralImporter implements InitializingBean, Converter<VistaDataChunk, Object> {

    Map<String, Converter> importers

    void afterPropertiesSet() {
        Assert.notNull(importers, "importers must not be null")
    }

    /**
     * Converts a  {@link VistaDataChunk} to an object by delegating to importers Map, set via Spring Environment profiles.
     * @param chunk
     * @return the result of the conversion for the chunk's domain
     */
    Object convert(VistaDataChunk chunk) {
        Converter importer = importers[chunk.domain]
        if (!importer)
            throw new ImportException("No importer configured for domain '${chunk.domain}'", chunk)
        Object o = importer.convert(chunk)
        return o
    }
}
