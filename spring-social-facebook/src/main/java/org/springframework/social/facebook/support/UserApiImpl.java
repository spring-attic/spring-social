package org.springframework.social.facebook.support;

import java.util.List;

import org.springframework.social.facebook.UserApi;
import org.springframework.social.facebook.UserLike;
import org.springframework.web.client.RestTemplate;

public class UserApiImpl extends AbstractFacebookApi implements UserApi {

	public UserApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
	}

	public List<UserLike> getLikes() {
		return getLikes("me");
	}

	public List<UserLike> getLikes(String userId) {
		return FacebookResponseExtractors.extractUserLikes(getConnection(userId, "likes"));
	}
}
