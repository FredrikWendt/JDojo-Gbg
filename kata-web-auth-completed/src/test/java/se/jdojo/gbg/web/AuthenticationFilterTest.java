package se.jdojo.gbg.web;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.jdojo.gbg.web.AuthenticationFilter.SSO_TOKEN_COOKIE_NAME;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.jdojo.gbg.auth.LdapService;
import se.jdojo.gbg.auth.SingleSignOnService;
import se.jdojo.gbg.web.AuthenticationFilter;


@RunWith(MockitoJUnitRunner.class)
public class AuthenticationFilterTest {

	private static final String INVALID_PASSWORD = "BadPassword";
	private static final String INVALID_USERNAME = "BadGuy";
	private static final String VALID_PASSWORD = "Hello";
	private static final String VALID_USERNAME = "Esa";
	private static final String VALID_TOKEN = "123AbCdE";
	private static final String INVALID_TOKEN = "123aBc";

	private AuthenticationFilter testee;

	private Cookie[] cookies;

	@Mock
	private HttpServletRequest servletRequest;
	@Mock
	private HttpServletResponse servletResponse;
	@Mock
	private FilterChain chain;
	@Mock
	private SingleSignOnService singleSignOnService;
	@Mock
	private LdapService ldapService;
	@Mock
	private HttpSession httpSession;

	@Before
	public void setup() {
		testee = new AuthenticationFilter();
		testee.setLdapService(ldapService);
		testee.setSingleSignOneService(singleSignOnService);

		when(ldapService.credentialsAreValid(VALID_USERNAME, VALID_PASSWORD)).thenReturn(true);
		when(singleSignOnService.tokenIsValid(VALID_TOKEN)).thenReturn(true);
	}

	@Test
	public void request_Parameter_Password_Is_Case_Sensitive_Lower() throws Exception {
		givenRequestContainsUsernameParameter(VALID_USERNAME);
		givenRequestContainsPasswordParameter(VALID_PASSWORD.toLowerCase());

		testee.doFilter(servletRequest, servletResponse, chain);

		verifyRequestIsRejected();
	}

	@Test
	public void request_Parameter_Password_Is_Case_Sensitive_Upper() throws Exception {
		givenRequestContainsUsernameParameter(VALID_USERNAME);
		givenRequestContainsPasswordParameter(VALID_PASSWORD.toUpperCase());

		testee.doFilter(servletRequest, servletResponse, chain);

		verifyRequestIsRejected();
	}

	@Test
	public void request_Parameter_Username_Is_Case_Sensitive_Lower() throws Exception {
		givenRequestContainsUsernameParameter(VALID_USERNAME.toLowerCase());
		givenRequestContainsPasswordParameter(VALID_PASSWORD);

		testee.doFilter(servletRequest, servletResponse, chain);

		verifyRequestIsRejected();
	}

	@Test
	public void request_Parameter_Username_Is_Case_Sensitive_Upper() throws Exception {
		givenRequestContainsUsernameParameter(VALID_USERNAME.toUpperCase());
		givenRequestContainsPasswordParameter(VALID_PASSWORD);

		testee.doFilter(servletRequest, servletResponse, chain);

		verifyRequestIsRejected();
	}

	@Test
	public void whenRequestContainsValidLdapCredentials_itsPassedOnDownTheFilterChain() throws Exception {
		givenTheRequestContainsValidUsernameAndPassword();

		testee.doFilter(servletRequest, servletResponse, chain);

		verify(ldapService).credentialsAreValid(VALID_USERNAME, VALID_PASSWORD);
		verify(singleSignOnService).acquireToken(VALID_USERNAME);
		verifyRequestIsAccepted();
	}

	@Test
	public void request_Is_Rejected_With_Invalid_Credentials_As_Parameter() throws Exception {
		givenTheRequestContainsInvalidUsernameAndPassword();

		testee.doFilter(servletRequest, servletResponse, chain);

		verify(ldapService).credentialsAreValid(INVALID_USERNAME, INVALID_PASSWORD);
		verify(singleSignOnService, never()).acquireToken(INVALID_USERNAME);
		verifyRequestIsRejected();
	}

