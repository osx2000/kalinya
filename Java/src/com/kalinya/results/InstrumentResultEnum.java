package com.kalinya.results;

import java.math.BigDecimal;

import com.kalinya.util.NumberUtil;

public enum InstrumentResultEnum {

	DURATION("Duration", NumberUtil.newBigDecimal(1./365.)), 
	MARKET_YIELD("Market Yield", BigDecimal.ZERO), 
	CONVEXITY("Convexity", BigDecimal.ZERO), 
	//TODO: create expression to derive these results
	//TODO: MaturityBucket doesn't belong here because it can't be resolved to a BigDecimal
	MATURITY_BUCKET("Maturity Bucket", BigDecimal.ZERO), TERM_TO_MATURITY_YEARS("Term to Maturity Years", NumberUtil.newBigDecimal(1./365.));

	private String name;
	private BigDecimal defaultCashValue;

	InstrumentResultEnum(String name, BigDecimal defaultCashValue) {
		this.name = name;
		this.defaultCashValue = defaultCashValue;
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

	public BigDecimal getDefaultCashValue() {
		return defaultCashValue;
	}
}
