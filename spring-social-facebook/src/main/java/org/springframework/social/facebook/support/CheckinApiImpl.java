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
package org.springframework.social.facebook.support;

import java.util.List;

import org.springframework.social.facebook.Checkin;
import org.springframework.social.facebook.CheckinApi;
import org.springframework.social.facebook.support.extractors.ResponseExtractors;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class CheckinApiImpl extends AbstractFacebookApi implements CheckinApi {
	public CheckinApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
	}
	
	public List<Checkin> getCheckins() {
		return getCheckins("me");
	}

	public List<Checkin> getCheckins(String objectId) {
		return getObjectConnection(objectId, "checkins", ResponseExtractors.CHECKIN_EXTRACTOR);
	}

	public Checkin getCheckin(String checkinId) {
		return getObject(checkinId, ResponseExtractors.CHECKIN_EXTRACTOR);
	}
	
	public String checkin(String placeId, double latitude, double longitude) {
		return checkin(placeId, latitude, longitude, null, (String[]) null);
	}
	
	public String checkin(String placeId, double latitude, double longitude, String message, String... tags) {
		MultiValueMap<String, String> data = new LinkedMultiValueMap<String, String>();
		data.set("place", placeId);
		data.set("coordinates", "{\"latitude\":\"" + latitude+"\",\"longitude\":\"" + longitude + "\"}");
		if(message != null) {
			data.set("message", message);
		}
		
		if(tags != null && tags.length > 0) {
			String tagsValue = tags[0];
			for(int i=1; i < tags.length; i++) {
				tagsValue += "," + tags[i];
			}
			data.set("tags", tagsValue);
		}
		return (String) publish("me", "checkins", data).get("id");
	}
}
