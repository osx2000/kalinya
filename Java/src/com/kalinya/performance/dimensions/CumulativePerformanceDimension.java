package com.kalinya.performance.dimensions;

public class CumulativePerformanceDimension extends BasePerformanceDimension {
	private static final long serialVersionUID = 5834960807980472638L;
	private static BasePerformanceDimension instance;

	public CumulativePerformanceDimension() {
	}

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new CumulativePerformanceDimension();
		}
		return instance;
	}
}