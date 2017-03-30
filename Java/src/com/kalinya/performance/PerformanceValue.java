package com.kalinya.performance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.kalinya.performance.enums.CurrencyBasis;
import com.kalinya.util.StringUtil;

final public class PerformanceValue implements Serializable {
	private Positions positions;
	private Date date;
	private BigDecimal startLocalMarketValue;
	private BigDecimal startBaseMarketValue;
	private BigDecimal endLocalMarketValue;
	private BigDecimal endBaseMarketValue;
	private BigDecimal localCashflowsAmount;
	private BigDecimal baseCashflowsAmount;
	private BigDecimal localGainLoss;
	private BigDecimal baseGainLoss;
	private BigDecimal localRateOfReturn;
	private BigDecimal baseRateOfReturn;
	
	private PerformanceValue() {
	}
	
	public PerformanceValue(Date date, BigDecimal startLocalMarketValue,
			BigDecimal startBaseMarketValue, BigDecimal endLocalMarketValue,
			BigDecimal endBaseMarketValue, BigDecimal localCashflowsAmount,
			BigDecimal baseCashflowsAmount, BigDecimal localGainLoss,
			BigDecimal baseGainLoss) {
		// TODO: add contributions, fees
		this();
		setDate(date);
		setStartLocalMarketValue(startLocalMarketValue);
		setStartBaseMarketValue(startBaseMarketValue);
		setEndLocalMarketValue(endLocalMarketValue);
		setEndBaseMarketValue(endBaseMarketValue);
		setLocalCashflowsAmount(localCashflowsAmount);
		setBaseCashflowsAmount(baseCashflowsAmount);
		setLocalGainLoss(localGainLoss);
		setBaseGainLoss(baseGainLoss);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Date [%s]", StringUtil.formatDate(getDate())));
		sb.append(String.format(" StartLocalMarketValue [%s]", StringUtil.formatDouble(getStartLocalMarketValue())));
		sb.append(String.format(" StartBaseMarketValue [%s]", StringUtil.formatDouble(getStartBaseMarketValue())));
		sb.append(String.format(" EndLocalMarketValue [%s]", StringUtil.formatDouble(getEndLocalMarketValue())));
		sb.append(String.format(" EndBaseMarketValue [%s]", StringUtil.formatDouble(getEndBaseMarketValue())));
		sb.append(String.format(" CashflowsAmount [%s]", StringUtil.formatDouble(getLocalCashflowsAmount())));
		sb.append(String.format(" LocalGainLoss [%s]", StringUtil.formatDouble(getLocalGainLoss())));
		sb.append(String.format(" BaseGainLoss [%s]", StringUtil.formatDouble(getBaseGainLoss())));
		if(getLocalRateOfReturn() != null) {
			sb.append(String.format(" LocalRateOfReturn [%s]", StringUtil.formatPrice(getLocalRateOfReturn())));
		}
		if(getBaseRateOfReturn() != null) {
			sb.append(String.format(" BaseRateOfReturn [%s]", StringUtil.formatPrice(getBaseRateOfReturn())));
		}
		return sb.toString();
	}

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getStartMarketValue(CurrencyBasis currencyBasis) {
		switch(currencyBasis) {
		case LOCAL:
			return getStartLocalMarketValue();
		case BASE:
			return getStartBaseMarketValue();
		}
		throw new IllegalArgumentException(String.format("Unsupported CurrencyBasis [%s]", currencyBasis.getName()));
	}
	
	public BigDecimal getStartLocalMarketValue() {
		return startLocalMarketValue;
	}

	public void setStartLocalMarketValue(BigDecimal startLocalMarketValue) {
		this.startLocalMarketValue = startLocalMarketValue;
	}

	public BigDecimal getEndMarketValue(CurrencyBasis currencyBasis) {
		switch(currencyBasis) {
		case LOCAL:
			return getEndLocalMarketValue();
		case BASE:
			return getEndBaseMarketValue();
		}
		throw new IllegalArgumentException(String.format("Unsupported CurrencyBasis [%s]", currencyBasis.getName()));
	}
	
	public BigDecimal getStartBaseMarketValue() {
		return startBaseMarketValue;
	}

	public void setStartBaseMarketValue(BigDecimal startBaseMarketValue) {
		this.startBaseMarketValue = startBaseMarketValue;
	}

	public BigDecimal getEndLocalMarketValue() {
		return endLocalMarketValue;
	}

