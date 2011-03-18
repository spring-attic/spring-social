package org.springframework.social.twitter;

import java.util.Date;
import java.util.List;

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
