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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Model class representing a photo.
 * @author Craig Walls
 */
public class Photo {
	private String id;
	
	private String name;
	
	private Reference from;
	
	private String link;
	
	private String icon;
	
	private Integer position;
	
	private Date createdTime;
	
	private Date updatedTime;
	
	private List<Tag> tags;
	
	private Image sourceImage;
	
	private Image smallImage;

	private Image albumImage;
		
	private Image tinyImage;
	
	private Photo(String id, Reference from, String link, String icon, Date createdTime, List<Image> images) {
		this.id = id;
		this.from = from;
		this.link = link;
		this.icon = icon;
		this.createdTime = createdTime;
		
		Map<Character, Image> imageMap = extractImages(images);
		this.tinyImage = imageMap.get('t');
		this.sourceImage = imageMap.get('n');
		this.smallImage = imageMap.get('s');
		this.albumImage = imageMap.get('a');
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Reference getFrom() {
		return from;
	}

	public String getLink() {
		return link;
	}

	public String getIcon() {
		return icon;
	}

	public Integer getPosition() {
		return position;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public Image getSourceImage() {
		return sourceImage;
	}
	
	public Image getSmallImage() {
		return smallImage;
	}
	
	public Image getAlbumImage() {
		return albumImage;
	}
	
	public Image getTinyImage() {
		return tinyImage;
	}
	
	public List<Tag> getTags() {
		return tags;
	}
	
	public static class Image {
		
		private final int width;
		
		private final int height;
		
		private final String source;
		
		public Image(String source, int width, int height) {
			this.source = source;
			this.width = width;
			this.height = height;
			
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public String getSource() {
			return source;
		}
	}

	private Map<Character, Image> extractImages(List<Image> images) {
		Map<Character, Image> imageMap = new HashMap<Character, Image>();
		
		for (Image image : images) {
			imageMap.put(image.getSource().charAt(image.getSource().length() - 5), image);
		}
	
		return imageMap;
	}
}

