package com.kalinya.results;

public enum InstrumentResultEnum {

	DURATION("Duration"), MARKET_YIELD("Market Yield"), CONVEXITY("Convexity"), 
	//TODO
	MATURITY_BUCKET("Maturity Bucket"), TERM_TO_MATURITY_YEARS("Term to Maturity Years");

	private String name;

	InstrumentResultEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static InstrumentResultEnum fromName(String name) {
		for(InstrumentResultEnum value: values()) 
			if(value.getName().equalsIgnoreCase(name)) {
				return value;
			}
		throw new IllegalArgumentException(String.format("Unknown name [%s]", name));
	}
}
