package auth.impl;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import auth.AuthenticationProcessor;
import auth.UserDirectory;


public class RequestParameterAuthenticatorTest {
	
	RequestParameterAuthenticator testee = new RequestParameterAuthenticator();
	private String userNameValue;
	@Mock
	private HttpServletRequest request;
	private String passwordValue;
	@Mock
	private UserDirectory userDirectory;
	
	@Before
	public void setup() {
		initMocks(this);
		testee = new RequestParameterAuthenticator();
		testee.setUserDirectory(userDirectory);
	}
	
	@Test
	public void requestParamAuthenticatorIsAnAutnenticationProcessor() throws Exception {
		if (testee instanceof AuthenticationProcessor == false) {
			fail("requestParam auth doesn't implement " + AuthenticationProcessor.class.getName());
		}
	}
	
	@Test
	public void requestWithValidUserNameAndPasswordIsMarkedValid() throws Exception {
		// given
		given(request.getParameter("username")).willReturn(userNameValue);
		given(request.getParameter("password")).willReturn(passwordValue);
		given(userDirectory.credentialsAreValid(userNameValue, passwordValue)).willReturn(true);
		
		// when
		testee.process(request);
		
		// then
		verify(request).setAttribute(AuthenticationProcessor.AUTHENTICATED, true);
	}
	
	@Test
	public void requestWithInvalidUserNameIsNotMarkedValid() throws Exception {
		// given
		given(request.getParameter("username")).willReturn(userNameValue);
		given(request.getParameter("password")).willReturn(passwordValue);
		given(userDirectory.credentialsAreValid(userNameValue, passwordValue)).willReturn(false);
		
		// when
		testee.process(request);
		
		// then
		verify(request, never()).setAttribute(eq(AuthenticationProcessor.AUTHENTICATED), anyBoolean());
	}

}
