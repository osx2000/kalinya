package com.kalinya.performance;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.performance.enums.AssetClass;
import com.kalinya.performance.enums.IndustryGroup;
import com.kalinya.performance.enums.InstrumentClass;
import com.kalinya.performance.enums.RiskGroup;
import com.kalinya.performance.enums.Sector;
import com.kalinya.util.ComparableEqualsBuilder;

public class Instrument implements Comparable<Instrument>, SecurityMasterData, Serializable {
	private static final long serialVersionUID = 3743124681155407825L;
	public static final Instrument CASH = Instrument.create("Cash");
	private String instrumentId;
	private String pricingTicker;
	private Portfolio portfolio;
	
	private IndustryGroup industryGroup;
	private Sector sector;
	private RiskGroup riskGroup;
	private InstrumentClass instrumentClass;
	private AssetClass assetClass;
	private Date maturityDate;

	private Instrument() {
		//Disable default ctor
		setIndustryGroup(IndustryGroup.UNKNOWN);
		setSector(Sector.UNKNOWN);
		setRiskGroup(RiskGroup.UNKNOWN);
		setInstrumentClass(InstrumentClass.UNKNOWN);
		setAssetClass(AssetClass.UNKNOWN);
	}
	
	public Instrument(String instrumentId) {
		this(null, instrumentId);
	}
	
	public Instrument(Portfolio portfolio, String instrumentId) {
		this();
		setPortfolio(portfolio);
		setInstrumentId(instrumentId);
		setPricingTicker(instrumentId);
	}

	@Override
	public String toString() {
		return toVerboseString();
	}
	
	public String toVerboseString() {
		//TODO: use ToStringBuilder(this).build();
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("InstrumentId [%s]", getInstrumentId()));

		if(getPortfolio() != null) {
			sb.append(String.format(" Portfolio [%s]", getPortfolio()));
		}
		if(getIndustryGroup() != null) {
			sb.append(String.format(" IndustryGroup [%s]", getIndustryGroup()));
		}
		if(getSector() != null) {
			sb.append(String.format(" Sector [%s]", getSector()));
		}
		if(getRiskGroup() != null) {
			sb.append(String.format(" RiskGroup [%s]", getRiskGroup()));
		}
		if(getInstrumentClass() != null) {
			sb.append(String.format(" InstrumentClass [%s]", getInstrumentClass()));
		}
		if(getAssetClass() != null) {
			sb.append(String.format(" AssetClass [%s]", getAssetClass()));
		}
		if(getMaturityDate() != null) {
			sb.append(String.format(" MaturityDate [%s]", getMaturityDate()));
		}
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<Instrument>(this, obj)
				.build();
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(instrumentId)
				.build();
	}
	
	@Override
	public int compareTo(Instrument that) {
		return new CompareToBuilder()
				.append(instrumentId, that.instrumentId)
				.build();
	}
	
	@XmlElement
	private void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}
	
	public String getInstrumentId() {
		return instrumentId;
	}
	
	public void setPricingTicker(String pricingTicker) {
		this.pricingTicker = pricingTicker;
	}
	
	public String getPricingTicker() {
		//TODO: probably will need to have a DAO for this
		return pricingTicker;
	}

	public void setInstrumentClass(InstrumentClass instrumentClass) {
		this.instrumentClass = instrumentClass;
	}
	
	public InstrumentClass getInstrumentClass() {
		return instrumentClass;
	}
	
	public boolean isShareable() {
		return instrumentClass == InstrumentClass.SHAREABLE;
	}
	
	public boolean isUnique() {
		return instrumentClass == InstrumentClass.UNIQUE;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public void addSecurityMasterData(SecurityMasters securityMasters) {
		setMaturityDate(securityMasters.getMaturityDate(getInstrumentId()));
		setIndustryGroup(securityMasters.getIndustryGroup(getInstrumentId()));
		setSector(securityMasters.getSector(getInstrumentId()));
		setRiskGroup(securityMasters.getRiskGroup(getInstrumentId()));
		setInstrumentClass(securityMasters.getInstrumentClass(getInstrumentId()));
		setAssetClass(securityMasters.getAssetClass(getInstrumentId()));
	}

	@Override
	public Date getMaturityDate() {
		return maturityDate;
	}
	
	private void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}
	
	@Override
	public IndustryGroup getIndustryGroup() {
		return industryGroup;
	}

	private void setIndustryGroup(IndustryGroup industryGroup) {
		this.industryGroup = industryGroup;
	}
	
	@Override
	public Sector getSector() {
		return sector;
	}
	
	private void setSector(Sector sector) {
		this.sector = sector;
	}

	@Override
	public RiskGroup getRiskGroup() {
		return riskGroup;
	}
	
	private void setRiskGroup(RiskGroup riskGroup) {
		this.riskGroup = riskGroup;
	}

	@Override
	public AssetClass getAssetClass() {
		return assetClass;
	}
	
	private void setAssetClass(AssetClass assetClass) {
		this.assetClass = assetClass;
	}

	public static Instrument create(String instrumentId) {
		return new Instrument(instrumentId);
	}
}
