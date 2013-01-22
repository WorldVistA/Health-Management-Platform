package EXT.DOMAIN.cpe.vpr.sync.vista.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import EXT.DOMAIN.cpe.datetime.PointInTime
import static org.junit.Assert.*

class MedicationViewDefTest extends AbstractImporterTest {

    private ObjectMapper jsonMapper = new ObjectMapper();
//http://localhost:9080/vpr/1/index/med-qualified-name/dose
    static final String UPDATE_RESULT_STRING_JSON = '''
{

    "apiVersion": "1.0",
    "data": {
        "updated": 20120831075725,
        "totalItems": 36,
        "items": [
            {
                "dosages": [
                    {
                        "dose": "50 MG",
                        "start": 20111201,
                        "stop": 20120830
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20120830,
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "quantityOrdered": 180,
                        "successor": "urn:va:F484:229:med:35759"
                    }
                ],
                "overallStart": 20111201,
                "overallStop": 20121201,
                "qualifiedName": "METOPROLOL TARTRATE TAB",
                "uid": "urn:va:F484:229:med:34210",
                "vaStatus": "DISCONTINUED (EDIT)",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "325 MG",
                        "start": 20120724,
                        "stop": 20120823
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20120830,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 30,
                        "fillsRemaining": 0,
                        "quantityOrdered": 120
                    }
                ],
                "overallStart": 20120724,
                "overallStop": 20120823,
                "qualifiedName": "ACETAMINOPHEN TAB",
                "uid": "urn:va:F484:229:med:35248",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "1 VIAL (1MG)",
                        "start": 20120717,
                        "stop": 20120816
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20120830,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 1,
                        "fillsRemaining": 0,
                        "quantityOrdered": 1
                    }
                ],
                "overallStart": 20120717,
                "overallStop": 20120816,
                "qualifiedName": "GLUCAGON INJ INJ",
                "uid": "urn:va:F484:229:med:35184",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "50 MG",
                        "start": 20100227,
                        "stop": 20110228
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20100227,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "quantityOrdered": 180
                    }
                ],
                "overallStart": 20100227,
                "overallStop": 20110228,
                "qualifiedName": "METOPROLOL TARTRATE TAB",
                "uid": "urn:va:F484:229:med:27952",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "40 MG",
                        "start": 20100227,
                        "stop": 20110228
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20100227,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "quantityOrdered": 90
                    }
                ],
                "overallStart": 20100227,
                "overallStop": 20110228,
                "qualifiedName": "SIMVASTATIN TAB",
                "uid": "urn:va:F484:229:med:28052",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "500 MG",
                        "start": 20100227,
                        "stop": 20100528
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20100227,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 0,
                        "quantityOrdered": 180
                    }
                ],
                "overallStart": 20100227,
                "overallStop": 20100528,
                "qualifiedName": "METFORMIN TAB,SA",
                "uid": "urn:va:F484:229:med:27852",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "500 MG",
                        "start": 20080128,
                        "stop": 20090128
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20080426,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "predecessor": "urn:va:F484:229:med:18085",
                        "quantityOrdered": 180
                    }
                ],
                "overallStart": 20080128,
                "overallStop": 20090128,
                "qualifiedName": "METFORMIN TAB,SA",
                "uid": "urn:va:F484:229:med:21154",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "50 MG",
                        "start": 20080128,
                        "stop": 20090128
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20080426,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "predecessor": "urn:va:F484:229:med:18083",
                        "quantityOrdered": 180
                    }
                ],
                "overallStart": 20080128,
                "overallStop": 20090128,
                "qualifiedName": "METOPROLOL TARTRATE TAB",
                "uid": "urn:va:F484:229:med:21155",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "40 MG",
                        "start": 20080128,
                        "stop": 20090128
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20080426,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "predecessor": "urn:va:F484:229:med:18086",
                        "quantityOrdered": 90
                    }
                ],
                "overallStart": 20080128,
                "overallStop": 20090128,
                "qualifiedName": "SIMVASTATIN TAB",
                "uid": "urn:va:F484:229:med:21156",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "50 MG",
                        "start": 20070411,
                        "stop": 20080128
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20070411,
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "quantityOrdered": 180,
                        "successor": "urn:va:F484:229:med:21155"
                    }
                ],
                "overallStart": 20070411,
                "overallStop": 20080411,
                "qualifiedName": "METOPROLOL TARTRATE TAB",
                "uid": "urn:va:F484:229:med:18083",
                "vaStatus": "DISCONTINUED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "500 MG",
                        "start": 20070411,
                        "stop": 20080128
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20070411,
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "predecessor": "urn:va:F484:229:med:17245",
                        "quantityOrdered": 180,
                        "successor": "urn:va:F484:229:med:21154"
                    }
                ],
                "overallStart": 20070411,
                "overallStop": 20080411,
                "qualifiedName": "METFORMIN TAB,SA",
                "uid": "urn:va:F484:229:med:18085",
                "vaStatus": "DISCONTINUED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "40 MG",
                        "start": 20070411,
                        "stop": 20080128
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20070411,
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 30,
                        "fillsRemaining": 5,
                        "predecessor": "urn:va:F484:229:med:17244",
                        "quantityOrdered": 90,
                        "successor": "urn:va:F484:229:med:21156"
                    }
                ],
                "overallStart": 20070411,
                "overallStop": 20080411,
                "qualifiedName": "SIMVASTATIN TAB",
                "uid": "urn:va:F484:229:med:18086",
                "vaStatus": "DISCONTINUED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "5 MG",
                        "start": 20060531,
                        "stop": 20060604
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20060604,
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 30,
                        "fillsRemaining": 5,
                        "quantityOrdered": 30
                    }
                ],
                "overallStart": 20060531,
                "overallStop": 20070601,
                "qualifiedName": "WARFARIN TAB",
                "uid": "urn:va:F484:229:med:17195",
                "vaStatus": "DISCONTINUED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "40 MG",
                        "start": 20060531,
                        "stop": 20060604
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20060604,
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 30,
                        "fillsRemaining": 5,
                        "predecessor": "urn:va:F484:229:med:16965",
                        "quantityOrdered": 90,
                        "successor": "urn:va:F484:229:med:18086"
                    }
                ],
                "overallStart": 20060531,
                "overallStop": 20070601,
                "qualifiedName": "SIMVASTATIN TAB",
                "uid": "urn:va:F484:229:med:17244",
                "vaStatus": "DISCONTINUED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "500 MG",
                        "start": 20060531,
                        "stop": 20060604
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 20060604,
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "predecessor": "urn:va:F484:229:med:16978",
                        "quantityOrdered": 180,
                        "successor": "urn:va:F484:229:med:18085"
                    }
                ],
                "overallStart": 20060531,
                "overallStop": 20070601,
                "qualifiedName": "METFORMIN TAB,SA",
                "uid": "urn:va:F484:229:med:17245",
                "vaStatus": "DISCONTINUED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "50 MG",
                        "start": 20050317,
                        "stop": 20060318
                    }
                ],
                "facilityName": "CAMP MASTER",
                "lastFilled": 20050317,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 30,
                        "fillsRemaining": 5,
                        "predecessor": "urn:va:F484:229:med:15223",
                        "quantityOrdered": 180
                    }
                ],
                "overallStart": 20050317,
                "overallStop": 20060318,
                "qualifiedName": "METOPROLOL TAB",
                "uid": "urn:va:F484:229:med:16964",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "40 MG",
                        "start": 20050317,
                        "stop": 20050317
                    }
                ],
                "facilityName": "CAMP MASTER",
                "lastFilled": 20050317,
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 30,
                        "fillsRemaining": 5,
                        "predecessor": "urn:va:F484:229:med:15224",
                        "quantityOrdered": 90,
                        "successor": "urn:va:F484:229:med:17244"
                    }
                ],
                "overallStart": 20050317,
                "overallStop": 20060318,
                "qualifiedName": "SIMVASTATIN TAB",
                "uid": "urn:va:F484:229:med:16965",
                "vaStatus": "DISCONTINUED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "500 MG",
                        "start": 20050317,
                        "stop": 20050317
                    }
                ],
                "facilityName": "CAMP MASTER",
                "lastFilled": 20050317,
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "quantityOrdered": 180,
                        "successor": "urn:va:F484:229:med:17245"
                    }
                ],
                "overallStart": 20050317,
                "overallStop": 20060318,
                "qualifiedName": "METFORMIN TAB,SA",
                "uid": "urn:va:F484:229:med:16978",
                "vaStatus": "DISCONTINUED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "50 MG",
                        "start": 20040330,
                        "stop": 20040330
                    }
                ],
                "facilityName": "CAMP MASTER",
                "lastFilled": 20040330,
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "quantityOrdered": 180,
                        "successor": "urn:va:F484:229:med:16964"
                    }
                ],
                "overallStart": 20040330,
                "overallStop": 20050331,
                "qualifiedName": "METOPROLOL TAB",
                "uid": "urn:va:F484:229:med:15223",
                "vaStatus": "DISCONTINUED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "40 MG",
                        "start": 20040330,
                        "stop": 20040330
                    }
                ],
                "facilityName": "CAMP MASTER",
                "lastFilled": 20040330,
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "quantityOrdered": 90,
                        "successor": "urn:va:F484:229:med:16965"
                    }
                ],
                "overallStart": 20040330,
                "overallStop": 20050331,
                "qualifiedName": "SIMVASTATIN TAB",
                "uid": "urn:va:F484:229:med:15224",
                "vaStatus": "DISCONTINUED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "40 MG",
                        "start": 20020305,
                        "stop": 20030306
                    }
                ],
                "facilityName": "CAMP MASTER",
                "lastFilled": 20020305,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 90,
                        "fillsRemaining": 3,
                        "quantityOrdered": 90
                    }
                ],
                "overallStart": 20020305,
                "overallStop": 20030306,
                "qualifiedName": "SIMVASTATIN TAB",
                "uid": "urn:va:F484:229:med:12722",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "facilityName": "CAMP MASTER",
                "lastFilled": 19990921,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 7,
                        "fillsRemaining": 4,
                        "quantityOrdered": 21
                    }
                ],
                "overallStart": 19990907,
                "overallStop": 20000907,
                "qualifiedName": "PSEUDOEPHEDRINE/TRIPROLIDINE TAB TAB",
                "uid": "urn:va:F484:229:med:10553",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 19990922,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 30,
                        "fillsRemaining": 4,
                        "quantityOrdered": 90
                    }
                ],
                "overallStart": 19990902,
                "overallStop": 20000902,
                "qualifiedName": "CAPTOPRIL TAB TAB",
                "uid": "urn:va:F484:229:med:10552",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "facilityName": "SLC-FO HMP DEV",
                "lastFilled": 19990513,
                "medStatusName": "historical",
                "orders": [
                    {
                        "daysSupply": 30,
                        "fillsRemaining": 1,
                        "quantityOrdered": 20
                    }
                ],
                "overallStart": 19990226,
                "overallStop": 20000227,
                "qualifiedName": "ACETYLCHOLINE CHLORIDE SOLN,OPH SOLN,OPH",
                "uid": "urn:va:F484:229:med:9527",
                "vaStatus": "EXPIRED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "81 MG"
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "medStatusName": "active",
                "qualifiedName": "ASPIRIN TAB,EC",
                "uid": "urn:va:F484:229:med:18084",
                "vaStatus": "ACTIVE",
                "vaType": "N"
            },
            {
                "dosages": [
                    {
                        "dose": "150 MG"
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 30,
                        "quantityOrdered": 60
                    }
                ],
                "overallStart": 20110615124459,
                "qualifiedName": "RANITIDINE TAB ",
                "uid": "urn:va:F484:229:med:33770",
                "vaStatus": "CANCELLED",
                "vaType": "O"
            },
            {
                "dosages": [
                    {
                        "dose": "50 MG",
                        "start": 201112011707,
                        "stop": 201112090000
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "medStatusName": "historical",
                "overallStart": 201112011707,
                "qualifiedName": "ACARBOSE TAB",
                "uid": "urn:va:F484:229:med:34241",
                "vaStatus": "EXPIRED",
                "vaType": "I"
            },
            {
                "dosages": [
                    {
                        "dose": "300 MG",
                        "start": 201201121504,
                        "stop": 201201200000
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "medStatusName": "historical",
                "overallStart": 201201121504,
                "qualifiedName": "ABACAVIR TAB",
                "uid": "urn:va:F484:229:med:34659",
                "vaStatus": "EXPIRED",
                "vaType": "I"
            },
            {
                "dosages": [
                    {
                        "dose": "10 MG",
                        "start": 201201121522,
                        "stop": 201201200000
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "medStatusName": "historical",
                "overallStart": 201201121522,
                "qualifiedName": "PROTRIPTYLINE TAB",
                "uid": "urn:va:F484:229:med:34660",
                "vaStatus": "EXPIRED",
                "vaType": "I"
            },
            {
                "dosages": [
                    {
                        "dose": "125 MG",
                        "start": 201201131346,
                        "stop": 201201200000
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "medStatusName": "historical",
                "overallStart": 201201131346,
                "qualifiedName": "ACETAZOLAMIDE TAB",
                "uid": "urn:va:F484:229:med:34661",
                "vaStatus": "EXPIRED",
                "vaType": "I"
            },
            {
                "dosages": [
                    {
                        "dose": "200 MG",
                        "start": 201205171839,
                        "stop": 201205250000
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "medStatusName": "historical",
                "overallStart": 201205171839,
                "qualifiedName": "ACARBOSE TAB",
                "uid": "urn:va:F484:229:med:34912",
                "vaStatus": "EXPIRED",
                "vaType": "I"
            },
            {
                "dosages": [
                    {
                        "dose": "500 MG",
                        "start": 201207171258,
                        "stop": 201207241730
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "medStatusName": "not active",
                "overallStart": 201207171258,
                "qualifiedName": "ACETAMINOPHEN TAB",
                "uid": "urn:va:F484:229:med:34978",
                "vaStatus": "DISCONTINUED",
                "vaType": "I"
            },
            {
                "facilityName": "SLC-FO HMP DEV",
                "medStatusName": "historical",
                "overallStart": 201206261914,
                "qualifiedName": "ACYCLOVIR INJ, ALBUMIN INJ in AMINO ACIDS 10% INJ",
                "uid": "urn:va:F484:229:med:35015",
                "vaStatus": "EXPIRED",
                "vaType": "V"
            },
            {
                "facilityName": "SLC-FO HMP DEV",
                "medStatusName": "historical",
                "overallStart": 201206261914,
                "qualifiedName": "ALATROFLOXACIN INJ,SOLN, ALPHA-1-PROTEINASE INHIBITOR,HUMAN INJ,SOLN in AMINO ACIDS 5.5% INJ",
                "uid": "urn:va:F484:229:med:35016",
                "vaStatus": "EXPIRED",
                "vaType": "V"
            },
            {
                "dosages": [
                    {
                        "dose": "500 MG/1VIAL",
                        "start": 201207171258,
                        "stop": 201207241730
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "medStatusName": "not active",
                "overallStart": 201207171258,
                "qualifiedName": "ACETAZOLAMIDE INJ INJ",
                "uid": "urn:va:F484:229:med:35216",
                "vaStatus": "DISCONTINUED",
                "vaType": "I"
            },
            {
                "dosages": [
                    {
                        "dose": "37.5 MG"
                    }
                ],
                "facilityName": "SLC-FO HMP DEV",
                "medStatusName": "not active",
                "orders": [
                    {
                        "daysSupply": 90,
                        "predecessor": "urn:va:F484:229:med:34210",
                        "quantityOrdered": 270
                    }
                ],
                "qualifiedName": "METOPROLOL TARTRATE TAB",
                "uid": "urn:va:F484:229:med:35759",
                "vaStatus": "PENDING",
                "vaType": "O"
            }
        ]
    }

}
'''

