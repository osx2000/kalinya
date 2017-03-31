package com.kalinya.performance.enums;

import com.kalinya.enums.EnumName;

public enum CsvHeader implements EnumName {
	DATE("Date","eod_date"), 
	PORTFOLIO("Portfolio","portfolio_name"), 
	INSTRUMENT_ID("InstrumentId","instrument_id"), 
	LEG_ID("LegId","leg_id"), 
	CURRENCY("Currency","currency_name"), 
	START_LOCAL_MARKET_VALUE("Start Local Market Value","start_local_market_value"),
	START_BASE_MARKET_VALUE("Start Base Market Value","start_base_market_value"),
	END_LOCAL_MARKET_VALUE("Market Value","end_local_market_value"), 
	END_BASE_MARKET_VALUE("Base Market Value","end_base_market_value"),
	LOCAL_CASHFLOWS_AMOUNT("Local Cashflows","local_cashflows_amount"),
	BASE_CASHFLOWS_AMOUNT("Base Cashflows","base_cashflows_amount"),
	LOCAL_RATE_OF_RETURN("Local Rate of Return","local_rate_of_return"),
	BASE_RATE_OF_RETURN("Base Rate of Return","base_rate_of_return"),
	LOCAL_GAIN_LOSS("Local Gain Loss","local_gain_loss"),
	BASE_GAIN_LOSS("Base Gain Loss","base_gain_loss"), 
	CASH_FLOW("Cash Flow","cash_flow"),
	PRICE_TYPE("Price Type","price_type"),
	TICKER("Ticker","ticker"),
	PRICE("Price","price"), 
	MATURITY_DATE("Maturity Date","maturity_date"), 
	ASSET_CLASS("Asset Class","asset_class"), 
	RISK_GROUP("Risk Group","risk_group"), 
	INDUSTRY_GROUP("Industry Group","industry_group"), 
	SECTOR("Sector","sector"), 
	INSTRUMENT_CLASS("Instrument Class","instrument_class"), 
	PORTFOLIO_GROUP("Portfolio Group", "portfolio_group"),
	BENCHMARK("Benchmark", "benchmark"),
	;

	private String name;
	private String userTableFieldName;

	CsvHeader(String name, String userTableFieldName) {
		this.name = name;
		this.userTableFieldName = userTableFieldName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUserTableFieldName() {
		return userTableFieldName;
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
