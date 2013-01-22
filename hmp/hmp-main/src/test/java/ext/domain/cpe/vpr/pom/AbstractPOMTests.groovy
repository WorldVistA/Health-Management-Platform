package EXT.DOMAIN.cpe.vpr.pom;
import EXT.DOMAIN.cpe.vpr.pom.POMIndex.MultiValueJDSIndex
import EXT.DOMAIN.cpe.vpr.pom.POMIndex.MultiValuePOMIndex
import EXT.DOMAIN.cpe.vpr.pom.POMIndex.RangeJDSIndex
import EXT.DOMAIN.cpe.vpr.pom.POMIndex.RangePOMIndex
import EXT.DOMAIN.cpe.vpr.pom.POMIndex.TermJDSIndex
import EXT.DOMAIN.cpe.vpr.pom.POMIndex.TermPOMIndex;
import EXT.DOMAIN.cpe.vpr.pom.POMIndex.ValueJDSIndex
import EXT.DOMAIN.cpe.vpr.pom.POMIndex.ValuePOMIndex;
import EXT.DOMAIN.cpe.vpr.pom.TestPatientObject as Patient;
import EXT.DOMAIN.cpe.vpr.termeng.TermEng

import EXT.DOMAIN.cpe.datetime.PointInTime

import EXT.DOMAIN.cpe.datetime.format.HL7DateTimeFormat;

import java.lang.reflect.Field

import org.joda.time.DateTime
import org.junit.Test;
import org.junit.Ignore;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*
import org.junit.Ignore;

public class AbstractPOMTests {
	
	// make patient date adjust to be the same as time goes on
	int age = 32;
	String dateOfBirth = new PointInTime(new DateTime().minusYears(age).year ,1,1).toString()
	

	def data = [
		uid: 'urn:id:1', personID: '229', dateOfBirth: dateOfBirth, 
		familyName: 'Doe', givenNames: 'John', pid: 1,
		gender: 'MALE', ssn: '123-45-6789', foo: 'bar',
		aliases: ['striker1'],
		addresses: [
			[city: 'SLC', stateProvince: 'UT'],
			[city: 'Miami', stateProvince: 'FL']
		]
	];
	
	@Test
	public void test() {
		Patient p = new Patient(data);
		
		// recognized fields are returned by regular getters and use appropriate types
		assertEquals('urn:id:1', p.getUid());
		assertEquals('Doe', p.getFamilyName());
		assertEquals('John', p.getGivenNames());
		assertEquals('229', p.getPersonID());
		assertEquals(HL7DateTimeFormat.parse(dateOfBirth), p.getDOB());
		assertEquals(Patient.Gender.MALE, p.getGender());
		
		// Nested objects are returned as expected
		List<Patient.Address> addrs = p.getAddresses();
		assertEquals(2, addrs.size());
		assertEquals('SLC', addrs.get(0).getCity());
		assertEquals('UT', addrs.get(0).getStateProvince());
		assertEquals('Miami', addrs.get(1).getCity());
		assertEquals('FL', addrs.get(1).getStateProvince());
		
		// testing a set being returned instead of an array
		Set<String> aliases = p.getAliases();
		assertEquals(1, aliases.size());
		assertEquals("striker1", aliases.iterator().next());
		
		// unrecognized fields are returned as properties
		assertEquals('bar', p.getProperty('foo'));
		
		// virtual fields could be both regular getters and generic properties
		assertEquals(2, p.getProperties().size());
		assertTrue(p.getProperties().containsKey('ssn'));
		assertEquals('123-45-6789', p.getSSN());
		assertEquals('123-45-6789', p.getProperty('ssn'));
		
		// business logic methods
		assertEquals('Doe, John', p.getFullName());
		assertEquals("Doe, John ("+age+"yo MALE)", p.getSummary());
		assertEquals(age, p.getAgeInYears());

		// by default getData() should return all the data (fields and properties) 
		// that are appropriate for serialization (mostly strings instead of objects)
		Map<String, Object> data = p.getData();
		assertEquals('urn:id:1', data.get('uid'));
		assertEquals('229', data.get('personID'));
		assertEquals('Doe', data.get('familyName'));
		assertEquals('John', data.get('givenNames'));
		assertEquals(dateOfBirth, data.get('dateOfBirth')); // string instead of PointInTime
		assertEquals('123-45-6789', data.get('ssn'));
		assertEquals('bar', data.get('foo'));
		assertEquals('MALE', data.get('gender')); // string instead of ENUM
		
		// getData() should return addresses in a serialization appropriate way (lists of maps) instead of objects
		Object x = data.get('addresses');
		assertTrue(x instanceof List);
		Map addr1 = x.get(0);
		assertEquals('SLC', addr1.city);
		assertEquals('UT', addr1.stateProvince);
		Map addr2 = x.get(1);
		assertEquals('Miami', addr2.city);
		assertEquals('FL', addr2.stateProvince);
		
		// sets get turned into arrays after serialization, since there really isnt a set semantic in JSON
		Object y = data.get('aliases');
		assertTrue(y instanceof List);
		assertEquals(ArrayList.class, y.getClass());
		
		// note how ageInYears is specifically missing since its not appropriate for serialization
		assertFalse(data.containsKey('ageInYears'));
		
		// However, getData() can also return Views (based on Jackson Views) 
		// that might be more appropriate for web services, the JSONViews.WSView
		// will include ageInYears
		data = p.getData(JSONViews.WSView);
		assertEquals(age, data.get('ageInYears'));
	}
	
