package org.springframework.social.facebook.support;

import java.util.List;

import org.springframework.social.facebook.Group;
import org.springframework.social.facebook.GroupApi;
import org.springframework.social.facebook.Reference;
import org.springframework.social.facebook.support.extractors.ResponseExtractors;
import org.springframework.web.client.RestTemplate;

public class GroupApiImpl extends AbstractFacebookApi implements GroupApi {

	public GroupApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
	}
	
	public Group getGroup(String groupId) {
		return getObject(groupId, ResponseExtractors.GROUP_EXTRACTOR);
	}
	
	public List<Reference> getMembers(String groupId) {
		return getObjectConnection(groupId, "members", ResponseExtractors.REFERENCE_EXTRACTOR);
	}
	
}
