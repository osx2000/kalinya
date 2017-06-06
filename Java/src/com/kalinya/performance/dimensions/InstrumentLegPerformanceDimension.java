package com.kalinya.performance.dimensions;

public class InstrumentLegPerformanceDimension extends BasePerformanceDimension {
	private static final long serialVersionUID = 5582941677972564388L;
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new InstrumentLegPerformanceDimension();
		}
		return instance;
	}
}