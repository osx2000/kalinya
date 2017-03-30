package com.kalinya.enums;

public enum RuntimeMode {
	PREVIEW("Preview"), EXECUTE("Execution"), TESTING("Testing");

	private String name;
	RuntimeMode(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}