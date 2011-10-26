package jdojo.oktober2011;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SSOFilter implements Filter {

    public static final String SSO_COOKIE_NAME = "sso";

    private SingleSignOnRegistry singleSignOnRegistry;

    public void setSingleSignOnRegistry(SingleSignOnRegistry singleSignOnRegistry) {
        this.singleSignOnRegistry = singleSignOnRegistry;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Cookie[] cookies = ((HttpServletRequest) servletRequest).getCookies();

        if (cookies != null && cookies.length > 0){
            for(Cookie cookie: cookies){
                if (SSO_COOKIE_NAME.equals(cookie.getName())) {
                    if (singleSignOnRegistry.tokenIsValid(cookie.getValue())) {
                        filterChain.doFilter(servletRequest, servletResponse);
                    }
                }
            }
        }
    }

    public void destroy() {

    }
}
