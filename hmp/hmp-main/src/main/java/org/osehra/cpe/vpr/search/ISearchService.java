package org.osehra.cpe.vpr.search;

import java.util.List;

public interface ISearchService {
    SearchPatientResults textSearchByPatient(String queryText, String vprPatient);
    List<String> textSuggestByPatient(String prefix, String vprPatient);
}
