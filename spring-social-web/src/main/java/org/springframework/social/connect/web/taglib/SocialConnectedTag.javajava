/*
 * Copyright 2013 the original author or authors.
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
package org.springframework.social.connect.web.taglib;

/**
 * JSP Tag to return true/false if you're connected to a provider.
 *
 * Sample usages in JSP:
 *
 * Include at top of JSP:
 * <%@ taglib prefix="social" uri="http://org.springframework/social" %>
 *
 * <social:connected provider="facebook">
 * 	    [ show some FB profile info ]
 * </social:connected>
 *
 * OR another usage, more suitable for if you need to toggle on a page a connect check
 * or multiple checks need to be made:
 *
 * <c:set var="connectedToFB" value="false"/>
 * <social:connected provider="facebook">
 * 	    <c:set var="connectedToFB" value="true"/>
 * 	    [ Show Disconnect link ]
 * </social:connected>
 * <c:if test="${!connectedToFB}">
 *  	[ Show Connect Button/link/form ]
 * </c:if>
 *
 * Note: You could use social:notConnected tag in place of using c:if and having to set the page scoped
 * connectedToFB var, but it's a bit more efficient to not have to make the FB connection more than once
 * on page
 *
 * @author Rick Reumann
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class SocialConnectedTag extends BaseSocialConnectedTag {

	@Override
	protected int doStartTagInternal() throws Exception {
		return super.evaluateBodyIfConnected(true);
	}

}
