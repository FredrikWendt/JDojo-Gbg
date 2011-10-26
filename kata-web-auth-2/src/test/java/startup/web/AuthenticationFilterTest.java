package startup.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

public class AuthenticationFilterTest {

	private AuthenticationFilter filter;
	private FilterChain filterChain;
	private FakeHttpServletRequest request;
	private FakeHttpServletResponse response;
	private MockSSORegistry ssoRegistry;

	@Before
	public void setup() {
		filter = new AuthenticationFilter();

		filterChain = null;
		request = new FakeHttpServletRequest();
		response = new FakeHttpServletResponse();
		ssoRegistry = new MockSSORegistry();
		filter.setSSORegistry(ssoRegistry);
	}

	@Test
	public void anEmptyRequestIsRejected() throws Exception {
		// Arrange

		// Act
		filter.doFilter(request, response, filterChain);

		assertResponseCode(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void aRequestWithValidSSOTokenIsAccepted() throws Exception {
		// Arrange
		request.setSSOCookie("valid_cookie");
		ssoRegistry.setValidToken("valid_cookie");
		// Act
		filter.doFilter(request, response, filterChain);

		assertResponseCode(HttpServletResponse.SC_OK);
	}

	public void assertResponseCode(int expectedStatus) {
		int responseCode = response.getStatus();
		assertEquals(expectedStatus, responseCode);
	}

	@Test
	public void InvalidSSOCookieIsNotAccepted() throws Exception {
		// Arrange
		request.setSSOCookie("notvalid_cookie");

		// Act
		filter.doFilter(request, response, filterChain);

		assertResponseCode(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Test
	public void requestWithInvalidUsernamePasswordIsUnauthorized() throws IOException, ServletException {
		// Arrange
		request.addParameter("username", "invaliduser");
		request.addParameter("password", "invalidpassword");
		// Act
		filter.doFilter(request, response, filterChain);

		assertResponseCode(HttpServletResponse.SC_UNAUTHORIZED);

	}

	@Test
	public void requestWithValidUsernamePasswordIsAuthorized() throws IOException, ServletException {
		// Arrange
		request.addParameter("username", "validuser");
		request.addParameter("password", "validpassword");
		// Act
		filter.doFilter(request, response, filterChain);

		assertResponseCode(HttpServletResponse.SC_OK);

	}

	@Test
	public void sessionIsStarted() throws IOException, ServletException {
		// Arrange
		request.addParameter("username", "validuser");
		request.addParameter("password", "validpassword");
		// Act
		filter.doFilter(request, response, filterChain);

		Cookie cookie = response.getCookie();
		assertTrue(ssoRegistry.validateToken(cookie.getValue()));

	}
}
