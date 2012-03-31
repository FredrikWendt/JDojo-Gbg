package se.jdojo.gbg.auth;

public interface LdapService {

	/**
	 * Returns true if the specified username and password match a record in the LDAP directory.
	 * 
	 * @param username the username to test with password
	 * @param password the password to test with username
	 * @return true if the specified username and password match a record in the LDAP directory,
	 *         otherwise false
	 */
	boolean credentialsAreValid(String username, String password);

}
