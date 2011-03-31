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
package org.springframework.social.facebook.support.extractors;

import java.util.Date;
import java.util.Map;

import org.springframework.social.facebook.RsvpStatus;
import org.springframework.social.facebook.UserEvent;

public class UserEventResponseExtractor extends AbstractResponseExtractor<UserEvent> {

	public UserEvent extractObject(Map<String, Object> eventMap) {
		String id = (String) eventMap.get("id");
		String name = (String) eventMap.get("name");
		Date startTime = toDate((String) eventMap.get("start_time"));
		Date endTime = toDate((String) eventMap.get("end_time"));
		RsvpStatus rsvpStatus = RsvpStatus.valueOf(((String) eventMap.get("rsvp_status")).toUpperCase());
		String location = (String) eventMap.get("location");
		return new UserEvent(id, name, startTime, endTime, rsvpStatus, location);
	}

}
