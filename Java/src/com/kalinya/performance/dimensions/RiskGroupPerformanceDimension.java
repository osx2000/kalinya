package com.kalinya.performance.dimensions;

public class RiskGroupPerformanceDimension extends BasePerformanceDimension {
	private static final long serialVersionUID = 1521166262820106453L;
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new RiskGroupPerformanceDimension();
		}
		return instance;
	}
}