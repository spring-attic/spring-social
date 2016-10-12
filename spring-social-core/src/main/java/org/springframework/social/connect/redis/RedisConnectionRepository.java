package org.springframework.social.connect.redis;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.*;
import org.springframework.social.connect.redis.data.SocialRedisConnection;
import org.springframework.social.connect.redis.data.SocialRedisConnectionRepository;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RedisConnectionRepository implements ConnectionRepository {

    private final ConnectionFactoryLocator connectionFactoryLocator;
    private final TextEncryptor textEncryptor;
    private final SocialRedisConnectionRepository socialRedisConnectionRepository;
    private final String userId;

    public RedisConnectionRepository(final ConnectionFactoryLocator connectionFactoryLocator, final TextEncryptor textEncryptor, final SocialRedisConnectionRepository socialRedisConnectionRepository, final String userId) {
        Assert.notNull(socialRedisConnectionRepository, "socialRedisConnectionRepository is required");
        Assert.notNull(userId, "userId is required");

        this.userId = userId;
        this.socialRedisConnectionRepository = socialRedisConnectionRepository;
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.textEncryptor = textEncryptor;
    }

    public MultiValueMap<String, Connection<?>> findAllConnections() {
        Iterable<SocialRedisConnection> allConnections = socialRedisConnectionRepository.findByUserId(userId);

        final MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<String, Connection<?>>();
        Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
        for (String registeredProviderId : registeredProviderIds) {
            connections.put(registeredProviderId, new ArrayList<Connection<?>>());
        }

        for (SocialRedisConnection connection : allConnections) {
            connections.add(connection.getProviderId(), connectionMapper.mapConnection(connection));
        }

        return connections;
    }

    public List<Connection<?>> findConnections(String providerId) {
        Iterable<SocialRedisConnection> connections = socialRedisConnectionRepository.findByProviderId(providerId);

        List<Connection<?>> providerConnections = new ArrayList<Connection<?>>();
        for (SocialRedisConnection connection : connections) {
            providerConnections.add(connectionMapper.mapConnection(connection));
        }

        return providerConnections;
    }

    public <A> List<Connection<A>> findConnections(Class<A> apiType) {
        List<?> connections = findConnections(getProviderId(apiType));
        return (List<Connection<A>>) connections;
    }

    public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
        return null;
    }

    public Connection<?> getConnection(ConnectionKey connectionKey) {
        try {
            return connectionMapper.mapConnection(socialRedisConnectionRepository.findOneByUserIdAndProviderIdAndProviderUserId(userId, connectionKey.getProviderId(), connectionKey.getProviderUserId()));
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchConnectionException(connectionKey);
        }
    }

    public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
        String providerId = getProviderId(apiType);
        return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
    }

    public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
        String providerId = getProviderId(apiType);
        Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
        if (connection == null) {
            throw new NotConnectedException(providerId);
        }
        return connection;
    }

    public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
        String providerId = getProviderId(apiType);
        return (Connection<A>) findPrimaryConnection(providerId);
    }

    public void addConnection(Connection<?> connection) {
        try {
            ConnectionData data = connection.createData();
            SocialRedisConnection redisConnection = new SocialRedisConnection(data.getProviderUserId(), userId, data.getProviderId(), data.getDisplayName(), data.getProfileUrl(), data.getImageUrl(), encrypt(data.getAccessToken()), encrypt(data.getSecret()), encrypt(data.getRefreshToken()), data.getExpireTime());
            socialRedisConnectionRepository.save(redisConnection);
        } catch (Exception e) {
            throw new DuplicateConnectionException(connection.getKey());
        }
    }

    public void updateConnection(Connection<?> connection) {
        ConnectionData data = connection.createData();
        SocialRedisConnection redisConnection = socialRedisConnectionRepository.findOneByUserIdAndProviderIdAndProviderUserId(userId, data.getProviderId(), data.getProviderUserId());

        redisConnection.setDisplayName(data.getDisplayName());
        redisConnection.setImageUrl(data.getImageUrl());
        redisConnection.setProfileUrl(data.getProfileUrl());
        redisConnection.setAccessToken(encrypt(data.getAccessToken()));
        redisConnection.setSecret(encrypt(data.getSecret()));
        redisConnection.setRefreshToken(encrypt(data.getRefreshToken()));
        redisConnection.setExpireTime(data.getExpireTime());

        socialRedisConnectionRepository.save(redisConnection);
    }

    public void removeConnections(String providerId) {
        Iterable<SocialRedisConnection> connections = socialRedisConnectionRepository.findByUserIdAndProviderId(userId, providerId);

        for (SocialRedisConnection redisConnection : connections) {
            socialRedisConnectionRepository.delete(redisConnection);
        }
    }

    public void removeConnection(ConnectionKey connectionKey) {
        // TODO: Wait for DATAKV-135 in order to use this:
        // socialRedisConnectionRepository.deleteByUserIdAndProviderIdAndProviderUserId
        // (userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());

        SocialRedisConnection socialRedisConnection = socialRedisConnectionRepository.findOneByUserIdAndProviderIdAndProviderUserId(userId, connectionKey.getProviderId(), connectionKey.getProviderUserId());
        socialRedisConnectionRepository.delete(socialRedisConnection);
    }

    private final RedisConnectionMapper connectionMapper = new RedisConnectionMapper();

    private final class RedisConnectionMapper {

        Connection<?> mapConnection(final SocialRedisConnection redisConnection) {
            if (redisConnection == null) {
                throw new EmptyResultDataAccessException(1);
            }
            ConnectionData connectionData = mapConnectionData(redisConnection);
            ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId());
            return connectionFactory.createConnection(connectionData);
        }

        private ConnectionData mapConnectionData(final SocialRedisConnection redisConnection) {
            return new ConnectionData(redisConnection.getProviderId(), redisConnection.getProviderUserId(), redisConnection.getDisplayName(), redisConnection.getProfileUrl(), redisConnection.getImageUrl(),
                    decrypt(redisConnection.getAccessToken()), decrypt(redisConnection.getSecret()), decrypt(redisConnection.getRefreshToken()), redisConnection.getExpireTime());
        }

        private String decrypt(String encryptedText) {
            return encryptedText == null ? null : textEncryptor.decrypt(encryptedText);
        }
    }

    private Connection<?> findPrimaryConnection(String providerId) {
        Iterable<SocialRedisConnection> redisConnections = socialRedisConnectionRepository.findByUserIdAndProviderId(userId, providerId);

        List<Connection<?>> primaryConnections = new ArrayList<Connection<?>>();
        for (SocialRedisConnection connection : redisConnections) {
            primaryConnections.add(connectionMapper.mapConnection(connection));
        }

        if (primaryConnections.size() > 0) {
            return primaryConnections.get(0);
        } else {
            return null;
        }
    }

    private <A> String getProviderId(Class<A> apiType) {
        return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
    }

    private String encrypt(String text) {
        return text == null ? null : textEncryptor.encrypt(text);
    }
}
