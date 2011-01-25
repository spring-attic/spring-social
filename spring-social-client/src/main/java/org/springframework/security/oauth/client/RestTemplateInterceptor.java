package org.springframework.security.oauth.client;

/*
 * This interface is a placeholder while I await Arjen's proper implementation of RestTemplate interceptors.
 * 
 * Once Arjen has finished his work on the real interceptor implementation, this can go away and all implementations
 * can be switched over to implement his interface.
 */
public interface RestTemplateInterceptor {
	void beforeExecution(ClientRequest request);
}
