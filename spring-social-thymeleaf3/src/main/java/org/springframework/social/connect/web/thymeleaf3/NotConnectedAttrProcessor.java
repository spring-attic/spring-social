/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.connect.web.thymeleaf3;

import org.springframework.context.ApplicationContext;
import org.springframework.social.connect.ConnectionRepository;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.spring5.context.SpringContextUtils;
import org.thymeleaf.standard.processor.AbstractStandardConditionalVisibilityTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Implementation of the Spring Social Thymeleaf dialect's <code>social:connected</code> attribute.
 * Conditionally renders content based on whether or not the current user is connected to the provider whose ID is given as the attribute value.
 * @author Craig Walls
 */
class NotConnectedAttrProcessor extends AbstractStandardConditionalVisibilityTagProcessor {

	public NotConnectedAttrProcessor() {
		super(TemplateMode.HTML, "social", "not-connected", 300);
	}

	@Override
	protected boolean isVisible(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue) {
		final String providerId = tag.getAttributeValue(attributeName);
		if (providerId == null || providerId.trim().equals("")) {
			return false;
		}
		ConnectionRepository connectionRepository = getConnectionRepository(context);
		return connectionRepository.findConnections(providerId).size() == 0;
	}

	private ConnectionRepository getConnectionRepository(final ITemplateContext context) {
		ApplicationContext applicationContext = getSpringApplicationContextFromThymeleafContext(context);
		ConnectionRepository connectionRepository = applicationContext.getBean(ConnectionRepository.class);
		return connectionRepository;
	}

	private ApplicationContext getSpringApplicationContextFromThymeleafContext(final ITemplateContext context) {
		return SpringContextUtils.getApplicationContext(context);
	}

}
