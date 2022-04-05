/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.social.connect.Connection;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Stefan Fussennegger
 * @param <S> The provider API type.
 */
public abstract class AbstractSocialAuthenticationService<S> implements SocialAuthenticationService<S>, InitializingBean {

	private ConnectionCardinality connectionCardinality = ConnectionCardinality.ONE_TO_ONE;

	private String connectionAddedRedirectUrl;

	private Set<String> returnToUrlParameters;

	public void afterPropertiesSet() throws Exception {
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

	protected String buildReturnToUrl(HttpServletRequest request) {
		StringBuffer sb = getProxyHeaderAwareRequestURL(request);
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

	protected StringBuffer getProxyHeaderAwareRequestURL(HttpServletRequest request) {
		String host = request.getHeader("Host");
		if (StringUtils.isEmpty(host)) {
			return request.getRequestURL();
		}
		StringBuffer sb = new StringBuffer();
		String schemeHeader = request.getHeader("X-Forwarded-Proto");
		String portHeader = request.getHeader("X-Forwarded-Port");
		String scheme = StringUtils.isEmpty(schemeHeader) ? request.getScheme()  : schemeHeader;
		String port = StringUtils.isEmpty(portHeader) ? "" : portHeader;
		if (scheme.equals("http") && port.equals("80")) {
			port = "";
		}
		if (scheme.equals("https") && port.equals("443")) {
			port = "";
		}
		sb.append(scheme);
		sb.append("://");
		sb.append(host);
		if (StringUtils.hasLength(port)) {
			sb.append(":");
			sb.append(port);
		}
		sb.append(request.getRequestURI());
		return sb;
	}

}
