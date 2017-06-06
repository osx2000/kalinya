package com.kalinya.performance.dimensions;

public class SectorPerformanceDimension extends BasePerformanceDimension {
	private static final long serialVersionUID = 5702928501768740487L;
	private static BasePerformanceDimension instance;

	public static BasePerformanceDimension getInstance() {
		if(instance == null) {
			instance = new SectorPerformanceDimension();
		}
		return instance;
	}
}