package com.kalinya.enums;

import java.math.BigDecimal;

public enum DayWeighting implements EnumIdName {
	END_OF_DAY(1, "End of Day", BigDecimal.ZERO),
	START_OF_DAY(2, "Start of Day", BigDecimal.ONE), 
	MIDDLE_OF_DAY(3, "Middle of Day", new BigDecimal("0.5"));

	private final int id;
	private final String name;
	private final BigDecimal weight;
	
	DayWeighting(final int id, final String name, final BigDecimal weight) {
		this.id = id;
		this.name = name;
		this.weight = weight;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public BigDecimal getWeight() {
		return weight;
	}
	
	public static DayWeighting fromId(int id) {
		for(DayWeighting value: values()) {
			if(value.getId() == id) {
				return value;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown enumeration id [%s]", id));
	}
	
	public static DayWeighting fromName(String s) {
		for(DayWeighting value: values()) {
			if(value.getName().equalsIgnoreCase(s)) {
				return value;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown enumeration name [%s]", s));
	}
	
	public static String getNames() {
		StringBuilder names = new StringBuilder();
		String concatenator = "";
		for(DayWeighting value: values()) {
			names.append(concatenator);
			names.append(value.getName());
			concatenator = ",";
		}
		return names.toString();
	}
}
