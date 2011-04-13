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

import org.springframework.social.twitter.support.json.LongList;
import org.springframework.social.twitter.support.json.TweetList;
import org.springframework.social.twitter.support.json.TwitterProfileList;
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
	
	public TimelineTemplate(LowLevelTwitterApi lowLevelApi) {
		super(lowLevelApi);
	}

	public List<Tweet> getPublicTimeline() {
		return getLowLevelTwitterApi().fetchObject("statuses/public_timeline.json", TweetList.class).getList();
	}

	public List<Tweet> getHomeTimeline() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("statuses/home_timeline.json", TweetList.class).getList();
	}

	public List<Tweet> getFriendsTimeline() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("statuses/friends_timeline.json", TweetList.class).getList();
	}

	public List<Tweet> getUserTimeline() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("statuses/user_timeline.json", TweetList.class).getList();
	}

	public List<Tweet> getUserTimeline(String screenName) {
		return getLowLevelTwitterApi().fetchObject("statuses/user_timeline.json", TweetList.class, Collections.singletonMap("screen_name", screenName)).getList();
	}

	public List<Tweet> getUserTimeline(long userId) {
		return getLowLevelTwitterApi().fetchObject("statuses/user_timeline.json", TweetList.class, Collections.singletonMap("user_id", String.valueOf(userId))).getList();
	}

	public List<Tweet> getMentions() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("statuses/mentions.json", TweetList.class).getList();
	}

	public List<Tweet> getRetweetedByMe() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("statuses/retweeted_by_me.json", TweetList.class).getList();
	}

	public List<Tweet> getRetweetedToMe() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("statuses/retweeted_to_me.json", TweetList.class).getList();
	}

	public List<Tweet> getRetweetsOfMe() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("statuses/retweets_of_me.json", TweetList.class).getList();
	}

	public Tweet getStatus(long tweetId) {
		return getLowLevelTwitterApi().fetchObject("statuses/show/" + tweetId + ".json", Tweet.class);
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
		return getLowLevelTwitterApi().fetchObject("statuses/retweets/" + tweetId + ".json", TweetList.class).getList();
	}

	public List<TwitterProfile> getRetweetedBy(long tweetId) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("statuses/" + tweetId + "/retweeted_by.json", TwitterProfileList.class).getList();
	}

	public List<Long> getRetweetedByIds(long tweetId) {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("statuses/" + tweetId + "/retweeted_by/ids.json", LongList.class).getList();
	}

	public List<Tweet> getFavorites() {
		requireUserAuthorization();
		return getLowLevelTwitterApi().fetchObject("favorites.json", TweetList.class).getList();
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
