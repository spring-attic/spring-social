package org.springframework.social.oauth;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpMethod;

public interface ClientRequest {
	/**
	 * Adds a header to the request.
	 * 
	 * @param headerName
	 *            the name of the header
	 * @param headerValue
	 *            the value to be assigned to the header
	 */
	void addHeader(String headerName, String headerValue);

	/**
	 * Adds a parameter to the request.
	 * 
	 * @param parameterName
	 *            the name of the parameter
	 * @param parameterValue
	 *            the value to assign to the parameter
	 */
	void setParameter(String parameterName, String parameterValue);

	/**
	 * Retrieves the requests query parameters.
	 * 
	 * @return the query parameters in the form of a {@link Map}
	 */
	Map<String, String> getQueryParameters();

	/**
	 * Retrieves the request's HTTP Method.
	 * 
	 * @return the {@link HttpMethod} of the request
	 */
	HttpMethod getHttpMethod();

	/**
	 * Retrieves the request's URI
	 * 
	 * @return A {@link URI} for the request
	 */
	URI getURI();
}