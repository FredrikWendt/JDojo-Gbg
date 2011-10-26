package auth.impl;

import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import auth.AuthenticationProcessor;
import auth.UserDirectory;

public class CookieAuthenticatorTest {

	CookieAuthenticator testee;

	@Mock
	private HttpServletRequest request;
	@Mock
	private UserDirectory userDirectory;
	@Mock
	private Cookie cookie;
	final private Cookie[] cookies = new Cookie[1];
	private final String sessionToken = "aUserSessionToken";

	@Before
	public void setupTestee() {
		MockitoAnnotations.initMocks(this);
		cookies[0] = cookie;
		testee = new CookieAuthenticator();
		testee.setUserDirectory(userDirectory);
	}

	@Test
	public void cookieAuthenticatorIsAnAuthenticationProcessor() throws Exception {
		if (testee instanceof AuthenticationProcessor == false) {
			fail("cookie auth doesn't implement " + AuthenticationProcessor.class.getName());
		}
	}

	@Test
	public void aRequestWithACookieContainingAValidTokenIsMarkedAsAuthenticated() throws Exception {
		// given
		given(request.getCookies()).willReturn(cookies);
		given(cookie.getValue()).willReturn(sessionToken);
		given(userDirectory.tokenIsValid(sessionToken)).willReturn(true);

		// when
		testee.process(request);

		// then
		verify(request).setAttribute(AuthenticationProcessor.AUTHENTICATED, true);
	}

	@Test
	public void cookieWithInvalidTokenIsNotMarkedAsAuthenticated() throws Exception {
		// given
		given(request.getCookies()).willReturn(cookies);
		given(cookie.getValue()).willReturn(sessionToken);
		given(userDirectory.tokenIsValid(sessionToken)).willReturn(false);

		// when
		testee.process(request);

		// then
		verify(request, never()).setAttribute(eq(AuthenticationProcessor.AUTHENTICATED),
				anyBoolean());
	}
}
