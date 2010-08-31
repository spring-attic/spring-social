package org.springframework.social.oauth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;
import org.springframework.social.account.Account;

// TODO: RECONCILE THIS TOKEN SERVICES WITH THE OFFICIAL ONE
public class JdbcOAuthConsumerTokenServices implements OAuthConsumerTokenServices {

	static final String SELECT_TOKEN_SQL = "select provider, accessToken, secret from ConnectedAccount where member = ? and provider = ?";
    static final String INSERT_TOKEN_SQL = "insert into ConnectedAccount (member, provider, accessToken, secret) values (?, ?, ?, ?)";
    static final String DELETE_TOKEN_SQL = "delete from ConnectedAccount where member = ? and provider = ?";

	private JdbcTemplate jdbcTemplate;
	
	public JdbcOAuthConsumerTokenServices(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public OAuthConsumerToken getToken(String resourceId, Object principal) throws AuthenticationException {
		if (!(principal instanceof Account)) {
			throw new BadCredentialsException("Expected principal to be an Account object");
		}

		Account account = (Account) principal;
		List<OAuthConsumerToken> accessTokens = jdbcTemplate.query(SELECT_TOKEN_SQL, new RowMapper<OAuthConsumerToken>() {
			public OAuthConsumerToken mapRow(ResultSet rs, int rowNum) throws SQLException {
				OAuthConsumerToken token = new OAuthConsumerToken();
				token.setAccessToken(true);
				token.setValue(rs.getString("accessToken"));
				token.setResourceId(rs.getString("provider"));
				token.setSecret(rs.getString("secret"));
				return token;
			}
				}, account.getId(), resourceId);
		OAuthConsumerToken accessToken = null;
		if (accessTokens.size() > 0) {
			accessToken = accessTokens.get(0);
		}
		return accessToken;
	}

	public void storeToken(String resourceId, Object memberId, OAuthConsumerToken token) {
		// Don't bother storing request tokens in the DB...session-storage is
		// fine
		// ...but in this impl. there is no session...what do we do about
		// request tokens???
		if (token.isAccessToken()) {
			jdbcTemplate.update(INSERT_TOKEN_SQL, memberId, token.getResourceId(), token.getValue(), token.getSecret());
		}
	}

	public void removeToken(String resourceId, Object memberId) {
		jdbcTemplate.update(DELETE_TOKEN_SQL, memberId, resourceId);
	}
}
