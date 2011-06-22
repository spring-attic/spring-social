package org.springframework.social.security.provider;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.social.connect.Connection;

public abstract class AbstractSocialAuthenticationService<S> implements SocialAuthenticationService<S>,
		InitializingBean {

	private AuthenticationMode authenticationMode = null;
	private ConnectionCardinality connectionCardinality = ConnectionCardinality.ONE_TO_ONE;
	private String connectionAddedRedirectUrl;

	public void afterPropertiesSet() throws Exception {
	}

	public AbstractSocialAuthenticationService(AuthenticationMode authenticationMode) {
		this.authenticationMode = authenticationMode;
	}

	public final AuthenticationMode getAuthenticationMode() {
		return authenticationMode;
	}

	public ConnectionCardinality getConnectionCardinality() {
		return connectionCardinality;
	}

	public void setConnectionCardinality(ConnectionCardinality connectionCardinality) {
		if (connectionCardinality == null) {
			throw new NullPointerException("connectionCardinality");
		}
		this.connectionCardinality = connectionCardinality;
	}

	public String getConnectionAddedRedirectUrl(HttpServletRequest request, Connection<?> connection) {
		return connectionAddedRedirectUrl;
	}

	public void setConnectionAddedRedirectUrl(String connectionAddedRedirectUrl) {
		this.connectionAddedRedirectUrl = connectionAddedRedirectUrl;
	}

}
