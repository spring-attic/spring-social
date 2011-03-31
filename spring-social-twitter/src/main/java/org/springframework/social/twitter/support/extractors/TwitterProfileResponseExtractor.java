package org.springframework.social.twitter.support.extractors;

import java.util.Map;

import org.springframework.social.twitter.types.TwitterProfile;

public class TwitterProfileResponseExtractor extends AbstractResponseExtractor<TwitterProfile> {

	public TwitterProfile extractObject(Map<String, Object> response) {
		TwitterProfile profile = new TwitterProfile();
		profile.setId(Long.valueOf(String.valueOf(response.get("id"))).longValue());
		profile.setScreenName(String.valueOf(response.get("screen_name")));
		profile.setName(String.valueOf(response.get("name")));
		profile.setDescription(String.valueOf(response.get("description")));
		profile.setLocation(String.valueOf(response.get("location")));
		profile.setUrl(String.valueOf(response.get("url")));
		profile.setProfileImageUrl(String.valueOf(response.get("profile_image_url")));
		profile.setCreatedDate(toTimelineDate(String.valueOf(response.get("created_at"))));
		return profile;
	}

}
