package se.jdojo.gbg;

import static org.junit.Assert.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;


public class AuthFilterTest {

	@Before
	public void setUp() throws Exception {
	}
	
	
	@Test
	public void emptyRequestIsRejected() throws Exception {
		// ARRANGE
		FilterChain filterChain = new FakeFilterChain();
		AuthFilter filter = new AuthFilter();
		ServletRequest request = null;
		FakeHttpServletResponse response = new FakeHttpServletResponse();
		
		// ACT
		filter.doFilter(request, response, filterChain);
		
		// ASSERT
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getResponseCode());
	}
	
//	@Test
//	public void aRequestWithValidSsoTokenIsPassedThroughFilterChain() throws Exception {
//		// A
//		
//		// A
//		
//		
//		// A
//		assertEquals(HttpServletResponse.SC_OK, response.getResponseCode());
//	}
	
	

}
