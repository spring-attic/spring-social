package org.springframework.social.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Florian Lopes
 */
@RunWith(SpringRunner.class)
public class SpringSocialConfigurerTest {

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	private UsersConnectionRepository usersConnectionRepository;

	@Autowired
	private SocialAuthenticationServiceLocator socialAuthenticationServiceLocator;

	@Test
	public void testConfigure() {
		final SecurityFilterChain defaultFilterChain = this.springSecurityFilterChain.getFilterChains().get(0);
		assertTrue(defaultFilterChain.getFilters().stream().anyMatch(filter -> filter instanceof SocialAuthenticationFilter));

		final SocialAuthenticationFilter socialAuthenticationFilter =
				(SocialAuthenticationFilter) defaultFilterChain.getFilters()
						.stream().filter(filter -> filter instanceof SocialAuthenticationFilter).findFirst().orElse(null);
		assertNotNull(socialAuthenticationFilter);

		assertTrue(ReflectionTestUtils.getField(socialAuthenticationFilter, "userIdSource") instanceof AuthenticationNameUserIdSource);

		final ProviderManager providerManager =
				(ProviderManager) ReflectionTestUtils.getField(socialAuthenticationFilter, "authenticationManager");
		assertTrue(providerManager.getProviders().stream().anyMatch(authenticationProvider -> authenticationProvider instanceof SocialAuthenticationProvider));

		assertNotNull(ReflectionTestUtils.getField(socialAuthenticationFilter, "rememberMeServices"));
		final SocialAuthenticationFailureHandler failureHandler =
				(SocialAuthenticationFailureHandler) ReflectionTestUtils.getField(socialAuthenticationFilter, "failureHandler");

		assertEquals("/postFailure", ReflectionTestUtils.getField(failureHandler.getDelegate(), "defaultFailureUrl"));
		final AuthenticationSuccessHandler successHandler =
				(AuthenticationSuccessHandler) ReflectionTestUtils.getField(socialAuthenticationFilter, "successHandler");
		assertEquals("/postLogin", ReflectionTestUtils.getField(successHandler, "defaultTargetUrl"));
		assertEquals("/social-login", ReflectionTestUtils.getField(socialAuthenticationFilter, "filterProcessesUrl"));
		assertEquals("/connectionAdded", ReflectionTestUtils.getField(socialAuthenticationFilter, "connectionAddedRedirectUrl"));
		assertEquals("/signup", ReflectionTestUtils.getField(socialAuthenticationFilter, "signupUrl"));

		assertSame(this.usersConnectionRepository, socialAuthenticationFilter.getUsersConnectionRepository());
		assertSame(this.socialAuthenticationServiceLocator, socialAuthenticationFilter.getAuthServiceLocator());
	}

	@EnableWebSecurity
	@Configuration
	static class SpringSocialSecurityConfig extends WebSecurityConfigurerAdapter {

		// @formatter:off
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.rememberMe()
				.and()
					.apply(new SpringSocialConfigurer()
							.userIdSource(new AuthenticationNameUserIdSource())
							.postLoginUrl("/postLogin")
							.postFailureUrl("/postFailure")
							.signupUrl("/signup")
							.connectionAddedRedirectUrl("/connectionAdded")
							.filterProcessesUrl("/social-login"));
		}
		// @formatter:on

		@Bean
		public UsersConnectionRepository usersConnectionRepository() {
			return mock(UsersConnectionRepository.class);
		}

		@Bean
		public SocialUserDetailsService socialUserDetailsService() {
			return mock(SocialUserDetailsService.class);
		}

		@Bean
		public SocialAuthenticationServiceLocator socialAuthenticationServiceLocator() {
			return mock(SocialAuthenticationServiceLocator.class);
		}
	}
}
