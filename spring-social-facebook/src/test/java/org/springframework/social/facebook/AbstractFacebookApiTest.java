package org.springframework.social.facebook;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.social.test.client.MockRestServiceServer;

public class AbstractFacebookApiTest {
	protected static final String ACCESS_TOKEN = "someAccessToken";

	protected FacebookTemplate facebook;
	protected MockRestServiceServer mockServer;
	protected HttpHeaders responseHeaders;

	@Before
	public void setup() {
		facebook = new FacebookTemplate(ACCESS_TOKEN);
		mockServer = MockRestServiceServer.createServer(facebook.getRestTemplate());
		responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	}


	private static final DateFormat FB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);

	protected Date toDate(String dateString) {
		try {
			return FB_DATE_FORMAT.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

}
