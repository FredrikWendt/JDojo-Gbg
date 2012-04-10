package se.jdojo.gbg.old;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import se.jdojo.gbg.Animal;

public class AnimalDAO {

	private static final String SELECT_ANIMAL_START = "SELECT "
			+ "animals.id, property_types.p_type, properties.p_key, animal_data.data "
			+ "FROM animal_data INNER JOIN properties ON (animal_data.property = properties.id) "
			+ "	INNER JOIN property_types ON (properties.p_type = property_types.id) "
			+ "	INNER JOIN animals ON (animal_data.animal = animals.id) ";
	private static final String QUERY_BY_PROPERTY = SELECT_ANIMAL_START //
			+ "WHERE animals.id IN ( " //
			+ "SELECT animal_data.animal " //
			+ "FROM animal_data INNER JOIN properties ON animal_data.property=properties.id "
			+ "WHERE animal_data.data=? AND properties.p_key=?" + ") " //
			+ "ORDER BY animals.id ";

	private final DataSource dataSource;
	private final DateUtil dateUtil;

	public AnimalDAO(DataSource connectionFactory, DateUtil dateUtil) {
		this.dataSource = connectionFactory;
		this.dateUtil = dateUtil;
	}

	public Animal loadById(final int id) {
		String query = SELECT_ANIMAL_START //
				+ "WHERE animals.id=? " //
				+ "ORDER BY animals.id";

		return new AnimalQuery(dataSource, dateUtil, query) {
			protected void setParameters(PreparedStatement statement) throws SQLException {
				statement.setObject(1, id);
			}
		}.executeSingle();
	}

	public List<Animal> loadAllAnimals() {
		String query = SELECT_ANIMAL_START + "ORDER BY animals.id";
		return new AnimalQuery(dataSource, dateUtil, query) {
		}.execute();
	}

	public Animal loadAnimalByName(final String name) {
		return new AnimalQuery(dataSource, dateUtil, QUERY_BY_PROPERTY) {
			@Override
			protected void setParameters(PreparedStatement statement) throws SQLException {
				statement.setObject(1, name);
				statement.setObject(2, "name");
			}
		}.executeSingle();
	}

	public List<Animal> loadBySpecies(final String species) {
		return new AnimalQuery(dataSource, dateUtil, QUERY_BY_PROPERTY) {
			@Override
			protected void setParameters(PreparedStatement statement) throws SQLException {
				statement.setObject(1, species);
				statement.setObject(2, "species");
			}
		}.execute();
	}

	public List<Animal> loadByNamesMatching(final String characterSequenceToMatch) {
		String query = SELECT_ANIMAL_START //
				+ "WHERE animals.id IN ( " //
				+ "SELECT animal_data.animal " //
				+ "FROM animal_data INNER JOIN properties ON animal_data.property=properties.id "
				+ "WHERE animal_data.data LIKE ? AND properties.p_key=?" + ") " //
				+ "ORDER BY animals.id";
		return new AnimalQuery(dataSource, dateUtil, query) {
			protected void setParameters(PreparedStatement statement) throws SQLException {
				statement.setObject(1, "%" + characterSequenceToMatch + "%");
				statement.setObject(2, "name");
			};
		}.execute();
	}

	public List<Animal> loadByDateAfterIncluding(Date pointInTime) {
		List<Animal> result = new ArrayList<Animal>();
		List<Animal> allAnimals = loadAllAnimals();
		for (Animal animal : allAnimals) {
			Date dateOfBirth = (Date) animal.get("dob");
			if (dateOfBirth.after(pointInTime) || dateOfBirth.equals(pointInTime)) {
				result.add(animal);
			}
		}
		return result;
	}

	public List<Animal> loadByDateBeforeIncluding(Date pointInTime) {
		List<Animal> result = new ArrayList<Animal>();
		List<Animal> allAnimals = loadAllAnimals();
		for (Animal animal : allAnimals) {
			Date dateOfBirth = (Date) animal.get("dob");
			if (dateOfBirth.before(pointInTime) || dateOfBirth.equals(pointInTime)) {
				result.add(animal);
			}
		}
		return result;
	}

	public List<Animal> loadByDateBetweenIncluding(Date from, Date until) {
		if (from.after(until)) {
			throw new IllegalArgumentException("Second date is before first in time - arguments passed in wrong order?");
		}
		
		List<Animal> result = new ArrayList<Animal>();
		List<Animal> allAnimals = loadAllAnimals();
		for (Animal animal : allAnimals) {
			Date dateOfBirth = (Date) animal.get("dob");
			if (dateOfBirth.before(from) || dateOfBirth.after(until)) {
				continue;
			}
			result.add(animal);
		}
		return result;
	}

	public List<Animal> loadBySpeciesAndDateAfterIncluding(String species, Date bornAtOrAfter) {
		List<Animal> bySpecies = loadBySpecies(species);
		List<Animal> byDate = loadByDateAfterIncluding(bornAtOrAfter);
		List<Animal> result = new ArrayList<Animal>(bySpecies);
		result.retainAll(byDate);
		return result;
	}

	public List<Animal> loadBySpeciesAndNamePattern(String string, String string2) {
		// TODO: both can be expressed as single SQL statement
		List<Animal> bySpecies = loadBySpecies(string);
		List<Animal> byName = loadByNamesMatching(string2);
		List<Animal> result = new ArrayList<Animal>(bySpecies);
		result.retainAll(byName);
		return result;
	}

}
