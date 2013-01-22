package EXT.DOMAIN.cpe.vpr.sync.vista.json;

import EXT.DOMAIN.cpe.vpr.Order;
import EXT.DOMAIN.cpe.vpr.sync.vista.VistaDataChunk;

import org.springframework.core.convert.converter.Converter;

public class OrderImporter extends AbstractJsonImporter<Order> implements Converter<VistaDataChunk, Order> {
    @Override
    protected Order create() {
        return new Order();
    }
}
