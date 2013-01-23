package org.osehra.cpe.vpr.sync.vista.json.integration

import org.osehra.cpe.test.junit4.runners.TestPatients
import org.junit.runner.RunWith
import org.junit.runners.Suite.SuiteClasses

import org.osehra.cpe.test.junit4.runners.ImportTestSession
import org.osehra.cpe.test.junit4.runners.ImportAllDomainsIntegrationTestRunner

@RunWith(ImportAllDomainsIntegrationTestRunner)
@SuiteClasses([ImportVisitsITCase,
ImportAccessionsITCase,
ImportProblemsITCase,
//ImportLabPanelsITCase,
ImportDocumentsITCase,
ImportVitalSignITCase,
ImportImmunizationsITCase,
ImportMedicationsITCase,
ImportOrdersITCase,
ImportSurgeriesITCase,
ImportConsultsITCase,
ImportRadiologyITCase,
ImportAllergiesITCase,
ImportAppointmentsITCase,
ImportHealthFactorsITCase,
ImportObservationsITCase])
@ImportTestSession(connectionUri = "vrpcb://10vehu;vehu10@localhost:29060")
@TestPatients(dfns = ["100846", "229", "301"])
class ImportAllDomainsITCase {
}
