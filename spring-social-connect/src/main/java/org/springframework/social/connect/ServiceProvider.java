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
package org.springframework.social.connect;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;


/**
 * Models the provider of a service that local member accounts may connect to and invoke.
 * Exposes service provider metadata along with connection management operations that allow for account connections to be established.
 * Also acts as a factory for a strongly-typed service API (S).
 * Once a connection with this provider is established, the service API can be used by the application to invoke the service on behalf of the member.
 * @author Keith Donald
 * @param <S> The service API hosted by this service provider.
 */
public interface ServiceProvider<S> {

	// provider meta-data
	
	/**
	 * The unique name or id of the service provider e.g. twitter.
	 * Unique across all service providers.
	 */
	String getName();
	
	/**
	 * A label suitable for display in a UI, typically used to inform the user which service providers he or she has connected with / may connect with. e.g. Twitter.
	 */
	String getDisplayName();

	/**
	 * The key used to identify the local application with the remote service provider.
	 * Used when establishing an account connection with the service provider.
	 * Available as a public property to support client code that wishes to manage the service connection process itself, for example, in JavaScript.
	 * The term "API key" is derived from the OAuth 2 specification. 
	 */
	String getApiKey();
	
	/**
	 * An alternate identifier for the local application in the remote service provider's system.
	 * May be null of no such alternate identifier exists.
	 * Used by ServiceProvider&lt;FacebookOperations&gt; to support "Like" functionality.
	 * @return an alternate app id, or null if no alternate id exists (null is the typical case, as the {@link #getApiKey()} is the primary means of consumer identification)
	 */
	Long getAppId();

	// connection management

	/**
	 * Begin the account connection process by fetching a new request token from
	 * this service provider. The new token should be stored in the member's
	 * session up until the authorization callback is made and it's time to
	 * {@link #connect(Serializable, AuthorizedRequestToken) connect}.
	 * 
	 * @param callbackUrl
	 *            the URL the provider should redirect to after the member
	 *            authorizes the connection (may be null for OAuth 1.0-based
	 *            service providers)
	 */
	OAuthToken fetchNewRequestToken(String callbackUrl);

	/**
	 * Construct the URL to redirect the member to for OAuth connection
	 * authorization.
	 * 
	 * @param authorizationParameters
	 *            Values to be plugged into the authorization URL. In the case
	 *            of OAuth 1 authorization, this should be the value of the
	 *            request token. For OAuth 2 it should include the application's
	 *            redirect URL and optionally the authorization scope.
	 * @return the absolute authorize URL to redirect the member to for
	 *         authorization
	 */
	String buildAuthorizeUrl(Map<String, String> authorizationParameters);

	/**
	 * Connects a member account to this OAuth 1 service provider. Called after
	 * the user authorizes the connection at the {@link #buildAuthorizeUrl(Map)
	 * authorizeUrl} and the service provider calls us back. Internally,
	 * exchanges the authorized request token for an access token, then stores
	 * the awarded access token with the member account. This access token
	 * identifies the connection between the member account and this service
	 * provider.
	 * <p>
	 * This method completes the OAuth-based account connection process.
	 * {@link #getServiceOperations(Serializable)} may now be called to get and
	 * invoke the service provider's API. The requestToken required during the
	 * connection handshake is no longer valid and cannot be reused.
	 * 
	 * @param accountId
	 *            the application account ID that the connection will be made
	 *            with.
	 * @param requestToken
	 *            the OAuth request token that was authorized by the member.
	 */
	void connect(Serializable accountId, AuthorizedRequestToken requestToken);

	/**
	 * Fetches an access token from the OAuth 1 provider, but does not create a
	 * connection between the member account and the account at the provider.
	 * This method is useful when you don't want to persist the connection or
	 * when you want to create the connection at a later time (such as in the
	 * registration/connect flow).
	 * 
	 * @param requestToken
	 *            an authorized request token and verifier
	 * @return an access token granted by the provider
	 */
	OAuthToken fetchAccessToken(AuthorizedRequestToken requestToken);

	/**
	 * Connects a member account to this OAuth 2 service provider. Called after
	 * the user authorizes the connection at the {@link #buildAuthorizeUrl(Map)
	 * authorizeUrl} and the service provider calls us back. Internally,
	 * exchanges the code given by the provider for an access token, then stores
	 * the awarded access token with the member account. This access token
	 * identifies the connection between the member account and this service
	 * provider.
	 * <p>
	 * This method completes the OAuth2-based account connection process.
	 * {@link #getServiceOperations(Serializable)} may now be called to get and
	 * invoke the service provider's API.
	 * 
	 * @param accountId
	 *            the application account ID that the connection will be made
	 *            with.
	 * @param redirectUri
	 *            the redirectUri that the application has registered with the
	 *            provider
	 * @param code
	 *            the authorization code granted by the provider after the user
	 *            agrees to the requested permissions at authorization
	 */
	void connect(Serializable accountId, String redirectUri, String code);

	/**
	 * Fetches an access token from the OAuth 2 provider, but does not create a
	 * connection between the member account and the account at the provider.
	 * This method is useful when you don't want to persist the connection or
	 * when you want to create the connection at a later time (such as in the
	 * registration/connect flow).
	 * 
	 * @param redirectUri
	 *            the redirectUri that the application has registered with the
	 *            provider
	 * @param code
	 *            the authorization code granted by the provider after the user
	 *            agrees to the requested permissions at authorization
	 * @return an access token granted by the provider
	 */
	OAuthToken fetchAccessToken(String redirectUri, String code);

	/**
	 * Creates a connection between a member account ID and a provider, given an
	 * access token granted by that provider.
	 * 
	 * @param accountId
	 *            the application account ID that the connection will be made
	 *            with.
	 * @param accessToken
	 *            an access token granted by the provider.
	 */
	void connect(Serializable accountId, OAuthToken accessToken);

