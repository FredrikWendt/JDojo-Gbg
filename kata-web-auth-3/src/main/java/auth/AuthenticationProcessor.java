package auth;

import javax.servlet.ServletRequest;

public interface AuthenticationProcessor {

	public static final String AUTHENTICATED = "AUTHENTICATED";

	void process(ServletRequest request);

}
