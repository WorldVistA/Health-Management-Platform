package EXT.DOMAIN.cpe.vistalink;

import EXT.DOMAIN.exception.FoundationsException;
import EXT.DOMAIN.vistalink.adapter.cci.VistaLinkAppProxyConnectionSpec;
import EXT.DOMAIN.vistalink.adapter.cci.VistaLinkConnection;
import EXT.DOMAIN.vistalink.adapter.cci.VistaLinkDuzConnectionSpec;
import EXT.DOMAIN.vistalink.adapter.record.LoginsDisabledFaultException;
import EXT.DOMAIN.vistalink.adapter.record.VistaLinkFaultException;
import EXT.DOMAIN.vistalink.rpc.*;
import EXT.DOMAIN.vistalink.security.m.SecurityFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jca.cci.core.CciTemplate;
import org.springframework.jca.cci.core.ConnectionCallback;
import org.springframework.util.Assert;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.ConnectionSpec;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the central class in the rpc package. It simplifies the use of VistaLink RPCs and helps to avoid common errors.
 * It executes core RPC workflow, leaving application code to provide RPC names and parameters and extract results. This
 * class executes RPCs with VistaLink, initiating iteration over RPC results and catching VistaLink exceptions and
 * translating them to the generic data access exception hierarchy defined in the org.springframework.dao package.
 */
public class VistaLinkTemplate {

    static final int DEFAULT_TIMEOUT = 10000;

    private static Logger log = LoggerFactory.getLogger(VistaLinkTemplate.class);

    private int timeOut = DEFAULT_TIMEOUT;
    private ConnectionFactoryLocator connectionFactoryLocator;
    private static final String UNABLE_TO_EXECUTE_RPC = "Unable to execute RPC";

    public VistaLinkTemplate(ConnectionFactoryLocator connectionFactoryLocator) {
        this.connectionFactoryLocator = connectionFactoryLocator;
    }

    public String rpcAsUser(final String division, final String duz, final String rpcContext, final String rpcName) throws DataAccessException {
        return rpcAsUser(division, duz, rpcContext, rpcName, (List) null);
    }

    public String rpcAsUser(final String division, final String duz, final String rpcContext, final String rpcName, Object... params) throws DataAccessException {
        return rpcAsUser(division, duz, rpcContext, rpcName, createParameterList(params));
    }

    public String rpcAsUser(final String division, final String duz, final String rpcContext, final String rpcName, final List params) throws DataAccessException {
        return rpc(new VistaLinkDuzConnectionSpec(division, duz), division, duz, rpcContext, rpcName, params);
    }

    public String rpcAsApplication(final String division, final String applicationProxyName, final String rpcContext, final String rpcName) throws DataAccessException {
        return rpcAsApplication(division, applicationProxyName, rpcContext, rpcName, (List) null);
    }

    public String rpcAsApplication(final String division, final String applicationProxyName, final String rpcContext, final String rpcName, Object... params) throws DataAccessException {
        return rpcAsApplication(division, applicationProxyName, rpcContext, rpcName, createParameterList(params));
    }

    public String rpcAsApplication(final String division, final String applicationProxyName, final String rpcContext, final String rpcName, final List params) throws DataAccessException {
        return rpc(new VistaLinkAppProxyConnectionSpec(division, applicationProxyName), division, applicationProxyName, rpcContext, rpcName, params);
    }

    public String rpc(ConnectionSpec connectionSpec, final String division, final String user, final String rpcContext, final String rpcName, final List params) throws DataAccessException {
        if (log.isDebugEnabled())
            log.debug(MessageFormat.format("''{0}'' called in context ''{1}'' by ''{2}'' at facility ''{3}'' with params: {4}", new Object[]{rpcName, rpcContext, user, division, params}));

        ConnectionFactory connectionFactory = connectionFactoryLocator.getConnectionFactory(division);
        try {
            CciTemplate t = new CciTemplate(connectionFactory, connectionSpec);
            RpcResponse response = (RpcResponse) t.execute(new ConnectionCallback() {
                public Object doInConnection(Connection connection, ConnectionFactory connectionFactory) throws ResourceException, SQLException, DataAccessException {
                    try {
                        Assert.isInstanceOf(VistaLinkConnection.class, connection);
                        VistaLinkConnection conn = (VistaLinkConnection) connection;
                        conn.setTimeOut(getTimeOut());

                        RpcRequest request = RpcRequestFactory.getRpcRequest(rpcContext, rpcName);
                        request.setUseProprietaryMessageFormat(true);
                        request.setXmlResponse(false);

                        if (params != null) {
                            request.setParams(params);
                        }

                        return conn.executeRPC(request);
                    } catch (IllegalArgumentException e) {
                        throw new InvalidDataAccessApiUsageException(UNABLE_TO_EXECUTE_RPC, e);
                    } catch (NoRpcContextFaultException e) {
                        throw new VistaLinkNoRpcContextException(e);
                    } catch (RpcNotOkForProxyUseException e) {
                        throw new VistaLinkRpcNotOkForProxyUseException(e);
                    } catch (RpcNotInContextFaultException e) {
                        throw new VistaLinkRpcNotInContextException(e);
                    } catch (LoginsDisabledFaultException e) {
                        throw new VistaLinkLoginsDisabledException(e);
                    } catch (SecurityFaultException e) {
                        throw new VistaLinkPermissionDeniedException(e);
                    } catch (VistaLinkFaultException e) {
                        throw new VistaLinkDataRetrievalFailureException(e);
                    } catch (FoundationsException e) {
                        throw new DataRetrievalFailureException(UNABLE_TO_EXECUTE_RPC, e);
                    }
                }
            });
            String result = response.getResults();
            if (log.isDebugEnabled()) {
                log.debug(MessageFormat.format("''{0}'' returned: {1}", new Object[]{rpcName, result}));
            }
            return result;
        } catch (IllegalArgumentException e) {
            throw new InvalidDataAccessApiUsageException(UNABLE_TO_EXECUTE_RPC, e);
        }
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public ConnectionFactoryLocator getConnectionFactoryLocator() {
        return connectionFactoryLocator;
    }

    private List createParameterList(Object... params) {
        List paramList = new ArrayList();
        for (Object param : params) {
            paramList.add(param);
        }
        return paramList;
    }
}
