package org.springframework.social.facebook.support.extractors;

import java.util.Date;
import java.util.Map;

import org.springframework.social.facebook.UserLike;

public class UserLikeResponseExtractor extends AbstractResponseExtractor<UserLike> {

	public UserLike extractObject(Map<String, Object> likeMap) {
		String id = (String) likeMap.get("id");
		String name = (String) likeMap.get("name");
		String category = (String) likeMap.get("category");
		Date createdTime = toDate((String) likeMap.get("created_time"));
		return new UserLike(id, name, category, createdTime);
	}

}
