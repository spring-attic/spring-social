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
import javax.servlet.jsp.tagext.TagSupport;

/**
 * JSP Tag for initializing Facebook's JavaScript API.
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class FacebookInitTag extends TagSupport {

	private String appId;

	/**
	 * Sets the application's Facebook ID.
	 * @param appId
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Override
	public int doStartTag() throws JspException {
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
			pageContext.getOut().append("FB.init({appId: '" + appId + "', status: true, cookie: true, xfbml: true});");
			pageContext.getOut().append("</script>");
		} catch (IOException e) {
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}

}
