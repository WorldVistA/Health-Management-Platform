package EXT.DOMAIN.cpe.vpr.sync.vista.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test

import static org.junit.Assert.assertTrue

class UpdateImporterTest extends AbstractImporterTest {

    private ObjectMapper jsonMapper = new ObjectMapper();

    static final String UPDATE_RESULT_STRING_JSON = '''
{
    "apiVersion": "1.01",
    "data": {
        "updated": "20120717191841-0700",
        "totalItems": 2,
        "items": [
            {
                "lastUpdate": "3120717:14",
                "patients": [
                    {
                        "deletes":[
                            {
                                "domainName":"factor",
                                "uid":"urn:va:F484:229:hf:132"
                            }],
                        "domains": [
                            {
                                "domainName": "document",
                                "items": [
                                    {
                                        "clinicians": [
                                            {
                                                "name": "AVIVAUSER,TWELVE",
                                                "role": "A",
                                                "uid": "urn:va:user:F484:1089"
                                            },
                                            {
                                                "name": "AVIVAUSER,TWELVE",
                                                "role": "S",
                                                "signature": "TWELVE AVIVAUSER ",
                                                "signedDateTime": 20120717104557,
                                                "uid": "urn:va:user:F484:1089"
                                            }
                                        ],
                                        "content": " LOCAL TITLE: ADVANCE DIRECTIVE                                  \\r\\nDATE OF NOTE: JUL 17, 2012@10:45     ENTRY DATE: JUL 17, 2012@10:45:32      \\r\\n      AUTHOR: AVI VAUSER,TWELVE     EXP COSIGNER:                           \\r\\n     URGENCY: STATUS: COMPLETED                     \\r\\n\\r\\nThis is a n ew Advanced Directive for this patient.  AVIVAPATIENT,TWENTYFOUR has\\r\\nrequeste d that he not be resuscitated in the event that he goes into\\r\\nrespiratory dist ress.   See attached scanned document for patient's\\r\\nsignature. \\r\\n \\r\\n \\r\\n test note for freshness\\r\\n \\r\\n/es/ TWELVE AVIVAUSER\\r\\n\\r\\nSigned: 07/17/2 012 10:45",
                                        "documentClass": "PROGRESS NOTES",
                                        "documentTypeCode": "D",
                                        "documentType Name": "Advance Directive",
                                        "encounterName": "7A GEN MED Jul 01, 2011",
                                        "encounterUid": "urn:va:F484:229:visit:7193",
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "localId": 4343,
                                        "localTitle": "ADVANCE DIRECTIVE",
                                        "referenceDateTime": 201207171045,
                                        "status": "completed",
                                        "uid": "urn:va:F484:229:tiu:4343"
                                    },
                                    {
                                        "clinicians": [
                                            {
                                                "name": "AVIVAUSER,TWELVE",
                                                "role": "A",
                                                "uid": "urn:va:user:F484:1089"
                                            },
                                            {
                                                "name": "AVIVA USER,TWELVE",
                                                "role": "S",
                                                "signature": "TWELVE AVIVAUSER ",
                                                "signedDateTime": 20120717125848,
                                                "uid": "urn:va:user:F484:1089"
                                            }
                                        ],
                                        "content": " LOCAL TITLE: C&P ACROMEGALY \\r\\nDATE OF NOTE: JUL 17, 2012@12:57     ENT RY DATE: JUL 17, 2012@12:57:53      \\r\\n      AUTHOR: AVIVAUSER,TWELVE     EXP C OSIGNER:                           \\r\\n     URGENCY: STATUS: COMPLETED                     \\r\\n\\r\\nThis is a test note for freshness again\\r\\n \\r\\n/es/ TWELVE AVIVAUSER\\r\\n\\r\\nSigned: 07/17/2012 12:58",
                                        "documentClass": "PROGRESS NOTES",
                                        "documentTypeCode": "PN",
                                        "documentTypeName": "Progress N ote",
                                        "encounterName": "7A GEN MED Jul 01, 2011",
                                        "encounterUid": "urn:va:F484:229:v isit:7193",
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "localId": 4344,
                                        "localTitle": "C&P ACROMEGALY",
                                        "referenceDateTime": 201207171257,
                                        "status": "complet ed",
                                        "uid": "urn:va:F484:229:tiu:4344"
                                    }
                                ],
                                "total": 2
                            },
                            {
                                "domainName": "order",
                                "items": [
                                    {
                                        "content": "ACETAMINOPHEN TAB \\r\\n500MG PO Q6H PRN",
                                        "displayGroup": "UD RX",
                                        "ent ered": 201205311827,
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "localI d": 34977,
                                        "locationCode": "urn:va:location:500D:158",
                                        "locationName": "7A GEN MED",
                                        "name": "ACETAMINOPHEN TAB ",
                                        "oiCode": "urn:va:oi:1348",
                                        "oiName": "ACETAMINOPHEN TAB ",
                                        "oiPackageRef": "5;99PSP",
                                        "providerName": "AVIVAUSER,TWELVE",
                                        "providerUid": "urn :va:user:F484:1089",
                                        "service": "PSJ",
                                        "start": 201207171300,
                                        "statusCode": "urn:va:or der-status:pend",
                                        "statusName": "PENDING",
                                        "statusPackageRef": "",
                                        "statusVuid": 4501114,
                                        "stop": "",
                                        "uid": "urn:va:F484:229:order:34977"
                                    },
                                    {
                                        "content": "ACETAMINOPHEN TAB \\r\\n500MG PO MO-WE@BID",
                                        "displayGroup": "UD RX",
                                        "entered": 201205311841,
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "localId": 34978,
                                        "locationCode": "urn: va:location:500D:158",
                                        "locationName": "7A GEN MED",
                                        "name": "ACETAMINOPHEN TAB ",
                                        "oiCode": "urn:va:oi:1348",
                                        "oiName": "ACETAMINOPHEN TAB ",
                                        "oiPackageRef": "5;99PSP",
                                        "providerName": "AVIVAUSER,TWELVE",
                                        "providerUid": "urn:va:user:F484:1089",
                                        "service": "PSJ",
                                        "start": 201207180900,
                                        "statusCode": "urn:va:order-status:pend",
                                        "statusName": "PENDING",
                                        "statusPackageRef": "",
                                        "statusVuid": 4501114,
                                        "stop": "",
                                        "uid": "urn:va:F4 84:229:order:34978"
                                    },
                                    {
                                        "content": "GLUCAGON INJ  1MG/VIAL\\r\\nINJECT 1 VIAL (1MG) INTRAMUSCULAR TWICE A DAY\\r\\nQuantity: 1 Refills: 0",
                                        "displayGroup": "O RX",
                                        "entered": 201207171045,
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "localId": 35184,
                                        "locationCode ": "urn:va:location:500D:158",
                                        "locationName": "7A GEN MED",
                                        "name": "GLUCAGON INJ ",
                                        "oiCode": "urn:va:oi:1635",
                                        "oiName": "GLUCAGON INJ ",
                                        "oiPackageRef": "292;99PSP",
                                        "providerName": "AVIVAUSER,TWELVE",
                                        "providerUid": "urn:va:user:F484:1089",
                                        "service": "PSO",
                                        "start": "",
                                        "statusCode": "urn:va:order-status:pend",
                                        "statusName": "PENDING",
                                        "statusPackageRef": "",
                                        "statusVuid": 4501114,
                                        "stop": "",
                                        "uid": "urn:va:F484:229:order:35184"
                                    },
                                    {
                                        "content": "ACETAZOLAMIDE INJ \\r\\n500MG/1VIAL IM BID",
                                        "displayGroup": "UD RX",
                                        "entered": 201207171258,
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "localId": 35216,
                                        "locationCode": "urn:va:location:500D:158",
                                        "locationName": "7 A GEN MED",
                                        "name": "ACETAZOLAMIDE INJ ",
                                        "oiCode": "urn:va:oi:1350",
                                        "oiName": "ACETA ZOLAMIDE INJ ",
                                        "oiPackageRef": "7;99PSP",
                                        "providerName": "AVIVAUSER,TWELVE",
                                        "provi derUid": "urn:va:user:F484:1089",
                                        "service": "PSJ",
                                        "start": 201207171700,
                                        "statusCode ": "urn:va:order-status:pend",
                                        "statusName": "PENDING",
                                        "statusPackageRef": "",
                                        "statu sVuid": 4501114,
                                        "stop": "",
                                        "uid": "urn:va:F484:229:order:35216"
                                    },
                                    {
                                        "content": "ALTEPL ASE INJ,LYPHL \\r\\n50MG/1VIAL IM BID",
                                        "displayGroup": "UD RX",
                                        "entered": 201207171343,
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "localId": 35217,
                                        "locat ionCode": "urn:va:location:500D:158",
                                        "locationName": "7A GEN MED",
                                        "name": "ALTEPLAS E INJ,LYPHL ",
                                        "oiCode": "urn:va:oi:3678",
                                        "oiName": "ALTEPLASE INJ,LYPHL ",
                                        "oiPacka geRef": "1490;99PSP",
                                        "providerName": "AVIVAUSER,TWELVE",
                                        "providerUid": "urn:va:user :F484:1089",
                                        "service": "PSJ",
                                        "start": 201207171700,
                                        "statusCode": "urn:va:order-status:pend",
                                        "statusName": "PENDING",
                                        "statusPackageRef": "",
                                        "statusVuid": 4501114,
                                        "stop ": "",
                                        "uid": "urn:va:F484:229:order:35217"
                                    },
                                    {
                                        "content": "AMANTADINE SYRUP \\r\\n50MG /5ML PO BID",
                                        "displayGroup": "UD RX",
                                        "entered": 201207171717,
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "localId": 35221,
                                        "locationCode": "urn:va:location :500D:158",
                                        "locationName": "7A GEN MED",
                                        "name": "AMANTADINE SYRUP ",
                                        "oiCode": "urn: va:oi:1365",
                                        "oiName": "AMANTADINE SYRUP ",
                                        "oiPackageRef": "22;99PSP",
                                        "providerName ": "AVIVAUSER,TWELVE",
                                        "providerUid": "urn:va:user:F484:1089",
                                        "service": "PSJ",
                                        "star t": 201207180900,
                                        "statusCode": "urn:va:order-status:pend",
                                        "statusName": "PENDING",
                                        " statusPackageRef": "",
                                        "statusVuid": 4501114,
                                        "stop": "",
                                        "uid": "urn:va:F484:229:order:35221"
                                    }
                                ],
                                "total": 6
                            },
                            {
                                "domainName": "pharmacy",
                                "items": [
                                    {
                                        "dosages": [
                                            {
                                                "dose": "500 MG",
                                                "relativeStart": 0,
                                                "relativeStop": 0,
                                                "routeName": "PO",
                                                "scheduleName": "Q6H PRN",
                                                "start": "201207171300-0700",
                                                "units": "MG"
                                            }
                                        ],
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "imo": false,
                                        "localId": "1559P;I",
                                        "medStatus": "urn:sct:73425007",
                                        "medStatusName": "not active",
                                        "medType": "urn:sct:105903003",
                                        "name": "ACETAMINOPHE N TAB ",
                                        "orders": [
                                            {
                                                "locationCode": "urn:va:location:500D:158",
                                                "locationName": "7A GEN MED",
                                                "orderUid": "urn:va:F484:229:order:34977",
                                                "ordered": "201205311827-0700",
                                                "pharmacistName": "",
                                                "pharmacistUid": "urn:va:user:F484:0",
                                                "providerName": "AVIVAUS ER,TWELVE",
                                                "providerUid": "urn:va:user:F484:1089"
                                            }
                                        ],
                                        "overallStart": "201207171300- 0700",
                                        "productFormName": "TAB",
                                        "products": [
                                            {
                                                "drugClassCode": "urn:vadc:CN103",
                                                "dru gClassName": "NON-OPIOID ANALGESICS",
                                                "ingredientCode": "urn:vuid:4017513",
                                                "ingredi entName": "ACETAMINOPHEN",
                                                "ingredientRole": "urn:sct:410942007",
                                                "strength": "500 MG ",
                                                "suppliedCode": "urn:vuid:4007154",
                                                "suppliedName": "ACETAMINOPHEN 500MG TAB"
                                            }
                                        ],
                                        "qualifiedName": "ACETAMINOPHEN TAB",
                                        "sig": "Give: 500MG PO Q6H PRN",
                                        "uid": "urn:va: F484:229:med:34977",
                                        "vaStatus": "PENDING",
                                        "vaType": "I"
                                    },
                                    {
                                        "dosages": [
                                            {
                                                "dose": "500 MG",
                                                "relativeStart": 0,
                                                "relativeStop": 0,
                                                "routeName": "PO",
                                                "scheduleName": "MO-WE@BID",
                                                "start": "201207180900-0700",
                                                "units": "MG"
                                            }
                                        ],
                                        "facility Code": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "imo": false,
                                        "localId": "1558P;I",
                                        "medStatus": "urn:sct:73425007",
                                        "medStatusName": "not active",
                                        "medType": "urn:sct:1059 03003",
                                        "name": "ACETAMINOPHEN TAB ",
                                        "orders": [
                                            {
                                                "locationCode": "urn:va:location:500D:158",
                                                "locationName": "7A GEN MED",
                                                "orderUid": "urn:va:F484:229:order:34978",
                                                "or dered": "201205311841-0700",
                                                "pharmacistName": "",
                                                "pharmacistUid": "urn:va:user:F484:0",
                                                "providerName": "AVIVAUSER,TWELVE",
                                                "providerUid": "urn:va:user:F484:1089"
                                            }
                                        ],
                                        "o verallStart": "201207180900-0700",
                                        "productFormName": "TAB",
                                        "products": [
                                            {
                                                "drugClassCode": "urn:vadc:CN103",
                                                "drugClassName": "NON-OPIOID ANALGESICS",
                                                "ingredientCode": "urn:vuid:4017513",
                                                "ingredientName": "ACETAMINOPHEN",
                                                "ingredientRole": "urn:sct:41 0942007",
                                                "strength": "500 MG",
                                                "suppliedCode": "urn:vuid:4007154",
                                                "suppliedName": "A CETAMINOPHEN 500MG TAB"
                                            }
                                        ],
                                        "qualifiedName": "ACETAMINOPHEN TAB",
                                        "sig": "Give: 500MG PO MO-WE@BID",
                                        "uid": "urn:va:F484:229:med:34978",
                                        "vaStatus": "PENDING",
                                        "vaType": " I"
                                    },
                                    {
                                        "dosages": [
                                            {
                                                "dose": "1 VIAL (1MG) 1MG/VIAL",
                                                "relativeStart": 0,
                                                "relativeStop ": 0,
                                                "routeName": "IM",
                                                "scheduleName": "BID"
                                            }
                                        ],
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "imo": false,
                                        "localId": "341S;O",
                                        "medStatus": "urn:sct:73425007",
                                        "medStatusName": "not active",
                                        "medType": "urn:sct:73639000",
                                        "name": "GLUCAGON INJ ",
                                        "orders": [
                                            {
                                                "daysSupply": 1,
                                                "fillsAllowed": 0,
                                                "locationCode": "urn:va:location:500D:158",
                                                "locationName": "7A GEN MED",
                                                "orderUid": "urn:va:F484:229:order:35184",
                                                "orde red": "201207171045-0700",
                                                "pharmacistName": "",
                                                "pharmacistUid": "urn:va:user:F484:0 ",
                                                "providerName": "AVIVAUSER,TWELVE",
                                                "providerUid": "urn:va:user:F484:1089",
                                                "quant ityOrdered": 1,
                                                "vaRouting": "W"
                                            }
                                        ],
                                        "productFormName": "INJ",
                                        "products": [
                                            {
                                                "drugClassC ode": "urn:vadc:HS503",
                                                "drugClassName": "ANTIHYPOGLYCEMICS",
                                                "ingredientCode": "urn: vuid:4017456",
                                                "ingredientName": "GLUCAGON",
                                                "ingredientRole": "urn:sct:410942007",
                                                " strength": "1 MG/VIAL",
                                                "suppliedCode": "urn:vuid:4000931",
                                                "suppliedName": "GLUCAGO N 1MG/VIL INJ"
                                            }
                                        ],
                                        "qualifiedName": "GLUCAGON INJ",
                                        "sig": "INJECT 1 VIAL (1MG) INTR AMUSCULAR TWICE A DAY",
                                        "type": "Prescription",
                                        "uid": "urn:va:F484:229:med:35184",
                                        " vaStatus": "PENDING",
                                        "vaType": "O"
                                    },
                                    {
                                        "dosages": [
                                            {
                                                "dose": "500 MG/1VIAL",
                                                "relativeS tart": 0,
                                                "relativeStop": 0,
                                                "routeName": "IM",
                                                "scheduleName": "BID",
                                                "start": "20120717 1700-0700",
                                                "units": "MG/1VIAL"
                                            }
                                        ],
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "imo": false,
                                        "localId": "1557P;I",
                                        "medStatus": "urn:sct:73425007",
                                        "medStatusName": "not active",
                                        "medType": "urn:sct:105903003",
                                        "name": "ACETAZOLAMIDE INJ ",
                                        "orders": [
                                            {
                                                "locationCode": "urn:va:location:500D:158",
                                                "locationName": "7A GEN MED",
                                                "o rderUid": "urn:va:F484:229:order:35216",
                                                "ordered": "201207171258-0700",
                                                "pharmacist Name": "",
                                                "pharmacistUid": "urn:va:user:F484:0",
                                                "providerName": "AVIVAUSER,TWELVE",
                                                "providerUid": "urn:va:user:F484:1089"
                                            }
                                        ],
                                        "overallStart": "201207171700-0700",
                                        "prod uctFormName": "INJ",
                                        "products": [
                                            {
                                                "drugClassCode": "urn:vadc:CV703",
                                                "drugClassName": "CARBONIC ANHYDRASE INHIBITOR DIURETICS",
                                                "ingredientCode": "urn:vuid:4017898",
                                                "i ngredientName": "ACETAZOLAMIDE",
                                                "ingredientRole": "urn:sct:410942007",
                                                "strength": " 500 MG/VIAL",
                                                "suppliedCode": "urn:vuid:4002721",
                                                "suppliedName": "ACETAZOLAMIDE NA 500MG/VIL INJ"
                                            }
                                        ],
                                        "qualifiedName": "ACETAZOLAMIDE INJ",
                                        "sig": "Give: 500MG/1VIAL IM BID",
                                        "uid": "urn:va:F484:229:med:35216",
                                        "vaStatus": "PENDING",
                                        "vaType": "I"
                                    },
                                    {
                                        "dosages": [
                                            {
                                                "dose": "50 MG/1VIAL",
                                                "relativeStart": 0,
                                                "relativeStop": 0,
                                                "routeName ": "IM",
                                                "scheduleName": "BID",
                                                "start": "201207171700-0700",
                                                "units": "MG/1VIAL"
                                            }
                                        ],
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "imo": false,
                                        "localId": "1560P ;I",
                                        "medStatus": "urn:sct:73425007",
                                        "medStatusName": "not active",
                                        "medType": "urn:s ct:105903003",
                                        "name": "ALTEPLASE INJ,LYPHL ",
                                        "orders": [
                                            {
                                                "locationCode": "urn:va:location:500D:158",
                                                "locationName": "7A GEN MED",
                                                "orderUid": "urn:va:F484:229:order:35217",
                                                "ordered": "201207171343-0700",
                                                "pharmacistName": "",
                                                "pharmacistUid": "urn:va: user:F484:0",
                                                "providerName": "AVIVAUSER,TWELVE",
                                                "providerUid": "urn:va:user:F484:1 089"
                                            }
                                        ],
                                        "overallStart": "201207171700-0700",
                                        "productFormName": "INJ,LYPHL",
                                        "products": [
                                            {
                                                "drugClassCode": "urn:vadc:BL115",
                                                "drugClassName": "THROMBOLYTICS",
                                                "ingredien tCode": "urn:vuid:4019599",
                                                "ingredientName": "ALTEPLASE",
                                                "ingredientRole": "urn:sct :410942007",
                                                "strength": "50 MG/VIAL",
                                                "suppliedCode": "urn:vuid:4025335",
                                                "supplied Name": "ALTEPLASE,RECOMBINANT 50MG/VIL INJ"
                                            }
                                        ],
                                        "qualifiedName": "ALTEPLASE INJ,LYP HL",
                                        "sig": "Give: 50MG/1VIAL IM BID",
                                        "uid": "urn:va:F484:229:med:35217",
                                        "vaStatus ": "PENDING",
                                        "vaType": "I"
                                    },
                                    {
                                        "dosages": [
                                            {
                                                "dose": "50 MG/5ML",
                                                "relativeStart": 0,
                                                "re lativeStop": 0,
                                                "routeName": "PO",
                                                "scheduleName": "BID",
                                                "start": "201207180900-0700",
                                                "units": "MG/5ML"
                                            }
                                        ],
                                        "facilityCode": "500D",
                                        "facilityName": "SLC-FO HMP DEV",
                                        "imo": false,
                                        "localId": "1561P;I",
                                        "medStatus": "urn:sct:73425007",
                                        "medStatusName": "not ac tive",
                                        "medType": "urn:sct:105903003",
                                        "name": "AMANTADINE SYRUP ",
                                        "orders": [
                                            {
                                                "locat ionCode": "urn:va:location:500D:158",
                                                "locationName": "7A GEN MED",
                                                "orderUid": "urn: va:F484:229:order:35221",
                                                "ordered": "201207171717-0700",
                                                "pharmacistName": "",
                                                "phar macistUid": "urn:va:user:F484:0",
                                                "providerName": "AVIVAUSER,TWELVE",
                                                "providerUid": "urn:va:user:F484:1089"
                                            }
                                        ],
                                        "overallStart": "201207180900-0700",
                                        "productFormName": " SYRUP",
                                        "products": [
                                            {
                                                "drugClassCode": "urn:vadc:AM800",
                                                "drugClassName": "ANTIVIRALS ",
                                                "ingredientCode": "urn:vuid:4019601",
                                                "ingredientName": "AMANTADINE",
                                                "ingredientR ole": "urn:sct:410942007",
                                                "strength": "50 MG/5ML",
                                                "suppliedCode": "urn:vuid:400519 3",
                                                "suppliedName": "AMANTADINE HCL 50MG/5ML SYRUP"
                                            }
                                        ],
                                        "qualifiedName": "AMANTADINE SYRUP",
                                        "sig": "Give: 50MG/5ML PO BID",
                                        "uid": "urn:va:F484:229:med:35221",
                                        "vaStatus": "PENDING",
                                        "vaType": "I"
                                    }
                                ],
                                "total": 6
                            }
                        ],
                        "patientDfn": 229,
                        "patientIcn": 10104
                    }
                ]
            }
        ]
    }
}
'''

