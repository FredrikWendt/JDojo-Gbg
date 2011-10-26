package web;

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

public class AuthenticationFilter implements Filter {

	private SsoReg ssoReg;
	private LdapAuthenticator ldap;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain fc) throws IOException,
			ServletException {
		HttpServletResponse hres = (HttpServletResponse) res;
		HttpServletRequest hreq = (HttpServletRequest) req;
		boolean validToken = checkSsoCookie(req, hres);
		if (validToken) {
			hres.setStatus(200);
//		} else (ldap.verifyCredentials(hreq.getParameter("username"), 
//				hreq.getParameter("password"))) {
//			
		} else {
			hres.setStatus(401);
		}
	}

	public boolean checkSsoCookie(ServletRequest req, HttpServletResponse hres) {
		boolean validToken = false;
		Cookie[] cookies = ((HttpServletRequest) req).getCookies();
		if (cookies != null && cookies[0].getName().equals("sso")) {
			validToken = ssoReg.verifyToken(cookies[0].getValue());
		}
		return validToken;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	public void setSsoReg(SsoReg ssoReg) {
		this.ssoReg = ssoReg;
	}

	public void setLdap(LdapAuthenticator ldap) {
		this.ldap = ldap;
		
	}
}
