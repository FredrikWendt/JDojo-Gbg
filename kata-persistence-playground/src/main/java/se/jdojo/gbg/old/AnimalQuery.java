package se.jdojo.gbg.old;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import se.jdojo.gbg.Animal;

public abstract class AnimalQuery {

	private final DataSource dataSource;
	private final String query;
	private final DateUtil dateUtil;

	public AnimalQuery(DataSource dataSource, DateUtil dateUtil, String query) {
		this.dataSource = dataSource;
		this.dateUtil = dateUtil;
		this.query = query;
	}

	public List<Animal> execute() {
		try {
			List<Animal> animals = new ArrayList<Animal>();
			AnimalImpl animal = null;
			Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(query);
			setParameters(statement);
			ResultSet resultSet = statement.executeQuery();
			long previousId = -1;
			while (resultSet.next()) {
				int animalId = resultSet.getInt(1);
				String propertyType = resultSet.getString(2);
				String propertyKey = resultSet.getString(3);
				String propertyValue = resultSet.getString(4);

				if (animal == null || previousId != animalId) {
					animal = new AnimalImpl();
					animals.add(animal);
					previousId = animalId;
				}
				animal.setId(animalId);
				animal.set(propertyKey, parseProperty(propertyType, propertyValue));
			}
			resultSet.close();
			statement.close();
			connection.close();
			return animals;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to load data", e);
		}
	}
	
	public Animal executeSingle() {
		List<Animal> result = execute();
		if (result.size() > 1) {
			throw new RuntimeException("More than one item found");
		}
		return result.iterator().next();
	}


	protected void setParameters(PreparedStatement statement) throws SQLException {
	}

	private Object parseProperty(String propertyType, String propertyValue) {
		if ("integer".equals(propertyType)) {
			return Integer.parseInt(propertyValue);
		}
		if ("date".equals(propertyType)) {
			return dateUtil.asDate(propertyValue);
		}
		return propertyValue;
	}

}
