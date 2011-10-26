package auth.impl;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class RequestParameterAuthenticator extends AuthenticationProcessorBase {

	@Override
	public void process(ServletRequest request) {
		if (containsValidUserNameAndPasswordRequestParameters((HttpServletRequest) request)) {
			markRequestAsAuthenticated(request);
		}
	}

	private boolean containsValidUserNameAndPasswordRequestParameters(HttpServletRequest request) {
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		return userDirectory.credentialsAreValid(userName, password);
	}

}
