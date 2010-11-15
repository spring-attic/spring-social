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
package org.springframework.social.greenhouse;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Model class that represents a Greenhouse event session.
 * 
 * @author Craig Walls
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventSession {
	@JsonProperty
	private long id;

	@JsonProperty
	private String title;

	@JsonProperty
	private String description;

	@JsonProperty
	private Date startTime;

	@JsonProperty
	private Date endTime;

	@JsonProperty
	private String hashtag;

	@JsonProperty
	private float rating;

	@JsonProperty
	private boolean favorite;

	@JsonProperty
	private Room room;

	@JsonProperty
	private List<Leader> leaders;

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getHashtag() {
		return hashtag;
	}

	public float getRating() {
		return rating;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public Room getRoom() {
		return room;
	}

	public List<Leader> getLeaders() {
		return leaders;
	}
	
	public void setLeaders(List<Leader> leaders) {
		this.leaders = leaders;
	}
	
	public String getJoinedLeaders(String separator) {		
		String s = "";
		int size = leaders.size();
		
		for (int i = 0; i < size; i++) {
			Leader leader = leaders.get(i);
			s += leader.getName();
			
			if (i < size-1) {
				s += separator;
			}
		}
		
		return s;
	}

	public static class Room {
		@JsonProperty
		private long parentId;

		@JsonProperty
		private long id;

		@JsonProperty
		private String label;

		public long getParentId() {
			return parentId;
		}

		public long getId() {
			return id;
		}

		public String getLabel() {
			return label;
		}
	}

	public static class Leader {
		@JsonProperty
		private String name;

		@JsonProperty
		private String firstName;

		@JsonProperty
		private String lastName;

		public String getName() {
			return name;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}
	}
}
