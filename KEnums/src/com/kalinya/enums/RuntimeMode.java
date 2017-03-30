/*
 * 7/29/2015 [OAG-4311] Stephen Dube
 */
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
