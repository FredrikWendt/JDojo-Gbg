package startup.web;

public class MockSSORegistry implements SSORegistry{

	private String validToken = "VAILD_TOKEN";

	@Override
	public boolean validateToken(String token) {
		if (validToken != null ) {
			return validToken.equals(token);
		} else {
			return false;
		}
	}

	public void setValidToken(String validToken) {
		this.validToken = validToken;
	}

	@Override
	public String beginSession() {
		return validToken;
	}

}
