package com.kalinya.performance.dimensions;

public class PortfolioPerformanceDimension extends BasePerformanceDimension {
	private static final long serialVersionUID = 2288731249142035987L;
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new PortfolioPerformanceDimension();
		}
		return instance;
	}
}