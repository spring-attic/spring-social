/*
 * Copyright 2015 the original author or authors.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

/**
 * @author Stefan Fussennegger
 * @param <S> The provider's API type.
 */
public class OAuth2AuthenticationService<S> extends AbstractSocialAuthenticationService<S> {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private OAuth2ConnectionFactory<S> connectionFactory;

	private Set<String> returnToUrlParameters;
	
	private String defaultScope = "";
	
	public OAuth2AuthenticationService(OAuth2ConnectionFactory<S> connectionFactory) {
		setConnectionFactory(connectionFactory);
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

	/**
	 * @param defaultScope OAuth scope to use, i.e. requested permissions
	 */
	public void setDefaultScope(String defaultScope) {
		this.defaultScope = defaultScope;
	}

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(getConnectionFactory(), "connectionFactory");
	}

	public SocialAuthenticationToken getAuthToken(HttpServletRequest request, HttpServletResponse response) throws SocialAuthenticationRedirectException {
		String code = request.getParameter("code");
		if (!StringUtils.hasText(code)) {
			OAuth2Parameters params =  new OAuth2Parameters();
			params.setRedirectUri(buildReturnToUrl(request));
			setScope(request, params);
			params.add("state", connectionFactory.generateState()); // TODO: Verify the state value after callback
			throw new SocialAuthenticationRedirectException(getConnectionFactory().getOAuthOperations().buildAuthenticateUrl(params));
		} else if (StringUtils.hasText(code)) {
			try {
				String returnToUrl = buildReturnToUrl(request);
				AccessGrant accessGrant = getConnectionFactory().getOAuthOperations().exchangeForAccess(code, returnToUrl, null);
				// TODO avoid API call if possible (auth using token would be fine)
				Connection<S> connection = getConnectionFactory().createConnection(accessGrant);
				return new SocialAuthenticationToken(connection, null);
			} catch (RestClientException e) {
				logger.debug("failed to exchange for access", e);
				return null;
			}
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

	private void setScope(HttpServletRequest request, OAuth2Parameters params) {
		String requestedScope = request.getParameter("scope");
		if (StringUtils.hasLength(requestedScope)) {
			params.setScope(requestedScope);
		} else if (StringUtils.hasLength(defaultScope)) {
			params.setScope(defaultScope);
		}
	}

}
