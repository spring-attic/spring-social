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
package org.springframework.social.facebook.api;

import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseExtractor;

/**
 * Defines low-level operations against Facebook's Graph API
 * @author Craig Walls
 */
public interface GraphApi {
	
	/**
	 * Fetches an object, extracting it into the type via the given {@link ResponseExtractor}.
	 * Requires appropriate permission to fetch the object.
	 * @param objectId the Facebook object's ID
	 * @param type the Java type to fetch
	 * @return an Java object representing the requested Facebook object.
	 */
	<T> T fetchObject(String objectId, Class<T> type);

	/**
	 * Fetches an object, extracting it into the type via the given {@link ResponseExtractor}.
	 * Requires appropriate permission to fetch the object.
	 * @param objectId the Facebook object's ID
	 * @param type the Java type to fetch
	 * @param queryParameters query parameters to include in the request
	 * @return an Java object representing the requested Facebook object.
	 */
	<T> T fetchObject(String objectId, Class<T> type, MultiValueMap<String, String> queryParameters);

	/**
	 * Fetches connections, extracting them into a Java type via the given {@link ResponseExtractor}.
	 * Requires appropriate permission to fetch the object connection.
	 * @param objectId the ID of the object to retrieve the connections for.
	 * @param connectionType the connection type.
	 * @param type the Java type to fetch
	 * @param fields the fields to include in the response.
	 * @return a list of Java objects representing the Facebook objects in the connections.
	 */
	<T> T fetchConnections(String objectId, String connectionType, Class<T> type, String... fields);

	/**
	 * Fetches connections, extracting them into a Java type via the given {@link ResponseExtractor}.
	 * Requires appropriate permission to fetch the object connection.
	 * @param objectId the ID of the object to retrieve the connections for.
	 * @param connectionType the connection type.
	 * @param type the Java type to fetch
	 * @param queryParameters query parameters to include in the request
	 * @return a list of Java objects representing the Facebook objects in the connections.
	 */
	<T> T fetchConnections(String objectId, String connectionType, Class<T> type, MultiValueMap<String, String> queryParameters);

	/**
	 * Fetches an image as an array of bytes.
	 * @param objectId the object ID
	 * @param connectionType the connection type
	 * @param imageType the type of image to retrieve (eg., small, normal, large, or square)
	 * @return an image as an array of bytes.
	 */
	byte[] fetchImage(String objectId, String connectionType, ImageType imageType);	

	/**
	 * Publishes data to an object's connection.
	 * Requires appropriate permission to publish to the object connection.
	 * @param objectId the object ID to publish to.
	 * @param connectionType the connection type to publish to.
	 * @param data the data to publish to the connection.
	 * @return the ID of the newly published object.
	 */
	String publish(String objectId, String connectionType, MultiValueMap<String, Object> data);	

	/**
	 * Publishes data to an object's connection. 
	 * Requires appropriate permission to publish to the object connection.
	 * This differs from publish() only in that it doesn't attempt to extract the ID from the response.
	 * This is because some publish operations do not return an ID in the response.
	 * @param objectId the object ID to publish to.
	 * @param connectionType the connection type to publish to.
	 * @param data the data to publish to the connection.
	 */
	void post(String objectId, String connectionType, MultiValueMap<String, String> data);
	
	/**
	 * Deletes an object.
	 * Requires appropriate permission to delete the object.
	 * @param objectId the object ID
	 */
	void delete(String objectId);
	
	/**
	 * Deletes an object connection.
	 * Requires appropriate permission to delete the object connection.
	 * @param objectId the object ID
	 * @param connectionType the connection type
	 */
	void delete(String objectId, String connectionType);
	
	static final String GRAPH_API_URL = "https://graph.facebook.com/";
	
	static final String OBJECT_URL = GRAPH_API_URL + "{objectId}";

	static final String CONNECTION_URL = OBJECT_URL + "/{connection}";


}