	@Test
	public void testExceptions() {
		try {
			Patient p = new Patient([:]);
			fail("Expected a IllegalArgument Exception");
		} catch (IllegalArgumentException ex) {
			// expected
		}
	}
	
	@Test
	public void testAliases() {
		// some fields may recognize multiple key values
		Patient p = new Patient(data);
		
		// dateOfBirth can also be specified as: born and dob
		assertEquals(HL7DateTimeFormat.parse(dateOfBirth), p.getDOB());
		p.setData('born', '20010101');
		assertEquals(HL7DateTimeFormat.parse('20010101'), p.getDOB());
		p.setData('dob', '19990101');
		assertEquals(HL7DateTimeFormat.parse('19990101'), p.getDOB());
		
		// dateOfDeath can also be specified as: died and dod
		assertNull(p.getData().get('dateOfDeath'));
		p.setData('dateOfDeath', '20120601');
		assertEquals('20120601', p.getData().get('dateOfDeath'));
		p.setData('died', '20110601');
		assertEquals('20110601', p.getData().get('dateOfDeath'));
		p.setData('dod', '20100601');
		assertEquals('20100601', p.getData().get('dateOfDeath'));
		
		// personID can be specified as icn
		assertEquals('229', p.getPersonID());
		p.setData('icn', '123');
		assertEquals('123', p.getPersonID());
		
		// familyName and givenNames can be specified as family_name and given_name
		p.setData([family_name: 'foo2', given_name: 'bar2']);
		assertEquals('foo2', p.getFamilyName());
		assertEquals('bar2', p.getGivenNames());
		p.setData([family_name: 'foo', given_name: 'bar']);
		assertEquals('foo', p.getFamilyName());
		assertEquals('bar', p.getGivenNames());
	}
	
	@Test
	public void testViews() {
		Patient p = new Patient(data);
		
		// age in years should only be returned for the web service view
		assertFalse(p.getData().containsKey("ageInYears"));
		assertTrue(p.getData(JSONViews.WSView).containsKey("ageInYears"));
		
		// TODO: SOLR fields, ModifiedFields
		
	}
	
	@Test
	public void testStandardEvents() {
		Patient p = new Patient(data);
		
		// dirty fields should be empty
		assertFalse(p.isModified());
		assertEquals(0, p.getModifiedFields().size());
		assertEquals(0, p.getEvents().size());
		
		// now change a few values
		p.setData([dateOfBirth: '20000101']);
		
		// assert that everything that uses that field is dirty now
		assertTrue(p.isModified());
		assertEquals(1, p.getModifiedFields().size());
		//assertTrue(p.getModifiedFields().containsAll(['dateOfBirth', 'summary']));
		
		// changing a field should fire a generic PatientEvent that has 2 changes listed
		List<PatientEvent> events = p.getEvents();
		PatientEvent evt = events.getAt(0);
		assertEquals(1, events.size());
		assertEquals(PatientEvent.class, evt.getClass());
		assertEquals(PatientEvent.Type.UPDATE, evt.getType());
		assertEquals(1, evt.getChanges().size());

		// change is the birthdate
		PatientEvent.Change change2 = evt.getChanges().get(0);
		assertEquals('dateOfBirth', change2.FIELD);
		assertEquals(dateOfBirth, change2.OLD_VALUE);
		assertEquals('20000101', change2.NEW_VALUE);
	}
	
