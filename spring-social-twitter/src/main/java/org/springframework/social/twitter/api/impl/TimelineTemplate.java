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
package org.springframework.social.twitter.api.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.social.twitter.api.StatusDetails;
import org.springframework.social.twitter.api.TimelineOperations;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link TimelineOperations}, providing a binding to Twitter's tweet and timeline-oriented REST resources.
 * @author Craig Walls
 */
class TimelineTemplate extends AbstractTwitterOperations implements TimelineOperations {
	
	private final RestTemplate restTemplate;

	public TimelineTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
		super(isAuthorizedForUser);
		this.restTemplate = restTemplate;
	}

	public List<Tweet> getPublicTimeline() {
		return restTemplate.getForObject(buildUri("statuses/public_timeline.json"), TweetList.class);
	}

	public List<Tweet> getHomeTimeline() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("statuses/home_timeline.json"), TweetList.class);
	}

	public List<Tweet> getFriendsTimeline() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("statuses/friends_timeline.json"), TweetList.class);
	}

	public List<Tweet> getUserTimeline() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("statuses/user_timeline.json"), TweetList.class);
	}

	public List<Tweet> getUserTimeline(String screenName) {
		return restTemplate.getForObject(buildUri("statuses/user_timeline.json", "screen_name", screenName), TweetList.class);
	}

	public List<Tweet> getUserTimeline(long userId) {
		return restTemplate.getForObject(buildUri("statuses/user_timeline.json", "user_id", String.valueOf(userId)), TweetList.class);
	}

	public List<Tweet> getMentions() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("statuses/mentions.json"), TweetList.class);
	}

	public List<Tweet> getRetweetedByMe() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("statuses/retweeted_by_me.json"), TweetList.class);
	}

	public List<Tweet> getRetweetedToMe() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("statuses/retweeted_to_me.json"), TweetList.class);
	}

	public List<Tweet> getRetweetsOfMe() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("statuses/retweets_of_me.json"), TweetList.class);
	}

	public Tweet getStatus(long tweetId) {
		return restTemplate.getForObject(buildUri("statuses/show/" + tweetId + ".json"), Tweet.class);
	}

	public void updateStatus(String message) {
		updateStatus(message, new StatusDetails());
	}

	public void updateStatus(String message, StatusDetails details) {
		requireUserAuthorization();
		MultiValueMap<String, Object> tweetParams = new LinkedMultiValueMap<String, Object>();
		tweetParams.add("status", message);
		tweetParams.putAll(details.toParameterMap());
		restTemplate.postForObject(buildUri("statuses/update.json"), tweetParams, String.class);
	}

	public void deleteStatus(long tweetId) {
		requireUserAuthorization();
		restTemplate.delete(buildUri("statuses/destroy/" + tweetId + ".json"));
	}

	public void retweet(long tweetId) {
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		restTemplate.postForObject(buildUri("statuses/retweet/" + tweetId + ".json"), data, String.class);
	}

	public List<Tweet> getRetweets(long tweetId) {
		return restTemplate.getForObject(buildUri("statuses/retweets/" + tweetId + ".json"), TweetList.class);
	}

	public List<TwitterProfile> getRetweetedBy(long tweetId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("statuses/" + tweetId + "/retweeted_by.json"), TwitterProfileList.class);
	}

	public List<Long> getRetweetedByIds(long tweetId) {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("statuses/" + tweetId + "/retweeted_by/ids.json"), LongList.class);
	}

	public List<Tweet> getFavorites() {
		requireUserAuthorization();
		return restTemplate.getForObject(buildUri("favorites.json"), TweetList.class);
	}

	public void addToFavorites(long tweetId) {
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		restTemplate.postForObject(buildUri("favorites/create/" + tweetId + ".json"), data, String.class);
	}

	public void removeFromFavorites(long tweetId) {
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		restTemplate.postForObject(buildUri("favorites/destroy/" + tweetId + ".json"), data, String.class);
	}

	@SuppressWarnings("serial")
	private static class LongList extends ArrayList<Long>{}
	
	@SuppressWarnings("serial")
	private static class TweetList extends ArrayList<Tweet> {}
}
