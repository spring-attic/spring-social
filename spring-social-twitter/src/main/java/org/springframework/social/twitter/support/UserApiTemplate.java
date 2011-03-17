package org.springframework.social.twitter.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.social.twitter.SuggestionCategory;
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

	public List<TwitterProfile> getUsers(long... userIds) {
		return lookupUsers(USER_LOOKUP_URL + "?user_id={user_id}", join(userIds));
	}

	public List<TwitterProfile> getUsers(String... screenNames) {
		return lookupUsers(USER_LOOKUP_URL + "?screen_name={screen_name}", join(screenNames));
	}

	public List<TwitterProfile> searchForUsers(String query) {
		return lookupUsers(USER_SEARCH_URL + "?q={query}", query);
	}
	
	public List<SuggestionCategory> getSuggestionCategories() {
		List<Map<String, String>> categoryList = restTemplate.getForObject(SUGGESTION_CATEGORIES_URL, List.class);
		List<SuggestionCategory> categories = new ArrayList<SuggestionCategory>();
		for (Map<String, String> categoryMap : categoryList) {
			categories.add(new SuggestionCategory(String.valueOf(categoryMap.get("name")), 
					String.valueOf(categoryMap.get("slug")), 
					Integer.valueOf(String.valueOf(categoryMap.get("size")))));
		}
		return categories;
	}

	public List<TwitterProfile> getSuggestions(String slug) {
		Map<String, List<Map<String, Object>>> suggestionsMap = restTemplate.getForObject(SUGGESTIONS_URL, Map.class, slug);
		List<Map<String, Object>> userList = suggestionsMap.get("users");
		List<TwitterProfile> profiles = new ArrayList<TwitterProfile>(userList.size());
		for (Map<String, Object> userMap : userList) {
			profiles.add(TwitterResponseHelper.getProfileFromResponseMap(userMap));
		}
		return profiles;		
	}

	private List<TwitterProfile> lookupUsers(String url, String... urlArgs) {
		List<Map<String, Object>> userList = restTemplate.getForObject(url, List.class, urlArgs);
		List<TwitterProfile> profiles = new ArrayList<TwitterProfile>(userList.size());
		for (Map<String, Object> userMap : userList) {
			profiles.add(TwitterResponseHelper.getProfileFromResponseMap(userMap));
		}
		return profiles;
	}

	private String join(long[] items) {
		if(items.length == 0) return "";
		StringBuffer sb = new StringBuffer();
		sb.append(items[0]);
		for(int i=1; i < items.length; i++) {
			sb.append(',').append(items[i]);
		}
		return sb.toString();
	}
	
	private String join(Object[] items) {
		if(items.length == 0) return "";
		StringBuffer sb = new StringBuffer();
		sb.append(items[0]);
		for(int i=1; i < items.length; i++) {
			sb.append(',').append(items[i]);
		}
		return sb.toString();
	}

	static final String VERIFY_CREDENTIALS_URL = TwitterTemplate.API_URL_BASE + "account/verify_credentials.json";
	static final String USER_PROFILE_URL = TwitterTemplate.API_URL_BASE + "users/show.json";
	static final String USER_LOOKUP_URL = TwitterTemplate.API_URL_BASE + "users/lookup.json";
	static final String USER_SEARCH_URL = TwitterTemplate.API_URL_BASE + "users/search.json";
	static final String SUGGESTION_CATEGORIES_URL = TwitterTemplate.API_URL_BASE + "users/suggestions.json";
	static final String SUGGESTIONS_URL = TwitterTemplate.API_URL_BASE + "users/suggestions/{slug}.json";
}
