package com.kalinya.performance.portfoliostatistics;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.kalinya.performance.Portfolio;
import com.kalinya.util.Assertions;
import com.kalinya.util.DateUtil;
import com.kalinya.util.NumberUtil;

public final class TrackingError extends PortfolioStatistic {
	private static final long serialVersionUID = -3566920295262669436L;
	private static TrackingError instance;

	public static TrackingError getInstance() {
		if(instance == null) {
			instance = new TrackingError();
		}
		return instance;
	}
	
	@Override
	public void calculate(final PortfolioStatistics portfolioStatistics) {
		final Map<Portfolio, Map<Date, BigDecimal>> activeReturnsByPortfolioByDate = portfolioStatistics.getActiveReturnsByPortfolioByDate();
		Assertions.notNullOrEmpty("ActiveReturnsByPortfolioByDate", activeReturnsByPortfolioByDate);
		final Set<Portfolio> portfolios = activeReturnsByPortfolioByDate.keySet();
		Assertions.notNullOrEmpty("Portfolios", portfolios);
		Assertions.notNullOrEmpty("ActiveReturnsByPortfolioByDate", activeReturnsByPortfolioByDate);
		
		Map<Portfolio, Map<Date, BigDecimal>> trackingError = new TreeMap<>();
		for(Portfolio portfolio: portfolios) {
			Map<Date, BigDecimal> portfolioActiveReturns = activeReturnsByPortfolioByDate.get(portfolio);
			Date date = DateUtil.getMaxDate(portfolioActiveReturns.keySet());
			Assertions.notNullOrEmpty("PortfolioActiveReturns", String.format("Portfolio [%s]", portfolio.getName()), portfolioActiveReturns);
			Assertions.notNull("Date", date);
			Collection<BigDecimal> activeReturns = portfolioActiveReturns.values();
			BigDecimal standardDeviation = NumberUtil.getStandardDeviation(activeReturns);
			trackingError.put(portfolio, new TreeMap<Date, BigDecimal>());
			trackingError.get(portfolio).put(date, standardDeviation);	
		}
		setValues(trackingError);
	}
}
