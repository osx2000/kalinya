package com.kalinya.performance;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.assetallocation.Dimension;
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
	private Dimension dimension;

	private SecurityMaster() {
	}

	private SecurityMaster(Builder builder) {
		this();
		setInstrumentId(builder.instrumentId);
		setMaturityDate(builder.maturityDate);
		setIndustryGroup(builder.industryGroup);
		setSector(builder.sector);
		setRiskGroup(builder.riskGroup);
		setInstrumentClass(builder.instrumentClass);
		setAssetClass(builder.assetClass);
		setDimension(builder.dimension);
	}

	public SecurityMaster(String instrumentId, Date maturityDate, IndustryGroup industryGroup, Sector sector, RiskGroup riskGroup,
			InstrumentClass instrumentClass, AssetClass assetClass) {
		this();
		//TODO: use Builder pattern
		setMaturityDate(maturityDate);
		setInstrumentId(instrumentId);
		setIndustryGroup(industryGroup);
		setSector(sector);
		setRiskGroup(riskGroup);
		setInstrumentClass(instrumentClass);
		setAssetClass(assetClass);
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

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	@Override
	public Dimension getDimension() {
		return dimension;
	}

	public static class Builder {
		public AssetClass assetClass;
		public InstrumentClass instrumentClass;
		public RiskGroup riskGroup;
		public Sector sector;
		public IndustryGroup industryGroup;
		private String instrumentId;
		private Date maturityDate;
		private Dimension dimension;

		private Builder() {
		}

		public Builder(String instrumentId) {
			this();
			this.instrumentId = instrumentId;
		}

		public SecurityMaster build() {
			return new SecurityMaster(this);
		}

		public Builder withAssetClass(AssetClass assetClass) {
			this.assetClass = assetClass;
			return this;
		}

		public Builder withInstrumentClass(InstrumentClass instrumentClass) {
			this.instrumentClass = instrumentClass;
			return this;
		}

		public Builder withRiskGroup(RiskGroup riskGroup) {
			this.riskGroup = riskGroup;
			return this;
		}

		public Builder withSector(Sector sector) {
			this.sector = sector;
			return this;
		}

		public Builder withIndustryGroup(IndustryGroup industryGroup) {
			this.industryGroup = industryGroup;
			return this;
		}

		public Builder withMaturityDate(Date maturityDate) {
			this.maturityDate = maturityDate;
			return this;
		}

		public Builder withDimension(Dimension dimension) {
			this.dimension = dimension;
			return this;
		}
	}

}
