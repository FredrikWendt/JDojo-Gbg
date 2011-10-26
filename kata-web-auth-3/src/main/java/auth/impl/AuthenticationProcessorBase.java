package auth.impl;

import javax.servlet.ServletRequest;

import auth.AuthenticationProcessor;
import auth.UserDirectory;

public abstract class AuthenticationProcessorBase implements AuthenticationProcessor {

	protected UserDirectory userDirectory;

	protected void markRequestAsAuthenticated(ServletRequest request) {
		request.setAttribute(AUTHENTICATED, true);
	}

	public void setUserDirectory(UserDirectory userDirectory) {
		this.userDirectory = userDirectory;
	}

}
