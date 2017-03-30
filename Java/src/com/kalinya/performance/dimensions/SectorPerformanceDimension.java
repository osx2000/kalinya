package com.kalinya.performance.dimensions;

public class SectorPerformanceDimension extends BasePerformanceDimension {
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new SectorPerformanceDimension();
		}
		return instance;
	}
}