	@Test
	@Ignore // Needs to be rewritten
	public void testCustomEvents() {
		Patient p = new Patient(data);
		
		// the patient object declares a custom event for PatientDeath
		// it is triggered by changing the died date on the patient from NULL to something
		p.setData([died: '20120601']);
		assertEquals(1, p.getEvents().size());
		
		assertEquals(Patient.PatientDeathEvent.class, p.getEvents().get(0).getClass());
		assertEquals(HL7DateTimeFormat.parse('20120601'), p.getEvents().get(0).deathDate);
		
	}
	
	@Test
	public void testExtractIndexDefinitions() {
		Field f = Patient.class.getDeclaredField("born");
		
		// get the indexes on the patient class, (on the same field, a TimeJDSIndex and DateRangeJDSIndex)
		List<POMIndex> indexes = POMIndex.extractIndexes(Patient.class);
		assertEquals(5, indexes.size());
		assertEquals(f, indexes.get(0).getField());
		assertEquals(f, indexes.get(1).getField());
		
		assertEquals(ValuePOMIndex.class, indexes.get(0).getClass());
		assertEquals(RangePOMIndex.class, indexes.get(1).getClass());
		assertEquals(MultiValuePOMIndex.class, indexes.get(2).getClass());
		assertEquals(TermPOMIndex.class, indexes.get(3).getClass());
		assertEquals(ValuePOMIndex.class, indexes.get(4).getClass());
		
		assertEquals(ValueJDSIndex.class, indexes.get(0).getAnnotation().annotationType());
		assertEquals(RangeJDSIndex.class, indexes.get(1).getAnnotation().annotationType());
		assertEquals(MultiValueJDSIndex.class, indexes.get(2).getAnnotation().annotationType());
		assertEquals(TermJDSIndex.class, indexes.get(3).getAnnotation().annotationType());
		assertEquals(ValueJDSIndex.class, indexes.get(4).getAnnotation().annotationType());
		
		// check the ValueJDSIndex on birthday
		ValuePOMIndex idx0 = indexes.get(0);
		assertEquals("birthday-index", idx0.getIndexName());
		assertEquals("birthday-index", idx0.getAnnotation().name());
		assertEquals("dateOfBirth", idx0.getAnnotation().field());
		
		// check the DateRangeJDSIndex on birth/death range
		RangePOMIndex idx1 = indexes.get(1);
		assertEquals("alive-time", idx1.getIndexName());
		assertEquals("alive-time", idx1.getAnnotation().name());
		assertEquals("dateOfBirth", idx1.getAnnotation().startField());
		assertEquals("dateOfDeath", idx1.getAnnotation().endField());
		
		// chekc the multivalue index on addresses
		MultiValuePOMIndex idx2 = indexes.get(2);
		assertEquals("city-list", idx2.getIndexName());
		assertEquals("city", idx2.getAnnotation().subfield());
		
		// check the Terminology index
		TermPOMIndex idx3 = indexes.get(3);
		assertEquals("loinc-code-index", idx3.getIndexName());
		assertEquals("code", idx3.getAnnotation().subfield());

		// check the ValueJDSIndex on multi-facility
		ValuePOMIndex idx4 = indexes.get(4);
		assertEquals("multiple-facility", idx4.getIndexName());
		assertEquals("T+30", idx4.getAnnotation().expiresat());
	}
	
	@Test
	public void testIndexValuesGeneration() {
		Patient p = new Patient(data);
		p.setData([died: '20120601']);
		
		// get the data generated by the indexes
		List<Map<String,Object>> idx = p.getIDX();
		assertEquals(7, idx.size());
		
		// birthday-index should have a single node
		assertTrue(idx.contains(['birthday-index': dateOfBirth]));
		
		// alive-time index should have 2 nodes, one for start, one for end
		assertTrue(idx.contains(['alive-time': dateOfBirth]));
		assertTrue(idx.contains(['alive-time': '20120601']));
		
		// multiple-facility should be true/false and have an expireat node
		assertTrue(idx.contains(['multiple-facility': false]));
		
		// city-list index should have 2 cities
		assertTrue(idx.contains(['city-list': 'SLC']));
		assertTrue(idx.contains(['city-list': 'Miami']));
		
		// loinc-code-index should have the mocked items (and the original)
		assertTrue(idx.contains(['loinc-code-index': 'urn:lnc:2345-7']));
	}
}

