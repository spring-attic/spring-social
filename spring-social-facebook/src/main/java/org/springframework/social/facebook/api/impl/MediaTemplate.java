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
package org.springframework.social.facebook.api.impl;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.social.facebook.api.Album;
import org.springframework.social.facebook.api.GraphApi;
import org.springframework.social.facebook.api.ImageType;
import org.springframework.social.facebook.api.MediaOperations;
import org.springframework.social.facebook.api.Photo;
import org.springframework.social.facebook.api.Video;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

class MediaTemplate implements MediaOperations {

	private final GraphApi graphApi;
	
	private final RestTemplate restTemplate;

	public MediaTemplate(GraphApi graphApi, RestTemplate restTemplate) {
		this.graphApi = graphApi;
		this.restTemplate = restTemplate;
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
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
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
		return graphApi.fetchConnections(albumId, "photos", PhotoList.class).getList();
	}
	
	public Photo getPhoto(String photoId) {
		return graphApi.fetchObject(photoId, Photo.class);
	}
	
	public byte[] getPhotoImage(String photoId) {
		return getPhotoImage(photoId, ImageType.NORMAL);
	}
	
	public byte[] getPhotoImage(String photoId, ImageType imageType) {
		return graphApi.fetchImage(photoId, "picture", imageType);
	}

	public String uploadPhoto(Resource photo) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.set("source", photo);
		return graphApi.publish("me", "photos", parts);
	}
	
	public String uploadPhoto(Resource photo, String caption) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.set("source", photo);
		parts.set("message", caption);
		return graphApi.publish("me", "photos", parts);
	}
	
	public String uploadPhoto(String albumId, Resource photo) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.set("source", photo);
		return graphApi.publish(albumId, "photos", parts);
	}
	
	public String uploadPhoto(String albumId, Resource photo, String caption) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.set("source", photo);
		parts.set("message", caption);
		return graphApi.publish(albumId, "photos", parts);
	}
	
	public List<Video> getVideos() {
		return getVideos("me");
	}
	
	public List<Video> getVideos(String userId) {
		return graphApi.fetchConnections(userId, "videos", VideoList.class).getList();
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
	
	@SuppressWarnings("unchecked")
	public String uploadVideo(Resource video) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.set("file", video);
		Map<String, Object> response = restTemplate.postForObject("https://graph-video.facebook.com/me/videos", parts, Map.class);
		return (String) response.get("id");
	}
	
	@SuppressWarnings("unchecked")
	public String uploadVideo(Resource video, String title, String description) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.set("file", video);
		parts.set("title", title);
		parts.set("description", description);
		Map<String, Object> response = restTemplate.postForObject("https://graph-video.facebook.com/me/videos", parts, Map.class);
		return (String) response.get("id");
	}
}
