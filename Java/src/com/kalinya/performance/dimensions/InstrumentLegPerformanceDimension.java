package com.kalinya.performance.dimensions;

public class InstrumentLegPerformanceDimension extends BasePerformanceDimension {
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new InstrumentLegPerformanceDimension();
		}
		return instance;
	}
}