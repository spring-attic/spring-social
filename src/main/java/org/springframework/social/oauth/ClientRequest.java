package org.springframework.social.oauth;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpMethod;

public interface ClientRequest {

	void addHeader(String headerName, String headerValue);

	void setParameter(String parameterName, String parameterValue);

	Map<String, String> getQueryParameters();

	HttpMethod getHttpMethod();

	URI getURI();
}