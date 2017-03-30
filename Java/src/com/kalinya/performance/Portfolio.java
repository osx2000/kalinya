package com.kalinya.performance;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.ToStringBuilder;

public class Portfolio implements Comparable<Portfolio>, Serializable {
	private String name;
	private String portfolioGroup;
	
	private Portfolio() {
	}

	public Portfolio(String name) {
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
		return new ToStringBuilder(this)
				.append("Name", name)
				.append("Group", portfolioGroup).build();
	}
	
	@Override
	public int compareTo(Portfolio that) {
		return new CompareToBuilder()
				.append(name.toUpperCase(), that.name.toUpperCase())
				.build();
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Portfolio other = (Portfolio) obj;
        return compareTo(other) == 0;
    }
	
    @Override
    public int hashCode() {
    	return Objects.hash(getName().toUpperCase());
    }
}
