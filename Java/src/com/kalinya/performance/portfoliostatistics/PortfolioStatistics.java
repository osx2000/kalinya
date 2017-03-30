package com.kalinya.performance.portfoliostatistics;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.kalinya.enums.DebugLevel;
import com.kalinya.performance.BenchmarkAssociations;
import com.kalinya.performance.PerformanceFactory;
import com.kalinya.performance.PerformanceValue;
import com.kalinya.performance.Portfolio;
import com.kalinya.performance.PortfolioPerformanceResult;
import com.kalinya.performance.Portfolios;
import com.kalinya.performance.dimensions.PerformanceDimensions;
import com.kalinya.util.Assertions;
import com.kalinya.util.BaseSet;
import com.kalinya.util.DateUtil;
import com.kalinya.util.Debuggable;
import com.kalinya.util.StringUtil;

public final class PortfolioStatistics extends BaseSet<PortfolioStatistic> implements Debuggable {
	private PerformanceFactory performanceFactory;
	public static final PortfolioStatistics EMPTY = new PortfolioStatistics();
	public static final ActiveReturn ACTIVE_RETURN = ActiveReturn.getInstance();
	public static final ExcessReturn EXCESS_RETURN = ExcessReturn.getInstance();
	public static final TrackingError TRACKING_ERROR = TrackingError.getInstance();
	public static final StandardDeviation STANDARD_DEIVATION = StandardDeviation.getInstance();
	public static final SharpeRatio SHARPE_RATIO = SharpeRatio.getInstance();
	
	private Set<PortfolioStatistic> set;
	private PortfolioPerformanceResult portfolioPerformanceResult;
	private BenchmarkAssociations benchmarkAssociations;
	private Portfolios portfolios;
	private Map<PerformanceDimensions, PerformanceValue> performanceValues;
	
	//Cached PortfolioStatistic results
	private Map<Portfolio, Map<Date, BigDecimal>> returnsByPortfolioByDate;
	private Map<Portfolio, Map<Date, BigDecimal>> activeReturnsByPortfolioByDate;
	private Map<Portfolio, Map<Date, BigDecimal>> excessReturnsByPortfolioByDate;
	private Map<Portfolio, Map<Date, BigDecimal>> cumulativeExcessReturnsByPortfolio;
	private Map<PortfolioStatistic, Map<Portfolio, Map<Date, BigDecimal>>> results;
	
	private PortfolioStatistics() {
		super();
		setSet(getSet());
		results = new TreeMap<>();
	}
	
	public PortfolioStatistics(final PerformanceFactory performanceFactory) {
		this();
		Assertions.notNull("PerformanceFactory", performanceFactory);
		setPerformanceFactory(performanceFactory);
	}
	
	public PortfolioStatistics(final PerformanceFactory performanceFactory, final PortfolioPerformanceResult portfolioPerformanceResult,
			final BenchmarkAssociations benchmarkAssociations) {
		this(performanceFactory);
		Assertions.notNullOrEmpty("BenchmarkAssociations", benchmarkAssociations);
		Assertions.notNull("PortfolioPerformanceResult", portfolioPerformanceResult);
		setPortfolioPerformanceResult(portfolioPerformanceResult);
		setBenchmarkAssociations(benchmarkAssociations);
		setPortfolios(portfolioPerformanceResult.getPortfoliosWithResults()
				.intersection(getBenchmarkAssociations().getPortfolios()));
		setPerformanceValues(getPortfolioPerformanceResult().getPerformanceValues());
	}
	
	private void setPerformanceFactory(PerformanceFactory performanceFactory) {
		this.performanceFactory = performanceFactory;
	}

	public void setPortfolioPerformanceResult(PortfolioPerformanceResult portfolioPerformanceResult) {
		this.portfolioPerformanceResult = portfolioPerformanceResult;
	}
	
