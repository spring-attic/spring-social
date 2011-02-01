package org.springframework.social.showcase.twitter;

import org.springframework.social.connect.ServiceProvider;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.twitter.DuplicateTweetException;
import org.springframework.social.twitter.TwitterOperations;
import org.springframework.social.web.connect.ConnectInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequest;

public class TweetAfterConnectInterceptor implements ConnectInterceptor<TwitterOperations> {

	public void preConnect(ServiceProvider<TwitterOperations> provider, WebRequest request) {
		if (StringUtils.hasText(request.getParameter(POST_TWEET_PARAMETER))) {
			request.setAttribute(POST_TWEET_ATTRIBUTE, Boolean.TRUE, WebRequest.SCOPE_SESSION);
		}
	}

	public void postConnect(ServiceProvider<TwitterOperations> provider,
			ServiceProviderConnection<TwitterOperations> connection, WebRequest request) {
		if (request.getAttribute(POST_TWEET_ATTRIBUTE, WebRequest.SCOPE_SESSION) != null) {
			try {
				connection.getServiceApi().updateStatus("I've connected with the Spring Social Showcase!");
			} catch (DuplicateTweetException e) {
			}
			request.removeAttribute(POST_TWEET_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		}
	}

	private static final String POST_TWEET_PARAMETER = "postTweet";

	private static final String POST_TWEET_ATTRIBUTE = "twitterConnect." + POST_TWEET_PARAMETER;
}
