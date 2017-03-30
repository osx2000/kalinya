package com.kalinya.performance.portfoliostatistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.performance.BenchmarkAssociations;
import com.kalinya.performance.Portfolio;
import com.kalinya.performance.Portfolios;
import com.kalinya.util.StringUtil;
import com.kalinya.util.ToStringBuilder;

abstract public class PortfolioStatistic implements Comparable<PortfolioStatistic>, Serializable {
	private String name;
	private Map<Portfolio, Map<Date, BigDecimal>> values;

	public PortfolioStatistic() {
		name = getClass().getSimpleName();
	}

	public String toString() {
		if(getValues() != null) {
			return asString();
		}
		return new ToStringBuilder(this)
				.withClassName()
				.build();
	}
	
	public String asString() {
		StringBuilder sb = new StringBuilder();
		String portfolioLineBreak = "";
		for(Portfolio portfolio: getValues().keySet()) {
			sb.append(String.format("%sPortfolio [%s]", portfolioLineBreak, portfolio.getName()));
			Map<Date, BigDecimal> statisticByDate = getValues().get(portfolio);
			for(Date date: statisticByDate.keySet()) {
				BigDecimal dailyStatistic = statisticByDate.get(date);
				sb.append(String.format("\n%s %s", StringUtil.formatDate(date), StringUtil.formatPrice(dailyStatistic)));
			}
			portfolioLineBreak = "\n";
		}
		return sb.toString();
	}

	public Map<Portfolio, Map<Date, BigDecimal>> getValues() {
		return values;
	}
	
	/**
	 * Stores the PortfolioStatistic result
	 * 
	 * @param values
	 */
	protected void setValues(final Map<Portfolio, Map<Date, BigDecimal>> values) {
		this.values = values;
	}

	/**
	 * This method is called once to calculate the statistic. Before completion,
	 * call the {@code setValues(Map<Portfolio, Map<Date, BigDecimal>>)} method.
	 * 
	 * @see #setValues(Map)
	 * @param portfolioStatistics
	 */
	public abstract void calculate(PortfolioStatistics portfolioStatistics);

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		PortfolioStatistic that = (PortfolioStatistic) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(name, that.name)
				.build();
	}
	
	@Override
	public int compareTo(PortfolioStatistic that) {
		return new CompareToBuilder()
				.append(name, that.name)
				.build();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(name)
				.build();
	}
}
