package com.kalinya.performance;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.ComparableEqualsBuilder;

public class Portfolio implements Comparable<Portfolio>, Serializable {
	private static final long serialVersionUID = -1881883571731118673L;
	private String name;
	private String portfolioGroup;
	
	private Portfolio() {
	}

	private Portfolio(String name) {
		this(name, null);
	}
	
	public Portfolio(String name, String portfolioGroup) {
		this();
		this.name = name;
		this.portfolioGroup = portfolioGroup;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPortfolioGroup() {
		return portfolioGroup;
	}
	
	public void setPortfolioGroup(String portfolioGroup) {
		this.portfolioGroup = portfolioGroup;
	}
	
	@Override 
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<Portfolio>(this, obj)
				.build();
	}
	
    @Override
    public int hashCode() {
    	return new HashCodeBuilder()
				.append(name.toUpperCase())
				.build();
    }
	
	@Override
	public int compareTo(Portfolio that) {
		return new CompareToBuilder()
				.append(name.toUpperCase(), that.name.toUpperCase())
				.build();
	}

	public static Portfolio create(String portfolioName) {
		return new Portfolio(portfolioName);
	}
}
