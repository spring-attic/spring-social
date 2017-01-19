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
package org.springframework.social.connect.web.thymeleaf;

import java.util.LinkedHashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

/**
 * <p>
 * Thymeleaf dialect offering Spring Social connectivity integration.
 * Offers a <code>social:connected</code> attribute that conditional renders content based on whether or not a user has a connection with a given provider.
 * </p>
 * 
 * <p>
 * Example:
 * </p>
 * 
 * <code>
 *   &lt;div social:connected="facebook"&gt;<br>
 *   &nbsp;&nbsp;Only rendered if connected to Facebook<br>
 *   &lt;/div&gt;
 * </code>
 * 
 * @author Craig Walls
 */
public class SpringSocialDialect extends AbstractDialect {
	// TODO: Consider implementing IExpressionEnhancingDialect and throwing some social stuff into context
	//       for use in standard/spring dialect expressions

	public SpringSocialDialect() {
		super();
	}
	
	public String getPrefix() {
		return "social";
	}

	@Override
	public Set<IProcessor> getProcessors() {
		final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
		processors.add(new ConnectedAttrProcessor());
		return processors;
	}

}
