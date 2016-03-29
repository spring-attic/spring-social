package org.springframework.social.connect.redis;

import org.junit.Assume;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

class RedisAvailableRule implements TestRule {

    private static ThreadLocal<JedisConnectionFactory> connectionFactoryResource = new ThreadLocal<>();

    private String redisHost;

    private int redisPort;

    RedisAvailableRule(final String redisHost, final int redisPort) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        RedisAvailable redisAvailable = description.getAnnotation(RedisAvailable.class);
        if (redisAvailable != null) {
            JedisConnectionFactory connectionFactory = null;
            try {
                connectionFactory = new JedisConnectionFactory();
                connectionFactory.setHostName(redisHost);
                connectionFactory.setPort(redisPort);
                connectionFactory.afterPropertiesSet();
                connectionFactory.getConnection();
                connectionFactoryResource.set(connectionFactory);
            } catch (Exception e) {
                if (connectionFactory != null) {
                    connectionFactory.destroy();
                }
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        Assume.assumeTrue("Skipping test class: Redis not available at port " + redisHost + ":" + redisPort, false);
                    }
                };
            }

            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    try {
                        base.evaluate();
                    } finally {
                        JedisConnectionFactory connectionFactory = connectionFactoryResource.get();
                        connectionFactoryResource.remove();
                        if (connectionFactory != null) {
                            connectionFactory.destroy();
                        }
                    }
                }
            };
        }

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
            }
        };

    }
}
