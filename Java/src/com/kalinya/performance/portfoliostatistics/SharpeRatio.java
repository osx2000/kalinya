package com.kalinya.performance.portfoliostatistics;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.kalinya.performance.PerformanceFactory;
import com.kalinya.performance.Portfolio;
import com.kalinya.util.Assertions;
import com.kalinya.util.DateUtil;

public final class SharpeRatio extends PortfolioStatistic {
	private static final long serialVersionUID = -1241719712280518264L;
	private static SharpeRatio instance;

	public static SharpeRatio getInstance() {
		if(instance == null) {
			instance = new SharpeRatio();
		}
		return instance;
	}
	
	/*
	 * SharpeRatio = (RoR(p) - RoR(f)) / StdDev(p)
	 */
	@Override
	public void calculate(final PortfolioStatistics portfolioStatistics) {
		final Map<Portfolio, Map<Date, BigDecimal>> cumulativeExcessReturnsByPortfolio = portfolioStatistics.getCumulativeExcessReturnsByPortfolio();
		final Map<Portfolio, Map<Date, BigDecimal>> standardDeviationByPortfolio = portfolioStatistics.getStandardDeviation();
		Assertions.notNullOrEmpty("ReturnsByPortfolioByDate", cumulativeExcessReturnsByPortfolio);
		Assertions.notNullOrEmpty("StandardDeviationByPortfolio", standardDeviationByPortfolio);
		final Set<Portfolio> portfolios = cumulativeExcessReturnsByPortfolio.keySet();
		Assertions.notNullOrEmpty("Portfolios", portfolios);
		
		Map<Portfolio, Map<Date, BigDecimal>> sharpeRatios = new TreeMap<>();
		for(Portfolio portfolio: portfolios) {
			//Get numerator = Excess Returns = R(p) - R(f)
			Map<Date, BigDecimal> cumulativeExcessReturns = cumulativeExcessReturnsByPortfolio.get(portfolio);
			Date date = DateUtil.getMaxDate(cumulativeExcessReturns.keySet());
			Assertions.notNullOrEmpty("CumulativeExcessReturns", String.format("Portfolio [%s]", portfolio.getName()), cumulativeExcessReturns);
			BigDecimal cumulativeExcessReturn = cumulativeExcessReturns.get(date);
			
			//Get denominator = StdDev(R(p))
			Map<Date, BigDecimal> standardDeviations = standardDeviationByPortfolio.get(portfolio);
			Assertions.notNullOrEmpty("StandardDeviations", String.format("Portfolio [%s]", portfolio.getName()), standardDeviations);
			date = DateUtil.getMaxDate(standardDeviations.keySet());
			BigDecimal standardDeviation = standardDeviations.get(date);
			Assertions.notZero("StandardDeviation", standardDeviation);
			BigDecimal sharpeRatio = cumulativeExcessReturn.divide(standardDeviation, PerformanceFactory.SCALE, PerformanceFactory.ROUNDING_MODE);
			sharpeRatios.put(portfolio, new TreeMap<Date, BigDecimal>());
			sharpeRatios.get(portfolio).put(date, sharpeRatio);	
		}
		setValues(sharpeRatios);
	}
}
