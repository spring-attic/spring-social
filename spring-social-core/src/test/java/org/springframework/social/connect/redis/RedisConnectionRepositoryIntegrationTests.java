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
import org.springframework.social.connect.support.AbstractConnection;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;
import redis.clients.jedis.JedisShardInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    private TestConnection testConnection;

    @Before
    public void init() {
        redisConnectionRepository = new RedisConnectionRepository(connectionFactoryLocator, Encryptors.noOpText(), socialRedisConnectionRepository, "Turbots");
        testConnection = new TestConnection(new TestTwitterApiAdapter());
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
    public void findPrimaryConnection() {
        redisConnectionRepository.addConnection(testConnection);

        Connection<TestTwitterApi> connection = redisConnectionRepository.findPrimaryConnection(TestTwitterApi.class);

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
            registry.addConnectionFactory(new TestTwitterConnectionFactory());
            return registry;
        }
    }

    class TestConnection extends AbstractConnection {

        TestConnection(ApiAdapter apiAdapter) {
            //noinspection unchecked
            super(apiAdapter);
        }

        @Override
        public Object getApi() {
            return "twitter";
        }

        @Override
        public ConnectionData createData() {
            return new ConnectionData("twitter", "providerUserId", "displayName", "profileUrl", "imageUrl", "accessToken", "secret", "refreshToken", 1000000L);
        }
    }

    private static class TestTwitterConnectionFactory extends OAuth1ConnectionFactory<TestTwitterApi> {

        public TestTwitterConnectionFactory() {
            super("twitter", new TestTwitterServiceProvider(), new TestTwitterApiAdapter());
        }

    }

    private static class TestTwitterServiceProvider implements OAuth1ServiceProvider<TestTwitterApi> {

        public OAuth1Operations getOAuthOperations() {
            return null;
        }

        public TestTwitterApi getApi(final String accessToken, final String secret) {
            return new TestTwitterApi() {
                public String getAccessToken() {
                    return accessToken;
                }

            };
        }

    }

    public interface TestTwitterApi {

        String getAccessToken();

    }

    private static class TestTwitterApiAdapter implements ApiAdapter<TestTwitterApi> {

        private String name = "@dhubau";

        public boolean test(TestTwitterApi api) {
            return true;
        }

        public void setConnectionValues(TestTwitterApi api, ConnectionValues values) {
        }

        public UserProfile fetchUserProfile(TestTwitterApi api) {
            return new UserProfileBuilder().setName(name).setUsername(name).build();
        }

        public void updateStatus(TestTwitterApi api, String message) {
        }

    }
}
