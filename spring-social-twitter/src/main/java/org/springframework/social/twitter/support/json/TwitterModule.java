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
package org.springframework.social.twitter.support.json;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.social.twitter.types.DirectMessage;
import org.springframework.social.twitter.types.SavedSearch;
import org.springframework.social.twitter.types.SuggestionCategory;
import org.springframework.social.twitter.types.Trend;
import org.springframework.social.twitter.types.Trends;
import org.springframework.social.twitter.types.Tweet;
import org.springframework.social.twitter.types.TwitterProfile;
import org.springframework.social.twitter.types.UserList;

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
	}
}
