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

import org.springframework.social.facebook.support.extractors.PhotoResponseExtractor;
import org.springframework.social.facebook.support.json.AlbumList;
import org.springframework.social.facebook.support.json.VideoList;
import org.springframework.social.facebook.types.Album;
import org.springframework.social.facebook.types.Photo;
import org.springframework.social.facebook.types.Video;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class MediaTemplate implements MediaOperations {

	private final PhotoResponseExtractor photoExtractor;
	private final GraphApi graphApi;

	public MediaTemplate(GraphApi graphApi) {
		this.graphApi = graphApi;
		photoExtractor = new PhotoResponseExtractor();
	}

	public List<Album> getAlbums() {
		return getAlbums("me");
	}

	public List<Album> getAlbums(String userId) {
		return graphApi.fetchConnections(userId, "albums", AlbumList.class).getList();
	}

	public Album getAlbum(String albumId) {
		return graphApi.fetchObject(albumId, Album.class);
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
		return graphApi.publish(ownerId, "albums", data);
	}
	
	public byte[] getAlbumImage(String albumId) {
		return getAlbumImage(albumId, ImageType.NORMAL);
	}
	
	public byte[] getAlbumImage(String albumId, ImageType imageType) {
		return graphApi.fetchImage(albumId, "picture", imageType);
	}
	
	public List<Photo> getPhotos(String albumId) {
		return graphApi.fetchConnections(albumId, "photos", photoExtractor);
	}
	
	public Photo getPhoto(String photoId) {
		return graphApi.fetchObject(photoId, photoExtractor);
	}
	
	public byte[] getPhotoImage(String photoId) {
		return getPhotoImage(photoId, ImageType.NORMAL);
	}
	
	public byte[] getPhotoImage(String photoId, ImageType imageType) {
		return graphApi.fetchImage(photoId, "picture", imageType);
	}

	public List<Video> getVideos() {
		return getVideos("me");
	}
	
	public List<Video> getVideos(String ownerId) {
		return graphApi.fetchConnections(ownerId, "videos", VideoList.class).getList();
	}
	
	public Video getVideo(String videoId) {
		return graphApi.fetchObject(videoId, Video.class);
	}
	
	public byte[] getVideoImage(String videoId) {
		return getVideoImage(videoId, ImageType.NORMAL);
	}
	
	public byte[] getVideoImage(String videoId, ImageType imageType) {
		return graphApi.fetchImage(videoId, "picture", imageType);
	}
}
