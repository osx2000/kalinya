package com.kalinya.performance;

import com.kalinya.performance.dimensions.PerformanceDimensions;

final public class PortfolioPerformanceResult extends PerformanceResult {
	private static final long serialVersionUID = 5924391831275313734L;

	public PortfolioPerformanceResult(PerformanceFactory performanceFactory, Portfolios portfolios,
			BenchmarkAssociations benchmarkAssociations, SecurityMasters securityMasters, Instruments instruments,
			InstrumentLegs instrumentLegs, Positions positions, Cashflows cashflows) {
		super(performanceFactory, portfolios, benchmarkAssociations, securityMasters, instruments, instrumentLegs,
				positions, cashflows);
	}

	public String toMinimalString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		if(getPerformanceDimensions().size() > 0) {
			sb.append("PerformanceDimensions [" + getPerformanceDimensions().toString() + "]");
		}
		if(getPerformanceValues().size() > 0) {
			sb.append("\nPerformanceValues");
			sb.append("\n " + getPerformanceValuesSummaryAsString());
		}
		if(getCumulativePerformanceValues().size() > 0) {
			sb.append("\nCumulativePerformanceValues");
			sb.append("\n " + getCumulativePerformanceValuesSummaryAsString());
		}
		return sb.toString();
		/*Map<Portfolio, BigDecimal> baseRatesOfReturn = new HashMap<>();
		for(Portfolio portfolio: getPortfolios()) {
			baseRatesOfReturn.put(portfolio, getPerformanceValues().get)
		}
		StringBuilder sb = new StringBuilder();
		
		return sb.toString();*/
	}

	public Portfolios getPortfoliosWithResults() {
		Portfolios portfolios = new Portfolios();
		for(PerformanceDimensions performanceDimensions: getPerformanceValues().keySet()) {
			portfolios.add(performanceDimensions.getPortfolio());
		}
		return portfolios;
	}
}