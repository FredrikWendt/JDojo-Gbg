package jdojo.oktober2011;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

/**
 * JDojo
 */
public class SSOFilterTest {

    public static final String VALID_TOKEN = "validToken";

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    SingleSignOnRegistry singleSignOnRegistry = mock(SingleSignOnRegistry.class);
    private SSOFilter testee;

    @Before
    public void before() {
        testee = new SSOFilter();
        testee.setSingleSignOnRegistry(singleSignOnRegistry);
        when(singleSignOnRegistry.tokenIsValid("validToken")).thenReturn(true);
    }

    @Test
    public void tokenInCookieIsValid() throws Exception {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sso", "validToken")});


        testee.doFilter(request, response, filterChain);

        verify(request).getCookies();
        verify(singleSignOnRegistry).tokenIsValid("validToken");
        verify(filterChain).doFilter(request, response);

    }

    @Test
    public void tokenInCookieIsNotValid() throws Exception {
        // arrange
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sso","invalidToken")});

        // act
        testee.doFilter(request, response, filterChain);

        // verify/assert
        verify(request).getCookies();
        verify(singleSignOnRegistry).tokenIsValid("invalidToken");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    public void noSSOCookieInRequest() throws Exception {
        //arrange
        when(request.getCookies()).thenReturn(new Cookie[]{});

         // act
        testee.doFilter(request, response, filterChain);

        // verify/assert
        verify(request).getCookies();
        verifyZeroInteractions(singleSignOnRegistry);
        verify(filterChain, never()).doFilter(request, response);

    }

    @Test
    public void noSSOTokenInCookies() throws Exception {
        //arrange
        when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("noSSO","")});

         // act
        testee.doFilter(request, response, filterChain);

        // Verify
        verify(request).getCookies();
        verifyZeroInteractions(singleSignOnRegistry);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    public void secondCookieContainsValidSSOToken() throws Exception {
        when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("noSSO",""),new Cookie("sso", VALID_TOKEN) });

        testee.doFilter(request, response, filterChain);

        verify(request).getCookies();
        verify(singleSignOnRegistry).tokenIsValid(VALID_TOKEN);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void validTokenInSession() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("sso")).thenReturn(VALID_TOKEN);

        testee.doFilter(request, response, filterChain);

        // verify(request).getSession();
        verify(session).getAttribute("sso");
        verify(singleSignOnRegistry).tokenIsValid(VALID_TOKEN);
        verify(filterChain).doFilter(request, response);
    }
}
