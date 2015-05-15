/*
 * Copyright 2015 the original author or authors.
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
package org.springframework.social.support;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Builds up a URI from individual URI components. Ensures that query parameters are application/x-www-form-urlencoded.
 * @author Craig Walls
 */
public class URIBuilder {
	
	private final String baseUri;
	
	private MultiValueMap<String, String> parameters;

	private URIBuilder(String baseUri) {
		this.baseUri = baseUri;
		parameters = new LinkedMultiValueMap<String, String>();
	}
	
	/**
	 * Creates a URIBuilder with a base URI string as the starting point
	 * @param baseUri a String URI to use as a starting point for the builder
	 * @return the URIBuilder
	 */
	public static URIBuilder fromUri(String baseUri) {
		return new URIBuilder(baseUri);
	}

	/**
	 * Creates a URIBuilder with a base URI string as the starting point
	 * @param baseUri a URI to use as a starting point for the builder
	 * @return the URIBuilder
	 */
	public static URIBuilder fromUri(URI baseUri) {
		return new URIBuilder(baseUri.toString());
	}

	/**
	 * Adds a query parameter to the URI
	 * @param name the parameter name
	 * @param value the parameter value
	 * @return the URIBuilder
	 */
	public URIBuilder queryParam(String name, String value) {
		parameters.add(name, value);
		return this;
	}

	/**
	 * Adds a query parameters to the URI
	 * @param params a Map of parameters to add the the URI
	 * @return the URIBuilder
	 */
	public URIBuilder queryParams(MultiValueMap<String, String> params) {
		parameters.putAll(params);
		return this;
	}

	/**
	 * Builds the URI
	 * @return the URI
	 */
	public URI build() {
		try {
			StringBuilder builder = new StringBuilder();
			Set<Entry<String, List<String>>> entrySet = parameters.entrySet();
			for (Iterator<Entry<String, List<String>>> entryIt = entrySet.iterator(); entryIt.hasNext();) {
				Entry<String, List<String>> entry = entryIt.next();
				String name = entry.getKey();
				List<String> values = entry.getValue();
				for(Iterator<String> valueIt = values.iterator(); valueIt.hasNext();) {
					String value = valueIt.next();
					builder.append(formEncode(name)).append("=");
					if(value != null) {
						builder.append(formEncode(value));
					}
					if(valueIt.hasNext()) {
						builder.append("&");
					}
				}
				if(entryIt.hasNext()) {
					builder.append("&");
				}
			}
			
			String queryDelimiter = "?";
			if(URI.create(baseUri).getQuery() != null) {
				queryDelimiter = "&";
			}
			return new URI(baseUri + (builder.length() > 0 ? queryDelimiter + builder.toString() : ""));
		} catch (URISyntaxException e) {
			throw new URIBuilderException("Unable to build URI: Bad URI syntax", e);
		}
	}
	
	private String formEncode(String data) {
		try {
			return URLEncoder.encode(data, "UTF-8");
		}
		catch (UnsupportedEncodingException wontHappen) {
			throw new IllegalStateException(wontHappen);
		}
	}
}
