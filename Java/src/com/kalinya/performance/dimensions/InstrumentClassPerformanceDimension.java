package com.kalinya.performance.dimensions;

public class InstrumentClassPerformanceDimension extends BasePerformanceDimension {
	private static final long serialVersionUID = -1146164988159878066L;
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new InstrumentClassPerformanceDimension();
		}
		return instance;
	}
}