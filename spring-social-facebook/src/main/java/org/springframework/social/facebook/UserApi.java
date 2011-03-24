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
package org.springframework.social.facebook;

import java.util.List;

public interface UserApi {
	
	/**
	 * Retrieves the profile for the authenticated user.
	 * @return the user's profile information.
	 */
	FacebookProfile getUserProfile();
	
	/**
	 * Retrieves the profile for the authenticated user.
	 * @param username the Facebook username to retrieve profile data for.
	 * @return the user's profile information.
	 */
	FacebookProfile getUserProfile(String username);

	/**
	 * Retrieves the profile for the authenticated user.
	 * @param userId the Facebook user ID to retrieve profile data for.
	 * @return the user's profile information.
	 */
	FacebookProfile getUserProfile(long userId);

	/**
	 * Retrieves a list of things that the authenticated user has liked.
	 * Requires "user_likes" permission.
	 * Returns an empty list if permission isn't granted.
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getLikes();
	
	/**
	 * Retrieves a list of things that the given user has liked.
	 * Requires "user_likes" permission for the authenticated user and "friends_likes" for the authenticated user's friends.
	 * Returns an empty list if permission isn't granted.
	 * @param userId the ID of the user
	 * @return a list of {@link UserLike} objects
	 */
	List<UserLike> getLikes(String userId);
	
}
