package EXT.DOMAIN.cpe.vpr.service

import org.springframework.data.domain.Page
import EXT.DOMAIN.cpe.vpr.Patient
import EXT.DOMAIN.cpe.datetime.IntervalOfTime
import org.springframework.data.domain.Pageable


public interface IPatientDomainService {
    Page queryForPage(Patient pt, String domain, IntervalOfTime dateRange, Set requestedQueryNames, Map remainingRequestParams, Pageable pageable);

    Class getDomainClass(String domain);
}
