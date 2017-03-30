package com.kalinya.performance.dimensions;

public class CumulativePerformanceDimension extends BasePerformanceDimension {
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