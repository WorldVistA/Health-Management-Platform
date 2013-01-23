package org.osehra.cpe.vpr

class DomainNameUtils {

    private static Map<Class, Set<String>> CLASS_TO_DOMAINS = [:];

    static {
        CLASS_TO_DOMAINS.put(Allergy, ["allergy"])
        CLASS_TO_DOMAINS.put(Document, ["document"])
        CLASS_TO_DOMAINS.put(Encounter, ["encounter"])
        CLASS_TO_DOMAINS.put(HealthFactor, ["factor"])
        CLASS_TO_DOMAINS.put(Immunization, ["immunization"])
        CLASS_TO_DOMAINS.put(Medication, ["medication"])
        CLASS_TO_DOMAINS.put(Observation, ["observation"])
        CLASS_TO_DOMAINS.put(Order, ["order"])
        CLASS_TO_DOMAINS.put(Problem, ["problem"])
        CLASS_TO_DOMAINS.put(Procedure, ["procedure", "consult"])
        CLASS_TO_DOMAINS.put(Result, ["laboratory"])
        CLASS_TO_DOMAINS.put(VitalSign, ["vitalsign"])
        CLASS_TO_DOMAINS.put(Task, ["task"])
    }

    static Class getClassForDomain(String domain) {
        for (Map.Entry<Class, Set<String>> entry : CLASS_TO_DOMAINS.entrySet()) {
            if (entry.getValue().contains(domain)) return entry.getKey();
        }
        throw new IllegalArgumentException("Unknown domain '" + domain + "'");
    }

    static Set<String> getDomainsForClass(Class domainClass) {
        if (!CLASS_TO_DOMAINS.containsKey(domainClass)) throw new IllegalArgumentException("Unknown domain class '" + domainClass + "'");
        return CLASS_TO_DOMAINS.get(domainClass);
    }
}
