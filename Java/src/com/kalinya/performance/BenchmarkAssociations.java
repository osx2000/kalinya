package com.kalinya.performance;

import java.util.Map;
import java.util.TreeMap;

import com.kalinya.util.Assertions;
import com.kalinya.util.BaseSet;
import com.kalinya.util.ToStringBuilder;

public class BenchmarkAssociations extends BaseSet<BenchmarkAssociation>{
	private Map<Portfolio, Portfolio> benchmarkAssociationsMap;

	public BenchmarkAssociations() {
		benchmarkAssociationsMap = new TreeMap<>();
	}
	
	@Override
	public String toMinimalString() {
		StringBuilder sb = new StringBuilder();
		String concatenator = "";
		for(BenchmarkAssociation benchmarkAssociation: getSet()) {
			sb.append(concatenator 
					+ benchmarkAssociation.getPortfolio().getName() 
					+ " = " 
					+ benchmarkAssociation.getBenchmark().getName());
			concatenator = ", ";
		}
		return sb.toString();
	}
	
	@Override
	public boolean add(BenchmarkAssociation benchmarkAssociation) {
		boolean b = super.add(benchmarkAssociation);
		setBenchmarkAssociationsMap();
		return b;
	}

	public BenchmarkAssociation get(String portfolioName) {
		for(BenchmarkAssociation benchmarkAssociation: getSet()) {
			if(benchmarkAssociation.getPortfolio().getName().equalsIgnoreCase(portfolioName)) {
				return benchmarkAssociation;
			}
		}
		throw new IllegalArgumentException("[" + portfolioName + "] is not in the set of benchmark associations");
	}
	
	public BenchmarkAssociation get(Portfolio portfolio) {
		Assertions.notNull("Portfolio", portfolio);
		for(BenchmarkAssociation benchmarkAssociation: getSet()) {
			if(benchmarkAssociation.getPortfolio().compareTo(portfolio) == 0) {
				return benchmarkAssociation;
			}
		}
		throw new IllegalArgumentException("[" + portfolio.getName() + "] is not in the set of benchmark associations");
	}

	public Portfolios getPortfolios() {
		return new Portfolios(benchmarkAssociationsMap.keySet());
	}
	
	public Portfolios getBenchmarks() {
		return new Portfolios(benchmarkAssociationsMap.values());
	}
	
	private void setBenchmarkAssociationsMap() {
		benchmarkAssociationsMap = new TreeMap<>();
		for(BenchmarkAssociation benchmarkAssociation: getSet()) {
			benchmarkAssociationsMap.put(benchmarkAssociation.getPortfolio(), benchmarkAssociation.getBenchmark());
		}
	}
	
	public Portfolio getBenchmark(Portfolio portfolio) {
		return benchmarkAssociationsMap.get(portfolio);
	}
}
