package org.springframework.social.showcase;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcUserRepository implements UserRepository {
	private final JdbcTemplate jdbcTemplate;

	@Inject
	public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public ShowcaseUser findUserByUsername(String username) {
		return jdbcTemplate.queryForObject("select firstName, lastName, email from Account where username = ?",
				new RowMapper<ShowcaseUser>() {
					public ShowcaseUser mapRow(ResultSet rs, int rowNum) throws SQLException {
						return new ShowcaseUser(rs.getString("firstName"), rs.getString("lastName"), rs
								.getString("email"));
					}
				}, username);
	}

}
