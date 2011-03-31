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
