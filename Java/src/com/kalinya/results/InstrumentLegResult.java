package com.kalinya.results;

public enum InstrumentLegResult {
	MTM("MTM");
	
	private String name;
	
	InstrumentLegResult(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static InstrumentLegResult fromName(String name) {
		for(InstrumentLegResult value: values()) 
			if(value.getName().equalsIgnoreCase(name)) {
				return value;
			}
		throw new IllegalArgumentException(String.format("Unknown name [%s]", name));
	}
}
