package auth.impl;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class HttpHeaderAuthenticator extends AuthenticationProcessorBase { 

	@Override
	public void process(ServletRequest request) {
		doProcess((HttpServletRequest) request);
	}

	private void doProcess(HttpServletRequest request) {
		String token = request.getHeader("authenticationHeader");
		if (userDirectory.tokenIsValid(token)) {
			markRequestAsAuthenticated(request);
		}
	}

}
