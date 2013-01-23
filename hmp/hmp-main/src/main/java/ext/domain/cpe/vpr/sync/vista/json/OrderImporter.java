package org.osehra.cpe.vpr.sync.vista.json;

import org.osehra.cpe.vpr.Order;
import org.osehra.cpe.vpr.sync.vista.VistaDataChunk;

import org.springframework.core.convert.converter.Converter;

public class OrderImporter extends AbstractJsonImporter<Order> implements Converter<VistaDataChunk, Order> {
    @Override
    protected Order create() {
        return new Order();
    }
}
