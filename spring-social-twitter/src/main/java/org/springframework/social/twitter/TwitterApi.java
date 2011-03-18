/*
 * Copyright 2010 the original author or authors.
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

/**
 * Interface specifying a basic set of operations for interacting with Twitter.
 * Implemented by TwitterTemplate. Not often used directly, but a useful option
 * to enhance testability, as it can easily be mocked or stubbed.
 *
 * @author Craig Walls
 */
public interface TwitterApi {

	/**
	 * Returns the portion of the Twitter API containing the user operations.
	 */
	UserApi userApi();

	/**
	 * Returns the portion of the Twitter API containing the tweet and timeline operations.
	 */
	TweetApi tweetApi();

	/**
	 * Returns the portion of the Twitter API containing the friends and followers operations.
	 */
	FriendsApi friendsApi();

	/**
	 * Returns the portion of the Twitter API containing the search operations.
	 */
	SearchApi searchApi();

	/**
	 * Returns the portion of the Twitter API containing the direct message operations.
	 */
	DirectMessageApi directMessageApi();

}