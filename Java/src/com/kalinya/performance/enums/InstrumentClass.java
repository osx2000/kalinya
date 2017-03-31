package com.kalinya.performance.enums;

import com.kalinya.enums.EnumIdName;

public enum InstrumentClass implements EnumIdName, SecurityMasterEnum {
	UNKNOWN(-1, "Unknown"),
	SHAREABLE(10,"Shared"),
	UNIQUE(20,"Unique"),
	PERPETUAL(30,"Perpetual")
	;

	private int id;
	private String name;

	InstrumentClass(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public int getId() {
		return id;
	}
	
	public static EnumIdName fromId(int id) {
		for(EnumIdName value: values()) {
			if(value.getId() == id) {
				return value;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown enumeration id [%s]", id));
	}
	
	public static InstrumentClass fromName(String s) {
		for(InstrumentClass value: values()) {
			if(value.getName().equalsIgnoreCase(s)) {
				return value;
			}
		}
		//throw new IllegalArgumentException(String.format("Unknown enumeration name [%s]", s));
		return UNKNOWN;
	}
}
