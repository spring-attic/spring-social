package org.springframework.social.facebook.api;

import java.util.List;

public interface ThreadOperations {
	List<FacebookThread> getAllThreads(String userId);
	
	FacebookThread getThread(String threadId);
}
