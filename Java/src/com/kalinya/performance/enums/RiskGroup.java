package com.kalinya.performance.enums;

public enum RiskGroup implements EnumIdName {
	UNKNOWN(-1, "Unknown"),
	CASH(1, "Cash"),
	COMMODITY(10,"Commodity"), 
	CREDIT(20,"Credit"), 
	EQUITY(30,"Equity"), 
	FOREIGN_EXCHANGE(40,"Foreign Exchange"),
	RATES(50,"Rates"),
	;
	
	private int id;
	private String name;

	RiskGroup(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public static EnumIdName fromId(int id) {
		for(EnumIdName value: values()) {
			if(value.getId() == id) {
				return value;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown enumeration id [%s]", id));
	}
	
	public static RiskGroup fromName(String s) {
		for(RiskGroup value: values()) {
			if(value.getName().equalsIgnoreCase(s)) {
				return value;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown enumeration name [%s]", s));
	}
}
