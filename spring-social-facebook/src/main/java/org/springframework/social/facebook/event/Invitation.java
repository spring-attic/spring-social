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
package org.springframework.social.facebook.event;

import java.util.Date;

/**
 * Model class representing an invitation to an event.
 * @author Craig Walls
 */
public class Invitation {

	private final String eventId;

	private final String name;

	private final Date startTime;

	private final Date endTime;

	private final String location;

	private final RsvpStatus rsvpStatus;

	public Invitation(String eventId, String name, Date startTime, Date endTime, RsvpStatus rsvpStatus) {
		this(eventId, name, startTime, endTime, rsvpStatus, null);
	}

	public Invitation(String eventId, String name, Date startTime, Date endTime, RsvpStatus rsvpStatus, String location) {
		this.eventId = eventId;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.location = location;
		this.rsvpStatus = rsvpStatus;
	}

	public String getEventId() {
		return eventId;
	}

	public String getName() {
		return name;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getLocation() {
		return location;
	}

	public RsvpStatus getRsvpStatus() {
		return rsvpStatus;
	};
}
