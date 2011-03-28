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

import org.springframework.social.facebook.Album;
import org.springframework.social.facebook.MediaApi;
import org.springframework.social.facebook.support.extractors.ResponseExtractors;
import org.springframework.web.client.RestTemplate;

public class MediaApiImpl extends AbstractFacebookApi implements MediaApi {

	public MediaApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
	}

	public List<Album> getAlbums() {
		return getAlbums("me");
	}

	public List<Album> getAlbums(String userId) {
		return getObjectConnection(userId, "albums", ResponseExtractors.ALBUM_EXTRACTOR);
	}

}
