package com.kalinya.performance.enums;

/**
 * GICS Industries
 * @see <a href="https://www.msci.com/gics">https://www.msci.com/gics</a>
 */
public enum IndustryGroup implements EnumIdName {
	UNKNOWN(-1, "Unknown"),
	CASH(1, "Cash"),
	ENERGY(1010,"Energy"), 
	MATERIALS(1510,"Materials"), 
	CAPITAL_GOODS(2010,"Capital Goods"),
	COMMERCIAL_AND_PROFESSIONAL_SERVICES(2020,"Commercial & Professional Services"),
	AUTOMOBILES_AND_COMPONENTS(2510,"Automobiles & Components"), 
	CONSUMER_SERVICES(2530,"Consumer Services"), 
	FOOD_AND_STAPLES_RETAILING(3010,"Food & Staples Retailing"), 
	HEALTH_CARE_EQUIPMENT_AND_SERVICES(3510,"Health Care Equipment & Services"), 
	BANKS(4010,"Banks"), 
	INSURANCE(4030,"Insurance"), 
	SOFTWARE_AND_SERVICES(4510,"Software & Services"), 
	TECHNOLOGY_HARDWARE_AND_EQUIPMENT(4520,"Technology Hardware & Equipment"), 
	TELECOMMUNICATION_SERVICES(5010,"Telecommunication Services"), 
	UTILITIES(5510,"Utilities"), 
	REAL_ESTATE(6010,"Real Estate"), 
	;

	private int id;
	private String name;

	IndustryGroup(int id, String name) {
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
	
	public static IndustryGroup fromName(String s) {
		for(IndustryGroup value: values()) {
			if(value.getName().equalsIgnoreCase(s)) {
				return value;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown enumeration name [%s]", s));
	}
}
