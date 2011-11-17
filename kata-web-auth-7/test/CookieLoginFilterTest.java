import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import dojo.SingleSignOnRegistry;

@RunWith(MockitoJUnitRunner.class)
public class CookieLoginFilterTest {

	private CookieLoginFilter testee = new CookieLoginFilter();
	private String validToken = "valid";

	@Mock
	private HttpServletRequest request;
	@Mock
	private ServletResponse response;
	@Mock
	private FilterChain filterChain;
	@Mock
	private SingleSignOnRegistry ssoRegistry;
	@Mock
	private HttpSession session;

	@Before
	public void setUp() {
		testee.setSingleSignOnRegistry(ssoRegistry);
	}

	@Test(expected = AuthException.class)
	public void anEmptyRequestWithNoAuthenticationInformationThrowsException() throws Exception {
		testee.doFilter(request, response, filterChain);
	}

	@Test(expected = AuthException.class)
	public void testInvalidTokenCookieNoSessionNoParametersException() throws Exception {
		given(request.getCookies()).willReturn(new Cookie[] { new Cookie("token", "invalid") });
		testee.doFilter(request, response, filterChain);
	}

	@Test(expected = AuthException.class)
	public void unrelatedCookieWithValidTokenThrowsException() throws Exception {
		given(request.getCookies()).willReturn(new Cookie[] { new Cookie("unrelated", "valid") });
		given(ssoRegistry.tokenIsValid("valid")).willReturn(true);

		testee.doFilter(request, response, filterChain);
	}

	@Test
	public void cookieWithValidTokenPassRequestDownTheFilterChain() throws Exception {
		given(request.getCookies()).willReturn(new Cookie[] { new Cookie("token", "valid") });
		given(ssoRegistry.tokenIsValid("valid")).willReturn(true);

		testee.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
		verify(ssoRegistry).tokenIsValid("valid");
	}

	@Test
	public void validSessionPassDownTheFilterChain() throws Exception {
		given(request.getSession()).willReturn(session);
		given(session.getAttribute("token")).willReturn(validToken);
		given(ssoRegistry.tokenIsValid(validToken)).willReturn(true);

		testee.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
		verify(ssoRegistry).tokenIsValid("valid");
	}

}
