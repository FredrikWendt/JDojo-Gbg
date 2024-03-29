package se.jdojo.gbg.old;

import java.util.HashMap;
import java.util.Map;

import se.jdojo.gbg.Animal;

public class AnimalImpl implements Animal {

	private long animalId;
	private Map<String, Object> properties = new HashMap<String, Object>();

	@Override
	public String toString() {
		return get("name") + "," + get("species") + "," + get("dob");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Animal) {
			Animal other = (Animal) obj;
			return properties.equals(other.getProperties());
		}
		return false;
	}

	@Override
	public long getId() {
		return animalId;
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setId(long animalId) {
		this.animalId = animalId;
	}

	public void set(String key, Object value) {
		this.properties.put(key, value);
	}

	@Override
	public Object get(String propertyKey) {
		return properties.get(propertyKey);
	}

}
