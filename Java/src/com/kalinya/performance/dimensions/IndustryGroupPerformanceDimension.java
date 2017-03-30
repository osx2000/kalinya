package com.kalinya.performance.dimensions;

public class IndustryGroupPerformanceDimension extends BasePerformanceDimension {
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new IndustryGroupPerformanceDimension();
		}
		return instance;
	}
}