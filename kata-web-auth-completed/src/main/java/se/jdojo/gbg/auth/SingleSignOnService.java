package se.jdojo.gbg.auth;

public interface SingleSignOnService {

	/**
	 * Acquires a single sign on token for the specified user(name), and if needed, creates a single
	 * sign on session associated to the user (and token).
	 * 
	 * @return a sso token that can be used for system access
	 */
	String acquireToken(String username);

	/**
	 * Returns true if the ssoToken is a token for a valid (open/non-closed) single sign on session.
	 * 
	 * @param ssoToken token to look at
	 * @return true if the token is associated with a valid session, otherwise false
	 */
	boolean tokenIsValid(String ssoToken);

	/**
	 * Invalidates the token so that it can't be used for system access.
	 * 
	 * @param ssoToken the token to invalidate
	 */
	void invalidateToken(String ssoToken);

}
