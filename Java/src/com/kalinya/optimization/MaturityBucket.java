package com.kalinya.optimization;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.Assertions;
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
	public MaturityBucket(String name) {
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
	
	/**
	 * Returns the first MaturityBucket that has an endDate greater than the parameter maturityDate
	 * 
	 * @param maturityBuckets
	 * @param maturityDate
	 * @return
	 */
	public static MaturityBucket getMaturityBucket(MaturityBucket[] maturityBuckets, Date maturityDate) {
		Set<MaturityBucket> sortedMaturityBuckets = new TreeSet<>();
		for(MaturityBucket maturityBucket: maturityBuckets) {
			sortedMaturityBuckets.add(maturityBucket);
		}
		for(MaturityBucket maturityBucket: sortedMaturityBuckets) {
			Date endDate = maturityBucket.getDate();
			if(endDate.compareTo(maturityDate) > 0) {
				return maturityBucket;
			}
		}
		return null;
	}
	
	public static MaturityBucket[] createMaturityBuckets(String[] bucketNames) {
		Assertions.notNullOrEmpty("BucketNames", bucketNames);
		MaturityBucket[] maturityBuckets = new MaturityBucket[bucketNames.length];
		for(int i = 0; i < maturityBuckets.length; i++) {
			String bucketName = bucketNames[i];
			maturityBuckets[i] = new MaturityBucket(bucketName);
		}
		return maturityBuckets;
	}
}
