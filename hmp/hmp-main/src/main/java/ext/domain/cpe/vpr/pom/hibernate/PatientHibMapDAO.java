package org.osehra.cpe.vpr.pom.hibernate;

import static org.hibernate.criterion.Restrictions.eq;
import org.osehra.cpe.dao.hibernate.PaginationUtils;
import org.osehra.cpe.vpr.Patient;
import org.osehra.cpe.vpr.pom.IPatientDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public class PatientHibMapDAO implements IPatientDAO {

	private GenericHibMapDAO dao;

	public PatientHibMapDAO(SessionFactory sessionFactory) {
		dao = new GenericHibMapDAO(sessionFactory, true, true);
	}

	private Criteria getPatientCritiera() {
		return dao.getBlankCritiera(Patient.class);
	}

	// primary DAO interface implementations ---------------------------

	@Override
	public Patient save(Patient pat) {
		dao.save(pat);
		return pat;
	}

	@Override
	public Patient findByVprPid(String pid) {
		Criteria crit = getPatientCritiera().add(eq("pid", pid.toString()));
		return dao.findOneByCriteria(Patient.class, crit);
	}

	@Override
	public Patient findByIcn(String icn) {
		Criteria crit = getPatientCritiera().add(eq("icn", icn));
		return dao.findOneByCriteria(Patient.class, crit);
	}

	@Override
	public Patient findByAnyPid(String pid) {
		if (pid == null) return null;
		
		// Lookie what named indexes can do!
		List<Patient> results = findAllByIndex(null, "patient-ids", pid, null, null);
		if (results != null && results.size() > 0) {
			return results.get(0);
		}
		try {
			return findByVprPid(pid);
		} catch (NumberFormatException ex) {
			// ignore
		}
		return null;
	}

	@Override
	public Patient findByLocalID(String systemOrCode, String dfn) {
		return findByAnyPid(systemOrCode + ";" + dfn);
	}

    @Override
    public Page<Patient> findAll(Pageable pageable) {
        Criteria c = getPatientCritiera();
        PaginationUtils.setPaginationCriteria(c, pageable);
        List<Patient> items = dao.mapToResults(Patient.class, c.list());

		//  new criteria, set the row count projection
		c = getPatientCritiera();
		c.setProjection(Projections.rowCount());
		Integer total = (Integer) c.uniqueResult();

		return new PageImpl(items, pageable, total);
	}

	@Override
	public List<String> listPatientIds() {
		return dao.curSess().createQuery("select pt.pid from patient pt").list();
	}

	@Override
	public int count() {
		return listPatientIds().size();
	}

	@Override
	public void deleteByUID(String uid) {
		Patient pt = findByUID(uid);
		deleteAllByPatientId(pt.getPid());
//		dao.deleteByUID(Patient.class, uid);
	}

	@Override
	public void deleteByPID(String pid) {
		Patient pt = findByAnyPid(pid);
		if(pt!=null)
		{	
			deleteAllByPatientId(pid);
//			dao.delete(findByPid(pid));
		}
	}

	public void deleteByPID(Class<?> clazz, String pid) {
		Patient pt = findByAnyPid(pid);
		if(pt!=null)
		{
			deleteAllByPatientId(pid);
//			dao.delete(pt);
		}
	}

	static Session delSession = null;

	protected Session getDelSession() {
		if (delSession == null || !delSession.isConnected()) {
			delSession = sessionFactory.openSession();
		}
		return delSession;
	}

	@Autowired
	private SessionFactory sessionFactory;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Transactional
	public synchronized int deleteAllByPatientId(final String pid) {
		getSession().clear(); // Brute force, force all current patient / etc. objects to be cleared out of the current session to avoid locking issues.

		// Results
        getSession().createSQLQuery("DELETE FROM result_organizer_results WHERE result_organizer_uid in (select uid FROM RESULT_ORGANIZER WHERE pid=" + pid + ")").executeUpdate();
        getSession().createSQLQuery("DELETE FROM result_organizer WHERE pid=" + pid).executeUpdate();
        getSession().createSQLQuery("DELETE FROM result WHERE pid=" + pid).executeUpdate();

		// Vital Signs
		getSession().createSQLQuery("DELETE FROM vital_sign WHERE organizer_uid in ( SELECT vso.uid FROM vital_sign_organizer as vso WHERE vso.pid=" + pid + ")").executeUpdate();
		getSession().createSQLQuery("DELETE FROM vital_sign_organizer WHERE pid = " + pid).executeUpdate();

		// Allergies
		getSession().createSQLQuery("DELETE FROM allergy_reaction WHERE allergy_id IN (SELECT uid FROM allergy WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("DELETE FROM allergy_comment WHERE allergy_id IN (SELECT uid FROM allergy WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("DELETE FROM allergy_product WHERE allergy_id IN (SELECT uid FROM allergy WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("DELETE FROM allergy WHERE pid = " + pid).executeUpdate();

		// Orders
		getSession().createSQLQuery("delete from clinical_order where pid = " + pid).executeUpdate();

		// Procedures
		getSession().createSQLQuery("delete from procedure_provider WHERE procedure_id IN (SELECT uid FROM clinical_procedure WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("delete from procedure_result WHERE procedure_id IN (SELECT uid FROM clinical_procedure WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("delete from clinical_procedure where pid = " + pid).executeUpdate();

		// Documents
		getSession().createSQLQuery("delete from document_clinician WHERE document_id IN (SELECT uid FROM document WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("delete from document where pid = " + pid).executeUpdate();

		// Encounters
		getSession().createSQLQuery("DELETE FROM encounter_provider WHERE encounter_id IN (SELECT uid FROM encounter WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("DELETE FROM encounter_document WHERE encounter_id IN (SELECT uid FROM encounter WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("DELETE FROM encounter WHERE pid = " + pid).executeUpdate();

		// Misc.
		getSession().createSQLQuery("DELETE FROM health_factor WHERE pid = " + pid).executeUpdate();
		getSession().createSQLQuery("DELETE FROM immunization WHERE pid = " + pid).executeUpdate();


		// Medication
		getSession().createSQLQuery("DELETE FROM medication_product WHERE med_id IN (SELECT uid FROM medication WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("DELETE FROM medication_order WHERE med_id IN (SELECT uid FROM medication WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("DELETE FROM medication_indication WHERE med_id IN (SELECT uid FROM medication WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("DELETE FROM medication_fill WHERE med_id IN (SELECT uid FROM medication WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("DELETE FROM medication_dose WHERE med_id IN (SELECT uid FROM medication WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("DELETE FROM medication WHERE pid = " + pid).executeUpdate();

		// Observations
		getSession().createSQLQuery("DELETE FROM observation_qualifier WHERE observation_id IN (SELECT uid FROM observation WHERE pid = " + pid + ")").executeUpdate();
		getSession().createSQLQuery("DELETE FROM observation WHERE pid = " + pid).executeUpdate();

		// Problem
        getSession().createSQLQuery("DELETE FROM problem_comment WHERE problem_id IN (SELECT uid FROM problem WHERE pid = " + pid + ")").executeUpdate();
        getSession().createSQLQuery("DELETE FROM problem WHERE pid = " + pid).executeUpdate();

		// Patient Dems
		getSession().createSQLQuery("DELETE FROM patient_address WHERE pid = " + pid).executeUpdate();
		getSession().createSQLQuery("DELETE FROM patient_alias WHERE pid = " + pid).executeUpdate();
		getSession().createSQLQuery("DELETE FROM patient_facility WHERE pid = " + pid).executeUpdate();
		getSession().createSQLQuery("DELETE FROM patient_object_map WHERE pid = " + pid).executeUpdate();
		getSession().createSQLQuery("DELETE FROM patient_prop WHERE pid = " + pid).executeUpdate();
		getSession().createSQLQuery("DELETE FROM patient_telecom WHERE pid = " + pid).executeUpdate();
		getSession().createSQLQuery("DELETE FROM VPR_INDEX WHERE pid = " + pid).executeUpdate();
		getSession().createSQLQuery("DELETE FROM patient WHERE pid = " + pid).executeUpdate();
        getSession().flush();

		return -1;
	}

	@Override
	public Patient findByUID(String uid) {
		return dao.findByUID(Patient.class, uid);
	}

	@Override
	public Page<Patient> findAllByPID(String pid, Pageable page) {
		ArrayList<Patient> ret = new ArrayList<Patient>();
		ret.add(findByAnyPid(pid));
		return new PageImpl(ret);
	}

	@Override
	public List<Patient> findAllByIndex(String pid, String indexName,
			String start, String end, Map<String, Object> where) {
		return dao.findAllByIndex(Patient.class, pid, indexName, start, end, where);
	}

}
