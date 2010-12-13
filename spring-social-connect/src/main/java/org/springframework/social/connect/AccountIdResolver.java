package org.springframework.social.connect;

import java.io.Serializable;

public interface AccountIdResolver {
	Serializable resolveAccountId();
}
