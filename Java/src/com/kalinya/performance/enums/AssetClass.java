package com.kalinya.performance.enums;

import java.util.Map;

import com.olf.openrisk.application.Session;

public enum AssetClass implements EnumIdName, SecurityMasterEnum {
	UNKNOWN(RiskGroup.UNKNOWN, -1, "Unknown"),
	CASH(RiskGroup.CASH, 1, "Cash"),
	
	COMMODITY_SWAP(RiskGroup.COMMODITY, 10, "Commodity Swap"),
	COMMODITY_OPTION_OTC(RiskGroup.COMMODITY, 11,"Commodity Option"),
	COMMODITY_SWAPTION(RiskGroup.COMMODITY, 12, "Commodity Swaption"),
	COMMODITY_FUTURE(RiskGroup.COMMODITY, 13, "Commodity Future"),
	COMMODITY_FUTURE_OPTION(RiskGroup.COMMODITY, 14, "Commodity Future Option"),
	
	CREDIT_DEFAULT_SWAP(RiskGroup.CREDIT, 20,"Credit Default Swap"),
	
	EQUITY(RiskGroup.EQUITY, 30,"Equity"),
	EQUITY_SWAP(RiskGroup.EQUITY, 31,"Equity Swap"),
	EQUITY_OPTION_OTC(RiskGroup.EQUITY, 32,"OTC Equity Option"),
	EQUITY_OPTION_LISTED(RiskGroup.EQUITY, 33, "Listed Equity Option"),
	EQUITY_FUTURE(RiskGroup.EQUITY, 34, "Equity Future"),
	EQUITY_FUTURE_OPTION(RiskGroup.EQUITY, 35, "Equity Future Option"),
	
	FOREIGN_EXCHANGE(RiskGroup.FOREIGN_EXCHANGE, 40,"Foreign Exchange"),
	FOREIGN_EXCHANGE_OPTION(RiskGroup.FOREIGN_EXCHANGE, 41,"FX Option"),
	FOREIGN_EXCHANGE_FUTURE(RiskGroup.FOREIGN_EXCHANGE, 42, "FX Future"),
	FOREIGN_EXCHANGE_FUTURE_OPTION(RiskGroup.FOREIGN_EXCHANGE, 43, "FX Future Option"),
	
	BOND(RiskGroup.RATES, 50,"Bond"),
	DISCOUNT_NOTE(RiskGroup.RATES, 51,"Discount Note"),
	INTEREST_RATE_SWAP(RiskGroup.RATES, 52,"Interest Rate Swap"),
	CROSS_CURRENCY_SWAP(RiskGroup.RATES, 53,"Cross Currency Swap"),
	INTEREST_RATE_OPTION(RiskGroup.RATES, 54,"Interest Rate Option"),
	SWAPTION(RiskGroup.RATES, 55,"Swaption"),
	;
	
	private RiskGroup riskGroup;
	private int id;
	private String name;
	
	AssetClass(RiskGroup riskGroup, int id, String name) {
		this.riskGroup = riskGroup;
		this.id = id;
		this.name = name;
	}
	
	public RiskGroup getRiskGroup() {
		return riskGroup;
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
	
	public static AssetClass fromName(String s) {
		for(AssetClass value: values()) {
			if(value.getName().equalsIgnoreCase(s)) {
				return value;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown enumeration name [%s]", s));
	}
}
