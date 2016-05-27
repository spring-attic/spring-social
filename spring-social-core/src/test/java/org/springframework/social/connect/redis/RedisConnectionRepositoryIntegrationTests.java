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
import org.springframework.social.connect.*;
import org.springframework.social.connect.redis.data.SocialRedisConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;
import redis.clients.jedis.JedisShardInfo;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@RedisAvailable
public class RedisConnectionRepositoryIntegrationTests {

    private static final String REDIS_HOST = "127.0.0.1";
    private static final int REDIS_PORT = 6379;

    @ClassRule
    public static TestRule redisAvailableRule = new RedisAvailableRule(REDIS_HOST, REDIS_PORT);

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private SocialRedisConnectionRepository socialRedisConnectionRepository;

    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    private RedisConnectionRepository redisConnectionRepository;

    private RedisUtils.TestConnection testConnection;

    @Before
    public void init() {
        redisConnectionRepository = new RedisConnectionRepository(connectionFactoryLocator, Encryptors.noOpText(), socialRedisConnectionRepository, "Turbots");
        testConnection = new RedisUtils.TestConnection(new RedisUtils.TestTwitterApiAdapter(), "twitter:dhubau");
        socialRedisConnectionRepository.deleteAll();
    }

    @Test
    public void addConnection() {
        redisConnectionRepository.addConnection(testConnection);

        MultiValueMap<String, Connection<?>> map = redisConnectionRepository.findAllConnections();

        assertEquals(1, map.get("twitter").size());
    }

    @Test
    public void removeConnections() {
        redisConnectionRepository.addConnection(testConnection);

        redisConnectionRepository.removeConnections("twitter");

        MultiValueMap<String, Connection<?>> map = redisConnectionRepository.findAllConnections();

        assertEquals(0, map.get("twitter").size());
    }

    @Test
    public void removeConnection() {
        redisConnectionRepository.addConnection(testConnection);

        redisConnectionRepository.removeConnection(testConnection.getKey());

        MultiValueMap<String, Connection<?>> map = redisConnectionRepository.findAllConnections();

        assertEquals(0, map.get("twitter").size());
    }

    @Test
    public void getConnectionWhenFound() {
        redisConnectionRepository.addConnection(testConnection);

        Connection<?> socialRedisConnection = redisConnectionRepository.getConnection(testConnection.getKey());
        assertNotNull(socialRedisConnection);
        assertEquals(testConnection.getKey().getProviderUserId(), socialRedisConnection.getKey().getProviderUserId());
    }

    @Test(expected = NoSuchConnectionException.class)
    public void getConnectionWhenNotFound() {
        ConnectionKey connectionKey = new ConnectionKey("foo", "bar");
        redisConnectionRepository.getConnection(connectionKey);
    }

    @Test
    public void updateConnection() {
        assertNull(testConnection.getDisplayName());
        redisConnectionRepository.addConnection(testConnection);

        redisConnectionRepository.updateConnection(testConnection);

        Connection<?> socialRedisConnection = redisConnectionRepository.getConnection(testConnection.getKey());
        assertNotNull(socialRedisConnection);
        assertEquals("displayName", socialRedisConnection.getDisplayName());
    }

    @Test
    public void getPrimaryConnectionOK() {
        redisConnectionRepository.addConnection(testConnection);

        Connection<RedisUtils.TestTwitterApi> connection = redisConnectionRepository.getPrimaryConnection(RedisUtils.TestTwitterApi.class);

        assertNotNull(connection);
        assertEquals(connection.getDisplayName(), "displayName");
    }

    @Test(expected = NotConnectedException.class)
    public void getPrimaryConnectionException() throws NotConnectedException {
        redisConnectionRepository.getPrimaryConnection(RedisUtils.TestTwitterApi.class);
    }

    @Test
    public void findPrimaryConnection() {
        redisConnectionRepository.addConnection(testConnection);

        Connection<RedisUtils.TestTwitterApi> connection = redisConnectionRepository.findPrimaryConnection(RedisUtils.TestTwitterApi.class);

        assertNotNull(connection);
        assertEquals(connection.getDisplayName(), "displayName");
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
