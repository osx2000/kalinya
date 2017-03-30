/*
 * 5/13/2015 [OAG-4263] Stephen Dube
 * Added support for index payment rounding OAG-4472
 */
package com.kalinya.enums;

public enum RoundingType {
	ROUND(1,"Round"), 
	TRUNCATE(2,"Truncate"), 
	NONE(3,"None");

	private final int ordinal;
	private final String name;

	RoundingType(int ordinal, String name) {
		this.ordinal = ordinal;
		this.name = name;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public String getName() {
		return name;
	}

	public static RoundingType fromString(String name) {
		if(name == null 
				|| name.length() < 1) {
			return null;
		}
		for (RoundingType instance : RoundingType.values()) {
			if (instance.getName().equals(name)) {
				return instance;
			}
		}
		throw new UnsupportedOperationException("Unsupported name [" + name + "] ");
	}

	public static RoundingType fromInt(int ordinal) {
		for (RoundingType instance : RoundingType.values()) {
			if (instance.getName().equals(ordinal)) {
				return instance;
			}
		}
		throw new UnsupportedOperationException("Unsupported ordinal [" + ordinal + "]");
	}
}
