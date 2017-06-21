package com.kalinya.instrument;

import java.util.HashMap;
import java.util.Map;

import com.kalinya.performance.Configurator;
import com.olf.openrisk.trading.EnumLegFieldId;
import com.olf.openrisk.trading.EnumPricingModel;

public class Mappings {
	
	private final static boolean ADD_RBA_MAPPINGS = true;

	public static Map<String, String> getInstrumentTypeMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("GBND", "GBND");
		return map;
	}

	public static Map<String, String> getIssuerBusinessUnitMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ISSUER - UST - BU", "GOVT US");
		map.put("ISSUER - JGB - BU", "GOVT JP");
		map.put("ISSUER - GBP - BU", "GOVT GB");
		map.put("ISSUER - CAD - BU", "GOVT CA");
		map.put("ISSUER - AUD - BU", "GOVT AU");
		map.put("ISSUER - DE - BU", "GOVT DE");
		map.put("ISSUER - OAT - BU", "GOVT FR");
		
		if(ADD_RBA_MAPPINGS) {
			map.put("short_name party_id 20651", "GOVT US"); 
			map.put("short_name party_id 20649", "GOVT GB"); 
			map.put("short_name party_id 21783", "GOVT CA"); 
			map.put("short_name party_id 22027", "GOVT CH"); //China
			map.put("short_name party_id 22029", "GOVT CH"); //People's Bank of China
			map.put("short_name party_id 20643", "GOVT DE"); 
			map.put("short_name party_id 20641", "GOVT FR");
			map.put("short_name party_id 20645", "GOVT JP");
			map.put("short_name party_id 20647", "GOVT NL"); //Netherlands
			map.put("short_name party_id 20655", "FFCB");
			map.put("short_name party_id 20657", "FHLB");
			map.put("short_name party_id 20659", "FHLMC");
			map.put("short_name party_id 20661", "FNMA");
			map.put("short_name party_id 20749", "EBRD"); //European Bank for Reconstruction and Development
			map.put("short_name party_id 21212", "IBRD"); //International Bank for Reconstruction and Development
			map.put("short_name party_id 21216", "KFW"); //Kreditanstalt für Wiederaufbau / Reconstruction Credit Institute
			map.put("short_name party_id 21220", "IADB"); //Inter-American Development Bank
			map.put("short_name party_id 21222", "ADB"); //Asian Development Bank
			map.put("short_name party_id 21228", "IFC"); //International Finance Corporation
			map.put("short_name party_id 21301", "NIB");
			map.put("short_name party_id 21844", "EFSF");
			map.put("short_name party_id 21845", "EU");
		}
		return map;
	}

	public static Map<String, String> getIssuerLegalEntityMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ISSUER - UST - LE","GOVT US - LEGAL");
		map.put("ISSUER - JGB - LE","GOVT JP - LEGAL");
		
		if(ADD_RBA_MAPPINGS) {
			map.put("short_name party_id 20650","GOVT US - LEGAL"); 
			map.put("short_name party_id 21782","GOVT CA - LEGAL"); 
			map.put("short_name party_id 22026", "GOVT CH - LEGAL"); //China
			map.put("short_name party_id 22028", "GOVT CH - LEGAL"); //People's Bank of China
			map.put("short_name party_id 20642","GOVT DE - LEGAL"); 
			map.put("short_name party_id 20640","GOVT FR - LEGAL"); 
			map.put("short_name party_id 20648","GOVT GB - LEGAL"); 
			map.put("short_name party_id 20644","GOVT JP - LEGAL"); 
			map.put("short_name party_id 20646", "GOVT NL - LEGAL");
			map.put("short_name party_id 20654", "FFCB - LEGAL");
			map.put("short_name party_id 20656", "FHLB - LEGAL");
			map.put("short_name party_id 20658", "FHLMC - LEGAL");
			map.put("short_name party_id 20660", "FNMA - LEGAL");
			map.put("short_name party_id 20748", "EBRD - LEGAL");
			map.put("short_name party_id 21211", "IBRD - LEGAL");
			map.put("short_name party_id 21215", "KFW - LEGAL");
			map.put("short_name party_id 21219", "IADB - LEGAL");
			map.put("short_name party_id 21221", "ADB - LEGAL");
			map.put("short_name party_id 21227", "IFC - LEGAL");
			map.put("short_name party_id 21300", "NIB - LEGAL");
			map.put("short_name party_id 21842", "EFSF - LEGAL");
			map.put("short_name party_id 21843", "EU - LEGAL");
		}
		
		return map;
	}	
	
	public static Map<String, String> getIndexMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("USDFED.AUD", "LIBOR.AUD");
		map.put("USDFED.CAD", "LIBOR.CAD");
		map.put("USDFED.EUR", "EURIBOR.EUR");
		map.put("USDFED.GBP", "LIBOR.GBP");
		map.put("USDFED.JPY", "LIBOR.JPY");
		map.put("USDFED.USD", "LIBOR.USD");
		
		map.put("ZERO_REPO.CAD", "REPO.JPY");
		map.put("ZERO_REPO.JPY", "REPO.JPY");
		map.put("ZERO_REPO.USD", "REPO.USD");
		map.put("ZERO_REPO.EUR", "REPO.EUR");
		
		map.put("PX_BONDS_DE.EUR", "MARKET_PX.EUR");
		map.put("PX_BONDS_FR.EUR", "MARKET_PX.EUR");
		map.put("PX_BONDS_GB.GBP", "MARKET_PX.GBP");
		map.put("PX_BONDS_JP.JPY", "MARKET_PX.JPY");
		map.put("PX_BONDS_US.USD", "MARKET_PX.USD");
		
		if(ADD_RBA_MAPPINGS) {
			map.put("GovtZero.CNY", "SHIBOR.CNY");
			map.put("GovtZero.CAD", "LIBOR.CAD");
			map.put("GovtZero.EUR", "EURIBOR.EUR");
			map.put("GovtZero.GBP", "LIBOR.GBP");
			map.put("GovtZero.JPY", "LIBOR.JPY");
			map.put("GovtZero.KRW", "LIBOR.KRW");
			map.put("GovtZero.USD", "LIBOR.USD");
		}
		
		return map;
	}	
	
	public static Map<String, String> getPricingModelMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(EnumPricingModel.Discounting.getName(), EnumPricingModel.DiscountingProceeds.getName());
		return map;
	}

	public static Map<String, String> getHolidayScheduleMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("USNY", "NYC");
		map.put("UST", "NYC");
		map.put("JPTO", "TKY");
		map.put("CAD", "TOR");
		map.put("EUR", "EUR (TARGET)");
		return map;
	}

	public static Map<String, String> getInstrumentGroupMap() {
		String defaultValue = "None";
		Map<String, String> map = new HashMap<String, String>();
		map.put("USD","FED");
		map.put("US Fed Reserve", "FED");
		map.put("JPY",defaultValue);
		map.put("China", defaultValue);
		map.put("Euroclear", defaultValue); 
		map.put("Canada", defaultValue);
		map.put("Japan", defaultValue);
		map.put("UK", defaultValue);
		
		return map;
	}

	public static Map<String, String> getPortfolioGroupMap() {
		Map<String, String> map = new HashMap<String, String>();
		String allPortfolios = "ALL PORTFOLIOS";
		map.put("All", allPortfolios);
		map.put("US ASSETS", allPortfolios);
		map.put("UK ASSETS", allPortfolios);
		map.put("EUROPEAN ASSETS", allPortfolios);
		map.put("CANADIAN ASSETS", allPortfolios);
		map.put("JAPANESE ASSETS", allPortfolios);
		map.put("CHINESE ASSETS", allPortfolios);
		return map;
	}

	public static Map<EnumLegFieldId, String> getFieldValueOverrides() {
		Map<EnumLegFieldId, String> fieldValueOverrides = new HashMap<EnumLegFieldId, String>();
		fieldValueOverrides.put(EnumLegFieldId.MarketPriceLookupType, Configurator.INSTRUMENT_REFERENCE_TYPE.toString());
		return fieldValueOverrides;
	}
}
