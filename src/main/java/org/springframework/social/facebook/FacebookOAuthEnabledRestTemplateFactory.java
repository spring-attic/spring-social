package org.springframework.social.facebook;

import static java.util.Arrays.*;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.oauth.OAuthClientRequestSigner;
import org.springframework.social.oauth.OAuthEnabledRestTemplate;
import org.springframework.social.oauth.OAuthEnabledRestTemplateFactory;

// TODO: This will probably go away with the new API changes
public class FacebookOAuthEnabledRestTemplateFactory extends OAuthEnabledRestTemplateFactory {
	protected OAuthClientRequestSigner getRequestSigner() {
		return new FacebookClientRequestSigner(getAccessTokenServices(), "Facebook");
	}

	protected OAuthEnabledRestTemplate createRestTemplate() {
		OAuthEnabledRestTemplate restTemplate = super.createRestTemplate();

		// Go figure: Facebook uses "text/javascript" as the JSON content type
		MappingJacksonHttpMessageConverter jsonMessageConverter = new MappingJacksonHttpMessageConverter();
		jsonMessageConverter.setSupportedMediaTypes(asList(new MediaType("text", "javascript")));
		restTemplate.getMessageConverters().add(jsonMessageConverter);

		return restTemplate;
	}
}
