package com.kalinya.performance.dimensions;

public class PortfolioPerformanceDimension extends BasePerformanceDimension {
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new PortfolioPerformanceDimension();
		}
		return instance;
	}
}