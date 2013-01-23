package org.osehra.cpe.vpr.sync.msg

import org.springframework.util.ErrorHandler

import org.springframework.beans.factory.annotation.Autowired

import org.osehra.cpe.vpr.SyncError
import org.springframework.transaction.UnexpectedRollbackException
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.osehra.cpe.vpr.dao.ISyncErrorDao

class SyncMessageErrorHandler implements ErrorHandler {

    private static Logger LOG = LoggerFactory.getLogger(SyncMessageErrorHandler.class);

    @Autowired
    ISyncErrorDao syncErrorDao

    void handleError(Throwable t) {
        if (t?.cause instanceof UnexpectedRollbackException || t instanceof UnexpectedRollbackException) return; // these are thrown as a result of other exceptions occurring during "sync", lets not log them again

        StringWriter sw = new StringWriter()
        t.printStackTrace(new PrintWriter(sw))

        if (t.cause) t = t.cause

        LOG.error("error processing JMS message", t);
        syncErrorDao.save(new SyncError(item: t.getClass().name, message: t.message, stackTrace: sw.toString()));
    }
}
