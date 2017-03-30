package com.kalinya.performance.enums;

/**
 * GICS Sectors
 * @see <a href="https://www.msci.com/gics">https://www.msci.com/gics</a>
 */
public enum Sector implements EnumIdName {
	UNKNOWN(-1, "Unknown"),
	CASH(1, "Cash"),
	ENERGY(10,"Energy"), 
	MATERIALS(15,"Materials"), 
	INDUSTRIALS(20,"Industrials"), 
	CONSUMER_DISCRETIONARY(25,"Consumer Discretionary"), 
	CONSUMER_STAPLES(30,"Consumer Staples"), 
	HEALTH_CARE(35,"Health Care"), 
	FINANCIALS(40,"Financials"), 
	INFORMATION_TECHNOLOGY(45,"Information Technology"), 
	TELECOMMUNICATION_SERVICES(50,"Telecommunication Services"), 
	UTILITIES(55,"Utilities"), 
	REAL_ESTATE(60,"Real Estate"), 
	;
	
	private int id;
	private String name;
	
	Sector(int id, String name) {
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
	
	public static Sector fromName(String s) {
		for(Sector value: values()) {
			if(value.getName().equalsIgnoreCase(s)) {
				return value;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown enumeration name [%s]", s));
	}
}
