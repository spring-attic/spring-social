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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Message converter that reads form-encoded data into a flat Map&lt;String, String&gt;.
 * In contrast to FormHttpMessageConverter which reads form-encoded data into a {@link MultiValueMap}.
 * Created primarily for use by {@link OAuth2Template} to handle cases where access token is returned as form-encoded data.
 * @author Craig Walls
 */
public class FormMapHttpMessageConverter implements HttpMessageConverter<Map<String, String>> {

	private final FormHttpMessageConverter delegate;
	
	public FormMapHttpMessageConverter() {
		delegate = new FormHttpMessageConverter();
	}
	
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		if (!Map.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (mediaType == null) {
			return true;
		}
		for (MediaType supportedMediaType : getSupportedMediaTypes()) {
			// we can't read multipart
			if (!supportedMediaType.equals(MediaType.MULTIPART_FORM_DATA) &&
				supportedMediaType.includes(mediaType)) {
				return true;
			}
		}
		return false;
	}

	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		if (!Map.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (mediaType == null || MediaType.ALL.equals(mediaType)) {
			return true;
		}
		for (MediaType supportedMediaType : getSupportedMediaTypes()) {
			if (supportedMediaType.isCompatibleWith(mediaType)) {
				return true;
			}
		}
		return false;
	}

	public List<MediaType> getSupportedMediaTypes() {
		return delegate.getSupportedMediaTypes();
	}

	public Map<String, String> read(Class<? extends Map<String, String>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		LinkedMultiValueMap<String, String> lmvm = new LinkedMultiValueMap<String, String>();
		@SuppressWarnings("unchecked")
		Class<LinkedMultiValueMap<String, String>> mvmClazz = (Class<LinkedMultiValueMap<String, String>>) lmvm.getClass();
		MultiValueMap<String, String> mvm = delegate.read(mvmClazz, inputMessage);

		return mvm.toSingleValueMap();
	}

	public void write(Map<String, String> t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		MultiValueMap<String, String> mvm = new LinkedMultiValueMap<String, String>();
		mvm.setAll(t);
		delegate.write(mvm, contentType, outputMessage);
	}

}
