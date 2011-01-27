package org.springframework.social.config.xml;

import java.io.Serializable;
import java.util.List;

import org.springframework.social.provider.support.Connection;
import org.springframework.social.provider.support.ConnectionRepository;

// TODO: Should probably just reuse the StubConnectionRepository from the client project, 
//       but an empty implementation is all that's needed here. And I will need to figure
//       out how to reuse test artifacts from another project in order to do that.
public class StubConnectionRepository implements ConnectionRepository {

	public boolean isConnected(Serializable accountId, String providerId) {
		return false;
	}

	public List<Connection> findConnections(Serializable accountId, String providerId) {
		return null;
	}

	public Connection saveConnection(Serializable accountId, String providerId, Connection connection) {
		return null;
	}

	public void removeConnection(Serializable accountId, String providerId, Long connectionId) {
	}

}
