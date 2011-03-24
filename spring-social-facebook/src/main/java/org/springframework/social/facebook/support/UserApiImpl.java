package org.springframework.social.facebook.support;

import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.FacebookProfile;
import org.springframework.social.facebook.UserApi;
import org.springframework.social.facebook.UserLike;
import org.springframework.web.client.RestTemplate;

public class UserApiImpl extends AbstractFacebookApi implements UserApi {

	public UserApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
	}

	public FacebookProfile getUserProfile() {
		return getUserProfile("me");
	}

	public FacebookProfile getUserProfile(long facebookId) {
		return getUserProfile(String.valueOf(facebookId));
	}

	public FacebookProfile getUserProfile(String facebookId) {
		Map<String, Object> profileMap = getObject(facebookId);
		return FacebookResponseExtractors.extractUserProfileFromMap(profileMap);
	}

	public List<UserLike> getLikes() {
		return getLikes("me");
	}

	public List<UserLike> getLikes(String userId) {
		return FacebookResponseExtractors.extractUserLikes(getConnection(userId, "likes"));
	}
}
