package com.kalinya.performance.portfoliostatistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.performance.Portfolio;
import com.kalinya.util.ComparableEqualsBuilder;
import com.kalinya.util.StringUtil;
import com.kalinya.util.ToStringBuilder;

abstract public class PortfolioStatistic implements Comparable<PortfolioStatistic>, Serializable {
	private static final long serialVersionUID = -2375541135587032064L;
	private String name;
	private Map<Portfolio, Map<Date, BigDecimal>> values;

	public PortfolioStatistic() {
		name = getClass().getSimpleName();
	}

	@Override
	public String toString() {
		if(getValues() != null) {
			return asString();
		}
		return new ToStringBuilder(this)
				.withClassName()
				.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<PortfolioStatistic>(this, obj)
				.build();
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(name)
				.build();
	}
	
	@Override
	public int compareTo(PortfolioStatistic that) {
		return new CompareToBuilder()
				.append(name, that.name)
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
}
