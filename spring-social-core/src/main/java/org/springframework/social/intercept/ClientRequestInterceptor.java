package org.springframework.social.intercept;

/*
 * This interface is a placeholder while I await Arjen's proper implementation of RestTemplate interceptors.
 * 
 * Once Arjen has finished his work on the real interceptor implementation, this can go away and all implementations
 * can be switched over to implement his interface.
 */
public interface ClientRequestInterceptor {
	void beforeExecution(ClientRequest request);
}
