package com.kalinya.performance.dimensions;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.ComparableEqualsBuilder;

public class BasePerformanceDimension implements Comparable<BasePerformanceDimension>, Serializable {
	private static final long serialVersionUID = -5575774013450087519L;

	public BasePerformanceDimension() {
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<BasePerformanceDimension>(this, obj)
				.build();
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(getName())
				.build();
	}
	
	@Override
	public int compareTo(BasePerformanceDimension that) {
		return new CompareToBuilder()
				.append(getName(), that.getName())
				.build();
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}
}
