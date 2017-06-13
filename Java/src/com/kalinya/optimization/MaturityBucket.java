package com.kalinya.optimization;

import java.util.Date;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.ComparableEqualsBuilder;
import com.kalinya.util.DateUtil;

public class MaturityBucket implements Comparable<MaturityBucket> {
	private Date endDate;
	private String name;
	
	private MaturityBucket() {
	}
	
	/**
	 * Represents a maturity bucket
	 * 
	 * @param name
	 *            A symbolic representation of the maturity bucket end date e.g.
	 *            "2Y", "6M", "13W"
	 */
	private MaturityBucket(String name) {
		this();
		this.name = name;
		endDate = DateUtil.parseSymbolicDate(name);
	}
	
	@Override 
	public String toString() {
		return name;
	}
	
	public Date getDate() {
		return endDate;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<MaturityBucket>(this, obj)
				.build();
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(endDate)
				.build();
	}
	
	
	@Override
	public int compareTo(MaturityBucket that) {
		return new CompareToBuilder()
				.append(endDate, that.endDate)
				.build();
	}

	public static MaturityBucket create(String name) {
		return new MaturityBucket(name);
	}
}