	public void setEndLocalMarketValue(BigDecimal endLocalMarketValue) {
		this.endLocalMarketValue = endLocalMarketValue;
	}

	public BigDecimal getEndBaseMarketValue() {
		return endBaseMarketValue;
	}

	public void setEndBaseMarketValue(BigDecimal endBaseMarketValue) {
		this.endBaseMarketValue = endBaseMarketValue;
	}

	public BigDecimal getCashflowsAmount(CurrencyBasis currencyBasis) {
		switch(currencyBasis) {
		case LOCAL:
			return getLocalCashflowsAmount();
		case BASE:
			return getBaseCashflowsAmount();
		}
		throw new IllegalArgumentException(String.format("Unsupported CurrencyBasis [%s]", currencyBasis.getName()));
	}
	
	public BigDecimal getLocalCashflowsAmount() {
		return localCashflowsAmount;
	}

	public void setLocalCashflowsAmount(BigDecimal localCashflowsAmount) {
		this.localCashflowsAmount = localCashflowsAmount;
	}
	
	public BigDecimal getBaseCashflowsAmount() {
		return baseCashflowsAmount;
	}

	public void setBaseCashflowsAmount(BigDecimal baseCashflowsAmount) {
		this.baseCashflowsAmount = baseCashflowsAmount;
	}

	public BigDecimal getGainLoss(CurrencyBasis currencyBasis) {
		switch(currencyBasis) {
		case LOCAL:
			return getLocalGainLoss();
		case BASE:
			return getBaseGainLoss();
		}
		throw new IllegalArgumentException(String.format("Unsupported CurrencyBasis [%s]", currencyBasis.getName()));
	}
	
	public BigDecimal getLocalGainLoss() {
		return localGainLoss;
	}

	public void setLocalGainLoss(BigDecimal localGainLoss) {
		this.localGainLoss = localGainLoss;
	}

	public BigDecimal getBaseGainLoss() {
		return baseGainLoss;
	}

	public void setBaseGainLoss(BigDecimal baseGainLoss) {
		this.baseGainLoss = baseGainLoss;
	}

	public Positions getPositions() {
		return positions;
	}

	public void setPositions(Positions positions) {
		this.positions = positions;
	}

	public BigDecimal getRateOfReturn(CurrencyBasis currencyBasis) {
		switch(currencyBasis) {
		case LOCAL:
			return getLocalRateOfReturn();
		case BASE:
			return getBaseRateOfReturn();
		}
		throw new IllegalArgumentException(String.format("Unsupported CurrencyBasis [%s]", currencyBasis.getName()));
	}
	
	public BigDecimal getLocalRateOfReturn() {
		return localRateOfReturn;
	}
	
	public void setLocalRateOfReturn(BigDecimal localRateOfReturn) {
		this.localRateOfReturn = localRateOfReturn;
	}
	
	public BigDecimal getBaseRateOfReturn() {
		return baseRateOfReturn;
	}
	
	public void setBaseRateOfReturn(BigDecimal baseRateOfReturn) {
		this.baseRateOfReturn = baseRateOfReturn;
	}

	public PerformanceValue add(PerformanceValue augend) {
		BigDecimal startLocalMarketValue = getStartLocalMarketValue().add(augend.getStartLocalMarketValue());
		BigDecimal startBaseMarketValue = getStartBaseMarketValue().add(augend.getStartBaseMarketValue());
		BigDecimal endLocalMarketValue = getEndLocalMarketValue().add(augend.getEndLocalMarketValue());
		BigDecimal endBaseMarketValue = getEndBaseMarketValue().add(augend.getEndBaseMarketValue());
		BigDecimal localCashflowsAmount = getLocalCashflowsAmount().add(augend.getLocalCashflowsAmount());
		BigDecimal baseCashflowsAmount = getBaseCashflowsAmount().add(augend.getBaseCashflowsAmount());
		BigDecimal localGainLoss = getLocalGainLoss().add(augend.getLocalGainLoss());
		BigDecimal baseGainLoss = getBaseGainLoss().add(augend.getBaseGainLoss());
		
		return new PerformanceValue(augend.getDate(), startLocalMarketValue,
				startBaseMarketValue, endLocalMarketValue, endBaseMarketValue,
				localCashflowsAmount, baseCashflowsAmount, localGainLoss, baseGainLoss);
	}
}
