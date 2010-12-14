package org.springframework.social.connect;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.encrypt.NoOpStringEncryptor;

public class JdbcAccountConnectionRepositoryTest {
	private JdbcTemplate jdbcTemplate;
	private JdbcAccountConnectionRepository repository;

	@Before
	public void setup() {
		jdbcTemplate = mock(JdbcTemplate.class);
		repository = new JdbcAccountConnectionRepository(jdbcTemplate, new NoOpStringEncryptor());
	}

	@Test
	public void getProviderAccountId() {
		String customQuery = "select screenName from twitterAccounts where accountId = ? and provider = ?";
		repository.setProviderAccountIdByMemberAndProviderQuery(customQuery);
		repository.getProviderAccountId("habuma", "twitter");
		verify(jdbcTemplate).queryForObject(eq(customQuery), eq(String.class), eq("habuma"), eq("twitter"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getAccessToken() {
		String customQuery = "select token, secret from tokentable where accountId = ? and provider = ?";
		repository.setAccessTokenByMemberAndProviderQuery(customQuery);
		repository.getAccessToken("habuma", "twitter");
		verify(jdbcTemplate).queryForObject(eq(customQuery), any(RowMapper.class), eq("habuma"), eq("twitter"));
	}

	@Test
	public void isConnected() {
		String customQuery = "select exists(select 1 from connections where account = ? and socialservice = ?)";
		repository.setCountConnectionsQuery(customQuery);
		repository.isConnected("habuma", "twitter");
		verify(jdbcTemplate).queryForInt(eq(customQuery), eq("habuma"), eq("twitter"));
	}

	@Test
	public void disconnect() {
		String customQuery = "delete from connections where member = ? and socialnetwork = ?";
		repository.setDeleteAccountConnectionQuery(customQuery);
		repository.disconnect(1234L, "twitter");
		verify(jdbcTemplate).update(eq(customQuery), eq(1234L), eq("twitter"));
	}

	@Test
	public void addConnection() {
		String customQuery = "insert into AccountConnection (member, provider, accessToken, secret, accountId, profileUrl) values (?, ?, ?, ?, ?, ?)";
		repository.setInsertAccountConnectionQuery(customQuery);
		repository.addConnection(4321L, "linkedin", new OAuthToken("access_token", "token_secret"), "habuma",
				"http://www.linkedin.com/habuma");
		verify(jdbcTemplate).update(eq(customQuery), eq(4321L), eq("linkedin"), eq("access_token"), eq("token_secret"),
				eq("habuma"), eq("http://www.linkedin.com/habuma"));
	}

}
