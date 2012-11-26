package org.springframework.social.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionKey;

/**
 * 
 * @author Craig Walls
 * @author Stefan Fussennegger
 */
class SignInAttempts {
	
	private static final String ATTR_SIGN_IN_ATTEMPT = SignInAttempts.class.getName();
	
	private Map<ConnectionKey, ConnectionData> attempts = new HashMap<ConnectionKey, ConnectionData>();
	
	/**
	 * @return always <code>true</code>
	 */
	public static boolean add(HttpSession session, ConnectionData data) {
		SignInAttempts signInAttempts = (SignInAttempts) session.getAttribute(ATTR_SIGN_IN_ATTEMPT);
		if (signInAttempts == null) {
			session.setAttribute(ATTR_SIGN_IN_ATTEMPT, signInAttempts = new SignInAttempts()); 
		}
		return signInAttempts.addAttempt(data);
	}
	
	/**
	 * @return unmodifiable list
	 */
	public static Collection<ConnectionData> get(HttpSession session) {
		SignInAttempts signInAttempts = (SignInAttempts) session.getAttribute(ATTR_SIGN_IN_ATTEMPT);
		if(signInAttempts == null) {
			return Collections.emptyList();
		} else {
			return signInAttempts.getAttempts();
		}
	}

	public static boolean remove(HttpSession session, ConnectionKey key) {
		SignInAttempts signInAttempts = (SignInAttempts) session.getAttribute(ATTR_SIGN_IN_ATTEMPT);
		return signInAttempts != null ? signInAttempts.removeAttempt(key) : false;
	}
	
	public static void clear(HttpSession session) {
		session.removeAttribute(ATTR_SIGN_IN_ATTEMPT);
	}
	
	private SignInAttempts() {
	}
	
	/**
	 * @return <code>true</code> if previous connection was replaced
	 */
	private boolean addAttempt(ConnectionData data) {
		return attempts.put(key(data), data) != null;
	}
	
	private boolean removeAttempt(ConnectionKey key) {
		return attempts.remove(key) != null;
	}
	
	private Collection<ConnectionData> getAttempts() {
		return attempts.values();
	}
	
	public static ConnectionKey key(ConnectionData data) {
		return new ConnectionKey(data.getProviderId(), data.getProviderUserId());
	}
}