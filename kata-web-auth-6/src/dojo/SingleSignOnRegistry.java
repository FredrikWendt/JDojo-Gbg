package dojo;

public interface SingleSignOnRegistry {

	boolean tokenIsValid(String token);

	String registerNewSession(String userName);

	void endSession(String token);
}
