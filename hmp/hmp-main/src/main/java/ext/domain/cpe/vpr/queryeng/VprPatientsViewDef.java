package org.osehra.cpe.vpr.queryeng;

import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.dao.ISyncErrorDao;
import org.osehra.cpe.vpr.pom.IPatientDAO;
import org.osehra.cpe.vpr.queryeng.query.JDSQuery;
import org.osehra.cpe.vpr.queryeng.query.QueryDef;
import org.osehra.cpe.vpr.queryeng.query.VprPatientQuery;
import org.osehra.cpe.vpr.viewdef.QueryMapper;
import org.osehra.cpe.vpr.viewdef.RenderTask;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * {@link ViewDef} for browsing through all patients in the VPR
 */
@Component(value = "org.osehra.cpe.vpr.queryeng.VprPatientsViewDef")
@Scope("prototype")
public class VprPatientsViewDef extends ViewDef {
    public VprPatientsViewDef() {
        // fetch VPR PIDs for all patients in the VPR
        VprPatientQuery primary = new VprPatientQuery();
        addQuery(primary);

        // fetch domain counts per patient
        addQuery(new QueryMapper.PerRowAppendMapper(new JDSQuery("pid", new QueryDef(), "/vpr/{pid}/count/domain") {
            @Override
            protected void filterTransformResults(RenderTask task, Map<String, Object> params, List<Map<String, Object>> items) {
                RenderTask.RowRenderSubTask rowtask = (RenderTask.RowRenderSubTask) task;
                String pid = (String) rowtask.getParentRowVal("pid");

                for (Map<String, Object> item : items) {
                    String topic = (String) item.get("topic");
                    Integer count = (Integer) item.get("count");

                    task.appendVal(pid, topic, count);
                }
            }
        }));
        // fetch demographics per patient
        addQuery(new QueryMapper.PerRowAppendMapper(new Query("pid", null) {

            @Override
            public void exec(RenderTask task) throws Exception {
                RenderTask.RowRenderSubTask rowtask = (RenderTask.RowRenderSubTask) task;
                String pid = (String) rowtask.getParentRowVal("pid");

                IPatientDAO patientDao = task.getResource(IPatientDAO.class);
                Patient pt = patientDao.findByVprPid(pid);
                if (pt == null) return;
                
                task.appendVal(pid, "fullName", pt.getFullName());
                task.appendVal(pid, "icn", pt.getIcn());

                Set<String> qdfns = new HashSet<String>(pt.getPatientIds());
                qdfns.remove(pt.getIcn());
                qdfns.remove(pt.getSsn());
                qdfns.remove(pt.getPid());
                task.appendVal(pid, "localPatientIds", StringUtils.collectionToCommaDelimitedString(qdfns));
            }

//            @Override
//            protected void filterTransformResults(RenderTask task, Map<String, Object> params, List<Map<String, Object>> items) {
//                RenderTask.RowRenderSubTask rowtask = (RenderTask.RowRenderSubTask) task;
//                String pid = (String) rowtask.getParentRowVal("pid");
//
//                Map<String,Object> demographics = items.get(0);
//                task.appendVal(pid, "fullName", demographics.get("fullName"));
//                task.appendVal(pid, "icn", demographics.get("icn"));
//
//
//                List<Map<String,Object>> facilities = (List<Map<String, Object>>) demographics.get("facilities");
//                if (facilities != null && !facilities.isEmpty()) {
//
//                }
//
//            }
        }));

        // fetch number of sync errors per patient
        addQuery(new QueryMapper.PerRowAppendMapper(new Query("pid", null) {
            @Override
            public void exec(RenderTask task) throws Exception {
                RenderTask.RowRenderSubTask rowtask = (RenderTask.RowRenderSubTask) task;
                String pid = (String) rowtask.getParentRowVal("pid");

                ISyncErrorDao syncErrorDao = task.getResource(ISyncErrorDao.class);
                Integer errorCount = syncErrorDao.countByPatientId(pid);

                if (errorCount > 0)
                    task.appendVal(pid, "error", errorCount);
            }
        }));

        addColumns(primary, "pid", "icn", "localPatientIds", "fullName", "error", "allergy", "document", "encounter", "factor", "laboratory", "medication", "order", "problem", "procedure", "vitalsign");

        getColumn("pid").setMetaData("text", "PID");
        getColumn("icn").setMetaData("text", "ICN");
        getColumn("localPatientIds").setMetaData("text", "QDFNs");
        getColumn("fullName").setMetaData("text", "Patient");

        getColumn("error").setMetaData("text", "Errs");

        getColumn("allergy").setMetaData("text", "Allergies");
        getColumn("document").setMetaData("text", "Docs");
        getColumn("encounter").setMetaData("text", "Encs");
        getColumn("factor").setMetaData("text", "HFs");
        getColumn("laboratory").setMetaData("text", "Labs");
        getColumn("medication").setMetaData("text", "Meds");
        getColumn("order").setMetaData("text", "Orders");
        getColumn("problem").setMetaData("text", "Probs");
        getColumn("procedure").setMetaData("text", "Procs");
        getColumn("vitalsign").setMetaData("text", "Vitals");

        // set default column width
        for (ColDef col : getColumns()) {
            col.setMetaData("width", 36);
        }
        getColumn("icn").setMetaData("width", 60);
        getColumn("localPatientIds").setMetaData("width", 60);
        getColumn("fullName").setMetaData("width", 186);
    }
}
