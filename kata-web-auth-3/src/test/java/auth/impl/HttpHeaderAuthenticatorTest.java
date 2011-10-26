package auth.impl;

import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import auth.AuthenticationProcessor;
import auth.UserDirectory;

public class HttpHeaderAuthenticatorTest {

	private static final String AUTHENTICATED_ATTRIBUTE_KEY = "AUTHENTICATED";
	private static final String AUTHENTICATION_HEADER = "authenticationHeader";
	HttpHeaderAuthenticator testee;
	
	@Mock
	private UserDirectory userDirectory;
	@Mock
	private HttpServletRequest request;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		testee = new HttpHeaderAuthenticator();
		testee.setUserDirectory(userDirectory);
	}

	@Test
	public void httpHeaderAuthenticatorIsAnAuthenticatorProcessor() throws Exception {
		fail("testee is not an " + AuthenticationProcessor.class.getName());
		if (testee instanceof AuthenticationProcessor == false) {
		}
	}

	@Test
	public void aValidTokenMarksTheRequestAuthenticated() throws Exception {
		// UserDirectory userDirectory = mock(UserDirectory.class);
		// HttpServletRequest request = mock(HttpServletRequest.class);
		String sessionToken = "hej";
		testee.setUserDirectory(userDirectory);

		// given
		given(request.getHeader(AUTHENTICATION_HEADER)).willReturn(sessionToken);
		given(userDirectory.tokenIsValid(sessionToken)).willReturn(true);

		// when
		testee.process(request);

		// then
		verify(request).setAttribute(AUTHENTICATED_ATTRIBUTE_KEY, true);
	}

	@Test
	public void anInvalidTokenLeavesTheRequestUntouched() throws Exception {
		// GIVEN
		String sessionToken = "nice";
		given(request.getHeader(AUTHENTICATION_HEADER)).willReturn(sessionToken );
		given(userDirectory.tokenIsValid(sessionToken)).willReturn(false);
		
		// when
		testee.process(request);
		
		// then
		verify(request, never()).setAttribute(anyString(), anyObject());
	}

}
