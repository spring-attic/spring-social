package org.springframework.social.facebook.support.extractors;

import java.util.Date;
import java.util.Map;

import org.springframework.social.facebook.UserEvent;
import org.springframework.social.facebook.UserEvent.RsvpStatus;

public class UserEventResponseExtractor extends AbstractResponseExtractor<UserEvent> {

	public UserEvent extractObject(Map<String, Object> eventMap) {
		String id = (String) eventMap.get("id");
		String name = (String) eventMap.get("name");
		Date startTime = toDate((String) eventMap.get("start_time"));
		Date endTime = toDate((String) eventMap.get("end_time"));
		RsvpStatus rsvpStatus = UserEvent.RsvpStatus.valueOf(((String) eventMap.get("rsvp_status")).toUpperCase());
		String location = (String) eventMap.get("location");
		return new UserEvent(id, name, startTime, endTime, rsvpStatus, location);
	}

}
