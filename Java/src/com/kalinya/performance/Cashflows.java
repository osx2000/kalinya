package com.kalinya.performance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.kalinya.performance.enums.CurrencyBasis;
import com.kalinya.util.BaseSet;

public class Cashflows extends BaseSet<Cashflow> {
	public static final Cashflows EMPTY = new Cashflows();
	private Set<Cashflow> set;
	private Set<Date> dates;
	//TODO: maybe create interfaces for HasDate, HasPortfolio, etc
	
	public Cashflows() {
		super();
		setSet(getSet());
	}

	@Override
	public String toString() {
		return toMinimalString();
	}
	
	public Cashflows getCashflows(Date date, InstrumentLeg instrumentLeg) {
		Cashflows cashflows = new Cashflows();
		for(Cashflow cashflow: getSet()) {
			if(cashflow.getDate().compareTo(date) == 0) {
				if(cashflow.getInstrumentLeg().compareTo(instrumentLeg) == 0) {
					cashflows.add(cashflow);
				}
			}
		}
		return cashflows;
	}
	
	public Set<Cashflow> getCashflowSet() {
		return set;
	}
	
	public void setSet(Set<Cashflow> set) {
		this.set = set;
		if(this.set != null && this.set.size() > 0 && getSet().size() == 0) {
			addAll(set);
		}
	}
	
	public Set<Date> getDates() {
		if(dates == null) {
			dates = new HashSet<Date>();
			for(Cashflow cashflow: getSet()) {
				dates.add(cashflow.getDate());
			}
		}
		return dates;
	}

	public BigDecimal getLocalSum() {
		return getSum(CurrencyBasis.LOCAL);
	}
	
	public BigDecimal getBaseSum() {
		return getSum(CurrencyBasis.BASE);
	}
	
	private BigDecimal getSum(CurrencyBasis currencyBasis) {
		BigDecimal sum = BigDecimal.ZERO;
		for(Cashflow cashflow: getSet()) {
			if(currencyBasis == CurrencyBasis.LOCAL) {
				sum = sum.add(cashflow.getLocalAmount());
			} else {
				sum = sum.add(cashflow.getBaseAmount());
			}
		}
		return sum;
	}
}
