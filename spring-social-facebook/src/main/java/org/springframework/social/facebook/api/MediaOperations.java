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
package org.springframework.social.facebook.api;

import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;


/**
 * Defines operations for working with albums, photos, and videos.
 * @author Craig Walls
 */
public interface MediaOperations {

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

	/**
	 * Retrieves data for a specific album.
	 * @param albumId the album ID
	 * @return the requested {@link Album} object.
	 */
	Album getAlbum(String albumId);
	
	/**
	 * Creates a new photo album.
	 * @param name the name of the album.
	 * @param description the album's description.
	 * @return the ID of the newly created album.
	 */
	String createAlbum(String name, String description);

	/**
	 * Retrieves an album's image as an array of bytes. Returns the image in Facebook's "normal" type.
	 * @param albumId the album ID
	 * @return an array of bytes containing the album's image.
	 */
	byte[] getAlbumImage(String albumId);

	/**
	 * Retrieves an album's image as an array of bytes.
	 * @param albumId the album ID
	 * @param imageType the image type (eg., small, normal, large. square)
	 * @return an array of bytes containing the album's image.
	 */
	byte[] getAlbumImage(String albumId, ImageType imageType);

	/**
	 * Retrieves photo data from a specific album.
	 * @param albumId the album's ID
	 * @return a list of {@link Photo}s in the specified album.
	 */
	List<Photo> getPhotos(String albumId);
	
	/**
	 * Retrieve data for a specified photo.
	 * @param photoId the photo's ID
	 * @return the requested {@link Photo}
	 */
	Photo getPhoto(String photoId);
	
	/**
	 * Retrieves a photo's image as an array of bytes. Returns the image in Facebook's "normal" type.
	 * @param photoId the photo ID
	 * @return an array of bytes containing the photo's image.
	 */
	byte[] getPhotoImage(String photoId);

	/**
	 * Retrieves a photo's image as an array of bytes.
	 * @param photoId the photo ID
	 * @param imageType the image type (eg., small, normal, large. square)
	 * @return an array of bytes containing the photo's image.
	 */
	byte[] getPhotoImage(String photoId, ImageType imageType);
	
	/**
	 * Uploads a photo to an album created specifically for photos uploaded by the application.
	 * If no album exists for the application, it will be created.
	 * @param photo A {@link Resource} for the photo data. The given Resource must implement the getFilename() method (such as {@link FileSystemResource} or {@link ClassPathResource}).
	 * @return the ID of the photo.
	 */
	String uploadPhoto(Resource photo);
	
	/**
	 * Uploads a photo to an album created specifically for photos uploaded by the application.
	 * If no album exists for the application, it will be created.
	 * @param photo A {@link Resource} for the photo data. The given Resource must implement the getFilename() method (such as {@link FileSystemResource} or {@link ClassPathResource}).
	 * @param caption A caption describing the photo.
	 * @return the ID of the photo.
	 */
	String uploadPhoto(Resource photo, String caption);
	
	/**
	 * Uploads a photo to a specific album.
	 * @param albumId the ID of the album to upload the photo to.
	 * @param photo A {@link Resource} for the photo data. The given Resource must implement the getFilename() method (such as {@link FileSystemResource} or {@link ClassPathResource}).
	 * @return the ID of the photo.
	 */
	String uploadPhoto(String albumId, Resource photo);
	
	/**
	 * Uploads a photo to a specific album.
	 * @param albumId the ID of the album to upload the photo to.
	 * @param photo A {@link Resource} for the photo data. The given Resource must implement the getFilename() method (such as {@link FileSystemResource} or {@link ClassPathResource}).
	 * @param caption A caption describing the photo.
	 * @return the ID of the photo.
	 */
	String uploadPhoto(String albumId, Resource photo, String caption);
	
	/**
	 * Retrieves a list of videos that the authenticated user is tagged in.
	 * @return a list of {@link Video} belonging to the authenticated user.
	 */
	List<Video> getVideos();

	/**
	 * Retrieves a list of videos that a specified user is tagged in.
	 * @param userId the ID of the user who is tagged in the videos
	 * @return a list of {@link Video} which the specified user is tagged in.
	 */
	List<Video> getVideos(String userId);
	
	/**
	 * Retrieves data for a specific video.
	 * @param videoId the ID of the video.
	 * @return the requested {@link Video} data.
	 */
	Video getVideo(String videoId);
	
	/**
	 * Retrieves a video's image as an array of bytes. Returns the image in Facebook's "normal" type.
	 * @param videoId the video ID
	 * @return an array of bytes containing the video's image.
	 */
	byte[] getVideoImage(String videoId);

	/**
	 * Retrieves a video's image as an array of bytes.
	 * @param videoId the video ID
	 * @param imageType the image type (eg., small, normal, large. square)
	 * @return an array of bytes containing the video's image.
	 */
	byte[] getVideoImage(String videoId, ImageType imageType);
}
