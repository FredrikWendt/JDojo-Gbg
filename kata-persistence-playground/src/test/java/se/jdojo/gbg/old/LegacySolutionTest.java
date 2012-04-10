package se.jdojo.gbg.old;

import static se.jdojo.gbg.old.ContainsAnimals.*;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
		verifyAnimalObjectIntegrity(result);
	}

	@Test
	public void load_By_Date_After_1970() throws Exception {
		final Date pointInTime = dateUtil.asDate("1970-01-01");

		List<Animal> result = testee.loadByDateAfterIncluding(pointInTime);

		assertEquals(6, result.size());
		for (Animal a : result) {
			assertAtOrAfter(pointInTime, (Date) a.get("dob"));
		}
		verifyAnimalObjectIntegrity(result);
	}

	@Test
	public void load_By_Date_Before_1981() throws Exception {
		Date pointInTime = dateUtil.asDate("1981-01-01");

		List<Animal> result = testee.loadByDateBeforeIncluding(pointInTime);

		assertEquals(5, result.size());
		for (Animal a : result) {
			assertAtOrBefore(pointInTime, (Date) a.get("dob"));
		}
		verifyAnimalObjectIntegrity(result);
	}

	@Test
	public void load_By_Date_Between_Two_Dates() throws Exception {
		Date from = dateUtil.asDate("1980-11-01");
		Date until = dateUtil.asDate("1980-12-31");

		List<Animal> result = testee.loadByDateBetweenIncluding(from, until);

		assertEquals(2, result.size());
		verifyAnimalObjectIntegrity(result);
	}

	@Test
	public void load_By_Date_Between_Two_Other_Dates() throws Exception {
		Date from = dateUtil.asDate("1980-10-31");
		Date until = dateUtil.asDate("1981-01-01");

		List<Animal> result = testee.loadByDateBetweenIncluding(from, until);

		assertEquals(4, result.size());
		verifyAnimalObjectIntegrity(result);
	}

	@Test
	public void load_By_Date_Must_Be_Passed_Arguments_In_Cronological_Order() throws Exception {
		Date from = dateUtil.asDate("1980-01-01");
		Date until = dateUtil.asDate("1970-01-01");

		try {
			testee.loadByDateBetweenIncluding(from, until);
			fail("Passing arguments in non-cronological order is illegal");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("order"));
		}
	}

	@Test
	public void load_By_Date_Between_Can_Be_Given_Two_Identical_Arguments() throws Exception {
		Date date = dateUtil.asDate("1980-10-15");

		List<Animal> result = testee.loadByDateBetweenIncluding(date, date);

		assertEquals("adam", result.iterator().next().get("name"));
		assertEquals(1, result.size());
	}

	@Test
	public void load_By_Species_And_Date() throws Exception {
		List<Animal> result = testee.loadBySpeciesAndDateAfterIncluding("fly", dateUtil.asDate("1981-01-01"));

		assertEquals(1, result.size());
		assertEquals("fredrik", result.iterator().next().get("name"));
	}
	
	@Test
	public void load_By_Species_And_Name_Pattern() throws Exception {
		List<Animal> result = testee.loadBySpeciesAndNamePattern("fly", "e");
		
		assertThat(result, containsAnimals("evan", "fredrik"));
	}

	@Test
	public void load_By_Species_And_Name_Pattern_Two() throws Exception {
		List<Animal> result = testee.loadBySpeciesAndNamePattern("fly", "f");
		
		assertThat(result, containsAnimals("fredrik"));
	}

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
