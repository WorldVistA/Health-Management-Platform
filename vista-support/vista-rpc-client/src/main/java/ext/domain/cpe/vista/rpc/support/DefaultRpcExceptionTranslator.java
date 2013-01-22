package EXT.DOMAIN.cpe.vista.rpc.support;

import EXT.DOMAIN.cpe.vista.rpc.RpcException;
import EXT.DOMAIN.cpe.vista.rpc.TimeoutWaitingForRpcResponseException;
import EXT.DOMAIN.cpe.vista.rpc.broker.conn.*;
import EXT.DOMAIN.cpe.vista.rpc.broker.protocol.*;
import EXT.DOMAIN.cpe.vista.rpc.pool.TimeoutWaitingForIdleConnectionException;
import org.springframework.dao.*;
import org.springframework.util.StringUtils;

public class DefaultRpcExceptionTranslator implements RpcExceptionTranslator {

    public static final String CLOSE_TASK = "close";

    @Override
    public DataAccessException translate(String task, String rpc, RpcException e) {
        if (e instanceof DivisionNotFoundException) {
            return new InvalidDataAccessResourceUsageException(getMessage(task, rpc), e);
        } else if (e instanceof BadCredentialsException) {
            return new PermissionDeniedDataAccessException(getMessage(task, rpc), e);
        } else if (e instanceof ServiceTemporarilyDownException) {
            return new DataAccessResourceFailureException(getMessage(task, rpc), e);
        } else if (e instanceof VerifyCodeExpiredException) {
            return new PermissionDeniedDataAccessException(getMessage(task, rpc), e);
        } else if (e instanceof DivisionMismatchException) {
            return new InvalidDataAccessApiUsageException(getMessage(task, rpc), e);
        } else if (e instanceof RpcContextAccessDeniedException) {
            return new PermissionDeniedDataAccessException(getMessage(task, rpc), e);
        } else if (e instanceof LockedException) {
            return new PermissionDeniedDataAccessException(getMessage(task, rpc), e);
        } else if (e instanceof RpcContextNotFoundException) {
            return new InvalidDataAccessResourceUsageException(getMessage(task, rpc), e);
        } else if (e instanceof RpcNotFoundException) {
            return new InvalidDataAccessResourceUsageException(getMessage(task, rpc), e);
        } else if (e instanceof ProductionMismatchException) {
            return new InvalidDataAccessResourceUsageException(getMessage(task, rpc), e);
        } else if (e instanceof ServerUnavailableException) {
            return new DataAccessResourceFailureException(getMessage(task, rpc), e);
        } else if (e instanceof ServerNotFoundException) {
            return new DataAccessResourceFailureException(getMessage(task, rpc), e);
        } else if (e instanceof ConnectionClosedException) {
            return new InvalidDataAccessApiUsageException(getMessage(task, rpc), e);
        } else if (e instanceof TimeoutWaitingForRpcResponseException) {
            return new DataAccessResourceFailureException(getMessage(task, rpc), e);
        } else if (e instanceof TimeoutWaitingForIdleConnectionException) {
            return new TransientDataAccessResourceException(getMessage(task, rpc), e);
        }

        if (CLOSE_TASK.equals(task)) {
            return new CleanupFailureDataAccessException(getMessage(task, rpc), e);
        }

        return new DataRetrievalFailureException(getMessage(task, rpc), e);
    }

    private String getMessage(String task, String rpc) {
        if (StringUtils.hasText(rpc))
            return "error during " + task + " of " + rpc;
        else
            return "error during " + task;
    }
}