	/**
	 * Records an existing connection between a member account and this service
	 * provider. Use when the connection process happens outside of the control
	 * of this package; for example, in JavaScript.
	 * 
	 * @param accountId
	 *            the application account ID that the connection will be made
	 *            with.
	 * @param accessToken
	 *            the access token that was granted as a result of the
	 *            connection
	 * @param providerAccountId
	 *            the id of the user in the provider's system; may be an
	 *            assigned number or a user-selected screen name.
	 */
	void addConnection(Serializable accountId, String accessToken, String providerAccountId);

	/**
	 * Returns true if the member account has any connections to this provider,
	 * false otherwise.
	 * 
	 * @param accountId
	 *            the application account ID to check for a connection with this
	 *            provider.
	 */
	boolean isConnected(Serializable accountId);

	/**
	 * Returns true if the member account is connected to a specific profile on
	 * this provider, false otherwise.
	 * 
	 * @param accountId
	 *            the application account ID to check for a connection with this
	 *            provider.
	 * @param providerAcountId
	 *            the id of the user in the provider's system.
	 */
	boolean isConnected(Serializable accountId, String providerAcountId);

	/**
	 * <p>
	 * Refreshes a connection whose access token has expired.
	 * </p>
	 * 
	 * <p>
	 * Only applicable for OAuth 2 providers that support refresh tokens.
	 * </p>
	 * 
	 * @throws UnsupportedOperationException
	 *             if the provider doesn't support connection refresh
	 * 
	 * @param accountId
	 *            the account ID on the application side of the connection.
	 * @param providerAccountId
	 *            the account ID on the provider side of the connection.
	 */
	void refreshConnection(Serializable accountId, String providerAccountId);

	/**
	 * <p>
	 * Gets a handle to the API offered by this service provider. This API may
	 * be used by the application to invoke the service on behalf of a member.
	 * </p>
	 * 
	 * <p>
	 * This method assumes that the user has established a connection with the
	 * provider via the connect() method and will create the operations instance
	 * based on that previously created connection. In the case where the user
	 * has established multiple connections with the provider, the first one
	 * found will be used to create the service operations instance.
	 * </p>
	 * 
	 * @param accountId
	 *            the application account ID to retrieve the service operations
	 *            for.
	 */
	S getServiceOperations(Serializable accountId);

	/**
	 * <p>
	 * Gets a handle to the API offered by this service provider for a given
	 * access token. This API may be used by the application to invoke the
	 * service on behalf of a member.
	 * </p>
	 * 
	 * <p>
	 * This method does not assume that a connection has been previously made
	 * through the connect() method.
	 * </p>
	 * 
	 * @param accessToken
	 *            An access token through which the service operations will be
	 *            granted authority to the provider.
	 */
	S getServiceOperations(OAuthToken accessToken);

	/**
	 * <p>
	 * Gets a handle to the API offered by this service provider. This API may
	 * be used by the application to invoke the service on behalf of a member.
	 * </p>
	 * 
	 * <p>
	 * This method assumes that the user has established a connection with the
	 * provider via the connect() method and will create the operations instance
	 * based on that previously created connection. The providerAccountId
	 * parameter is the user's identity on the service provider, used to
	 * identify the specific connection among (potentially) many connections
	 * between a single user and a service provider (e.g., a single application
	 * user may have multiple Twitter identities).
	 * </p>
	 * 
	 * @param accountId
	 *            the application account ID to retrieve the service operations
	 *            for.
	 * @param providerAccountId
	 *            the user's account ID on the service provider, used to select
	 *            a specific connection in the case where the user may have
	 *            created multiple connections for a single provider.
	 * @return an object providing operations to perform against the provider.
	 */
	S getServiceOperations(Serializable accountId, String providerAccountId);

	/**
	 * Retrieves all connections that the user has made with the provider.
	 * Commonly, this collection would contain a single entry, but it is
	 * possible that the user may have multiple profiles on a provider and has
	 * created connections for all of them.
	 * 
	 * @param accountId
	 *            the application account ID to retrieve connections for.
	 * 
	 * @return a collection of {@link AccountConnection}s that the user has
	 *         established with the provider.
	 */
	Collection<AccountConnection> getConnections(Serializable accountId);

	/**
	 * Severs all connections between the member account and this service
	 * provider. Has no effect if no connection is established to begin with.
	 * 
	 * @param accountId
	 *            the application account ID for which all connections with this
	 *            provider should be severed.
	 */
	void disconnect(Serializable accountId);

	/**
	 * Severs a specific connection between the member account and this service
	 * provider.
	 * 
	 * @param accountId
	 *            the application account ID for which the connection will be
	 *            severed.
	 * @param providerAccountId
	 *            the user's account ID on the service provider to select which
	 *            (potentially among many) connections to sever.
	 */
	void disconnect(Serializable accountId, String providerAccountId);

	// additional finders

	/**
	 * The id of the member in the provider's system.
	 * May be an assigned internal identifier, such as a sequence number, or a user-selected screen name.
	 * Generally unique across accounts registered with this provider.
	 */
	String getProviderAccountId(Serializable accountId);

	/**
	 * Retrieves an object containing the user's profile information from the
	 * provider.
	 * 
	 * @param accessToken
	 *            the access token used to lookup the profile information.
	 * @return a {@link Serializable} object containing the user's provider
	 *         profile data.
	 */
	Serializable getProviderUserProfile(OAuthToken accessToken);

	/**
	 * The style of authorization supported by this service provider (e.g.,
	 * OAuth 1, OAuth 2)
	 * 
	 * @return the authorization type
	 */
	AuthorizationStyle getAuthorizationStyle();
}