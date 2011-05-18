/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.facebook.api.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.social.BadCredentialsException;
import org.springframework.social.facebook.api.Account;
import org.springframework.social.facebook.api.FacebookLink;
import org.springframework.social.facebook.api.GraphApi;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.api.PageOperations;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class PageTemplate implements PageOperations {

	private final GraphApi graphApi;

	public PageTemplate(GraphApi graphApi) {
		this.graphApi = graphApi;
	}

	public Page getPage(String pageId) {
		return graphApi.fetchObject(pageId, Page.class);
	}

	public boolean isPageAdmin(String pageId) {
		return getAccount(pageId) != null;
	}
	
	public String post(String pageId, String message) {
		String pageAccessToken = getPageAccessToken(pageId);
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.set("message", message);
		map.set("access_token", pageAccessToken);
		return graphApi.publish(pageId, "feed", map);
	}
	
	public String post(String pageId, String message, FacebookLink link) {
		String pageAccessToken = getPageAccessToken(pageId);
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.set("link", link.getLink());
		map.set("name", link.getName());
		map.set("caption", link.getCaption());
		map.set("description", link.getDescription());
		map.set("message", message);
		map.set("access_token", pageAccessToken);
		return graphApi.publish(pageId, "feed", map);
	}

	public String postPhoto(String pageId, String albumId, Resource photo) {
		return postPhoto(pageId, albumId, photo, null);
	}
	
	public String postPhoto(String pageId, String albumId, Resource photo, String caption) {
		String pageAccessToken = getPageAccessToken(pageId);
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.set("source", photo);
		if(caption != null) {
			parts.set("message", caption);
		}
		parts.set("access_token", pageAccessToken);
		return graphApi.publish(albumId, "photos", parts);
	}
	
	// private helper methods
	
	private Map<String, Account> accountCache = new HashMap<String, Account>();
	
	private String getPageAccessToken(String pageId) {
		Account account = getAccount(pageId);
		if(account == null) {
			throw new BadCredentialsException("The user is not an admin of the page " + pageId);
		}
		return account.getAccessToken();
	}
	
	private Account getAccount(String pageId) {
		if(!accountCache.containsKey(pageId)) {
			// only bother fetching the account data in the event of a cache miss
			List<Account> accounts = graphApi.fetchConnections("me", "accounts", AccountList.class).getList();
			for (Account account : accounts) {
				accountCache.put(account.getId(), account);
			}
		}
		return accountCache.get(pageId);
	}
}
