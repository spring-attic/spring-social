package org.springframework.social.connect.redis.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("socialRedisConnection")
public class SocialRedisConnection {

    @Id
    @Indexed
    private String providerUserId;

    @Indexed
    private String userId;

    @Indexed
    private String providerId;

    private String displayName;

    private String profileUrl;

    private String imageUrl;

    private String accessToken;

    private String secret;

    private String refreshToken;

    private Long expireTime;

    public SocialRedisConnection(String providerUserId, String userId, String providerId, String displayName, String profileUrl, String imageUrl, String accessToken, String secret, String refreshToken, Long expireTime) {
        this.providerUserId = providerUserId;
        this.userId = userId;
        this.providerId = providerId;
        this.displayName = displayName;
        this.profileUrl = profileUrl;
        this.imageUrl = imageUrl;
        this.accessToken = accessToken;
        this.secret = secret;
        this.refreshToken = refreshToken;
        this.expireTime = expireTime;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public String getUserId() {
        return userId;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getSecret() {
        return secret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}
