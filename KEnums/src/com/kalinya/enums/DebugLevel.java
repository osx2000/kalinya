package com.kalinya.enums;

public enum DebugLevel {
	LOW(0),
	HIGH(1);

	private int index;

	DebugLevel(int index) {
		this.index = index;
	}
	
	public boolean atLeast(DebugLevel debugLevel) {
		return getIndex() >= debugLevel.getIndex();
	}

	private int getIndex() {
		return index;
	}
}
