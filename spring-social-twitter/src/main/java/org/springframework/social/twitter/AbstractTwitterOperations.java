package org.springframework.social.twitter;

class AbstractTwitterOperations {
	
	private final LowLevelTwitterApi lowLevelApi;

	public AbstractTwitterOperations(LowLevelTwitterApi lowLevelApi) {
		this.lowLevelApi = lowLevelApi;
	}
	
	protected LowLevelTwitterApi getLowLevelTwitterApi() {
		return lowLevelApi;
	}
	
	protected void requireUserAuthorization() {
		if(!lowLevelApi.isAuthorizedForUser()) {
			throw new IllegalStateException("User authorization required: TwitterTemplate must be created with OAuth credentials to perform this operation.");
		}
	}
}
