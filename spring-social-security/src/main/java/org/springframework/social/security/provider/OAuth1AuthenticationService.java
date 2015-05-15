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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuth1Version;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Stefan Fussennegger
 * @param <S> The provider's API type.
 */
public class OAuth1AuthenticationService<S> extends AbstractSocialAuthenticationService<S> implements InitializingBean {

	private final Log logger = LogFactory.getLog(getClass());
	
	private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";

	private Set<String> returnToUrlParameters;
	
	private OAuth1ConnectionFactory<S> connectionFactory;

	public OAuth1AuthenticationService(OAuth1ConnectionFactory<S> connectionFactory) {
		setConnectionFactory(connectionFactory);
	}
	
	public OAuth1ConnectionFactory<S> getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(OAuth1ConnectionFactory<S> connectionFactory) {
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

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(getConnectionFactory(), "connectionFactory");
	}

	public SocialAuthenticationToken getAuthToken(HttpServletRequest request, HttpServletResponse response) throws SocialAuthenticationRedirectException {
		/**
		 * OAuth Authentication flow: See http://dev.twitter.com/pages/auth
		 */
		String verifier = request.getParameter("oauth_verifier");
		if (!StringUtils.hasText(verifier)) {
			// First phase: get a request token
			OAuth1Operations ops = getConnectionFactory().getOAuthOperations();
			String returnToUrl = buildReturnToUrl(request);
			OAuthToken requestToken = ops.fetchRequestToken(returnToUrl, null);
			request.getSession().setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken);

			// Redirect to the service provider for authorization
			OAuth1Parameters params;
			if (ops.getVersion() == OAuth1Version.CORE_10) {
				params = new OAuth1Parameters();
				params.setCallbackUrl(returnToUrl);
			} else {
				params = OAuth1Parameters.NONE;
			}			
			throw new SocialAuthenticationRedirectException(ops.buildAuthenticateUrl(requestToken.getValue(), params));
		} else {
			// Second phase: request an access token
			OAuthToken requestToken = extractCachedRequestToken(request);
			if (requestToken == null) {
				logger.warn("requestToken unavailable for oauth_verifier");
				return null;
			}
			OAuthToken accessToken = getConnectionFactory().getOAuthOperations().exchangeForAccessToken(new AuthorizedRequestToken(requestToken, verifier), null);
			// TODO avoid API call if possible (auth using token would be fine)
            Connection<S> connection = getConnectionFactory().createConnection(accessToken);
            return new SocialAuthenticationToken(connection, null);
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

	private OAuthToken extractCachedRequestToken(HttpServletRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getSession().getAttribute(OAUTH_TOKEN_ATTRIBUTE);
		request.getSession().removeAttribute(OAUTH_TOKEN_ATTRIBUTE);
		return requestToken;
	}

}
