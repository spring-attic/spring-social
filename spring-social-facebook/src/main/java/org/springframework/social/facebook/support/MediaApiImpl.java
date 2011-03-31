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
import java.util.Map;

import org.springframework.social.facebook.Album;
import org.springframework.social.facebook.MediaApi;
import org.springframework.social.facebook.Photo;
import org.springframework.social.facebook.Video;
import org.springframework.social.facebook.support.extractors.AlbumResponseExtractor;
import org.springframework.social.facebook.support.extractors.PhotoResponseExtractor;
import org.springframework.social.facebook.support.extractors.VideoResponseExtractor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class MediaApiImpl extends AbstractFacebookApi implements MediaApi {

	private final AlbumResponseExtractor albumExtractor;
	private final PhotoResponseExtractor photoExtractor;
	private final VideoResponseExtractor videoExtractor;

	public MediaApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
		albumExtractor = new AlbumResponseExtractor();
		photoExtractor = new PhotoResponseExtractor();
		videoExtractor = new VideoResponseExtractor();
	}

	public List<Album> getAlbums() {
		return getAlbums("me");
	}

	public List<Album> getAlbums(String userId) {
		return getObjectConnection(userId, "albums", albumExtractor);
	}

	public Album getAlbum(String albumId) {
		return getObject(albumId, albumExtractor);
	}
	
	public String createAlbum(String name, String description) {
		return createAlbum("me", name, description);
	}
	
	// TODO: Expose this method once we figure out how to use alternate access tokens.
	//       That is, this method only makes sense when creating albums for something
	//       other than the authenticated user (a group, for example). To do that, you'd
	//       need to use an access token for that group...not the access token for the user.
	//       You can get those tokens via the /{user}/accounts...but the question is
	//       how to best design the API to use these.
	public String createAlbum(String ownerId, String name, String description) {
		MultiValueMap<String, String> data = new LinkedMultiValueMap<String, String>();
		data.set("name", name);
		data.set("message", description);
		return (String) ((Map<String, Object>) publish(ownerId, "albums", data)).get("id");
	}
	
	public List<Photo> getPhotos(String albumId) {
		return getObjectConnection(albumId, "photos", photoExtractor);
	}
	
	public Photo getPhoto(String photoId) {
		return getObject(photoId, photoExtractor);
	}

	public List<Video> getVideos() {
		return getVideos("me");
	}
	
	public List<Video> getVideos(String ownerId) {
		return getObjectConnection(ownerId, "videos", videoExtractor);
	}
	
	public Video getVideo(String videoId) {
		return getObject(videoId, videoExtractor);
	}
}
