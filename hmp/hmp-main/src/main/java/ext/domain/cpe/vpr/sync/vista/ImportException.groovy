package org.osehra.cpe.vpr.sync.vista

import groovy.xml.XmlUtil
import com.fasterxml.jackson.databind.ObjectMapper

class ImportException extends RuntimeException {

    private final VistaDataChunk chunk

    ImportException(String message, VistaDataChunk chunk) {
        super(message);
        this.chunk = chunk
    }

    ImportException(VistaDataChunk chunk, Throwable cause) {
        super(ImportException.getMessage(chunk), cause);
        this.chunk = chunk
    }

    VistaDataChunk getChunk() {
        chunk
    }

    static String getMessage(VistaDataChunk chunk) {
        return "error importing json chunk ${chunk.itemIndex + 1}" +
                " of ${chunk.itemCount} from ${chunk.rpcUri}\n" +
                "${new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(chunk.json)}".toString()
    }
}
