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

import java.util.Map;

import org.springframework.social.facebook.EventInvitee;
import org.springframework.social.facebook.RsvpStatus;

public class InviteeResponseExtractor extends AbstractResponseExtractor<EventInvitee> {
	
	public EventInvitee extractObject(Map<String, Object> inviteeMap) {
		return new EventInvitee((String) inviteeMap.get("id"), 
				(String) inviteeMap.get("name"), 
				RsvpStatus.valueOf(((String) inviteeMap.get("rsvp_status")).toUpperCase()));
	}
	
}
