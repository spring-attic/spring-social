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
package org.springframework.social.twitter;

import java.util.Collections;
import java.util.List;

import org.springframework.social.twitter.support.extractors.TweetResponseExtractor;
import org.springframework.social.twitter.support.extractors.TwitterProfileResponseExtractor;
import org.springframework.social.twitter.types.StatusDetails;
import org.springframework.social.twitter.types.Tweet;
import org.springframework.social.twitter.types.TwitterProfile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Implementation of {@link TimelineOperations}, providing a binding to Twitter's tweet and timeline-oriented REST resources.
 * @author Craig Walls
 */
class TimelineTemplate extends AbstractTwitterOperations implements TimelineOperations {

	private TwitterProfileResponseExtractor profileExtractor;
	
	private TweetResponseExtractor tweetExtractor;

	public TimelineTemplate(LowLevelTwitterApi lowLevelApi) {
		super(lowLevelApi);
		this.profileExtractor = new TwitterProfileResponseExtractor();
		this.tweetExtractor = new TweetResponseExtractor();
	}

	public List<Tweet> getPublicTimeline() {
		return getLowLevelTwitterApi().fetchObjects("statuses/public_timeline.json", tweetExtractor);
	}

	public List<Tweet> getHomeTimeline() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObjects("statuses/home_timeline.json", tweetExtractor);
	}

	public List<Tweet> getFriendsTimeline() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObjects("statuses/friends_timeline.json", tweetExtractor);
	}

	public List<Tweet> getUserTimeline() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObjects("statuses/user_timeline.json", tweetExtractor);
	}

	public List<Tweet> getUserTimeline(String screenName) {
		return getLowLevelTwitterApi().fetchObjects("statuses/user_timeline.json", tweetExtractor, Collections.singletonMap("screen_name", screenName));
	}

	public List<Tweet> getUserTimeline(long userId) {
		return getLowLevelTwitterApi().fetchObjects("statuses/user_timeline.json", tweetExtractor, Collections.singletonMap("user_id", String.valueOf(userId)));
	}

	public List<Tweet> getMentions() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObjects("statuses/mentions.json", tweetExtractor);
	}

	public List<Tweet> getRetweetedByMe() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObjects("statuses/retweeted_by_me.json", tweetExtractor);
	}

	public List<Tweet> getRetweetedToMe() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObjects("statuses/retweeted_to_me.json", tweetExtractor);
	}

	public List<Tweet> getRetweetsOfMe() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObjects("statuses/retweets_of_me.json", tweetExtractor);
	}

	public Tweet getStatus(long tweetId) {
		return getLowLevelTwitterApi().fetchObject("statuses/show/" + tweetId + ".json", tweetExtractor);
	}

	public void updateStatus(String message) {
		updateStatus(message, new StatusDetails());
	}

	public void updateStatus(String message, StatusDetails details) {
		requireUserAuthorization();
		MultiValueMap<String, Object> tweetParams = new LinkedMultiValueMap<String, Object>();
		tweetParams.add("status", message);
		tweetParams.setAll(details.toParameterMap());
		getLowLevelTwitterApi().publish("statuses/update.json", tweetParams);
	}

	public void deleteStatus(long tweetId) {
		requireUserAuthorization();
		getLowLevelTwitterApi().delete("statuses/destroy/" + tweetId + ".json");
	}

	public void retweet(long tweetId) {
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		getLowLevelTwitterApi().publish("statuses/retweet/" + tweetId + ".json", data);
	}

	public List<Tweet> getRetweets(long tweetId) {
		return getLowLevelTwitterApi().fetchObjects("statuses/retweets/" + tweetId + ".json", tweetExtractor);
	}

	public List<TwitterProfile> getRetweetedBy(long tweetId) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObjects("statuses/" + tweetId + "/retweeted_by.json", profileExtractor);
	}

	@SuppressWarnings("unchecked")
	public List<Long> getRetweetedByIds(long tweetId) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("statuses/" + tweetId + "/retweeted_by/ids.json", List.class);
	}

	public List<Tweet> getFavorites() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObjects("favorites.json", tweetExtractor);
	}

	public void addToFavorites(long tweetId) {
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		getLowLevelTwitterApi().publish("favorites/create/" + tweetId + ".json", data);
	}

	public void removeFromFavorites(long tweetId) {
		requireUserAuthorization();
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		getLowLevelTwitterApi().publish("favorites/destroy/" + tweetId + ".json", data);
	}

}
