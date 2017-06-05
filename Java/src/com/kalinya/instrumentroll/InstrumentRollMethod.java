package com.kalinya.instrumentroll;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.ComparableEqualsBuilder;

public class InstrumentRollMethod implements Comparable<InstrumentRollMethod> {
	public static final InstrumentRollMethod DURATION_NEUTRAL = new InstrumentRollMethod("Duration Neutral");
	public static final InstrumentRollMethod DV01_NEUTRAL = new InstrumentRollMethod("DV01 Neutral");
	public static final InstrumentRollMethod CASH_NEUTRAL = new InstrumentRollMethod("Cash Neutral");
	private String name;
	
	private InstrumentRollMethod() {
	}
	
	public InstrumentRollMethod(String name) {
		this();
		this.name = name;
	}
	
	@Override 
	public String toString() {
		return getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<InstrumentRollMethod>(this, obj)
				.build();
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(name)
				.build();
	}
	
	@Override
	public int compareTo(InstrumentRollMethod that) {
		return new CompareToBuilder()
				.append(name, that.name)
				.build();
	}
	
	public String getName() {
		return name;
	}
}
