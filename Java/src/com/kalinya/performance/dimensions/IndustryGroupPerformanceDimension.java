package com.kalinya.performance.dimensions;

public class IndustryGroupPerformanceDimension extends BasePerformanceDimension {
	private static final long serialVersionUID = -6850096795084603169L;
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new IndustryGroupPerformanceDimension();
		}
		return instance;
	}
}