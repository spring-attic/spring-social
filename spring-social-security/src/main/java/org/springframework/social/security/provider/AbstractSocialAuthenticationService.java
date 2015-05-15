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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.social.connect.Connection;

/**
 * @author Stefan Fussennegger
 * @param <S> The provider API type.
 */
public abstract class AbstractSocialAuthenticationService<S> implements SocialAuthenticationService<S>, InitializingBean {

	private ConnectionCardinality connectionCardinality = ConnectionCardinality.ONE_TO_ONE;

	private String connectionAddedRedirectUrl;

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

}
