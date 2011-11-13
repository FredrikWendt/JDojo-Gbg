package se.jdojo.gbg;

/**
 * Version 0.1 of a single sign on registry.
 */
public interface SingleSignOnRegistry {

	/**
	 * Returns a token for a new session, for the specified user.
	 * 
	 * @param username user name that the new session belongs to
	 * @return a token
	 */
	String createSession(String username);

	/**
	 * Returns true if the specified token is associated with a session.
	 * 
	 * @param token the token to validate
	 * @return true if the token is associated with a session
	 */
	boolean tokenIsValid(String token);

}