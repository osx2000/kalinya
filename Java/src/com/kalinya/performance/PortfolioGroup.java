package com.kalinya.performance;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.ComparableEqualsBuilder;
import com.kalinya.util.ToStringBuilder;

public class PortfolioGroup implements Comparable<PortfolioGroup>, Serializable {
	private static final long serialVersionUID = 1244424305578696957L;
	private String name;
	
	private PortfolioGroup() {
	}
	
	public PortfolioGroup(String name) {
		this();
		setName(name);
	}
	
	@Override 
	public String toString() {
		return new ToStringBuilder(this)
				.append("Name", name)
				.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<PortfolioGroup>(this, obj)
				.build();
	}
	
    @Override
    public int hashCode() {
    	return new HashCodeBuilder()
				.append(name.toUpperCase())
				.build();
    }
	
	@Override
	public int compareTo(PortfolioGroup that) {
		return new CompareToBuilder()
				.append(name.toUpperCase(), that.name.toUpperCase())
				.build();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
