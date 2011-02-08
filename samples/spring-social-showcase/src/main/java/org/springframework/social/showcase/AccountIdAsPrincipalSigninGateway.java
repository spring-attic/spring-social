package org.springframework.social.showcase;

import java.io.Serializable;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.web.connect.SignInControllerGateway;

public class AccountIdAsPrincipalSigninGateway implements SignInControllerGateway {

	@Override
	public void signIn(Serializable accountId) {
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(accountId, null, null));
	}

}
