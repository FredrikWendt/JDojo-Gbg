import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import dojo.SingleSignOnRegistry;

public class CookieLoginFilter implements Filter {

	private SingleSignOnRegistry ssoRegistry;

	public void setSingleSignOnRegistry(SingleSignOnRegistry ssoRegistry) {
		this.ssoRegistry = ssoRegistry;
	}
	
	@Override
	public void destroy() {
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
			ServletException {
		Cookie[] cookies = ((HttpServletRequest) request).getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("token".equals(cookie.getName())) {
					if (ssoRegistry.tokenIsValid(cookie.getValue())) {
						filterChain.doFilter(request, response);
						return;
					}
				}
			}
		}
		if(validSession(((HttpServletRequest)request).getSession())){
			filterChain.doFilter(request, response);
			return;
		}
		
		throw new AuthException();

	}

	private boolean validSession(HttpSession httpSession) {
		if(httpSession != null){
			String attribute = (String) httpSession.getAttribute("token");
			return ssoRegistry.tokenIsValid(attribute);
		}
		return false;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
