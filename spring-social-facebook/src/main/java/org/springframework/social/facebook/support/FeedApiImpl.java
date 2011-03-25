/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.facebook.support;

import java.util.List;

import org.springframework.social.facebook.FacebookLink;
import org.springframework.social.facebook.FeedApi;
import org.springframework.social.facebook.FeedEntry;
import org.springframework.social.facebook.support.extractors.ResponseExtractors;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class FeedApiImpl extends AbstractFacebookApi implements FeedApi {

	public FeedApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
	}

	public List<FeedEntry> getFeed() {
		return getFeed("me");
	}

	public List<FeedEntry> getFeed(String ownerId) {
		return getObjectConnection(ownerId, "feed", ResponseExtractors.FEED_ENTRY_EXTRACTOR);
	}

	public List<FeedEntry> getHomeFeed() {
		return getHomeFeed("me");
	}

	public List<FeedEntry> getHomeFeed(String userId) {
		return getObjectConnection(userId, "home", ResponseExtractors.FEED_ENTRY_EXTRACTOR);
	}

	public FeedEntry getFeedEntry(String entryId) {
		return getObject(entryId, ResponseExtractors.FEED_ENTRY_EXTRACTOR);
	}

	public String updateStatus(String message) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("message", message);
		return (String) publish("me", "feed", map).get("id");
	}

	public String postLink(String message, FacebookLink link) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("link", link.getLink());
		map.set("name", link.getName());
		map.set("caption", link.getCaption());
		map.set("description", link.getDescription());
		map.set("message", message);
		return (String) publish("me", "feed", map).get("id");
	}

	public void deleteFeedEntry(String id) {
		delete(id);
	}

}
