package org.springframework.social.facebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * This is the central class for interacting with Facebook.
 * 
 * @author Craig Walls
 */
public class FacebookTemplate implements FacebookOperations {
	private RestOperations restOperations;
	private final String accessToken;

	/**
	 * Create a new instance of FacebookTemplate.
	 * 
	 * This constructor creates the FacebookTemplate using a given access token.
	 * 
	 * @param accessToken
	 *            An access token given by Facebook after a successful OAuth 2
	 *            authentication (or through Facebook's JS library).
	 */
	public FacebookTemplate(String accessToken) {
		this.accessToken = accessToken;
		RestTemplate restTemplate = new RestTemplate();
		// must be CommonsClientHttpRequestFactory or else the location header
		// in an HTTP 302 won't be followed
		restTemplate.setRequestFactory(new CommonsClientHttpRequestFactory());
		MappingJacksonHttpMessageConverter json = new MappingJacksonHttpMessageConverter();
		json.setSupportedMediaTypes(Arrays.asList(new MediaType("text", "javascript")));
		restTemplate.getMessageConverters().add(json);
		this.restOperations = restTemplate;
	}

	public String getProfileId() {
		return Long.toString(getUserInfo().getId());
	}

	public void setStatus(String status) {
		postToWall(status);
	}

	public FacebookUserInfo getUserInfo() {
		return restOperations.getForObject(OBJECT_URL + "?access_token={accessToken}", FacebookUserInfo.class, "me",
				accessToken);
    }

	public List<String> getFriendIds() {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> response = restOperations.getForEntity(CONNECTION_URL, Map.class, CURRENT_USER, FRIENDS,
				accessToken);

		@SuppressWarnings("unchecked")
		Map<String, List<Map<String, String>>> resultsMap = response.getBody();
		List<Map<String, String>> friends = resultsMap.get("data");
		
		List<String> friendIds = new ArrayList<String>();
		for (Map<String, String> friendData : friends) {
	        friendIds.add(friendData.get("id"));
        }
	    return friendIds;
    }
	
	public void postToWall(String message) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("message", message);
		publish(CURRENT_USER, FEED, map);
	}
	
	public void postToWall(String message, FacebookLink link) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("link", link.getLink());
		map.set("name", link.getName());
		map.set("caption", link.getCaption());
		map.set("description", link.getDescription());
		map.set("message", message);
		publish(CURRENT_USER, FEED, map);
	}
	
	public void publish(String object, String connection, MultiValueMap<String, String> data) {
		MultiValueMap<String, String> requestData = new LinkedMultiValueMap<String, String>(data);
		restOperations.postForLocation(CONNECTION_URL, requestData, object, connection, accessToken);
	}
	
	public byte[] getProfilePicture() {
		return getProfilePicture(CURRENT_USER);
	}

	public byte[] getProfilePicture(String profileId) {
		ResponseEntity<byte[]> imageBytes = restOperations.getForEntity(PROFILE_LARGE_PICTURE_URL, byte[].class,
				profileId, accessToken);
		return imageBytes.getBody();
	}
	
	static final String PROFILE_LARGE_PICTURE_URL = "https://graph.facebook.com/{profile}/picture?type=large&access_token={accessToken}";
	static final String OBJECT_URL = "https://graph.facebook.com/{objectId}";
	static final String CONNECTION_URL = OBJECT_URL + "/{connection}?access_token={accessToken}";
	
	static final String FRIENDS = "friends";
	static final String FEED = "feed";
	static final String CURRENT_USER = "me";
}
