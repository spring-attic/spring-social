/*
 * Copyright 2015 the original author or authors.
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
 * JSP Tag to return true/false if you're NOT connected to a provider. See {@link SocialConnectedTag} for sample usages with a JSP.
 * 
 * @author Rick Reumann
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class SocialNotConnectedTag extends BaseSocialConnectedTag {

	@Override
	protected int doStartTagInternal() throws Exception {
		return super.evaluateBodyIfConnected(false);
	}

}
