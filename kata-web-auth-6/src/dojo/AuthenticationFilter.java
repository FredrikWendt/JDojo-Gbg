package dojo;

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

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		String username = request.getParameter("username");
		
		if (username != null && "validUsername".equals(username)) {
			filterChain.doFilter(req, resp);
			request.getSession().setAttribute("SSOToken", "foo");
			response.addCookie(new Cookie("SSOToken","sometoken"));
		} else {
			response.setStatus(401);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
