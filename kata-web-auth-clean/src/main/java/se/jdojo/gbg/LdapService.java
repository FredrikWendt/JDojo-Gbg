package se.jdojo.gbg;

/**
 * Version 0.1 of a LDAP interface.
 */
public interface LdapService {

	/**
	 * Returns true if the specified credentials are valid and match a user account in the LDAP
	 * registry.
	 * 
	 * @param username a username such as "bob"
	 * @param password a password such as "b0bsPW"
	 * @return true if the credentials are valid, otherwise false.
	 */
	boolean credentialsAreValid(String username, String password);

}