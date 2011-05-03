package org.springframework.social.facebook.api.impl;

import java.util.List;

import org.springframework.social.facebook.api.GraphApi;
import org.springframework.social.facebook.api.FacebookThread;
import org.springframework.social.facebook.api.ThreadOperations;

public class ThreadTemplate implements ThreadOperations {
	private final GraphApi graphApi;
	
	public ThreadTemplate(GraphApi graphApi) {
		this.graphApi = graphApi;
	}

	public List<FacebookThread> getAllThreads(String userId) {
		return graphApi.fetchConnections(userId, "threads", ThreadList.class).getList();
	}

	public FacebookThread getThread(String threadId) {
		return graphApi.fetchObject(threadId, FacebookThread.class);
	}

}
