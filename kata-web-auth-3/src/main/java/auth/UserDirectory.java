package auth;


public interface UserDirectory {

	boolean tokenIsValid(String sessionToken);

	boolean credentialsAreValid(String userName, String passwordParameterValue);

}
