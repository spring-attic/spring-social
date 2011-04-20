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
package org.springframework.social.facebook.api;

import java.util.Date;
import java.util.List;


/**
 * Model class representing a feed Post to announce a Checkin on a user's wall.
 * Note that although a CheckinPost contains some details about a Checkin, it is not the Checkin object itself.
 * To get the Checkin, get the Checkin ID by calling getCheckinId() and then call getChecking(checkinId) on CheckinOperations.
 * @author Craig Walls
 */
public class CheckinPost extends Post {
	
	private Place place;
	
	private List<Tag> tags;
	
	public CheckinPost(String id, Reference from, Date createdTime, Date updatedTime) {
		super(id, from, createdTime, updatedTime);
	}

	public Place getPlace() {
		return place;
	}
	
	public List<Tag> getTags() {
		return tags;
	}
	
	public String checkinId() {
		return this.getId().split("_")[1];
	}
}
