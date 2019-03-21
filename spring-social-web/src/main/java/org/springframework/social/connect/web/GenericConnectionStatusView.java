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
package org.springframework.social.connect.web;

import static org.springframework.web.util.HtmlUtils.htmlEscape;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.util.UriUtils;

/**
 * <p>
 * Generic {@link View} implementation that displays the connection statis for a provider.
 * Can be used as both a connected view and a disconnected view.
 * </p>
 * 
 * <p>
 * Configure as a bean whose name is "connect/{providerId}Connect" and/or "connect/{providerId}Connected".
 * For example:
 * </p>
 * 
 * <code>
 * &#064;Bean(name={"connect/facebookConnect", "connect/facebookConnected"})
 * public View facebookConnectView() {
 * 	return new GenericConnectionStatusView("facebook", "Facebook");
 * }
 * </code>
 * 
 * @author Craig Walls
 */
public class GenericConnectionStatusView extends AbstractView {
	
	private final String providerId;
	
	private final String providerDisplayName;

	/**
	 * Constructs the generic status view.
	 * @param providerId the provider Id (e.g., "facebook")
	 * @param providerDisplayName a friendly, displayable name for the provider (e.g., "Facebook") 
	 */
	public GenericConnectionStatusView(String providerId, String providerDisplayName) {
		this.providerId = providerId;
		this.providerDisplayName = providerDisplayName;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-Type", "text/html");
		response.getWriter().write(GenericConnectionStatusHtmlCreator.generateConnectionViewHtml(
				providerDisplayName, providerId, getProfileIfConnected(model)));
	}



	private UserProfile getProfileIfConnected(Map<String, Object> model) {
		@SuppressWarnings("unchecked")
		List<Connection<?>> connections = (List<Connection<?>>) model.get("connections");
		if (connections != null) {
			for (Connection<?> connection : connections) {
				if (connection.getKey().getProviderId().equals(providerId)) {
					return connection.fetchUserProfile();
				}
			}
		}
		return null;
	}
	
	public static class GenericConnectionStatusHtmlCreator {
		public static String generateConnectionViewHtml(String providerDisplayName, String providerId, UserProfile profile) {
			String escProviderDisplayName = escape(providerDisplayName);
			StringBuilder builder = new StringBuilder();
			if (profile == null) {
				builder.append("<h3>Connect to " + escProviderDisplayName + "</h3>");
				builder.append("<form action=\"/connect/");
				
				try {
					String escProviderId = UriUtils.encodePath(providerId, "UTF-8");
					builder.append(escProviderId);
				} catch (UnsupportedEncodingException e) { /* Default to UTF-8...should be fine.*/ }

				builder.append("\" method=\"POST\">");
				builder.append("<div class=\"formInfo\">");
				builder.append("<p>You aren't connected to ");
				builder.append(escProviderDisplayName);
				builder.append(" yet. Click the button to connect with your ");
				builder.append(escProviderDisplayName);
				builder.append(" account.</p>");
				builder.append("</div>");
				builder.append("<p><button type=\"submit\">Connect to ");
				builder.append(escProviderDisplayName);
				builder.append("</button></p>");
				builder.append("</form>");
			} else {
				String escProfileName = escape(profile.getName());
				builder.append("<h3>Connected to ");
				builder.append(escProviderDisplayName);
				builder.append("</h3>");
				builder.append("<p>Hello, ");
				builder.append(escProfileName);
				builder.append("!</p><p>You are now connected to ");
				builder.append(escProviderDisplayName);
				String username = profile.getUsername();
				if (username !=null) {
					builder.append(" as ");
					builder.append(escape(username));
				}
				builder.append(".</p>");
			}
			return builder.toString();
		}
		
		private static String escape(String in) {
			return htmlEscape(in).replaceAll("\\{", "&#123;").replaceAll("\\}", "&#125;");
		}
	}
}
