package org.osehra.cpe.vista.rpc.broker.conn;

import org.osehra.cpe.vista.rpc.broker.protocol.TransportMetrics;
import org.osehra.cpe.vista.rpc.conn.ConnectionMetrics;

public class BrokerConnectionMetrics implements ConnectionMetrics {
    private long requestCount = 0;
    private long responseCount = 0;

    private final TransportMetrics inTransportMetrics;
    private final TransportMetrics outTransportMetrics;

    public BrokerConnectionMetrics(TransportMetrics inTransportMetrics, TransportMetrics outTransportMetrics) {
        this.inTransportMetrics = inTransportMetrics;
        this.outTransportMetrics = outTransportMetrics;
    }

    @Override
    public long getRequestCount() {
        return requestCount;
    }

    @Override
    public long getResponseCount() {
        return responseCount;
    }

    @Override
    public long getSentBytesCount() {
        if (outTransportMetrics != null) {
            return outTransportMetrics.getBytesTransferred();
        } else {
            return -1;
        }
    }

    @Override
    public long getReceivedBytesCount() {
        if (inTransportMetrics != null) {
            return inTransportMetrics.getBytesTransferred();
        } else {
            return -1;
        }
    }

    public void incrementRequestCount() {
        this.requestCount++;
    }

    public void incrementResponseCount() {
        this.responseCount++;
    }

    @Override
    public void reset() {
        this.requestCount = 0;
        this.responseCount = 0;

        if (inTransportMetrics != null) {
            inTransportMetrics.reset();
        }
        if (outTransportMetrics != null) {
            outTransportMetrics.reset();
        }
    }
}
