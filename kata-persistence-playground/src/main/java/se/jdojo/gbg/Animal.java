package se.jdojo.gbg;

import java.util.Map;

public interface Animal {

	long getId();

	Map<String, Object> getProperties();

	Object get(String propertyKey);

}
