package com.kalinya.performance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.kalinya.util.StringUtil;

public final class Cashflow implements Comparable<Cashflow>, Serializable {
	private Portfolio portfolio;
	private InstrumentLeg instrumentLeg;
	private Date date;
	private String currency;
	private BigDecimal localAmount;
	private BigDecimal baseAmount;
	
	private Cashflow() {
		//Disable default ctor
	}
	
	@Deprecated
	public Cashflow(InstrumentLeg instrumentLeg, Date date, String currency, BigDecimal localAmount) {
		this(instrumentLeg, date, currency, localAmount, localAmount);
	}
	
	public Cashflow(InstrumentLeg instrumentLeg, Date date, String currency, BigDecimal localAmount, BigDecimal baseAmount) {
		this();
		setPortfolio(instrumentLeg.getPortfolio());
		setInstrumentLeg(instrumentLeg);
		setDate(date);
		setCurrency(currency);
		setLocalAmount(localAmount);
		setBaseAmount(baseAmount);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("InstrumentLeg [" + getInstrumentLeg().toString() + "] ");
		sb.append("Date [" + StringUtil.formatDate(getDate()) + "] ");
		sb.append("LocalAmount [" + StringUtil.formatDouble(getLocalAmount()) + "]");
		sb.append("BaseAmount [" + StringUtil.formatDouble(getBaseAmount()) + "]");
		return sb.toString();
	}
	
	@Override
	public int compareTo(Cashflow that) {
		if (this == that) {
			return 0;
		}
		int i = getDate().compareTo(that.getDate());
		if(i != 0) return i;
		
		i = getInstrumentLeg().compareTo(that.getInstrumentLeg());
		if(i != 0) return i;
		
		i = getCurrency().compareTo(that.getCurrency());
		if(i != 0) return i;
		
		i = getLocalAmount().compareTo(that.getLocalAmount());
		if(i != 0) return i;
		
		i = getBaseAmount().compareTo(that.getBaseAmount());
		if(i != 0) return i;
		
		//TODO: use CompareToBuilder
		//TODO: this won't permit having two cashflows on the same instrument, same date and same amount.
		return 0;
	}
	
	public InstrumentLeg getInstrumentLeg() {
		return instrumentLeg;
	}
	
	private void setInstrumentLeg(InstrumentLeg instrumentLeg) {
		this.instrumentLeg = instrumentLeg;
	}

	private void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	private void setLocalAmount(BigDecimal value) {
		this.localAmount = value;
	}

	public BigDecimal getLocalAmount() {
		return localAmount;
	}
	
	private void setBaseAmount(BigDecimal baseAmount) {
		this.baseAmount = baseAmount;
	}

	public BigDecimal getBaseAmount() {
		return baseAmount;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	private void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