	@Test
	public void a_Cookie_With_Right_Name_And_Valid_Token_Is_Accepted() throws Exception {
		givenTheRequestContainsCookieWith(SSO_TOKEN_COOKIE_NAME, VALID_TOKEN);

		testee.doFilter(servletRequest, servletResponse, chain);

		verifyRequestIsAccepted();
	}

	@Test
	public void request_Request_When_Cookie_With_Right_Name_But_Invalid_Token() throws Exception {
		givenTheRequestContainsCookieWith(SSO_TOKEN_COOKIE_NAME, INVALID_TOKEN);

		testee.doFilter(servletRequest, servletResponse, chain);

		verifyRequestIsRejected();
	}

	@Test
	public void reject_Request_When_A_Cookie_With_Wrong_Name_But_Valid_Token() throws Exception {
		givenTheRequestContainsCookieWith(SSO_TOKEN_COOKIE_NAME + "x", VALID_TOKEN);

		testee.doFilter(servletRequest, servletResponse, chain);

		verifyRequestIsRejected();
	}

	@Test
	public void reject_Request_When_Session_Contains_Invalid_Token() throws Exception {
		given_The_Http_Session_Contains_Sso_Token(INVALID_TOKEN);

		testee.doFilter(servletRequest, servletResponse, chain);

		verifyRequestIsRejected();
	}

	@Test
	public void whenSessionContainsValidToken_theRequestIsPassedOnDownTheFilterChain() throws Exception {
		given_The_Http_Session_Contains_Sso_Token(VALID_TOKEN);

		testee.doFilter(servletRequest, servletResponse, chain);

		verifyRequestIsAccepted();
	}

	@Test
	public void an_Empty_Request_Is_Rejected() throws Exception {
		testee.doFilter(servletRequest, servletResponse, chain);

		verifyRequestIsRejected();
	}

	private void verifyRequestIsAccepted() throws IOException, ServletException {
		verify(chain).doFilter(servletRequest, servletResponse);
		verify(servletResponse, never()).setStatus(anyInt());
	}

	private void verifyRequestIsRejected() throws Exception {
		verify(chain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
		verify(servletResponse).setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

	private void given_The_Http_Session_Contains_Sso_Token(String token) {
		given(servletRequest.getSession()).willReturn(httpSession);
		given(httpSession.getAttribute(AuthenticationFilter.SSO_TOKEN_SESSION_KEY)).willReturn(token);
	}

	private void givenTheRequestContainsValidUsernameAndPassword() {
		givenRequestContainsUsernameParameter(VALID_USERNAME);
		givenRequestContainsPasswordParameter(VALID_PASSWORD);
	}

	private void givenRequestContainsUsernameParameter(String username) {
		when(servletRequest.getParameter(AuthenticationFilter.REQUEST_PARAMETER_USER_NAME)).thenReturn(username);
	}

	private void givenRequestContainsPasswordParameter(String password) {
		when(servletRequest.getParameter(AuthenticationFilter.REQUEST_PARAMETER_PASSWORD)).thenReturn(password);
	}

	private void givenTheRequestContainsInvalidUsernameAndPassword() {
		given(servletRequest.getParameter("user-name")).willReturn(INVALID_USERNAME);
		when(servletRequest.getParameter("password")).thenReturn(INVALID_PASSWORD);
	}

	private void givenTheRequestContainsCookieWith(String... cookieNameAndValuePairs) {
		cookies = new Cookie[cookieNameAndValuePairs.length / 2];
		for (int i = 0; i < cookieNameAndValuePairs.length; i = i + 2) {
			cookies[i / 2] = new Cookie(cookieNameAndValuePairs[i], cookieNameAndValuePairs[i + 1]);
		}
		given(servletRequest.getCookies()).willReturn(cookies);
	}

}
