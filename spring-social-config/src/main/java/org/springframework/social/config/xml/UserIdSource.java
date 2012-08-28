/*
 * Copyright 2012 the original author or authors.
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
package org.springframework.social.config.xml;

import org.springframework.social.connect.ConnectionRepository;

/**
 * Strategy interface for determining a value that uniquely identifies a user within an application.
 * Used when creating a {@link ConnectionRepository} bean to manage connections for a user.
 *  
 * @author Craig Walls
 */
public interface UserIdSource {

	/**
	 * @return A String that uniquely identifies a user within the application.
	 */
	String getUserId();

}