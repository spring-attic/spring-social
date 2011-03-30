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
package org.springframework.social.connect.spi;

public class ProviderProfile {

	private String id;
	
	private String name;

	private String url;
	
	private String pictureUrl;

	public ProviderProfile(String id, String name, String url, String pictureUrl) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.pictureUrl = pictureUrl;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}
	
}
