package org.springframework.social.connect.support;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionFactory;
import org.springframework.social.connect.ServiceProviderConnectionRepository;

public class JdbcServiceProviderConnectionRepository implements ServiceProviderConnectionRepository {

	private final JdbcTemplate jdbcTemplate;
	
	private final TextEncryptor textEncryptor;

	private final ServiceProviderConnectionFactory connectionFactory;
	
	public JdbcServiceProviderConnectionRepository(JdbcTemplate jdbcTemplate, TextEncryptor textEncryptor, ServiceProviderConnectionFactory connectionFactory) {
		this.jdbcTemplate = jdbcTemplate;
		this.textEncryptor = textEncryptor;
		this.connectionFactory = connectionFactory;
	}

	public Map<String, List<ServiceProviderConnection<?>>> findConnections(Serializable accountId) {
		
		return null;
	}

	public List<ServiceProviderConnection<?>> findConnectionsToProvider(Serializable accountId, String providerId) {
		return jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where accountId = ? and providerId = ? order by providerId, id", connectionMapper, accountId, providerId);
	}

	public List<ServiceProviderConnection<?>> findConnectionsById(List<Long> connectionIds) {
		return jdbcTemplate.query(SELECT_FROM_SERVICE_PROVIDER_CONNECTION + " where id in (?) order by providerId, id", connectionMapper, connectionIds);
	}

	public ServiceProviderConnection<?> findConnectionById(Long connectionId) {
		return null;
	}

	public List<ServiceProviderConnection<?>> findConnectionsToProviderAccount(String providerId, String providerAccountId) {
		return null;
	}

	public ServiceProviderConnection<?> saveConnection(Serializable accountId, String providerId, ServiceProviderConnection<?> connection) {
		return null;
	}

	public void removeConnections(Serializable accountId, String providerId) {
		jdbcTemplate.update("delete from ServiceProviderConnection where accountId = ? and providerId = ?");
	}

	public void removeConnection(Long connectionId) {
		jdbcTemplate.update("delete from ServiceProviderConnection where id = ?");
	}

	private final static String SELECT_FROM_SERVICE_PROVIDER_CONNECTION = "select accountId, providerId, id, providerAccountId, profileName, profileUrl, profilePictureUrl, allowSignIn, accessToken, secret, refreshToken from ServiceProviderConnection";
		
	private final ServiceProviderConnectionRowMapper connectionMapper = new ServiceProviderConnectionRowMapper();
	
	private class ServiceProviderConnectionRowMapper implements RowMapper<ServiceProviderConnection<?>> {
		
		public ServiceProviderConnection<?> mapRow(ResultSet rs, int rowNum) throws SQLException {
			return connectionFactory.createConnection(connectionMemento(rs));
		}
		
		private ServiceProviderConnectionMemento connectionMemento(ResultSet rs) throws SQLException {
			return new ServiceProviderConnectionMemento((Serializable) rs.getObject("accountId"), rs.getString("providerId"), rs.getInt("id"), rs.getString("providerAccountId"),
					rs.getString("profileName"), rs.getString("profileUrl"), rs.getString("profilePictureUrl"),
					rs.getBoolean("allowSignin"),
					decrypt(rs.getString("accessToken")), decrypt(rs.getString("secret")), decrypt(rs.getString("refreshToken")));							
		}
		
		private String decrypt(String encryptedText) {
			return textEncryptor.decrypt(encryptedText);
		}
		
	}
}