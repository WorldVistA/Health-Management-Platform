package org.osehra.cpe.vista.rpc.pool;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.osehra.cpe.vista.rpc.RpcIoException;
import org.osehra.cpe.vista.rpc.RpcResponse;
import org.osehra.cpe.vista.rpc.broker.protocol.DefaultRpcMessageReader;

import java.io.IOException;
import java.io.Reader;

import org.junit.Before;
import org.junit.Test;

public class DefaultRpcMessageReaderTest {
    private Reader reader;
    private DefaultRpcMessageReader defaultRpcMessageReader;
    
	@Before
    public void setUp() throws IOException {
    }

	@Test
	public void testReadServerPacketNormalFlow() throws IOException {
        reader = mock(Reader.class);
        when(reader.read()).thenReturn(4);
		defaultRpcMessageReader = new DefaultRpcMessageReader(reader);
		try {
			RpcResponse response = defaultRpcMessageReader.readResponse();
		}catch(Throwable t) {
			fail("Should not throw RpcIoException");
		}
	}
	
	@Test
	public void testReadServerPacketThrowsRpcIoException() throws IOException {
        reader = mock(Reader.class);
        when(reader.read()).thenReturn(-1);
		defaultRpcMessageReader = new DefaultRpcMessageReader(reader);
		try {
			defaultRpcMessageReader.readResponse();
			fail("Should throw RpcIoException");
		}catch(Throwable t) {
	        assertTrue(t instanceof RpcIoException);
		}
	}
}
