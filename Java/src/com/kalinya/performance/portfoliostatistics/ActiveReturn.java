package com.kalinya.performance.portfoliostatistics;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.kalinya.performance.BenchmarkAssociations;
import com.kalinya.performance.Portfolio;
import com.kalinya.performance.Portfolios;
import com.kalinya.util.Assertions;
import com.kalinya.util.StringUtil;

public final class ActiveReturn extends PortfolioStatistic {
	private static final long serialVersionUID = -4837093185287620310L;
	private static ActiveReturn instance;

	public static ActiveReturn getInstance() {
		if(instance == null) {
			instance = new ActiveReturn();
		}
		return instance;
	}
	
	@Override
	public void calculate(PortfolioStatistics portfolioStatistics) {
		final Portfolios portfolios = portfolioStatistics.getPortfolios();
		final BenchmarkAssociations benchmarkAssociations = portfolioStatistics.getBenchmarkAssociations();
		final Map<Portfolio, Map<Date, BigDecimal>> returnsByPortfolioByDate = portfolioStatistics.getReturnsByPortfolioByDate();
		Assertions.notNullOrEmpty("Portfolios", portfolios);
		Assertions.notNullOrEmpty("BenchmarkAssociations", benchmarkAssociations);
		Assertions.notNullOrEmpty("ReturnsByPortfolioByDate", returnsByPortfolioByDate);
		
		Map<Portfolio, Map<Date, BigDecimal>> activeReturnsByPortfolioByDate = new TreeMap<>();
		for (Portfolio portfolio : portfolios) {
			Portfolio benchmark = benchmarkAssociations.getBenchmark(portfolio);
			if(benchmark == null) {
				continue;
			}
			Map<Date, BigDecimal> portfolioAbsoluteReturnsByDate = returnsByPortfolioByDate.get(portfolio);
			Assertions.notNullOrEmpty("PortfolioAbsoluteReturnsByDate", String.format("PortfolioName [%s]", portfolio.getName()), portfolioAbsoluteReturnsByDate);
			Map<Date, BigDecimal> benchmarkAbsoluteReturnsByDate = returnsByPortfolioByDate.get(benchmark);
			Assertions.notNullOrEmpty("BenchmarkAbsoluteReturnsByDate", String.format("BenchmarkName [%s]", benchmark.getName()), benchmarkAbsoluteReturnsByDate);
			
			Set<Date> dates = portfolioAbsoluteReturnsByDate.keySet();
			for(Date date: dates) {
				if(!activeReturnsByPortfolioByDate.containsKey(portfolio)) {
					activeReturnsByPortfolioByDate.put(portfolio, new TreeMap<Date, BigDecimal>());
				}
				BigDecimal portfolioRateOfReturn = portfolioAbsoluteReturnsByDate.get(date);
				BigDecimal benchmarkRateOfReturn = benchmarkAbsoluteReturnsByDate.get(date);
				Assertions.notNull("BenchmarkRateOfReturn", String.format("BenchmarkName [%s] Date [%s]", benchmark.getName(), StringUtil.formatDate(date)), benchmarkRateOfReturn);
				BigDecimal activeBaseRateOfReturn = portfolioRateOfReturn.subtract(benchmarkRateOfReturn);
				activeReturnsByPortfolioByDate.get(portfolio).put(date, activeBaseRateOfReturn);
			}
		}
		setValues(activeReturnsByPortfolioByDate);
	}
}
