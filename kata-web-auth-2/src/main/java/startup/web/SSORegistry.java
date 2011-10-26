package startup.web;

public interface SSORegistry {
	
	boolean validateToken(String token);
	String beginSession();
}
