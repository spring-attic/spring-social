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
package org.springframework.social.twitter.json;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.social.twitter.Tweet;
import org.springframework.social.twitter.TwitterProfile;
import org.springframework.social.twitter.direct.DirectMessage;
import org.springframework.social.twitter.direct.DirectMessageMixin;
import org.springframework.social.twitter.list.UserList;
import org.springframework.social.twitter.list.UserListMixin;
import org.springframework.social.twitter.search.SavedSearch;
import org.springframework.social.twitter.search.SavedSearchMixin;
import org.springframework.social.twitter.search.SearchResults;
import org.springframework.social.twitter.search.SearchResultsMixin;
import org.springframework.social.twitter.search.Trend;
import org.springframework.social.twitter.search.TrendMixin;
import org.springframework.social.twitter.search.Trends;
import org.springframework.social.twitter.search.TrendsMixin;
import org.springframework.social.twitter.user.SuggestionCategory;
import org.springframework.social.twitter.user.SuggestionCategoryMixin;
import org.springframework.social.twitter.user.TwitterProfileMixin;

/**
 * Jackson module for registering mixin annotations against Twitter model classes.
 */
public class TwitterModule extends SimpleModule {
	public TwitterModule() {
		super("TwitterModule", new Version(1, 0, 0, null));
	}
	
	@Override
	public void setupModule(SetupContext context) {
		context.setMixInAnnotations(TwitterProfile.class, TwitterProfileMixin.class);
		context.setMixInAnnotations(SavedSearch.class, SavedSearchMixin.class);
		context.setMixInAnnotations(Trend.class, TrendMixin.class);
		context.setMixInAnnotations(Trends.class, TrendsMixin.class);
		context.setMixInAnnotations(SuggestionCategory.class, SuggestionCategoryMixin.class);
		context.setMixInAnnotations(DirectMessage.class, DirectMessageMixin.class);
		context.setMixInAnnotations(UserList.class, UserListMixin.class);
		context.setMixInAnnotations(Tweet.class, TweetMixin.class);
		context.setMixInAnnotations(SearchResults.class, SearchResultsMixin.class);
	}
}
