package org.osehra.cpe.vpr.search


class SearchPatientResults {
    String errorMessage = ''
    String original = ''
    String altQuery = ''
    List<SummaryItem> foundItems = []
    List<String> corrections = []
    int elapsed = 0
}
