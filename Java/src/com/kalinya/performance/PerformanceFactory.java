package com.kalinya.performance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import com.kalinya.application.FindurSession;
import com.kalinya.enums.DebugLevel;
import com.kalinya.performance.datasource.DataSource;
import com.kalinya.performance.dimensions.PerformanceDimensions;
import com.kalinya.performance.portfoliostatistics.PortfolioStatistics;
import com.kalinya.util.Assertions;
import com.olf.openrisk.application.Session;

final public class PerformanceFactory {
	private FindurSession findurSession;
	public static final int SCALE = 12;
	public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

	private DebugLevel debugLevel;

	private PerformanceFactory() {
		setDebugLevel(DebugLevel.LOW);
	}

	public PerformanceFactory(FindurSession findurSession) {
		this();
		this.findurSession = findurSession;
	}

	@Override
	public String toString() {
		return getFindurSession().toString();
	}

	public FindurSession getFindurSession() {
		return findurSession;
	}

	public Session getSession() {
		return findurSession.getSession();
	}

	public DebugLevel getDebugLevel() {
		return debugLevel;
	}

	public void setDebugLevel(DebugLevel debugLevel) {
		this.debugLevel = debugLevel;
	}

	public int getScale() {
		return SCALE ;
	}

	public RoundingMode getRoundingMode() {
		return ROUNDING_MODE;
	}

	public PerformanceDimensions createPerformanceDimensions() {
		return new PerformanceDimensions();
	}

	public Portfolios getPortfolios(DataSource dataSource) {
		return dataSource.getPortfolios();
	}

	public Positions getPositions(DataSource dataSource) {
		return dataSource.getPositions();
	}

	public SecurityMasters getSecurityMasters(DataSource dataSource) {
		return dataSource.getSecurityMasterData();
	}

	public Instruments getInstruments(DataSource dataSource) {
		return dataSource.getInstruments();
	}

	public InstrumentLegs getInstrumentLegs(DataSource dataSource) {
		return dataSource.getInstrumentLegs();
	}

	public Cashflows getCashflows(DataSource dataSource) {
		return dataSource.getCashflows();
	}

	//TODO: looks like we don't need BenchmarkAssociations
	public PerformanceResult calculateResults(Portfolios portfolios, BenchmarkAssociations benchmarkAssociations,
			SecurityMasters securityMasters, Instruments instruments, InstrumentLegs instrumentLegs,
			Positions positions, Cashflows cashflows, PerformanceDimensions performanceDimensions) {
		Assertions.notNullOrEmpty("Portfolios", portfolios);
		Assertions.notNullOrEmpty("BenchmarkAssociations", benchmarkAssociations);
		Assertions.notNullOrEmpty("SecurityMasters", securityMasters);
		Assertions.notNullOrEmpty("Instruments", instruments);
		Assertions.notNullOrEmpty("InstrumentLegs", instrumentLegs);
		Assertions.notNullOrEmpty("Positions", positions);
		Assertions.notNullOrEmpty("Cashflows", cashflows);
		Assertions.notNullOrEmpty("PerformanceDimensions", performanceDimensions);
		performanceDimensions.validate();

		PerformanceResult performanceResult = null;
		if(performanceDimensions.equals(PerformanceDimensions.BY_DATE_BY_PORTFOLIO)) {
			performanceResult = new PortfolioPerformanceResult(this, portfolios, benchmarkAssociations,
					securityMasters, instruments, instrumentLegs, positions, cashflows);
		} else {
			performanceResult = new PerformanceResult(this, portfolios, benchmarkAssociations,
					securityMasters, instruments, instrumentLegs, positions, cashflows);
		}
		//TODO: toString() method doesn't resolve until there are performanceDimensions
		performanceResult.calculateReturns(performanceDimensions);
		return performanceResult;
	}

	public PerformanceResult calculateResults(DataSource dataSource, PerformanceDimensions performanceDimensions) {
		Assertions.notNull("DataSource", dataSource);
		Assertions.notNullOrEmpty("PerformanceDimensions", performanceDimensions);
		performanceDimensions.validate();
		PerformanceResult performanceResult = new PerformanceResult(this, dataSource);
		performanceResult.calculateReturns(performanceDimensions);
		return performanceResult;
	}
	
	public PortfolioStatistics createPortfolioStatistics() {
		return new PortfolioStatistics(this);
	}
	
	public static BigDecimal getChainLinkedReturn(BigDecimal...ratesOfReturn) {
		BigDecimal chainLinkedRateOfReturn = BigDecimal.ONE;
		for(BigDecimal rateOfReturn: ratesOfReturn) {
			if(rateOfReturn == null) {
				rateOfReturn = BigDecimal.ZERO;
			}
			chainLinkedRateOfReturn = chainLinkedRateOfReturn.multiply(rateOfReturn.add(BigDecimal.ONE));
		}
		return chainLinkedRateOfReturn.subtract(BigDecimal.ONE);
	}

	public static BigDecimal getChainLinkedReturn(Collection<? extends BigDecimal> ratesOfReturn) {
		return getChainLinkedReturn(ratesOfReturn.toArray(new BigDecimal[ratesOfReturn.size()]));
	}
}
