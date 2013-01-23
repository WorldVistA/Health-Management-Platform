package org.osehra.cpe.vista.rpc.broker.conn;

import org.osehra.cpe.vista.rpc.LinesFromRpcResponseExtractor;
import org.osehra.cpe.vista.rpc.RpcResponse;
import org.osehra.cpe.vista.rpc.RpcResponseExtractionException;
import org.osehra.cpe.vista.rpc.RpcResponseExtractor;
import org.osehra.cpe.vista.util.VistaStringUtils;
import org.springframework.dao.DataAccessException;

import java.util.Collections;

public class ConnectionUserResponseExtractor implements RpcResponseExtractor<ConnectionUser> {

    private LinesFromRpcResponseExtractor linesFromRpcResponseExtractor = new LinesFromRpcResponseExtractor();

    @Override
    public ConnectionUser extractData(RpcResponse response) throws RpcResponseExtractionException {
        String[] lines = linesFromRpcResponseExtractor.extractData(response);

        ConnectionUser user = new ConnectionUser();
        user.setDUZ(lines[0]);
        user.setName(lines[1]);
        user.setStandardName(lines[2]);
        String division = VistaStringUtils.piece(lines[3], VistaStringUtils.U, 3);
        user.setDivision(division);
        user.setDivisionNames(Collections.singletonMap(division, VistaStringUtils.piece(lines[3], VistaStringUtils.U, 2)));
        user.setTitle(lines[4]);
        user.setServiceSection(lines[5]);
        user.setLanguage(lines[6]);
        user.setDTime(lines[7]);
        return user;
    }
}
