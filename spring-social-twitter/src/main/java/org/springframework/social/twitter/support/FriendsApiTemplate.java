package org.springframework.social.twitter.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.social.ResponseStatusCodeTranslator;
import org.springframework.social.SocialException;
import org.springframework.social.twitter.FriendsApi;
import org.springframework.social.twitter.TwitterProfile;
import org.springframework.social.twitter.TwitterTemplate;
import org.springframework.web.client.RestTemplate;

public class FriendsApiTemplate implements FriendsApi {
	
	private final RestTemplate restTemplate;
	private final ResponseStatusCodeTranslator statusCodeTranslator;

	public FriendsApiTemplate(RestTemplate restTemplate, ResponseStatusCodeTranslator statusCodeTranslator) {
		this.restTemplate = restTemplate;
		this.statusCodeTranslator = statusCodeTranslator;
	}

	public List<TwitterProfile> getFriends(long userId) {
		return getFriendsOrFollowers(FRIENDS_STATUSES_URL + "?user_id={user_id}", userId);
	}

	public List<TwitterProfile> getFriends(String screenName) {
		return getFriendsOrFollowers(FRIENDS_STATUSES_URL + "?screen_name={screen_name}", screenName);
	}
	
	public List<Long> getFriendIds(long userId) {
		return restTemplate.getForObject(FRIEND_IDS_URL + "?user_id={userId}", List.class, userId);
	}

	public List<Long> getFriendIds(String screenName) {
		return restTemplate.getForObject(FRIEND_IDS_URL + "?screen_name={screenName}", List.class, screenName);
	}

	public List<TwitterProfile> getFollowers(long userId) {
		return getFriendsOrFollowers(FOLLOWERS_STATUSES_URL + "?user_id={user_id}", userId);
	}

	public List<TwitterProfile> getFollowers(String screenName) {
		return getFriendsOrFollowers(FOLLOWERS_STATUSES_URL + "?screen_name={screen_name}", screenName);
	}

	public List<Long> getFollowerIds(long userId) {
		return restTemplate.getForObject(FOLLOWER_IDS_URL + "?user_id={userId}", List.class, userId);
	}

	public List<Long> getFollowerIds(String screenName) {
		return restTemplate.getForObject(FOLLOWER_IDS_URL + "?screen_name={screenName}", List.class, screenName);
	}

	public String follow(String screenName) {
	    return this.friendshipAssist(FOLLOW_URL, screenName);	    
	}
	
	public String unfollow(String screenName) {
	    return this.friendshipAssist(UNFOLLOW_URL, screenName);
	}
	
	private List<TwitterProfile> getFriendsOrFollowers(String url, Object... urlArgs) {
		List<Map<String, Object>> response = restTemplate.getForObject(url, List.class, urlArgs);
		List<TwitterProfile> followers = new ArrayList<TwitterProfile>(response.size());
		for (Map<String, Object> item : response) {
			followers.add(TwitterResponseHelper.getProfileFromResponseMap(item));
		}
		return followers;
	}

	private String friendshipAssist(String url, String screenName) {
	    ResponseEntity<Map> response = restTemplate.postForEntity(url, "", Map.class, Collections.singletonMap("screen_name", screenName));
		handleResponseErrors(response);
        Map<String, Object> body = response.getBody();
        return (String) body.get("screen_name");
	}	
	
	private void handleResponseErrors(ResponseEntity<Map> response) {
		SocialException exception = statusCodeTranslator.translate(response);
		if (exception != null) {
			throw exception;
		}
	}

	static final String FRIEND_IDS_URL = TwitterTemplate.API_URL_BASE + "friends/ids.json";
	static final String FOLLOWER_IDS_URL = TwitterTemplate.API_URL_BASE + "followers/ids.json";
	static final String FRIENDS_STATUSES_URL = TwitterTemplate.API_URL_BASE + "statuses/friends.json";
	static final String FOLLOWERS_STATUSES_URL = TwitterTemplate.API_URL_BASE + "statuses/followers.json";
	static final String FOLLOW_URL = TwitterTemplate.API_URL_BASE + "friendships/create.json?screen_name={screen_name}";
	static final String UNFOLLOW_URL = TwitterTemplate.API_URL_BASE + "friendships/destroy.json?screen_name={screen_name}";
	
}
