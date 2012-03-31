package se.jdojo.gbg.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.jdojo.gbg.auth.LdapService;
import se.jdojo.gbg.auth.SingleSignOnService;


public class AuthenticationFilter implements Filter {

	public static final String REQUEST_PARAMETER_PASSWORD = "password";
	public static final String REQUEST_PARAMETER_USER_NAME = "user-name";
	public static final String SSO_TOKEN_SESSION_KEY = "SSO_TOKEN";
	public static final String SSO_TOKEN_COOKIE_NAME = "SSO_TOKEN";

	private SingleSignOnService singleSignOnService;
	private LdapService ldapService;

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	public void setSingleSignOneService(SingleSignOnService singleSignOnService) {
		this.singleSignOnService = singleSignOnService;
	}

	public void setLdapService(LdapService ldapService) {
		this.ldapService = ldapService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		doFilterWithHttpClasses(httpServletRequest, httpServletResponse, chain);
	}

	private void doFilterWithHttpClasses(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (sessionContainsValidToken(request)) {
			acceptRequest(request, response, chain);
			return;
		}
		
		if (cookieContainsValidToken(request.getCookies())) {
			acceptRequest(request, response, chain);
			return;
		}

		if (requestContainsValidLdapCredentials(request)) {
			singleSignOnService.acquireToken(request.getParameter(REQUEST_PARAMETER_USER_NAME));
			acceptRequest(request, response, chain);
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

	private boolean cookieContainsValidToken(Cookie[] cookies) {
		if (cookies == null) {
			return false;
		}
		for (Cookie c : cookies) {
			if (SSO_TOKEN_COOKIE_NAME.equals(c.getName())) {
				if (singleSignOnService.tokenIsValid(c.getValue())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean requestContainsValidLdapCredentials(HttpServletRequest request) {
		String username = request.getParameter(REQUEST_PARAMETER_USER_NAME);
		String password = request.getParameter(REQUEST_PARAMETER_PASSWORD);
		return ldapService.credentialsAreValid(username, password);
	}

	private boolean sessionContainsValidToken(HttpServletRequest request) {
		if (request.getSession() != null) {
			String token = (String) request.getSession().getAttribute(SSO_TOKEN_SESSION_KEY);
			return singleSignOnService.tokenIsValid(token);
		}
		return false;
	}

	private void acceptRequest(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		chain.doFilter(request, response);
	}

}
