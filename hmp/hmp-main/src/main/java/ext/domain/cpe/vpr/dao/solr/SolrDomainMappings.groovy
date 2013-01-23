package org.osehra.cpe.vpr.dao.solr

import org.osehra.cpe.vpr.Allergy
import org.osehra.cpe.vpr.Document
import org.osehra.cpe.vpr.Problem
import org.osehra.cpe.vpr.Encounter
import org.osehra.cpe.vpr.HealthFactor
import org.osehra.cpe.vpr.Immunization
import org.osehra.cpe.vpr.Medication
import org.osehra.cpe.vpr.MedicationProduct
import org.osehra.cpe.vpr.Observation
import org.osehra.cpe.vpr.Order
import org.osehra.cpe.vpr.Procedure
import org.osehra.cpe.vpr.Tag
import org.osehra.cpe.vpr.Result
import org.osehra.cpe.vpr.Tagger
import org.osehra.cpe.vpr.VitalSign

// TODO: implement these mappings using Jackson views
class SolrDomainMappings {

    static {
        MAPPINGS.put(Allergy, {
            except = ['localId', 'adverseEventTypeCode', 'severityCode', 'historical', 'reference']
            constant name: 'domain', value: 'allergy'

            facilityName name: 'facility'
            products name: 'allergy_product', value: { products ->
                products.collect {
                    it.name
                }
            }
            reactions name: 'allergy_reaction', value: { reactions ->
                reactions.collect { it.name }
            }
            severityName name: 'allergy_severity'
            comments name: 'comment', value: {comments ->
                comments.collect
                        {it.comment}
            }
            //TODO - delete it when no longer needed.
			//taggers name: "tag", value: {it ? it*.tags*.tagName : null }
        })
        MAPPINGS.put(Document, {
            //except = ['id', 'localId', 'encounter', 'content', 'clinicians', 'links', 'author']
            except = ['id', 'localId', 'encounter', 'clinicians', 'links', 'author']
            constant name: 'domain', value: 'document'

            facilityName name: 'facility'
            text name: 'body', value: { text -> 
				text.collect {
					it.content
				}
			} 
            documentTypeName name: "document_type"
        })
        MAPPINGS.put(Encounter, {
            except = ['localId', 'typeCode', 'duration', 'specialty',
                    'reasonCode', 'providers', 'parent', 'stay']
            constant name: 'domain', value: 'encounter'

            facilityName name: 'facility'
            typeName name: 'encounter_type'
			patientClassName name: 'patientClass'
            categoryName name: 'encounter_category'
            dispositionName name: 'discharge_disposition'
            sourceName name: 'admission_source'
            referrerName name: 'referrer'
            dateTime name: 'visit_date_time'
            primaryProvider value: {it?.providerName}
            //TODO - delete it when no longer needed.
			//taggers name: "tag", value: {it ? it*.tags*.tagName : null }
        })
        MAPPINGS.put(HealthFactor, {
            except = ['id', 'localId', 'encounterUid']
            constant name: 'domain', value: 'factor'

            facilityName name: 'facility'
            name name: 'health_factor_name'
            recorded name: 'health_factor_date_time'
            comment name: 'comment'
			//TODO - delete it when no longer needed.			
			//taggers name: "tag", value: {it ? it*.tags*.tagName : null }
        })
        MAPPINGS.put(Immunization, {
            except = ['id', 'localId', 'contraindicated', 'series', 'reaction']
            constant name: 'domain', value: 'immunization'

            facilityName name: 'facility'
            name name: 'immunization_name'
            comments name: "comment"
            //TODO - delete it when no longer needed.
			//taggers name: "tag", value: {it ? it*.tags*.tagName : null }
        })
        MAPPINGS.put(Medication, {
            except = ['id', 'localId', 'predecessor', 'successor', 'productFormCode',
                    'productFormName', 'stopped', 'medStatus', 'medType', 'vaType',
                    'vaStatus', 'IMO', 'dosages', 'fills', 'medStatusName']
            constant name: 'domain', value: 'medication'

            facilityName name: 'facility'
            sig name: 'med_sig'
            patientInstruction name: 'med_pt_instruct'
            indications component: true, prefix: "med_indication_"
            products component: true, prefix: "med_"
            orders name: 'med_provider', value: {orders ->
                orders.collect
                        {it.providerName}
            }
			//TODO - delete it when no longer needed.
			//taggers name: "tag", value: {it ? it*.tags*.tagName : null }
        })
        MAPPINGS.put(MedicationProduct, {
            except = ['ingredientRole', 'strength', 'volume', 'ivBag', 'relatedOrder']
        })
        MAPPINGS.put(Observation, {
            except = ['id', 'localId', 'typeCode', 'units', 'methodCode', 'bodySiteCode', 'vaStatus', 'qualifiers']
            constant name: 'domain', value: 'observation'

            facilityName name: 'facility'
            typeName name: 'obs_typeName'
            result name: 'obs_result'
            interpretation name: 'obs_flag'
            resultStatus name: 'obs_status'
            comment name: 'obs_comment'
        })
        MAPPINGS.put(Order, {
            except = ['id', 'localId', 'locationName', 'locationId', 'oiIen',
                    'entered', 'stop', 'provider']
            constant name: 'domain', value: 'order'

            facilityName name: 'facility'
            content name: "comment"
            name name: "order_name"
            start name: "order_start_date_time"
			//TODO - delete it when no longer needed.
			//taggers name: "tag", value: {it ? it*.tags*.tagName : null }
            displayGroup name: "order_group_va"
            statusName name: "order_status_va"
        })
        MAPPINGS.put(Problem, {
            constant name: 'domain', value: 'problem'
//            only = ['domain', 'uid', 'patient', 'facilityName', 'locationName', 'service', 'provider', 'problemText', 'icd', 'status', 'onset', 'comments', 'kind', 'summary']
            only = ['domain', 'uid', 'pid','facilityName', 'locationName', 'service', 'providerName', 'problemText', 'icdName','onset', 'comments', 'kind', 'summary']
//        except = ['id', 'localId', 'code', 'predecessor', 'successor', 'problemType', 'acuity', 'history', 'unverified', 'removed', 'entered', 'updated', 'resolved', 'serviceConnected' ]

            facilityName name: 'facility'
            comments name: 'comment', value: {comments -> comments.collect {it.comment} }
			//TODO - delete it when no longer needed.
			//taggers name: "tag", value: {it ? it*.tags*.tagName : null }
            statusName name: "problem_status"
        })
        MAPPINGS.put(Procedure, {
            except = ['id', 'localId', 'typeCode', 'category', 'bodySite', 'status',
                    'reason', 'encounter', 'providers', 'results', 'links',
                    'consultProcedure', 'orderId', 'service', 'verified']
            constant name: 'domain', value: 'procedure'

            facilityName name: 'facility'
            dateTime name: "procedure_date_time"
            typeName name: "procedure_type"
            results name: 'body', value: { results ->
                results.collect {
                    it.document
                }
            }
        })
        MAPPINGS.put(Result, {
            except = ['id', 'localId', 'organizers', 'typeCode', 'method', 'bodySite', 'accession']
            constant name: 'domain', value: 'result'

            facilityName name: 'facility'
            typeName name: 'lab_result_type'
            interpretationName name: "interpertation_name"
            resultStatusName name: 'status'
            document name: 'body'
			//TODO - delete it when no longer needed.
            //taggers name: "tag", value: {it ? it*.tags*.tagName : null }
        })
        MAPPINGS.put(Tag, {
            except = ["tagName"]
        })
        MAPPINGS.put(Tagger, {
            except = ["tags"]
        })
        MAPPINGS.put(VitalSign, {
            except = ['id', 'localId', 'organizer', 'typeCode', 'method', 'bodySite']
            constant name: 'domain', value: 'vital_sign'

            facilityName name: 'facility'
            typeName name: 'vital_sign_type'
            interpretation: 'interpretation_name'
            resultStatusName name: 'status'
            document name: 'body'
			//TODO - delete it when no longer needed.
			//taggers name: "tag", value: {it ? it*.tags*.tagName : null }
        })
    }

    public static final Map<Class, Closure> MAPPINGS = [:]
}