	public void setBenchmarkAssociations(BenchmarkAssociations benchmarkAssociations) {
		this.benchmarkAssociations = benchmarkAssociations;
	}
	
	public void setPortfolios(Portfolios portfolios) {
		this.portfolios = portfolios;
	}
	
	public void setPerformanceValues(Map<PerformanceDimensions, PerformanceValue> performanceValues) {
		this.performanceValues = performanceValues;
	}
	
	@Override
	public String toString() {
		return toVerboseString();
	}
	
	public void setSet(Set<PortfolioStatistic> set) {
		this.set = set;
		if(this.set != null && this.set.size() > 0 && getSet().size() == 0) {
			addAll(set);
		}
	}

	public void addAll(PortfolioStatistic[] portfolioStatistics) {
		for(PortfolioStatistic portfolioStatistic: portfolioStatistics) {
			add(portfolioStatistic);
		}
	}
	
	public Map<PerformanceDimensions, PerformanceValue> getPerformanceValues() {
		return performanceValues;
	}

	public PortfolioPerformanceResult getPortfolioPerformanceResult() {
		return portfolioPerformanceResult;
	}

	public Portfolios getPortfolios() {
		return portfolios;
	}

	public BenchmarkAssociations getBenchmarkAssociations() {
		return benchmarkAssociations;
	}

	public void calculate(final PortfolioPerformanceResult portfolioPerformanceResult,
			final BenchmarkAssociations benchmarkAssociations) {
		Assertions.notNullOrEmpty("BenchmarkAssociations", benchmarkAssociations);
		Assertions.notNull("PortfolioPerformanceResult", portfolioPerformanceResult);
		setPortfolioPerformanceResult(portfolioPerformanceResult);
		setBenchmarkAssociations(benchmarkAssociations);
		setPortfolios(portfolioPerformanceResult.getPortfoliosWithResults()
				.intersection(getBenchmarkAssociations().getPortfolios()));
		setPerformanceValues(getPortfolioPerformanceResult().getPerformanceValues());
		for(PortfolioStatistic portfolioStatistic: getSet()) {
			calculate(portfolioStatistic);
		}
	}
	
	private void calculate(PortfolioStatistic portfolioStatistic) {
		if(getResults().get(portfolioStatistic) == null) {
			if(getPerformanceFactory().getDebugLevel().atLeast(DebugLevel.HIGH)) {
				System.out.println(String.format("Calculating [%s]", portfolioStatistic.toString()));
			}
			portfolioStatistic.calculate(this);
			Map<Portfolio, Map<Date, BigDecimal>> portfolioStatisticResult = portfolioStatistic.getValues();
			addResult(portfolioStatistic, portfolioStatisticResult);
			System.out.println(portfolioStatistic.getClass().getSimpleName() + "\n" + portfolioStatistic.asString());
		} else {
			if(performanceFactory.getDebugLevel().atLeast(DebugLevel.HIGH)) {
				System.out.println("Skipped calculation because we alrady did it!");
			}
		}
	}

	public PerformanceFactory getPerformanceFactory() {
		return performanceFactory;
	}

	private void addResult(PortfolioStatistic portfolioStatistic, Map<Portfolio, Map<Date, BigDecimal>> result) {
		Assertions.notNull("PortfolioStatistic", portfolioStatistic);
		getResults().put(portfolioStatistic, result);
	}

	public Map<PortfolioStatistic, Map<Portfolio, Map<Date, BigDecimal>>> getResults() {
		return results;
	}

	/**
	 * The active return of the portfolio over the portfolio's benchmark.
	 * <p>
	 * RoR(p) - RoR(b)
	 * </p>
	 * Compare to {@link #getExcessReturnsByPortfolioByDate()}
	 * 
	 * @return
	 */
	public Map<Portfolio, Map<Date, BigDecimal>> getActiveReturnsByPortfolioByDate() {
		return getResults(ActiveReturn.getInstance());
	}
	
