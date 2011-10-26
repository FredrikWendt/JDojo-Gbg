package startup.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

public class FakeHttpServletRequest extends AbstractHttpServletRequest {

	private String ssoToken;
	private Map<String,String> parameters = new HashMap<String,String>();

	@Override
	public String getParameter(String param) {
		return parameters.get(param);
	}

	@Override
	public Cookie[] getCookies() {
		return new Cookie[] { new Cookie("SSO", ssoToken) }; 
	}

	public void setSSOCookie(String token) {
		this.ssoToken = token;
	}

	public void addParameter(String name, String value) {
		parameters.put(name, value);
	}

}
