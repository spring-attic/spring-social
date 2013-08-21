package org.springframework.social.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.social.connect.UsersConnectionRepository;

/**
 * Configurer that adds a {@link SocialAuthenticationProvider} to Spring Security's authentication manager.
 * @author Craig Walls
 */
public class SpringSocialAuthenticationConfigurer extends SecurityConfigurerAdapter<AuthenticationManager, AuthenticationManagerBuilder> {

	private UsersConnectionRepository usersConnectionRepository;

	private SocialUserDetailsService socialUsersDetailsService;
	
	public SpringSocialAuthenticationConfigurer(UsersConnectionRepository usersConnectionRepository, SocialUserDetailsService socialUsersDetailsService) {
		this.usersConnectionRepository = usersConnectionRepository;
		this.socialUsersDetailsService = socialUsersDetailsService;
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder builder) throws Exception {
		builder.authenticationProvider(socialAuthenticationProvider());
	}
	
	@Bean
	public AuthenticationProvider socialAuthenticationProvider() {
		return new SocialAuthenticationProvider(usersConnectionRepository, socialUsersDetailsService);
	}
	
}
