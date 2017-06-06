package com.kalinya.performance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.performance.enums.AssetClass;
import com.kalinya.performance.enums.IndustryGroup;
import com.kalinya.performance.enums.InstrumentClass;
import com.kalinya.performance.enums.RiskGroup;
import com.kalinya.performance.enums.Sector;
import com.kalinya.util.ComparableEqualsBuilder;
import com.kalinya.util.StringUtil;

public final class Position implements Comparable<Position>, SecurityMasterData, Serializable {
	private static final long serialVersionUID = 7608030994517625249L;
	private Portfolio portfolio;
	private Instrument instrument;
	private InstrumentLeg instrumentLeg;
	private String currency;
	private Date date;
	private BigDecimal marketValue;
	private BigDecimal baseMarketValue;
	private Cashflows cashflows;
	
	private Position() {
		//Disable default ctor
	}
	
	public Position(InstrumentLeg instrumentLeg, Date date,
			BigDecimal marketValue, BigDecimal baseMarketValue,
			Cashflows cashflows) {
		// TODO: add Contributions, Fees
		this();
		setInstrumentLeg(instrumentLeg);
		setPortfolio(getInstrumentLeg().getPortfolio());
		setInstrument(getInstrumentLeg().getInstrument());
		setCurrency(getInstrumentLeg().getCurrency());
		setDate(date);
		setMarketValue(marketValue);
		setBaseMarketValue(baseMarketValue);
		setCashflows(cashflows);		
	}
	
	/**
	 * Creates a new Position without cashflows. Use
	 * {@link Position#setCashflows(Cashflows)} to set the cashflows.
	 * 
	 * @param instrumentLeg
	 * @param date
	 * @param marketValue
	 * @param baseMarketValue
	 */
	public Position(final InstrumentLeg instrumentLeg, final Date date,
			final BigDecimal marketValue, final BigDecimal baseMarketValue) {
		this(instrumentLeg, date, marketValue, baseMarketValue, Cashflows.EMPTY);
	}
	
	public Position(final InstrumentLeg instrumentLeg, final Date date) {
		this(instrumentLeg, date, BigDecimal.ZERO, BigDecimal.ZERO, Cashflows.EMPTY);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(getDate() != null) {
			sb.append("Date [" + StringUtil.formatDate(getDate()) + "]");
		}
		
		if(getPortfolio() != null) {
			sb.append(" Portfolio [" + getPortfolio().getName() + "]");
		}
		if(getInstrument() != null) {
			sb.append(" Instrument [" + getInstrument().getInstrumentId() + "]");
		}
		if(getInstrumentLeg() != null) {
			sb.append(" LegId [" + getInstrumentLeg().getLegId() + "]");
		}
		if(getMarketValue() != null) {
			sb.append(" MarketValue [" + StringUtil.formatDouble(getMarketValue()) + "]");
		}
		if(getBaseMarketValue() != null) {
			sb.append(" BaseMarketValue [" + StringUtil.formatDouble(getBaseMarketValue()) + "]");
		}
		if(getLocalCashflowsAmount() != null) {
			sb.append(" CashflowsAmount [" + StringUtil.formatDouble(getLocalCashflowsAmount()) + "]");
		}
		return sb.toString();
	}
	
	public String toVerboseString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Date [" + StringUtil.formatDate(getDate()) + "]");
		sb.append(" Portfolio [" + getPortfolio().getName() + "]");
		sb.append(" Instrument [" + getInstrumentId() + "]");
		sb.append(" LegId [" + getInstrumentLeg().getLegId() + "]");
		sb.append(" MarketValue [" + StringUtil.formatDouble(getMarketValue()) + "]");
		sb.append(" BaseMarketValue [" + StringUtil.formatDouble(getBaseMarketValue()) + "]");
		
		if(getMaturityDate() != null) {
			sb.append(" MaturityDate [" + getMaturityDate() + "]");
		}
		if(getIndustryGroup() != null) {
			sb.append(" IndustryGroup [" + getIndustryGroup() + "]");
		}
		if(getSector() != null) {
			sb.append(" Sector [" + getSector() + "]");
		}
		if(getInstrumentClass() != null) {
			sb.append(" InstrumentClass [" + getInstrumentClass() + "]");
		}
		if(getRiskGroup() != null) {
			sb.append(" RiskGroup [" + getRiskGroup() + "]");
		}
		if(getAssetClass() != null) {
			sb.append(" AssetClass [" + getAssetClass() + "]");
		}
		
		return sb.toString();
	}
	
	public Instrument getInstrument() {
		return instrument;
	}
	
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}
	
	public InstrumentLeg getInstrumentLeg() {
		return instrumentLeg;
	}

	public void setInstrumentLeg(InstrumentLeg instrumentLeg) {
		this.instrumentLeg = instrumentLeg;
	}
	
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	public Date getDate() {
		return date;
	}
	
	private void setDate(Date date) {
		this.date = date;
	}

	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<Position>(this, obj)
				.build();
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(date)
				.append(portfolio)
				.append(instrumentLeg)
				.build();
	}
	@Override
	public int compareTo(Position that) {
		return new CompareToBuilder()
		.append(this.date, that.date)
		.append(this.portfolio, that.portfolio)
		.append(this.instrumentLeg, that.instrumentLeg)
		.build();
	}
	
	public BigDecimal getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(BigDecimal marketValue) {
		this.marketValue = marketValue;
	}

	public BigDecimal getBaseMarketValue() {
		return baseMarketValue;
	}

	public void setBaseMarketValue(BigDecimal baseMarketValue) {
		this.baseMarketValue = baseMarketValue;
	}

	public Cashflows getCashflows() {
		return cashflows;
	}
	
	public BigDecimal getLocalCashflowsAmount() {
		return getCashflows().getLocalSum();
	}

	public void setCashflows(Cashflows cashflows) {
		this.cashflows = cashflows;
	}
	
	public BigDecimal getBaseCashflowsAmount() {
		return getCashflows().getLocalSum();
	}
	
	public String getCurrency() {
		return currency;
	}
	
	private void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String getInstrumentId() {
		return getInstrument().getInstrumentId();
	}

	@Override
	public Date getMaturityDate() {
		return getInstrument().getMaturityDate();
	}

	@Override
	public IndustryGroup getIndustryGroup() {
		return getInstrument().getIndustryGroup();
	}

	@Override
	public Sector getSector() {
		return getInstrument().getSector();
	}

	@Override
	public InstrumentClass getInstrumentClass() {
		return getInstrument().getInstrumentClass();
	}

	@Override
	public RiskGroup getRiskGroup() {
		return getInstrument().getRiskGroup();
	}

	@Override
	public AssetClass getAssetClass() {
		return getInstrument().getAssetClass();
	}
}
