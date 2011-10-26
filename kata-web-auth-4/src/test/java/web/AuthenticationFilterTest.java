package web;

import static org.mockito.Mockito.*;
import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

public class AuthenticationFilterTest {

	private HttpServletResponse response;
	private HttpServletRequest request;
	private AuthenticationFilter filter;
	private FilterChain filterChain;
	private SsoReg ssoReg;

	@Before
	public void before() {
		response = mock(HttpServletResponse.class);
		request = mock(HttpServletRequest.class);
		
		filter = new AuthenticationFilter();
		filterChain = null;
		ssoReg = mock(SsoReg.class);
		filter.setSsoReg(ssoReg);

	}

	@Test
	public void anEmptyRequestIsRejected() throws Exception {
		// Arrange

		// Act
		filter.doFilter(request, response, filterChain);

		// Assert/verify
		verify(response).setStatus(401);
	}

	@Test
	public void anSsoCookieIsSentToSsoReg() throws Exception {
		// Arrange
		String ssoToken = "hej";
		Cookie[] cookies = createCookieArray(ssoToken);
		when(request.getCookies()).thenReturn(cookies);

		// Act
		filter.doFilter(request, response, filterChain);

		// Assert
		verify(ssoReg).verifyToken(ssoToken);
	}

	@Test
	public void rejectedIfCookieIsWrong() throws Exception {
		String ssoToken = "invalid";
		Cookie[] cookies = createCookieArray(ssoToken);
		when(request.getCookies()).thenReturn(cookies);

		filter.doFilter(request, response, filterChain);

		verify(response).setStatus(401);
		verify(ssoReg).verifyToken(ssoToken);
	}

	@Test
	public void acceptedIfCookieIsCorrect() throws Exception {
		String ssoToken = "hej";
		Cookie[] cookies = createCookieArray(ssoToken);
		when(request.getCookies()).thenReturn(cookies);
		when(ssoReg.verifyToken(ssoToken)).thenReturn(true);

		filter.doFilter(request, response, filterChain);

		verify(response).setStatus(200);
		verify(ssoReg).verifyToken(ssoToken);
	}

	@Test
	public void cookieWithIncorrectNameIsRejected() throws Exception {
		Cookie cookie = new Cookie("InteSSOCookie", "hej");
		Cookie[] cookies = new Cookie[] { cookie };
		when(request.getCookies()).thenReturn(cookies);

		// act
		filter.doFilter(request, response, filterChain);
		// assert
		verify(response).setStatus(401);
		verify(ssoReg, never()).verifyToken(anyString());
	}
	
	@Test
	public void credentialsAreVerified() throws Exception {
		LdapAuthenticator ldap = mock(LdapAuthenticator.class);
		String username = "validuser";
		String password = "validpassword";
		when(request.getParameter("username")).thenReturn(username);
		when(request.getParameter("password")).thenReturn(password);
		filter.setLdap(ldap);
		
		filter.doFilter(request, response, filterChain);
		
		verify(ldap ).verifyCredentials(username, password);
	}

	private Cookie[] createCookieArray(String ssoToken) {
		Cookie ssoCookie = new Cookie("sso", ssoToken);
		Cookie[] cookies = new Cookie[] { ssoCookie };
		return cookies;
	}

}