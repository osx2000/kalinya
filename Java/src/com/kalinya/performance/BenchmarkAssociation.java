package com.kalinya.performance;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.ComparableEqualsBuilder;
import com.kalinya.util.ToStringBuilder;

public class BenchmarkAssociation implements Comparable<BenchmarkAssociation>, Serializable {
	private static final long serialVersionUID = 5790953051999832009L;
	private Portfolio portfolio;
	private Portfolio benchmark;
	
	private BenchmarkAssociation() {
	}

	public BenchmarkAssociation(Portfolio portfolio, Portfolio benchmark) {
		this();
		this.portfolio = portfolio;
		this.benchmark = benchmark;
	}
	
	@Override 
	public String toString() {
		return new ToStringBuilder(this)
				.append("Portfolio", portfolio.getName())
				.append("Benchmark", benchmark.getName())
				.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<BenchmarkAssociation>(this, obj)
				.build();
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(portfolio.getName().toUpperCase())
				.append(benchmark.getName().toUpperCase())
				.build();
	}
	
	@Override
	public int compareTo(BenchmarkAssociation that) {
		return new CompareToBuilder()
				.append(portfolio.getName().toUpperCase(), that.portfolio.getName().toUpperCase())
				.append(benchmark.getName().toUpperCase(), that.benchmark.getName().toUpperCase())
				.build();
	}
	
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	public Portfolio getBenchmark() {
		return benchmark;
	}
}
