package com.kalinya.performance.dimensions;

import java.util.Date;

public class DatePerformanceDimension extends BasePerformanceDimension {
	private static final long serialVersionUID = 2759545694321940980L;
	private static BasePerformanceDimension instance;
	private Date date;

	public DatePerformanceDimension(Date date) {
		setDate(date);
	}

	public DatePerformanceDimension() {
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public Date getDate() {
		return date;
	}

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new DatePerformanceDimension();
		}
		return instance;
	}
}