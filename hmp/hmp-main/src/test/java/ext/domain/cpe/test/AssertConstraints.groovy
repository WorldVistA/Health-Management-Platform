package org.osehra.cpe.test

class AssertConstraints {

    static void assertValidField(def instance, String fieldName, String value) {
        instance[fieldName] = value
        junit.framework.Assert.assertTrue "expected field ${fieldName}=${value} to validate", instance.validate([fieldName])
        junit.framework.Assert.assertNull "expected no errors for ${fieldName}", instance.errors[fieldName]
    }

    static void assertInvalidField(String expectedInvalidConstraint, def instance, String fieldName, String value) {
        instance[fieldName] = value
        junit.framework.Assert.assertFalse "expected field ${fieldName}=${value} not to validate", instance.validate([fieldName])
        groovy.util.GroovyTestCase.assertEquals "expected validation error for ${expectedInvalidConstraint} constraint of field ${fieldName}", expectedInvalidConstraint, instance.errors[fieldName]
    }
}
