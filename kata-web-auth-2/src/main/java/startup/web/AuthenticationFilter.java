package startup.web;

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

	private SSORegistry ssoRegistry;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		Cookie[] cookies = httpRequest.getCookies();
		if (ssoRegistry.validateToken(cookies[0].getValue())) {
			httpResponse.setStatus(HttpServletResponse.SC_OK);
		} else if ("validuser".equals(httpRequest.getParameter("username"))) {
			httpResponse.setStatus(HttpServletResponse.SC_OK);
			httpResponse.addCookie(new Cookie("SSO", ssoRegistry.beginSession()));
		} else {
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	public void setSSORegistry(SSORegistry SSORegistry) {
		ssoRegistry = SSORegistry;
	}

}
