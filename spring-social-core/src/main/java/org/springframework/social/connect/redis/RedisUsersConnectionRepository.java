package org.springframework.social.connect.redis;

import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.redis.data.SocialRedisConnection;
import org.springframework.social.connect.redis.data.SocialRedisConnectionRepository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RedisUsersConnectionRepository implements UsersConnectionRepository {

    private final ConnectionFactoryLocator connectionFactoryLocator;
    private final TextEncryptor textEncryptor;
    private final SocialRedisConnectionRepository socialRedisConnectionRepository;

    public RedisUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator, TextEncryptor textEncryptor, SocialRedisConnectionRepository socialRedisConnectionRepository) {
        Assert.notNull(connectionFactoryLocator);
        Assert.notNull(textEncryptor);
        Assert.notNull(socialRedisConnectionRepository);

        this.connectionFactoryLocator = connectionFactoryLocator;
        this.textEncryptor = textEncryptor;
        this.socialRedisConnectionRepository = socialRedisConnectionRepository;
    }

    public List<String> findUserIdsWithConnection(final Connection<?> connection) {
        String providerId = connection.getKey().getProviderId();
        String providerUserId = connection.getKey().getProviderUserId();

        Iterable<SocialRedisConnection> connections = socialRedisConnectionRepository.findByProviderIdAndProviderUserId(providerId, providerUserId);

        List<String> userIds = new ArrayList<String>();
        for (SocialRedisConnection socialRedisConnection : connections) {
            userIds.add(socialRedisConnection.getUserId());
        }

        return userIds;
    }

    public Set<String> findUserIdsConnectedTo(final String providerId, final Set<String> providerUserIds) {
        Set<String> userIds = new HashSet<String>();

        for (String providerUserId : providerUserIds) {
            for (SocialRedisConnection socialRedisConnection : socialRedisConnectionRepository.findByProviderIdAndProviderUserId(providerId, providerUserId)) {
                userIds.add(socialRedisConnection.getUserId());
            }
        }

        return userIds;
    }

    public ConnectionRepository createConnectionRepository(final String userId) {
        return new RedisConnectionRepository(connectionFactoryLocator, textEncryptor, socialRedisConnectionRepository, userId);
    }
}
