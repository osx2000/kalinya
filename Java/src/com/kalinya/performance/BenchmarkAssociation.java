package com.kalinya.performance;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.util.ToStringBuilder;

public class BenchmarkAssociation implements Comparable<BenchmarkAssociation>, Serializable {
	private Portfolio portfolio;
	private Portfolio benchmark;
	
	private BenchmarkAssociation() {
	}

	private BenchmarkAssociation(String name) {
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
	public int compareTo(BenchmarkAssociation that) {
		return new CompareToBuilder()
				.append(portfolio.getName().toUpperCase(), that.portfolio.getName().toUpperCase())
				.append(benchmark.getName().toUpperCase(), that.benchmark.getName().toUpperCase())
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
        BenchmarkAssociation other = (BenchmarkAssociation) obj;
        return compareTo(other) == 0;
    }
	
    @Override
    public int hashCode() {
    	return Objects.hash(
    			portfolio.getName().toUpperCase(),
    			benchmark.getName().toUpperCase());
    }

	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	public Portfolio getBenchmark() {
		return benchmark;
	}
}
