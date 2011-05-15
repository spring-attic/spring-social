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

/**
 * Model class representing a Facebook page.
 * A Facebook page could represent any number of things, including businesses, government agencies, people, organizations, etc.
 * A page may even represent a place that a user may check into using Facebook Places, if the page has location data.
 * The data available for a page will vary depending on the category it belongs to and what data the page administrator has entered.
 * @author Craig Walls
 */
public class Page {

	private final String id;

	private final String name;

	private final String category;

	private final String link;

	private String description;
	
	private Location location;
	
	private String website;
	
	private String picture;
	
	private String phone;
	
	private String affiliation;
	
	private String companyOverview;
	
	private int fanCount;
	
	private int likes;
	
	private int checkins;

	public Page(String id, String name, String link, String category) {
		this.id = id;
		this.name = name;
		this.link = link;
		this.category = category;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getLink() {
		return link;
	}

	public String getCategory() {
		return category;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Location getLocation() {
		return location;
	}

	public String getWebsite() {
		return website;
	}

	public String getPicture() {
		return picture;
	}

	public String getPhone() {
		return phone;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public String getCompanyOverview() {
		return companyOverview;
	}
	
	public int getFanCount() {
		return fanCount;
	}

	public int getLikes() {
		return likes;
	}
	
	public int getCheckins() {
		return checkins;
	}

}
