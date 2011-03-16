package org.springframework.social.twitter.support;

import java.util.Map;

import org.springframework.social.twitter.TwitterProfile;
import org.springframework.social.twitter.TwitterTemplate;
import org.springframework.social.twitter.UserApi;
import org.springframework.web.client.RestTemplate;

public class UserApiTemplate implements UserApi {

	private final RestTemplate restTemplate;

	public UserApiTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String getProfileId() {
		Map<?, ?> response = restTemplate.getForObject(VERIFY_CREDENTIALS_URL, Map.class);
		return (String) response.get("screen_name");
	}

	public TwitterProfile getUserProfile() {
		return getUserProfile(getProfileId());
	}

	public TwitterProfile getUserProfile(String screenName) {
		Map<?, ?> response = restTemplate.getForObject(USER_PROFILE_URL + "?screen_name={screenName}", Map.class,
				screenName);
		return TwitterResponseHelper.getProfileFromResponseMap(response);
	}

	public TwitterProfile getUserProfile(long userId) {
		Map<?, ?> response = restTemplate.getForObject(USER_PROFILE_URL + "?user_id={userId}", Map.class, userId);
		return TwitterResponseHelper.getProfileFromResponseMap(response);
	}

	static final String VERIFY_CREDENTIALS_URL = TwitterTemplate.API_URL_BASE + "account/verify_credentials.json";
	static final String USER_PROFILE_URL = TwitterTemplate.API_URL_BASE + "users/show.json";

}