    @Test
    public void testUpdate() throws Exception {
        JsonNode json = jsonMapper.readValue(UPDATE_RESULT_STRING_JSON, JsonNode.class)
        JsonNode dataNode = json.path("data").path("items")

        String dose
        String medDose
        String medName
        Boolean doseChange

        String daysSupply
        String fillsRemaining
        String pred
        String quantityOrdered
        String start
        String stop
        String succ
        String uid
        def meds = [:]
        def successor = [:]
        def predecessor = [:]
        //convert json nodes to map
        for (int i = 0; i < dataNode.size(); i++) {
            def data = [:]
            JsonNode items = dataNode.path(i)
            if (items.get("vaType").textValue() == "V") continue
            medName = items.get("qualifiedName")
            uid = items.get("uid")
            data["name"] = medName
            data["uid"] =
            data["start"] = items.get("overallStart")
            data["stop"] = items.get("overallStop")
            data["status"] = items.get("vaStatus")
            if (!data["start"]) data["start"] = PointInTime.now()
            data["doseChange"] = "none"
            JsonNode dosage = items.path("dosages")
            medDose = ''
            def mStart = data["start"].toString().toFloat() ?: PointInTime.now()
            doseChange = false
            //determine the latest dosage amount for a Med. Complex Meds
            for (int d = 0; d < dosage.size(); d++) {
                dose = dosage.path(d).path("dose").textValue()
                start = dosage.path(d).path("start") ?: 0
                stop = dosage.path(d).path("stop") ?: 0
                if (medDose == '') {
                    medDose = dose
                    data["start"] = compareDoseDate(mStart,start.toFloat())
//                    if (start > ) data["start"] = start
                    data.put("dose", medDose)
//                    data["doseChangeDate"] = data["start"]
                }
                if (medDose != dose) {
                    def doseMap = [:]
                    doseMap["dose"] = dose
                    doseMap["start"] = start
                    doseMap["stop"] = stop
                    compareDoseMap(data, doseMap)
                }
//                data.put("dose", medDose)
            }
            //get order information
            JsonNode orders = items.path("orders")
            daysSupply = orders.path(0).path("daysSupply")
            fillsRemaining = orders.path(0).get("fillsRemaining")
            quantityOrdered = orders.path(0).get("quantityOrdered")
            data.put("supply", daysSupply)
            data["fills"] = fillsRemaining.toString()
            data["quantity"] = quantityOrdered.toString()
            pred = orders.path(0).get("predecessor")
            succ = orders.path(0).get("successor")

            //build a list of meds that have the predecessor field populated
            //use to build a history of meds (Renewed, Copy)
            if (pred) {
                data["predecessor"] = pred
                def temp = [:]
                temp["dose"] = medDose
                temp["predecessor"] = pred
                predecessor[uid] = temp
            }
            //build a list of meds that the successor field populated
            //these are meds that have been replaced by a new med (Renewed, Change)
            if (succ) {
                def temp = [:]
//                successor["uid"] = uid
                temp["dose"] = medDose
                temp["successor"] = succ
                successor[uid] = temp
                temp["start"] = data["start"]
            }
            //do not add meds that have been replaced to the final list.
            if (succ) continue
            meds.put(uid, data)
            assertTrue(meds.size() > 0)
        }
        if (successor && meds) compareDose(meds, successor)

        //determine if the final list of meds have replaced older meds.
        //Build a history for detail display view
        meds.each() {key, value ->
            if (value.predecessor) {
//                println(value.getClass().getName())
                List hist = []
                buildHistory(predecessor, value.predecessor, hist)
                if (value instanceof Map) {((Map) value).put("history", hist)}
                else {throw "Not a map"}

            }
        }
        assertTrue(meds.size() > 0)
    }

