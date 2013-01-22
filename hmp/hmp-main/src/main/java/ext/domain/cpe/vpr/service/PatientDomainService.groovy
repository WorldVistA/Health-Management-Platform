package EXT.DOMAIN.cpe.vpr.service

import EXT.DOMAIN.cpe.datetime.IntervalOfTime
import EXT.DOMAIN.cpe.vpr.Patient
import EXT.DOMAIN.cpe.vpr.VprConstants
import EXT.DOMAIN.cpe.vpr.pom.IGenericPatientObjectDAO
import grails.util.GrailsNameUtils

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.util.ClassUtils
import EXT.DOMAIN.cpe.vpr.web.UnknownDomainException
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired

@Service
class PatientDomainService implements IPatientDomainService {

    public static final Map DOMAIN_ALIASES = [
            'accession': [aliasFor: 'result-organizer', queryName: 'accessions'],
            'chemistry': [aliasFor: 'result', queryName: 'chemistry'],
            'consult': [aliasFor: 'procedure', queryName: 'consults'],
            'factor': [aliasFor: 'health-factor'],
            'lab': [aliasFor: 'result', queryName: 'chemistry'],
            'microbiology': [aliasFor: 'result', queryName: 'microbiology'],
            'radiology': [aliasFor: 'procedure', queryName: 'radiology'],
            'surgery': [aliasFor: 'procedure', queryName: 'surgery'],
			'visit': [aliasFor: 'encounter', queryName: 'visit'],
            'vital': [aliasFor: 'vital-sign']
    ]

    private Map<String, Class> domainClassesByShortPropertyName

    private IGenericPatientObjectDAO genericPatientObjectDao

    PatientDomainService() {
        domainClassesByShortPropertyName = getPatientRelatedDomainClasses()
    }

    @Autowired
    void setGenericPatientObjectDao(IGenericPatientObjectDAO genericPatientObjectDao) {
        this.genericPatientObjectDao = genericPatientObjectDao
    }

    private Map<String, Class> getPatientRelatedDomainClasses() {
        Map<String, Class> classesByShortName = new HashMap<String, Class>();
        for (Class c: VprConstants.PATIENT_RELATED_DOMAIN_CLASSES) {
            classesByShortName.put(ClassUtils.getShortNameAsProperty(c), c);
        }
        return classesByShortName;
    }

    Page queryForPage(Patient pt, String domain, IntervalOfTime dateRange, Set requestedQueryNames, Map remainingRequestParams, Pageable pageable) {
        Class domainClass = getDomainClass(domain)
        return genericPatientObjectDao.findAllByIndex(domainClass, pt.getPid(), dateRange.low, dateRange.high, remainingRequestParams)
//        return genericPatientObjectDao.findAllByPatientDateRangeNamedQueryAndMatchingProperties(domainClass, pt, dateRange, requestedQueryNames, remainingRequestParams, pageable)
    }

    Class getDomainClass(String domain) {
        // handle domain aliases in lookup table
        if (PatientDomainService.DOMAIN_ALIASES.containsKey(domain)) {
            domain = PatientDomainService.DOMAIN_ALIASES[domain].aliasFor
        }

        // convert hyphenated names to logical property name
        domain = GrailsNameUtils.getPropertyNameForLowerCaseHyphenSeparatedName(domain)

        Class domainClass = domainClassesByShortPropertyName.get(domain)
        if (!domainClass) throw new UnknownDomainException(domain)

        return domainClass
    }
}
