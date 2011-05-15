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
package org.springframework.social.tripit.api.impl;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.oauth1.AbstractOAuth1ApiTemplate;
import org.springframework.social.tripit.api.Trip;
import org.springframework.social.tripit.api.TripIt;
import org.springframework.social.tripit.api.TripItProfile;
import org.springframework.web.client.RestTemplate;

/**
 * The central class for interacting with TripIt.
 * <p>
 * TripIt operations require OAuth 1 authentication. Therefore TripIt template
 * must be given the minimal amount of information required to sign requests to
 * the TripIt API with an OAuth <code>Authorization</code> header.
 * </p>
 * @author Craig Walls
 */
public class TripItTemplate extends AbstractOAuth1ApiTemplate implements TripIt {

	/**
	 * Constructs a TripItTemplate with the minimal amount of information required to sign requests with an OAuth <code>Authorization</code> header.
	 * @param consumerKey the application's API key
	 * @param consumerSecret the application's API secret
	 * @param accessToken an access token acquired through OAuth authentication with LinkedIn
	 * @param accessTokenSecret an access token secret acquired through OAuth authentication with LinkedIn
	 */
	public TripItTemplate(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		super(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		registerTripItJsonModule(getRestTemplate());
	}

	public String getProfileId() {
		return getUserProfile().getId();
	}

	public String getProfileUrl() {
		return getUserProfile().getProfileUrl();
	}

	public TripItProfile getUserProfile() {
		return getRestTemplate().getForObject("https://api.tripit.com/v1/get/profile?format=json", TripItProfile.class);
	}

	public List<Trip> getUpcomingTrips() {
		return getRestTemplate().getForObject("https://api.tripit.com/v1/list/trip/traveler/true/past/false?format=json", TripList.class).getList();
	}

	// private helper
	
	private void registerTripItJsonModule(RestTemplate restTemplate) {
		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
		for (HttpMessageConverter<?> converter : converters) {
			if(converter instanceof MappingJacksonHttpMessageConverter) {
				MappingJacksonHttpMessageConverter jsonConverter = (MappingJacksonHttpMessageConverter) converter;
				ObjectMapper objectMapper = new ObjectMapper();				
				objectMapper.registerModule(new TripItModule());
				jsonConverter.setObjectMapper(objectMapper);
			}
		}
	}

}