	@Test
	public void testUpdate() throws Exception {
		JsonNode json = jsonMapper.readValue(UPDATE_RESULT_STRING_JSON, JsonNode.class)
        JsonNode dataNode = json.path("data").path("items")
        String updateValue = dataNode.get(0).path("lastUpdate").textValue()
        JsonNode patientNode = dataNode.get(0).get("patients")
        String domainName
        assertTrue(patientNode.size() > 0)
        for (int i = 0; i < patientNode.size(); i++) {
            String dfn = patientNode.path(i).path("patientDfn").intValue()
            JsonNode domainNode = patientNode.path(i).get("domains")
            for (int d=0; d < domainNode.size(); d ++) {
                JsonNode items = domainNode.path(d)
                domainName = items.get("domainName")
                JsonNode item = items.get("items")
                for (int c=0; c < item.size(); c ++) {
                    JsonNode value = item.get(c);
                }
            }
            JsonNode deleteNode = patientNode.path(i).get("deletes")
            String uid
            for (int d=0; d < deleteNode.size(); d ++) {
                JsonNode items = deleteNode.path(d)
                domainName = items.get("domainName")
                uid = items.get("uid")
            }
        }
	}

//    @Test
//	public void testMed() throws Exception {
//		VistaDataChunk fragment = MockVistaDataChunks.createFromJson(MEDICATION_RESULT_STRING_JSON_ONE, mockPatient, "pharmacy")
//		MedicationImporter m1 = new MedicationImporter()
//		Medication m = m1.convert(fragment)
//        assertThat(m.getPid(), is(equalTo(MOCK_PID)))
//        assertThat(m.getFacilityCode(), is("500"))
//        assertThat(m.getFacilityName(), is("CAMP MASTER"))
//        assertEquals(UidUtils.getMedicationUid(MockVistaDataChunks.VISTA_ID, "229", "27844"), m.getUid());
//        assertEquals("403838;O", m.getLocalId());
//        assertEquals(new PointInTime(2010, 5, 28), m.getOverallStop());
//        assertNull(m.getStopped());
//        assertEquals("active", m.getMedStatusName());
//        assertEquals(CodeConstants.SCT_MED_STATUS_ACTIVE, m.getMedStatus());
////        assertEquals(CodeConstants.SCT_MED_TYPE_PRESCRIBED, m.getMedType());
//
//
//	}
}
