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
package org.springframework.social.facebook.web;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * JSP Tag for initializing Facebook's JavaScript API.
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class FacebookInitTag extends RequestContextAwareTag {

	private String apiKey;

	/**
	 * Sets the application's Facebook API. If not specified, this tag will attempt to resolve the tag through a configured FacebookServiceProvider.
	 * @param apiKey
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	protected int doStartTagInternal() throws Exception {
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			if (pageContext.getRequest().isSecure()) {
				pageContext.getOut().append("<script src='https://connect.facebook.net/en_US/all.js'></script>");
			} else {
				pageContext.getOut().append("<script src='http://connect.facebook.net/en_US/all.js'></script>");
			}
			pageContext.getOut().append("<div id='fb-root'></div>");
			pageContext.getOut().append("<script type='text/javascript'>");
			pageContext.getOut().append("FB.init({appId: '" + apiKey + "', status: true, cookie: true, xfbml: true});");
			pageContext.getOut().append("</script>");
		} catch (IOException e) {
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}

}
