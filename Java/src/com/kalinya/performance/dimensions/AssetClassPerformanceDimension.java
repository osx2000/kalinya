package com.kalinya.performance.dimensions;

public class AssetClassPerformanceDimension extends BasePerformanceDimension {
	private static final long serialVersionUID = 321638147427038953L;
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new AssetClassPerformanceDimension();
		}
		return instance;
	}
}