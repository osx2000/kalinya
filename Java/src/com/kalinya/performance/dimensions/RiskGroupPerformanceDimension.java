package com.kalinya.performance.dimensions;

public class RiskGroupPerformanceDimension extends BasePerformanceDimension {
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new RiskGroupPerformanceDimension();
		}
		return instance;
	}
}