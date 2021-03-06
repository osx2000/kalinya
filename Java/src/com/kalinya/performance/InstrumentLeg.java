package com.kalinya.performance;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.performance.dimensions.AssetClassPerformanceDimension;
import com.kalinya.performance.dimensions.IndustryGroupPerformanceDimension;
import com.kalinya.performance.dimensions.InstrumentClassPerformanceDimension;
import com.kalinya.performance.dimensions.InstrumentLegPerformanceDimension;
import com.kalinya.performance.dimensions.PerformanceDimensions;
import com.kalinya.performance.dimensions.PortfolioPerformanceDimension;
import com.kalinya.performance.dimensions.RiskGroupPerformanceDimension;
import com.kalinya.performance.dimensions.SectorPerformanceDimension;
import com.kalinya.performance.enums.AssetClass;
import com.kalinya.performance.enums.IndustryGroup;
import com.kalinya.performance.enums.InstrumentClass;
import com.kalinya.performance.enums.RiskGroup;
import com.kalinya.performance.enums.Sector;
import com.kalinya.util.ComparableEqualsBuilder;

public class InstrumentLeg implements Comparable<InstrumentLeg>, SecurityMasterData, Serializable {
	private static final long serialVersionUID = -6700200847132884132L;
	private Portfolio portfolio;
	private Instrument instrument;
	private Integer legId;
	private String currency;

	private InstrumentLeg() {
		//Disable default ctor
	}
	
	public InstrumentLeg(Instrument instrument, Integer legId, String currency) {
		this(instrument.getPortfolio(), instrument, legId, currency);
	}
	
	public InstrumentLeg(Portfolio portfolio, Instrument instrument, Integer legId, String currency) {
		this();
		setInstrument(instrument);
		setLegId(legId);
		setCurrency(currency);
		setPortfolio(portfolio);
	}
	
	private void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	private void setLegId(Integer legId) {
		this.legId = legId;
	}
	
	public Integer getLegId() {
		return legId;
	}
	
	private void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(getPortfolio() != null) {
			sb.append("Portfolio [" + getPortfolio().getName() + "]"); 
		}
		if(getInstrument() != null) {
			sb.append(" InstrumentId [" + getInstrument().getInstrumentId() + "]"); 
		}
		if(getLegId() != null) {
			sb.append(" LegId [" + getLegId().toString() + "]"); 
		}
		if(getCurrency() != null) {
			sb.append(" Currency [" + getCurrency() + "]"); 
		}
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<InstrumentLeg>(this, obj)
				.build();
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(portfolio)
				.append(instrument)
				.append(legId)
				.build();
	}

	@Override
	public int compareTo(InstrumentLeg that) {
		return new CompareToBuilder()
				.append(portfolio, that.portfolio)
				.append(instrument, that.instrument)
				.append(legId, that.legId)
				.build();
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	private void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
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

	public PerformanceDimensions getPerformanceDimensionsKey(
			PerformanceFactory performanceFactory, PerformanceDimensions performanceDimensions) {
		PerformanceDimensions key = performanceFactory.createPerformanceDimensions();
		if(performanceDimensions.contains(PortfolioPerformanceDimension.getInstance())) {
			key  = key.withPortfolioDimension(getPortfolio());
		}
		if(performanceDimensions.contains(InstrumentLegPerformanceDimension.getInstance())) {
			key = key.withInstrumentLegDimension(this);
		}
		if(performanceDimensions.contains(IndustryGroupPerformanceDimension.getInstance())) {
			key = key.withIndustryGroupDimension(getIndustryGroup());
		}
		if(performanceDimensions.contains(SectorPerformanceDimension.getInstance())) {
			key = key.withSectorDimension(getSector());
		}
		if(performanceDimensions.contains(InstrumentClassPerformanceDimension.getInstance())) {
			key = key.withInstrumentClassDimension(getInstrumentClass());
		}
		if(performanceDimensions.contains(RiskGroupPerformanceDimension.getInstance())) {
			key = key.withRiskGroupDimension(getRiskGroup());
		}
		if(performanceDimensions.contains(AssetClassPerformanceDimension.getInstance())) {
			key = key.withAssetClassDimension(getAssetClass());
		}
		return key;
	}
}
