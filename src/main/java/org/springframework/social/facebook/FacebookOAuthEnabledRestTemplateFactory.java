package org.springframework.social.facebook;

import static java.util.Arrays.*;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.oauth.OAuthClientRequestSigner;
import org.springframework.social.oauth.OAuthEnabledRestTemplateFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @deprecated This class is likely to soon go away and be replaced with a new
 *             API
 */
public class FacebookOAuthEnabledRestTemplateFactory extends OAuthEnabledRestTemplateFactory {
	protected OAuthClientRequestSigner getRequestSigner() {
		return new FacebookClientRequestSigner(getAccessTokenServices(), "Facebook");
	}

	protected RestOperations createRestTemplate() {
		RestTemplate restTemplate = (RestTemplate) super.createRestTemplate();

		// Go figure: Facebook uses "text/javascript" as the JSON content type
		MappingJacksonHttpMessageConverter jsonMessageConverter = new MappingJacksonHttpMessageConverter();
		jsonMessageConverter.setSupportedMediaTypes(asList(new MediaType("text", "javascript")));
		restTemplate.getMessageConverters().add(jsonMessageConverter);

		return restTemplate;
	}
}
