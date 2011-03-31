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
package org.springframework.social.facebook;

import java.util.List;

import org.springframework.social.facebook.types.Group;
import org.springframework.social.facebook.types.Reference;

/**
 * Defines operations for retrieving data about groups and group members.
 * @author Craig Walls
 */
public interface GroupApi {	
	
	/**
	 * Retrieve data for a specified group.
	 * @param groupId the ID of the group
	 * @return a {@link Group} object
	 */
	Group getGroup(String groupId);
	
	/**
	 * Retrieves the members of the specified group.
	 * @param groupId the ID of the group
	 * @return a list of {@link Reference}s, one for each member of the group.
	 */
	List<Reference> getMembers(String groupId);
		
}
