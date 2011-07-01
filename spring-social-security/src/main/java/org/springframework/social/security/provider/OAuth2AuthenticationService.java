/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.social.security.provider;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class OAuth2AuthenticationService<S> extends AbstractSocialAuthenticationService<S> {

	private OAuth2ConnectionFactory<S> connectionFactory;
	private Set<String> returnToUrlParameters;
	private String scope;
	
	public OAuth2AuthenticationService() {
		super(AuthenticationMode.EXPLICIT);
	}

	public OAuth2AuthenticationService(OAuth2ConnectionFactory<S> connectionFactory) {
		this();
		setConnectionFactory(connectionFactory);
	}

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(getConnectionFactory(), "connectionFactory");
	}

	public SocialAuthenticationToken getAuthToken(AuthenticationMode authMode, HttpServletRequest request,
			HttpServletResponse response) throws SocialAuthenticationRedirectException {

		if (authMode != AuthenticationMode.EXPLICIT) {
			return null;
		}

		String code = request.getParameter("code");

		if (!StringUtils.hasText(code)) {
			// First phase: get a request token
			String returnToUrl = buildReturnToUrl(request);
			String scope = getScope(); // TODO set scope
			String redirect = getConnectionFactory().getOAuthOperations().buildAuthenticateUrl(
					GrantType.AUTHORIZATION_CODE, new OAuth2Parameters(returnToUrl, scope));
			throw new SocialAuthenticationRedirectException(redirect);
		} else if (StringUtils.hasText(code)) {
			String returnToUrl = buildReturnToUrl(request);
			AccessGrant accessGrant = getConnectionFactory().getOAuthOperations().exchangeForAccess(code, returnToUrl,
					null);

			// TODO avoid API call if possible (auth using token would be fine)
			ConnectionData data = getConnectionFactory().createConnection(accessGrant).createData();
			return new SocialAuthenticationToken(data, null);
		} else {
			return null;
		}
	}

	protected String buildReturnToUrl(HttpServletRequest request) {
		StringBuffer sb = request.getRequestURL();
		sb.append("?");

		for (String name : getReturnToUrlParameters()) {
			// Assume for simplicity that there is only one value
			String value = request.getParameter(name);

			if (value == null) {
				continue;
			}
			sb.append(name).append("=").append(value).append("&");

		}

		sb.setLength(sb.length() - 1); // strip trailing ? or &

		return sb.toString();
	}

	public OAuth2ConnectionFactory<S> getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(OAuth2ConnectionFactory<S> connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public void setReturnToUrlParameters(Set<String> returnToUrlParameters) {
		Assert.notNull(returnToUrlParameters, "returnToUrlParameters cannot be null");
		this.returnToUrlParameters = returnToUrlParameters;
	}

	public Set<String> getReturnToUrlParameters() {
		if (returnToUrlParameters == null) {
			returnToUrlParameters = new HashSet<String>();
		}
		return returnToUrlParameters;
	}

	public String getScope() {
		return scope;
	}

	/**
	 * @param scope OAuth scope to use, i.e. requested permissions
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	
}
