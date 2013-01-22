package EXT.DOMAIN.cpe.datetime.solr.schema;

import EXT.DOMAIN.cpe.datetime.PointInTime;
import EXT.DOMAIN.cpe.datetime.format.HL7DateTimeFormat;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.search.SortField;
import org.apache.solr.common.SolrException;
import org.apache.solr.response.TextResponseWriter;
import org.apache.solr.response.XMLWriter;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;

import java.io.IOException;
import java.util.Map;

/**
 * This FieldType accepts HL7 time stamp (TS) strings.
 * <p/>
 * Format: YYYY[MM[DD[HHMM[SS[.S[S[S[S]]]]]]]][+/-ZZZZ]^<degree of precision>
 *
 * @see EXT.DOMAIN.cpe.datetime.PointInTime
 */
public class HL7DateField extends FieldType {

    @Override
    protected void init(IndexSchema schema, Map<String, String> args) {
        super.init(schema, args);

        // Tokenizing makes no sense
        restrictProps(TOKENIZED);
    }

    @Override
    public SortField getSortField(SchemaField field, boolean top) {
        return getStringSort(field, top);
    }

    @Override
    public void write(XMLWriter xmlWriter, String name, Fieldable f) throws IOException {
        xmlWriter.writeStr(name, f.stringValue());
    }

    @Override
    public void write(TextResponseWriter writer, String name, Fieldable f) throws IOException {
        writer.writeStr(name, f.stringValue(), false);
    }

    @Override
    public String toInternal(String val) {
        try {
            HL7DateTimeFormat.parse(val);
        } catch (IllegalArgumentException e) {
            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "Invalid HL7 Date String: '" + val + "'");
        }
        return val;
    }

    public String toInternal(PointInTime t) {
        if (t == null) return null;
        return t.toString();
    }

    @Override
    public PointInTime toObject(Fieldable f) {
        return HL7DateTimeFormat.parse(f.stringValue());
    }
}
