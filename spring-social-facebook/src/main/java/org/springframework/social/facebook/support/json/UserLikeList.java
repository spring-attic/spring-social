package org.springframework.social.facebook.support.json;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.facebook.types.UserLike;

public class UserLikeList {
	private final List<UserLike> list;

	@JsonCreator
	public UserLikeList(@JsonProperty("data") List<UserLike> list) {
		this.list = list;
	}

	public List<UserLike> getList() {
		return list;
	}
}
