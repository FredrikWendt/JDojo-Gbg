package jdojo.oktober2011;


public interface LdapAuthenticationGateway {
   boolean credentialsAreValid(String userName, String password);

}
