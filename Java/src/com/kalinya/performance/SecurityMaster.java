package com.kalinya.performance;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.assetallocation.AllocationDimension;
import com.kalinya.performance.enums.AssetClass;
import com.kalinya.performance.enums.IndustryGroup;
import com.kalinya.performance.enums.InstrumentClass;
import com.kalinya.performance.enums.RiskGroup;
import com.kalinya.performance.enums.Sector;
import com.kalinya.util.ComparableEqualsBuilder;
import com.kalinya.util.DateUtil;
import com.kalinya.util.StringUtil;
import com.kalinya.util.ToStringBuilder;

public class SecurityMaster implements Comparable<SecurityMaster>, SecurityMasterData, Serializable {
	private static final long serialVersionUID = 1133324168398009965L;
	private static final Date MAXIMUM_MATURITY_DATE = DateUtil.parseDate("12/31/2099");
	private String instrumentId;
	private Date maturityDate;
	private IndustryGroup industryGroup;
	private Sector sector;
	private RiskGroup riskGroup;
	private InstrumentClass instrumentClass;
	private AssetClass assetClass;
	private AllocationDimension allocationDimension;
	
	private SecurityMaster() {
	}

	public SecurityMaster(String instrumentId, Date maturityDate, IndustryGroup industryGroup, Sector sector, RiskGroup riskGroup,
			InstrumentClass instrumentClass, AssetClass assetClass, AllocationDimension allocationDimension) {
		this();
		//TODO: use Builder pattern
		setMaturityDate(maturityDate);
		setInstrumentId(instrumentId);
		setIndustryGroup(industryGroup);
		setSector(sector);
		setRiskGroup(riskGroup);
		setInstrumentClass(instrumentClass);
		setAssetClass(assetClass);
		setAllocationDimension(allocationDimension);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("InstrumentId", getInstrumentId())
				.append("MaturityDate", StringUtil.formatDate(getMaturityDate()))
				.append("IndustryGroup", getIndustryGroup())
				.append("Sector", getSector())
				.append("RiskGroup", getRiskGroup())
				.append("InstrumentClass", getInstrumentClass())
				.append("AssetClass", getAssetClass())
				.build();
	}

	@Override
	public boolean equals(Object obj) {
		return new ComparableEqualsBuilder<SecurityMaster>(this, obj)
				.build();
	}

	@Override
	public int hashCode(){
		return new HashCodeBuilder()
				.append(instrumentId.toUpperCase())
				.build();
	}

	@Override
	public int compareTo(SecurityMaster that) {
		return new CompareToBuilder()
				.append(instrumentId.toUpperCase(), that.instrumentId.toUpperCase())
				.build();
	}

	@Override
	public String getInstrumentId() {
		return instrumentId;
	}

	private void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}

	@Override
	public Date getMaturityDate() {
		return maturityDate;
	}

	private void setMaturityDate(Date maturityDate) {
		if(maturityDate == null) {
			this.maturityDate = MAXIMUM_MATURITY_DATE;
		} else {
			this.maturityDate = maturityDate;
		}
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
	public InstrumentClass getInstrumentClass() {
		return instrumentClass;
	}

	private void setInstrumentClass(InstrumentClass instrumentClass) {
		this.instrumentClass = instrumentClass;
	}

	@Override
	public AssetClass getAssetClass() {
		return assetClass;
	}

	public void setAssetClass(AssetClass assetClass) {
		this.assetClass = assetClass;
	}
	
	public void setAllocationDimension(AllocationDimension allocationDimension) {
		this.allocationDimension = allocationDimension;
	}

	public AllocationDimension getAllocationDimension() {
		return allocationDimension;
	}

}
