package org.osehra.cpe.vpr.service

import org.springframework.data.domain.Page
import org.osehra.cpe.vpr.Patient
import org.osehra.cpe.datetime.IntervalOfTime
import org.springframework.data.domain.Pageable


public interface IPatientDomainService {
    Page queryForPage(Patient pt, String domain, IntervalOfTime dateRange, Set requestedQueryNames, Map remainingRequestParams, Pageable pageable);

    Class getDomainClass(String domain);
}
