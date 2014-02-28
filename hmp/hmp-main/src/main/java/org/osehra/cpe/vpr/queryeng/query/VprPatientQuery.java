package org.osehra.cpe.vpr.queryeng.query;

import org.osehra.cpe.vpr.pom.IPatientDAO;
import org.osehra.cpe.vpr.queryeng.Query;
import org.osehra.cpe.vpr.viewdef.RenderTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Queries for all patient IDs currently in the VPR
 */
public class VprPatientQuery extends Query {

    public VprPatientQuery() {
        super("pid", null);
    }

    @Override
    public void exec(RenderTask task) throws Exception {
        IPatientDAO patientDao = task.getResource(IPatientDAO.class);

        // TODO: add paging from params and use patientDao.listPatientIds(new PageRequest());
        List<String> pids = patientDao.listPatientIds();
        for (String pid : pids) {
            Map<String, Object> row = new HashMap<String, Object>();
            row.put("pid", pid);
            task.add(row);
        }
    }
}
