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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.web.FacebookCookieParser;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * authenticate via Facebook Connect cookies
 * 
 * @author stf@molindo.at
 */
public class FacebookCookieAuthenticationService extends AbstractSocialAuthenticationService<Facebook> implements InitializingBean {

	private OAuth2AuthenticationService<Facebook> oAuthService;
	private OAuth2ConnectionFactory<Facebook> connectionFactory;

	private String appId;
	private String appSecret;
	private boolean oAuthEnabled = true;
	
	public FacebookCookieAuthenticationService() {
		super(null);
	}
	
	public FacebookCookieAuthenticationService(OAuth2ConnectionFactory<Facebook> connectionFactory) {
		this();
		setConnectionFactory(connectionFactory);
	}
	
	public FacebookCookieAuthenticationService(OAuth2ConnectionFactory<Facebook> connectionFactory, String appId, String appSecret) {
		this(connectionFactory);
		setAppId(appId);
		setAppSecret(appSecret);
	}
	
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		Assert.notNull(getConnectionFactory(), "connectionFactory not configured");
		Assert.notNull(appId, "appId not configured");
		Assert.notNull(appSecret, "appSecret not configured");
		
		if (isOAuthEnabled() && getOAuthService() == null) {
			setOAuthService(new FacebookAuthenticationService(getConnectionFactory()));
		}
	}

	public SocialAuthenticationToken getAuthToken(AuthenticationMode authMode, final HttpServletRequest request,
			final HttpServletResponse response) {

		// always try simple implicit auth first
		SocialAuthenticationToken token = getCookieAuthToken(request, response);

		if (token == null && authMode == AuthenticationMode.EXPLICIT && isOAuthEnabled()) {
			token = getOAuthService().getAuthToken(authMode, request, response);
		}
		
		return token;
	}

	protected SocialAuthenticationToken getCookieAuthToken(final HttpServletRequest request,
			HttpServletResponse response) {

		final OAuth2ConnectionFactory<Facebook> connectionFactory = getConnectionFactory();

		final Map<String, String> fbParams = getFbParams(request, appId, appSecret);
		if (fbParams.isEmpty()) {
			return null;
		}

		final String uid = fbParams.get("uid");
		final String accessToken = fbParams.get("access_token");
		final String expiresStr = fbParams.get("expires");
		final Long expires = StringUtils.hasText(expiresStr) ? Long.parseLong(expiresStr) * 1000L : null;

		if (StringUtils.hasText(uid) && StringUtils.hasText(accessToken)) {
			// no need to create connections since we have everything we need to
			// identify user
			// use verify() before saving connection though
			ConnectionData data = new ConnectionData(connectionFactory.getProviderId(), uid, null, null, null,
					accessToken, null, null, expires);

			return new SocialAuthenticationToken(data, fbParams);
		} else {
			return null;
		}
	}

	protected Map<String, String> getFbParams(final HttpServletRequest request, String appId, String appSecret) {
		Map<String, String> params = FacebookCookieParser.getFacebookCookieData(request.getCookies(), appId,
				appSecret);
		if (params == null) {
			// TODO fallback to request parameters
		}
		return params;
	}

	public void setConnectionFactory(OAuth2ConnectionFactory<Facebook> connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public OAuth2ConnectionFactory<Facebook> getConnectionFactory() {
		if (connectionFactory == null && oAuthService != null) {
			return oAuthService.getConnectionFactory();
		}
		return connectionFactory;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public boolean isOAuthEnabled() {
		return oAuthEnabled;
	}

	public void setOAuthEnabled(boolean oAuthEnabled) {
		this.oAuthEnabled = oAuthEnabled;
	}

	public OAuth2AuthenticationService<Facebook> getOAuthService() {
		return oAuthService;
	}

	public void setOAuthService(OAuth2AuthenticationService<Facebook> oAuthService) {
		this.oAuthService = oAuthService;
	}


}
