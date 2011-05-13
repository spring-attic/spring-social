package org.springframework.social.security.provider;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.social.connect.Connection;
import org.springframework.util.Assert;

public abstract class AbstractSocialAuthenticationService<S> implements SocialAuthenticationService<S>,
		InitializingBean {

	private AuthenticationMode authenticationMode;
	private String connectionAddedRedirectUrl = "/";

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(connectionAddedRedirectUrl, "connectionAddedRedirectUrl not configured");
	}

	public AbstractSocialAuthenticationService(AuthenticationMode authenticationMode) {
		this.authenticationMode = authenticationMode;
	}

	public final AuthenticationMode getAuthenticationMode() {
		return authenticationMode;
	}

	public String getConnectionAddedRedirectUrl(HttpServletRequest request, Connection<?> connection) {
		return connectionAddedRedirectUrl;
	}

	public void setConnectionAddedRedirectUrl(String connectionAddedRedirectUrl) {
		this.connectionAddedRedirectUrl = connectionAddedRedirectUrl;
	}

}
