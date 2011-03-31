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

import org.springframework.social.facebook.Group;
import org.springframework.social.facebook.GroupApi;
import org.springframework.social.facebook.Reference;
import org.springframework.social.facebook.support.extractors.GroupResponseExtractor;
import org.springframework.web.client.RestTemplate;

public class GroupApiImpl extends AbstractFacebookApi implements GroupApi {

	private GroupResponseExtractor groupExtractor;

	public GroupApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
		groupExtractor = new GroupResponseExtractor();
	}
	
	public Group getGroup(String groupId) {
		return getObject(groupId, groupExtractor);
	}
	
	public List<Reference> getMembers(String groupId) {
		return getObjectConnection(groupId, "members", referenceExtractor);
	}
	
}
