package org.springframework.social.connect.redis;

import org.springframework.social.connect.*;
import org.springframework.social.connect.support.AbstractConnection;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;

class RedisUtils {

    static class TestConnection extends AbstractConnection {

        private String providerUserId;

        TestConnection(final ApiAdapter apiAdapter, final String providerUserId) {
            super(apiAdapter);
            this.providerUserId = providerUserId;
        }

        @Override
        public Object getApi() {
            return new TestTwitterApi() {
                @Override
                public String getAccessToken() {
                    return "accessToken-123";
                }
            };
        }

        @Override
        public ConnectionKey getKey() {
            return new ConnectionKey("twitter", providerUserId);
        }

        @Override
        public ConnectionData createData() {
            return new ConnectionData("twitter", providerUserId, "displayName", "profileUrl", "imageUrl", "accessToken", "secret", "refreshToken", 1000000L);
        }
    }

    static class TestTwitterConnectionFactory extends OAuth1ConnectionFactory<TestTwitterApi> {

        TestTwitterConnectionFactory() {
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

    interface TestTwitterApi {

        String getAccessToken();

    }

    static class TestTwitterApiAdapter implements ApiAdapter<TestTwitterApi> {

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