	/**
	 * The excess return of the portfolio over the portfolio's risk-free
	 * benchmark.
	 * <p>
	 * RoR(p) - RoR(f)
	 * </p>
	 * 
	 * @return
	 */
	public Map<Portfolio, Map<Date, BigDecimal>> getExcessReturnsByPortfolioByDate() {
		return getResults(ExcessReturn.getInstance());
	}
	
	/**
	 * The portfolio's tracking error.
	 * 
	 * @return
	 */
	public Map<Portfolio, Map<Date, BigDecimal>> getTrackingError() {
		return getResults(TrackingError.getInstance());
	}
	
	/**
	 * The portfolio's standard deviation.
	 * 
	 * @return
	 */
	public Map<Portfolio, Map<Date, BigDecimal>> getStandardDeviation() {
		return getResults(StandardDeviation.getInstance());
	}
	
	public <T extends PortfolioStatistic> Map<Portfolio, Map<Date, BigDecimal>> getResults(PortfolioStatistic portfolioStatistic) {
		if(getResults().get(portfolioStatistic) == null) {
			calculate(portfolioStatistic);
		}
		return getResults().get(portfolioStatistic);
	}

	public final Map<Portfolio, Map<Date, BigDecimal>> getReturnsByPortfolioByDate() {
		if(returnsByPortfolioByDate != null) {
			return returnsByPortfolioByDate;
		}
		returnsByPortfolioByDate = new TreeMap<>();
		Assertions.notNullOrEmpty("PerformanceValues", getPerformanceValues());
		Set<PerformanceDimensions> dimensionsSet = getPerformanceValues().keySet();
		//TODO: Need to enforce the dimensions available in getPortfolioPerformanceResult().getPerformanceDimensions()
		for(PerformanceDimensions dimensions: dimensionsSet) {
			Date date = dimensions.getDate();
			Portfolio portfolio = dimensions.getPortfolio();
			
			PerformanceValue performanceValue = getPerformanceValues().get(dimensions);
			BigDecimal baseRateOfReturn = performanceValue.getBaseRateOfReturn();
			if(!returnsByPortfolioByDate.containsKey(portfolio)) {
				returnsByPortfolioByDate.put(portfolio, new TreeMap<Date, BigDecimal>());
			}
			returnsByPortfolioByDate.get(portfolio).put(date, baseRateOfReturn);
		}
		return returnsByPortfolioByDate;
	}

	/**
	 * The chain-linked excess return of the portfolio over the portfolio's
	 * risk-free benchmark.
	 * <p>
	 * RoR(p) - RoR(f)
	 * </p>
	 * 
	 * @return
	 */
	public Map<Portfolio, Map<Date, BigDecimal>> getCumulativeExcessReturnsByPortfolio() {
		//TODO: duplicative logic also in PerformanceResult
		if(cumulativeExcessReturnsByPortfolio != null) {
			return cumulativeExcessReturnsByPortfolio;
		}
		cumulativeExcessReturnsByPortfolio = new TreeMap<>();
		for(Portfolio portfolio: getExcessReturnsByPortfolioByDate().keySet()) {
			cumulativeExcessReturnsByPortfolio.put(portfolio, new TreeMap<Date, BigDecimal>());
			Map<Date, BigDecimal> excessReturnsByPortfolio = getExcessReturnsByPortfolioByDate().get(portfolio);
			BigDecimal cumulativeExcessReturn = PerformanceFactory.getChainLinkedReturn(excessReturnsByPortfolio.values());
			Date date = DateUtil.getMaxDate(excessReturnsByPortfolio.keySet());
			cumulativeExcessReturnsByPortfolio.get(portfolio).put(date, cumulativeExcessReturn);
		}
		return cumulativeExcessReturnsByPortfolio;
	}

	@Override
	public DebugLevel getDebugLevel() {
		return getPerformanceFactory().getDebugLevel();
	}
}
