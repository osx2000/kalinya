package com.kalinya.performance.dimensions;

public class InstrumentClassPerformanceDimension extends BasePerformanceDimension {
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new InstrumentClassPerformanceDimension();
		}
		return instance;
	}
}