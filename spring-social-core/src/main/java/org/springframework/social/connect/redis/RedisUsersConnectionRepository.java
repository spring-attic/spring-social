package org.springframework.social.connect.redis;

import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.redis.data.SocialRedisConnectionRepository;
import org.springframework.util.Assert;

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
        return null;
    }

    public Set<String> findUserIdsConnectedTo(final String providerId, final Set<String> providerUserIds) {
        return null;
    }

    public ConnectionRepository createConnectionRepository(final String userId) {
        return null;
    }
}
