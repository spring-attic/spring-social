/*
 * Copyright 2016 the original author or authors.
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
package org.springframework.social.connect.web.thymeleaf;

import org.thymeleaf.context.IContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.processor
		.AbstractStandardConditionalVisibilityTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import org.springframework.context.ApplicationContext;
import org.springframework.social.connect.ConnectionRepository;

/**
 * Implementation of the Spring Social Thymeleaf dialect's <code>social:connected</code> attribute.
 * Conditionally renders content based on whether or not the current user is connected to the provider whose ID is given as the attribute value.
 * @author Craig Walls
 * @author Eddú Meléndez
 */
class ConnectedAttrProcessor extends AbstractStandardConditionalVisibilityTagProcessor
		implements IProcessor {

	public ConnectedAttrProcessor(TemplateMode templateMode, String dialectPrefix) {
		super(templateMode, dialectPrefix, "connected", 300);
	}

	@Override
	protected boolean isVisible(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue) {
		final String providerId = tag.getAttributeValue(attributeName);
		if (providerId == null || providerId.trim().equals("")) {
			return false;
		}
		ConnectionRepository connectionRepository = getConnectionRepository(context);
		return connectionRepository.findConnections(providerId).size() > 0;
	}

	private ConnectionRepository getConnectionRepository(final IContext context) {
		ApplicationContext applicationContext = getSpringApplicationContextFromThymeleafContext(context);
		ConnectionRepository connectionRepository = applicationContext.getBean(ConnectionRepository.class);
		return connectionRepository;
	}

	private ApplicationContext getSpringApplicationContextFromThymeleafContext(final IContext context) {
		if (!(context instanceof ApplicationContext)) {
			throw new ConfigurationException(
					"Thymeleaf execution context is not a Spring web context (implementation of " +
							ApplicationContext.class.getName() + ". Spring Social integration can only be used in " +
					"web environments with a Spring application context.");
		}
		return (ApplicationContext) context;
	}

}
