/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.showcase;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcUserRepository implements UserRepository {

	private final JdbcTemplate jdbcTemplate;
	private final PasswordEncoder passwordEncoder;

	@Inject
	public JdbcUserRepository(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
		this.jdbcTemplate = jdbcTemplate;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void createUser(ShowcaseUser user) throws UsernameAlreadyInUseException {
		try {
			jdbcTemplate.update(
					"insert into ShowcaseUser (firstName, lastName, username, password) values (?, ?, ?, ?)",
					user.getFirstName(), user.getLastName(), user.getUsername(),
					passwordEncoder.encodePassword(user.getPassword(), null));
		} catch (DuplicateKeyException e) {
			throw new UsernameAlreadyInUseException(user.getUsername());
		}
	}

	public ShowcaseUser findUserByUsername(String username) {
		return jdbcTemplate.queryForObject("select username, firstName, lastName from ShowcaseUser where username = ?",
				new RowMapper<ShowcaseUser>() {
					public ShowcaseUser mapRow(ResultSet rs, int rowNum) throws SQLException {
						return new ShowcaseUser(rs.getString("username"), null, rs.getString("firstName"), rs
								.getString("lastName"));
					}
				}, username);
	}

}
