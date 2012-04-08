package se.jdojo.gbg.old;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.jdojo.gbg.Animal;

public class LegacySolutionTest extends SqlDatabaseBase {

	private DateUtil dateUtil = new DateUtil();
	private AnimalDAO testee;

	@Before
	public void setup() throws Exception {
		initializeDatabase("animals.sql");
		testee = new AnimalDAO(dataSource, dateUtil);
	}

	@After
	public void cleanUp() throws Exception {
		tearDownDatabase();
	}

	@Test
	public void load_Animal_One() throws Exception {
		Animal animal = testee.loadById(1);

		verifyDataOfAnimalOne(animal);
	}

	@Test
	public void load_All_Animals() throws Exception {
		List<Animal> result = testee.loadAllAnimals();

		assertEquals(6, result.size());
		verifyDataOfAnimalOne(result.iterator().next());
		verifyAnimalObjectIntegrity(result);
	}

	@Test
	public void load_By_Name() throws Exception {
		Animal animal = testee.loadAnimalByName("fredrik");

		assertEquals("fredrik", animal.get("name"));
	}

	@Test
	public void load_By_Species() throws Exception {
		List<Animal> result = testee.loadBySpecies("fly");

		assertEquals(2, result.size());
		for (Animal a : result) {
			assertEquals("fly", a.get("species"));
		}
		verifyAnimalObjectIntegrity(result);
	}

	@Test
	public void load_By_Species_With_Unknown_Returns_Empty_List() throws Exception {
		List<Animal> result = testee.loadBySpecies("human");

		assertTrue(result.isEmpty());
	}

	@Test
	public void load_By_Name_With_Pattern_A() throws Exception {
		List<Animal> result = testee.loadByNamesMatching("a");

		assertEquals(3, result.size());
		for (Animal a : result) {
			assertTrue(((String) a.get("name")).contains("a"));
		}
		verifyAnimalObjectIntegrity(result);
	}

	@Test
	public void load_By_Name_With_Pattern_Matching_Nothing() throws Exception {
		List<Animal> result = testee.loadByNamesMatching("this is a no match");

		assertTrue(result.isEmpty());
	}

	@Test
	public void load_By_Date_After_1981() throws Exception {
		final Date pointInTime = dateUtil.asDate("1981-01-01");
		List<Animal> result = testee.loadByDateAfterIncluding(pointInTime);
		
		assertEquals(2, result.size());
		for (Animal a : result) {
			assertAtOrAfter(pointInTime, (Date) a.get("dob"));
		}
	}
	
	@Test
	public void load_By_Date_After_1970() throws Exception {
		final Date pointInTime = dateUtil.asDate("1970-01-01");
		List<Animal> result = testee.loadByDateAfterIncluding(pointInTime);
		
		assertEquals(6, result.size());
		for (Animal a : result) {
			assertAtOrAfter(pointInTime, (Date) a.get("dob"));
		}
	}
	
	@Test
	public void load_By_Date_Before_1981() throws Exception {
		Date pointInTime = dateUtil.asDate("1981-01-01");
		List<Animal> result = testee.loadByDateBeforeIncluding(pointInTime);
		
		assertEquals(5, result.size());
		for (Animal a : result) {
			assertAtOrBefore(pointInTime, (Date) a.get("dob"));
		}
	}
	

	// FIXME: test load by date between
	// FIXME: test load by species, name, date

	// TODO: negative path tests ...
	// TODO: rewrite matching with prettier Hamcrest matcHers

	private void assertAtOrBefore(Date expected, Date actual) {
		if (actual.getTime() > expected.getTime()) {
			fail("" + actual + " is not at or before " + expected);
		}
	}
	private void assertAtOrAfter(Date expected, Date actual) {
		if (actual.getTime() < expected.getTime()) {
			fail("" + actual + " is not at or after " + expected);
		}
	}

	private void verifyAnimalObjectIntegrity(List<Animal> animals) {
		String[] properties = "name,species,dob,legs".split(",");
		for (Animal animal : animals) {
			for (String p : properties) {
				assertNotNull("missing property: " + p, animal.get(p));
			}
		}
	}

	private void verifyDataOfAnimalOne(Animal animal) throws Exception {
		assertEquals("adam", animal.get("name"));
		assertEquals("dog", animal.get("species"));
		assertEquals(4, animal.get("legs"));
		assertEquals(dateUtil.asDate("1980-10-15"), animal.get("dob"));
	}

}
