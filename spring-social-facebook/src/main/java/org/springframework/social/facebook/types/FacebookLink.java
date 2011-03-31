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
package org.springframework.social.facebook.types;

/**
 * Model class representing a link to be posted to a users Facebook wall.
 * @author Craig Walls
 */
public class FacebookLink {
	private final String link;
	private final String name;
	private final String caption;
	private final String description;

	/**
	 * Creates a FacebookLink.
	 * 
	 * @param link The link's URL
	 * @param name The name of the link
	 * @param caption A caption to be displayed with the link
	 * @param description The description of the link
	 */
	public FacebookLink(String link, String name, String caption, String description) {
		this.link = link;
		this.name = name;
		this.caption = caption;
		this.description = description;	
	}

	public String getLink() {
    	return link;
    }

	public String getName() {
    	return name;
    }

	public String getCaption() {
    	return caption;
    }

	public String getDescription() {
    	return description;
    }
}
