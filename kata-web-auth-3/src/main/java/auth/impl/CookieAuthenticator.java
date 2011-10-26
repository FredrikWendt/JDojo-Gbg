package auth.impl;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieAuthenticator extends AuthenticationProcessorBase  {

	@Override
	public void process(ServletRequest request) {
		if (aCookieContainsValidSessionToken((HttpServletRequest) request)) {
			markRequestAsAuthenticated(request);
		}
	}

	private boolean aCookieContainsValidSessionToken(HttpServletRequest request) {
		for (Cookie cookie : request.getCookies()) {
			if (userDirectory.tokenIsValid(cookie.getValue())) {
				return true;
			}
		}
		return false;
	}

}
