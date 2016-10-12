package org.springframework.social.connect.redis;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.redis.data.SocialRedisConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.JedisShardInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@RedisAvailable
public class RedisUsersConnectionRepositoryIntegrationTests {

    private static final String REDIS_HOST = "127.0.0.1";
    private static final int REDIS_PORT = 6379;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private SocialRedisConnectionRepository socialRedisConnectionRepository;

    @ClassRule
    public static TestRule redisAvailableRule = new RedisAvailableRule(REDIS_HOST, REDIS_PORT);

    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    private RedisUsersConnectionRepository redisUsersConnectionRepository;

    private RedisUtils.TestConnection connectionForDhubau;
    private RedisUtils.TestConnection connectionForMyBuddy;

    @Before
    public void init() {
        redisUsersConnectionRepository = new RedisUsersConnectionRepository(connectionFactoryLocator, Encryptors.noOpText(), socialRedisConnectionRepository);
        connectionForDhubau = new RedisUtils.TestConnection(new RedisUtils.TestTwitterApiAdapter(), "twitter:dhubau");
        connectionForMyBuddy = new RedisUtils.TestConnection(new RedisUtils.TestTwitterApiAdapter(), "twitter:buddy");
        socialRedisConnectionRepository.deleteAll();
    }

    @Test
    public void findUserIdsWithConnectionNoUsers() {
        List<String> userIds = redisUsersConnectionRepository.findUserIdsWithConnection(connectionForDhubau);

        assertEquals(0, userIds.size());
    }

    @Test
    public void findUserIdsWithConnectionOneUser() {
        ConnectionRepository connectionRepository = redisUsersConnectionRepository.createConnectionRepository("dhubau");
        connectionRepository.addConnection(connectionForDhubau);
        connectionRepository.addConnection(connectionForMyBuddy);

        List<String> userIds = redisUsersConnectionRepository.findUserIdsWithConnection(connectionForDhubau);

        assertEquals(1, userIds.size());
    }

    @Test
    public void findUserIdsConnectedToWithTwoUsers() {
        ConnectionRepository connectionRepository = redisUsersConnectionRepository.createConnectionRepository("dhubau");
        connectionRepository.addConnection(connectionForDhubau);
        connectionRepository = redisUsersConnectionRepository.createConnectionRepository("buddy");
        connectionRepository.addConnection(connectionForMyBuddy);

        Set<String> providerUserIds = new HashSet<>();
        Collections.addAll(providerUserIds, "twitter:dhubau", "twitter:buddy");
        Set<String> userIds = redisUsersConnectionRepository.findUserIdsConnectedTo("twitter", providerUserIds);

        assertEquals(2, userIds.size());
    }

    @Configuration
    @EnableRedisRepositories(includeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*SocialRedisConnectionRepository")})
    static class Config {

        @Bean
        RedisTemplate<?, ?> redisTemplate() {
            JedisConnectionFactory connectionFactory = new JedisConnectionFactory(new JedisShardInfo(REDIS_HOST, REDIS_PORT));
            connectionFactory.afterPropertiesSet();

            RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);

            return template;
        }

        @Bean
        public ConnectionFactoryLocator connectionFactoryLocator() {
            ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
            registry.addConnectionFactory(new RedisUtils.TestTwitterConnectionFactory());
            return registry;
        }
    }
}
