package EXT.DOMAIN.cpe.vpr.ws.link

import EXT.DOMAIN.cpe.HmpProperties
import EXT.DOMAIN.cpe.feed.atom.Link
import EXT.DOMAIN.cpe.vpr.Medication
import EXT.DOMAIN.cpe.vpr.Patient
import EXT.DOMAIN.cpe.vpr.Problem
import EXT.DOMAIN.cpe.vpr.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment

import EXT.DOMAIN.cpe.vpr.pom.IPatientObject
import EXT.DOMAIN.cpe.vpr.pom.IPatientDAO

class OpenInfoButtonLinkGenerator implements ILinkGenerator, EnvironmentAware {
    static final Map<Class, String> SUPPORTED_TYPES = [
            'EXT.DOMAIN.cpe.vpr.Medication': "MLREV",
            'EXT.DOMAIN.cpe.vpr.Result': "LABRREV",
            'EXT.DOMAIN.cpe.vpr.Problem': "PROBLISTREV",
    ]

    boolean supports(Object object) {
        return SUPPORTED_TYPES.keySet().contains(object.class.name)
    }

//  TODO: Brian refactoring TermEng, ressurect when done
//    @Autowired
//    TermEng termEng

    @Autowired
    IPatientDAO patientDao

    Environment environment

	public OpenInfoButtonLinkGenerator() {
	}
	
	Link generateLinkFromMap(Map<String, Object> map) {
		String endpointHref = environment.getProperty(HmpProperties.INFO_BUTTON_URL)
		if (!endpointHref) return null
		
		// required values
		if (!map.gender || !map.age || !map.context || !map.searchText) {
			return null;
		}

		// generate the link
		// TODO: Currently redundant with generateLink()
		StringBuilder url = new StringBuilder(endpointHref)
		url.append("?representedOrganization.id.root=1.3.6.1.4.1.3768")
		// url.append("&patientPerson.administrativeGenderCode.c=${pt.genderCode}")
		url.append("&patientPerson.genderCode=${map.gender}")
		url.append("&age.v.v=${map.age}")
		url.append("&age.v.u=a")
		url.append("&taskContext.c.c=${map.context}")
        url.append("&mainSearchCriteria.v.dn=${map.searchText}")
        if (map.searchCode && map.searchCodeSet) {
			// optional values
            url.append("&mainSearchCriteria.v.c=${map.searchCode}")
            url.append("&mainSearchCriteria.v.cs=${map.searchCodeSet}")
        }
		url.append("&performer=PROV")
		return new Link(rel: LinkRelation.OPEN_INFO_BUTTON.toString(), href: url.toString())
	}
	
    Link generateLink(Object object) {
        if (!(object instanceof IPatientObject)) return null
        String pid = ((IPatientObject) object).getPid()
        Patient pt = patientDao.findByVprPid(pid);
        if (!pt) return null

        String endpointHref = environment.getProperty(HmpProperties.INFO_BUTTON_URL)
		if (!endpointHref) return null

        StringBuilder url = new StringBuilder(endpointHref)
        url.append("?representedOrganization.id.root=1.3.6.1.4.1.3768")
//        url.append("&patientPerson.administrativeGenderCode.c=${pt.gender.code}")
//        url.append("&patientPerson.administrativeGenderCode.c=${pt.genderCode}")
        url.append("&patientPerson.genderCode=${pt.genderCode}")
        url.append("&age.v.v=${pt.age}")
        url.append("&age.v.u=a")
        url.append("&taskContext.c.c=${SUPPORTED_TYPES[object.class.name]}")
        url.append(getMainSearchCriteria(object))
        url.append("&performer=PROV")
        return new Link(rel: LinkRelation.OPEN_INFO_BUTTON.toString(), href: url.toString())
    }

    private String getMainSearchCriteria(Object object) {
        StringBuilder url = new StringBuilder()
        if (object instanceof Medication) {
            Medication med = (object as Medication)
            url.append("&mainSearchCriteria.v.dn=${med.qualifiedName}")
            // TODO: This is the wrong field name to pass to the Terminology Engine!! its just a placeholder for now.
            // TODO: only include these two lines if the mapping returned something
//            String code = termEng.getMapping(med.productFormName, "VHAT", "RxNorm")
//            if (termEng && code) {
//                url.append("&mainSearchCriteria.v.c=${code}")
//                url.append("&mainSearchCriteria.v.cs=2.16.840.1.113883.6.88")
//            }

        } else if (object instanceof Problem) {
            Problem problem = (object as Problem)
            url.append("&mainSearchCriteria.v.dn=${problem.problemText}")
            if (problem.icdCode && problem.icdCode.startsWith("urn:icd:")) {
                url.append("&mainSearchCriteria.v.c=${problem.icdCode.substring(8)}")
                url.append("&mainSearchCriteria.v.cs=2.16.840.1.113883.6.103")
            }
        } else if (object instanceof Result) {
            Result result = (object as Result)
            url.append("&mainSearchCriteria.v.dn=${result.typeName}")
            if (result.typeCode && result.typeCode.startsWith("urn:lnc:")) {
                url.append("&mainSearchCriteria.v.c=${result.typeCode.substring(8)}")
                url.append("&mainSearchCriteria.v.cs=2.16.840.1.113883.6.1")
            }
        }
        return url.toString()
    }
}
