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
package org.springframework.social.twitter.search;

import java.util.Date;
import java.util.List;

/**
 * Represents a list of trending topics at a specific point in time.
 * @author Craig Walls
 */
public class Trends {
	private final Date time;
	private final List<Trend> trends;

	public Trends(Date time, List<Trend> trends) {
		this.time = time;
		this.trends = trends;
	}

	public Date getTime() {
		return time;
	}

	public List<Trend> getTrends() {
		return trends;
	}

}
