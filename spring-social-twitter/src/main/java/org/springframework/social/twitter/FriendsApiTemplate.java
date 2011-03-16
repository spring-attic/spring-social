package org.springframework.social.twitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

public class FriendsApiTemplate implements FriendsApi {
	
	private final RestTemplate restTemplate;

	public FriendsApiTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public List<String> getFriends(String screenName) {
		List<Map<String, String>> response = restTemplate.getForObject(FRIENDS_STATUSES_URL, List.class, Collections.singletonMap("screen_name", screenName));
		List<String> friends = new ArrayList<String>(response.size());
		for (Map<String, String> item : response) {
			friends.add(item.get("screen_name"));
		}
		return friends;
	}

	static final String FRIENDS_STATUSES_URL = TwitterTemplate.API_URL_BASE + "statuses/friends.json?screen_name={screen_name}";
	
}
