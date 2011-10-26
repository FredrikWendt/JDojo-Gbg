package se.jdojo.gbg;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


public class FakeFilterChain implements FilterChain {

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1) throws IOException, ServletException {
		HttpServletResponse x = ((HttpServletResponse) arg1);
		x.setStatus(HttpServletResponse.SC_OK);
	}

}
