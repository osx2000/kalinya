package com.kalinya.performance.enums;

import com.kalinya.enums.EnumName;

public enum PriceType implements EnumName {
	UNKNOWN("Unknown"),
	EXCHANGE_RATE("Exchange Rate"), 
	MARKET_PRICE("Market Price"), 
	;

	private String name;

	PriceType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static EnumName fromName(String s) {
		for(EnumName value: values()) {
			if(value.getName().equalsIgnoreCase(s)) {
				return value;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown enumeration name [%s]", s));
	}
}
