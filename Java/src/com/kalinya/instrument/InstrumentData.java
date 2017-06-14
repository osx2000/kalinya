package com.kalinya.instrument;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.ComparableEqualsBuilder;
import com.kalinya.util.StringUtil;
import com.kalinya.util.ToStringBuilder;

@SuppressWarnings("rawtypes")
public class InstrumentData<E extends InstrumentData> implements Comparable<E>, Serializable {
	private static final long serialVersionUID = 1545472790094046240L;
	protected String name;
	
	private InstrumentData() {
	}
	
	public InstrumentData(String name) {
		this();
		this.name = StringUtil.toTitleCase(name);
	}
	
	@Override
	public final String toString() {
		return new ToStringBuilder(this).append(this.getClass().getSimpleName() + "Name", StringUtil.toTitleCase(name)).build();
	}
	
	public final String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		return new ComparableEqualsBuilder<>(this, o).build();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).build();
	}
	
	@Override
	public int compareTo(E o) {
		return new CompareToBuilder().append(getName(), o.getName()).build();
	}
}
