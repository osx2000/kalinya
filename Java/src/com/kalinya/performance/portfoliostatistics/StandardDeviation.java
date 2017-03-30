package com.kalinya.performance.portfoliostatistics;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.kalinya.performance.PerformanceFactory;
import com.kalinya.performance.Portfolio;
import com.kalinya.util.Assertions;
import com.kalinya.util.DateUtil;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.StringUtil;

public final class StandardDeviation extends PortfolioStatistic {
	private static StandardDeviation instance;

	public static StandardDeviation getInstance() {
		if(instance == null) {
			instance = new StandardDeviation();
		}
		return instance;
	}
	
	@Override
	public void calculate(final PortfolioStatistics portfolioStatistics) {
		final Map<Portfolio, Map<Date, BigDecimal>> returnsByPortfolioByDate = portfolioStatistics.getReturnsByPortfolioByDate();
		final Set<Portfolio> portfolios = returnsByPortfolioByDate.keySet();
		Assertions.notNullOrEmpty("Portfolios", portfolios);
		Assertions.notNullOrEmpty("ReturnsByPortfolioByDate", returnsByPortfolioByDate);
		
		Map<Portfolio, Map<Date, BigDecimal>> standardDeviations = new TreeMap<>();
		for(Portfolio portfolio: portfolios) {
			Map<Date, BigDecimal> portfolioReturns = returnsByPortfolioByDate.get(portfolio);
			Date date = DateUtil.getMaxDate(portfolioReturns.keySet());
			Assertions.notNullOrEmpty("PortfolioReturns", String.format("Portfolio [%s]", portfolio.getName()), portfolioReturns);
			Assertions.notNull("Date", date);
			Collection<BigDecimal> returns = portfolioReturns.values();
			BigDecimal standardDeviation = NumberUtil.getStandardDeviation(returns);
			standardDeviations.put(portfolio, new TreeMap<Date, BigDecimal>());
			standardDeviations.get(portfolio).put(date, standardDeviation);	
		}
		setValues(standardDeviations);
	}
}