    private void printMap(Map data) {
        data.each() { key, value ->
            println(key)
            println(value)

        }
    }

    //determine when a dose change for a med and if it increase/decrease/unknown (Change, Renewal)
    private void compareDose(Map data, Map successor) {
        successor.each() { key, value ->
            String succ = value.successor
            data.findAll {it.key == succ}.each {
                compareDoseMap(it.value, value)
            }
        }
    }

    private void buildHistory(Map pred, String start, List result) {

        pred.findAll {it.key == start}.each {
            result.push(it.key)
            if (it.value.predecessor) buildHistory(pred, it.value.predecessor, result)
        }
        if (!result.contains(start)) result.push(start)
    }

    private void compareDoseMap(Map data, Map comparison) {
        String originalDose = data["dose"].toString()
        String newDose = comparison["dose"].toString()
        if (originalDose == newDose) return

        String oDose = originalDose.split()
        String nDose = newDose.split()

        Integer date
        if (nDose > oDose) data["doseChange"] = "decrease"
        else if (nDose < oDose) data["doseChange"] = "increase"
        else data["doseChange"] = "unknown"

        def nDate
        def oDate

        if (!comparison["stop"]) nDate = 0
        else nDate = comparison["stop"].toString().toFloat()
        if (!data["doseChangeDate"]) oDate = 0
        else oDate = data["doseChangeDate"].toString().toFloat()
        data["doseChangeDate"] = compareDoseDate(oDate,nDate)
    }

    private Float compareDoseDate(Float oDate, Float nDate) {
        Float date = nDate > oDate ? nDate : oDate
        if (date == 0)
        return date
    }


}


