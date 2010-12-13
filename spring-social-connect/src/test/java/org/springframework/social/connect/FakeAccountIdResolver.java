package org.springframework.social.connect;

public class FakeAccountIdResolver implements AccountIdResolver {
	private Long accountId = 1L;

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public java.io.Serializable resolveAccountId() {
		return accountId;
	};
}
