package se.jdojo.gbg.old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import se.jdojo.gbg.Animal;

public class ContainsAnimals extends BaseMatcher<List<Animal>> {
	
	private final Collection<String> names = new ArrayList<String>();

	public ContainsAnimals(String[] names) {
		for (String n : names) {
			this.names.add(n);
		}
	}

	public static ContainsAnimals containsAnimals(String... names) {
		return new ContainsAnimals(names);
	}

	@Override
	public boolean matches(Object arg0) {
		@SuppressWarnings("unchecked")
		List<Animal> result = (List<Animal>) arg0;
		List<String> names = new ArrayList<String>();
		for (Animal animal : result) {
			names.add((String) animal.get("name"));
		}
		return names.containsAll(this.names);
	}

	@Override
	public void describeTo(Description arg0) {
		arg0.appendText("expected list to contain some animals, one or more was missing");
	}

}
