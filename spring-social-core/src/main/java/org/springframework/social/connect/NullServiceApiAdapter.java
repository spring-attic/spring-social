package org.springframework.social.connect;

import org.springframework.social.connect.spi.ProviderProfile;
import org.springframework.social.connect.spi.ServiceApiAdapter;

final class NullServiceApiAdapter implements ServiceApiAdapter<Object> {

	public static final NullServiceApiAdapter INSTANCE = new NullServiceApiAdapter();
	
	public boolean test(Object serviceApi) {
		return true;
	}

	public ProviderProfile getProfile(Object serviceApi) {
		return EMPTY_PROFILE;
	}

	public void updateStatus(Object serviceApi, String message) {
	}

	private NullServiceApiAdapter() {}
	
	private static final ProviderProfile EMPTY_PROFILE = new ProviderProfile(null, null, null, null);
}
