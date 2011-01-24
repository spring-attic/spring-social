package org.springframework.social.provider.jdbc;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.provider.support.Connection;
import org.springframework.social.provider.support.ConnectionRepository;

/**
 * JDBC-based connection repository implementation.
 * @author Keith Donald
 */
public class JdbcConnectionRepository implements ConnectionRepository {

	private final JdbcTemplate jdbcTemplate;
	
	private final TextEncryptor textEncryptor;

	/**
	 * Creates a JDBC-based connection repository.
	 * @param dataSource the data source
	 * @param textEncryptor the encryptor to use when storing oauth keys
	 */
	public JdbcConnectionRepository(DataSource dataSource, TextEncryptor textEncryptor) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.textEncryptor = textEncryptor;
	}

	public boolean isConnected(Serializable accountId, String providerId) {
		return jdbcTemplate.queryForObject("select exists(select 1 from Connection where accountId = ? and providerId = ?)", Boolean.class, accountId, providerId);
	}

	public List<Connection> findConnections(Serializable accountId, String providerId) {
		return jdbcTemplate.query("select id, accessToken, secret, refreshToken from Connection where accountId = ? and providerId = ? order by id", new RowMapper<Connection>() {
			public Connection mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Connection(rs.getLong("id"), decrypt(rs.getString("accessToken")), decrypt(rs.getString("secret")), decrypt(rs.getString("refreshToken")));
			}
		}, accountId, providerId);
	}

	public void removeConnection(Serializable accountId, String providerId, Long connectionId) {
		jdbcTemplate.update("delete from Connection where accountId = ? and providerId = ? and id = ?", accountId, providerId, connectionId);
	}

	public Connection saveConnection(Serializable accountId, String providerId, Connection connection) {
		try {
			Long connectionId = (Long) insert("insert into Connection (accountId, providerId, accessToken, secret, refreshToken) values (?, ?, ?, ?, ?)",
					accountId, providerId, encrypt(connection.getAccessToken()), encrypt(connection.getSecret()), encrypt(connection.getRefreshToken()));
			return new Connection(connectionId, connection.getAccessToken(), connection.getSecret(), connection.getRefreshToken());
		} catch (DuplicateKeyException e) {
			throw new IllegalArgumentException("Access token is not unique: a connection already exists!", e);
		}
	}

	// internal helpers
	
	private String encrypt(String text) {
		return text != null ? textEncryptor.encrypt(text) : text;
	}
	
	private String decrypt(String encryptedText) {
		return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
	}
	
	private Number insert(String sql, Object... args) {
		// contribute a similar insert method back to Spring JdbcTemplate?
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
	    PreparedStatementCreatorFactory creatorFactory = new PreparedStatementCreatorFactory(sql, sqlTypes(args));
	    creatorFactory.setReturnGeneratedKeys(true);
	    creatorFactory.setGeneratedKeysColumnNames(new String[] { "id" } );
	    jdbcTemplate.update(creatorFactory.newPreparedStatementCreator(Arrays.asList(args)), keyHolder);
	    return keyHolder.getKey();
	}
	
	private int[] sqlTypes(Object... args) {
		int[] sqlTypes = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			sqlTypes[i] = arg != null ? StatementCreatorUtils.javaTypeToSqlParameterType(arg.getClass()) : Types.NULL;
		}
		return sqlTypes;
	}
	
}