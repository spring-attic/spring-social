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

public interface MediaApi {

	/**
	 * Retrieves a list of albums belonging to the authenticated user.
	 * Requires "user_photos" or "friends_photos" permission.
	 * @return a list {@link Album}s for the user, or an empty list if not available.
	 */
	List<Album> getAlbums();

	/**
	 * Retrieves a list of albums belonging to a specific owner (user, page, etc).
	 * Requires "user_photos" or "friends_photos" permission.
	 * @param ownerId the album owner's ID
	 * @return a list {@link Album}s for the user, or an empty list if not available.
	 */
	List<Album> getAlbums(String ownerId);

}
