package com.kalinya.performance.enums;

import com.kalinya.enums.EnumIdName;

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
	//TODO: Update list to support sovereign issuers!
	SOVEREIGN(9999,"Sovereign")
	
	/**
	 * TODO: decide whether to stick with MSCI's GICS or use the NAICS
	 * + GICS
	 * 		https://www.msci.com/gics
	 * + NAICS
	 * 		https://www.census.gov/cgi-bin/sssd/naics/naicsrch?chart=2017
	 */
	
	/*UNKNOWN(-1, "Unknown"),
	CASH(1, "Cash"),
	AGRICULTURE(11, "Agriculture, Forestry, Fishing and Hunting"),
	MINING(21, "Mining, Quarrying, and Oil and Gas Extraction"),
	UTILITIES(22, "Utilities"),
	CONSTRUCTION(23, "Construction"),
	MANUFACTURING(31-33, "Manufacturing"),
	WHOLESALE_TRADE(42, "Wholesale Trade"),
	RETAIL(44-45, "Retail Trade"),
	TRANSPORTATION(48-49, "Transportation and Warehousing"),
	INFORMATION(51, "Information"),
	FINANCE_AND_INSURANCE(52, "Finance and Insurance"),
	REAL_ESTATE(53, "Real Estate and Rental and Leasing"),
	PROFESSIONAL_SERVICES(54, "Professional, Scientific, and Technical Services"),
	MANAGEMENT(55, "Management of Companies and Enterprises"),
	ADMINISTRATIVE_SERVICES(56, "Administrative and Support and Waste Management and Remediation Services"),
	EDUCATIONAL(61, "Educational Services"),
	HEALTHCARE(62, "Health Care and Social Assistance"),
	ARTS_AND_ENTERTAINMENT(71, "Arts, Entertainment, and Recreation"),
	ACCOMMODATION_AND_FOOD_SERVICES(72, "Accommodation and Food Services"),
	OTHER_SERVICES(81, "Other Services (except Public Administration)"),
	PUBLIC_ADMINISTRATION(92, "Public Administration")*/
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
