package web;

public interface LdapAuthenticator {

	public boolean verifyCredentials(String username, String password);
}
