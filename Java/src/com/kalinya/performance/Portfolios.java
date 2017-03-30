package com.kalinya.performance;

import java.util.Collection;

import com.kalinya.util.BaseSet;

public class Portfolios extends BaseSet<Portfolio>{
	public Portfolios() {
		super();
	}
	
	public Portfolios(Collection<Portfolio> portfolioSet) {
		this();
		for(Portfolio portfolio: portfolioSet) {
			add(portfolio);
		}
	}

	public Portfolios intersection(Portfolios that) {
		Portfolios intersection = new Portfolios();
		for(Portfolio portfolio: getSet()) {
			if(that.contains(portfolio)) {
				intersection.add(portfolio);
			}
		}
		return intersection;
	}
	@Override
	public String toMinimalString() {
		StringBuilder sb = new StringBuilder();
		String concatenator = "";
		for(Portfolio portfolio: getSet()) {
			sb.append(concatenator + portfolio.getName());
			concatenator = ", ";
		}
		return sb.toString();
	}

	public Portfolio get(String portfolioName) {
		for(Portfolio portfolio: getSet()) {
			if(portfolio.getName().equalsIgnoreCase(portfolioName)) {
				return portfolio;
			}
		}
		throw new IllegalArgumentException("[" + portfolioName + "] is not in the set of loaded portfolios");
	}
}
