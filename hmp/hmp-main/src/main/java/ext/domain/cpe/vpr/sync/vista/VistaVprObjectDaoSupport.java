package EXT.DOMAIN.cpe.vpr.sync.vista;

import com.fasterxml.jackson.databind.JsonNode;
import EXT.DOMAIN.cpe.vista.rpc.JacksonRpcResponseExtractor;
import EXT.DOMAIN.cpe.vista.rpc.RpcOperations;
import EXT.DOMAIN.cpe.vista.rpc.RpcResponse;
import EXT.DOMAIN.cpe.vista.util.VistaStringUtils;
import EXT.DOMAIN.cpe.vpr.pom.DefaultNamingStrategy;
import EXT.DOMAIN.cpe.vpr.pom.INamingStrategy;
import org.springframework.beans.factory.annotation.Required;

public class VistaVprObjectDaoSupport {
    protected RpcOperations rpcTemplate;
    private INamingStrategy namingStrategy = new DefaultNamingStrategy();

    @Required
    public void setRpcTemplate(RpcOperations rpcTemplate) {
        this.rpcTemplate = rpcTemplate;
    }

    public void setNamingStrategy(INamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    protected String getCollectionName(Class entityType) {
        return namingStrategy.collectionName(entityType);
    }

    protected JsonNode executeForJsonAndSplitLastArg(String rpcUri, Object... args) {
        args[args.length - 1] = VistaStringUtils.splitLargeStringIfNecessary((String) args[args.length - 1]);
        return rpcTemplate.executeForJson(rpcUri, args);
    }
}
