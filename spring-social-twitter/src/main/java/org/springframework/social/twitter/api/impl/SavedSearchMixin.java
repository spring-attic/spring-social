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
package org.springframework.social.twitter.api.impl;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Mixin class for adding Jackson annotations to SavedSearch.
 * @author Craig Walls
 */
@JsonIgnoreProperties(ignoreUnknown=true)
abstract class SavedSearchMixin {
	@JsonCreator
	SavedSearchMixin(
			@JsonProperty("id") long id,
			@JsonProperty("name") String name,
			@JsonProperty("query") String query,
			@JsonProperty("position") int position,
			@JsonProperty("created_at") @JsonDeserialize(using=TimelineDateDeserializer.class) Date createdAt) {}
}
