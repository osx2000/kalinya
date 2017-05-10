package com.kalinya.performance.dimensions;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.kalinya.performance.InstrumentLeg;
import com.kalinya.performance.Portfolio;
import com.kalinya.performance.SecurityMasterData;
import com.kalinya.performance.enums.AssetClass;
import com.kalinya.performance.enums.IndustryGroup;
import com.kalinya.performance.enums.InstrumentClass;
import com.kalinya.performance.enums.RiskGroup;
import com.kalinya.performance.enums.Sector;
import com.kalinya.util.BaseSet;
import com.kalinya.util.StringUtil;

public class PerformanceDimensions extends BaseSet<BasePerformanceDimension> implements Comparable<PerformanceDimensions>, SecurityMasterData {

	public enum Predefined {
    	ByDateByLeg(PerformanceDimensions.BY_DATE_BY_LEG),
    	ByDate(PerformanceDimensions.BY_DATE), 
    	ByDateByPortfolio(PerformanceDimensions.BY_DATE_BY_PORTFOLIO), 
    	CumulativeByLeg(PerformanceDimensions.CUMULATIVE_BY_LEG), 
    	CumulativeByPortfolio(PerformanceDimensions.CUMULATIVE_BY_PORTFOLIO), 
    	
    	ByDateByIndustry(PerformanceDimensions.BY_DATE_BY_INDUSTRY_GROUP), 
    	ByDateBySector(PerformanceDimensions.BY_DATE_BY_SECTOR),
    	ByDateByRiskGroup(PerformanceDimensions.BY_DATE_BY_RISK_GROUP),
    	ByDateByInstrumentClass(PerformanceDimensions.BY_DATE_BY_INSTRUMENT_CLASS),
    	ByDateByAssetClass(PerformanceDimensions.BY_DATE_BY_ASSET_CLASS)
    	;

        private final PerformanceDimensions performanceDimensions;
		private final String name;

        Predefined(final PerformanceDimensions performanceDimensions) {
            this.performanceDimensions = performanceDimensions;
            this.name = toString();
        }

        public PerformanceDimensions getPerformanceDimensions() {
            return performanceDimensions;
        }
        
        public String getName() {
			return name;
		}

		public static Predefined fromName(String name) {
			for(Predefined predefined: values()) 
				if(predefined.getName().equalsIgnoreCase(name)) {
					return predefined;
				}
			throw new IllegalArgumentException(String.format("Unknown name [%s]", name));
		}
    }
	
	private Date date;
	private Date maturityDate;
	private Portfolio portfolio;
	private InstrumentLeg instrumentLeg;
	private IndustryGroup industryGroup;
	private Sector sector;
	private RiskGroup riskGroup;
	private InstrumentClass instrumentClass;
	private AssetClass assetClass;
	
	public PerformanceDimensions() {
		super();
	}

	public PerformanceDimensions(Date date, Portfolio portfolio, InstrumentLeg instrumentLeg,
			IndustryGroup industryGroup, Sector sector, RiskGroup riskGroup, InstrumentClass instrumentClass,
			AssetClass assetClass) {
		this();
		setDate(date);
		setInstrumentLeg(instrumentLeg);
		if(getInstrumentLeg() != null) {
			setPortfolio(instrumentLeg.getPortfolio());
		} else {
			setPortfolio(portfolio);
		}
		setIndustryGroup(industryGroup);
		setSector(sector);
		setRiskGroup(riskGroup);
		setInstrumentClass(instrumentClass);
		setAssetClass(assetClass);
	}
	
	public static final PerformanceDimensions DEFAULT = new PerformanceDimensions();
	public static final PerformanceDimensions BY_DATE = new PerformanceDimensions().withDateDimension();
	public static final PerformanceDimensions BY_DATE_BY_PORTFOLIO = new PerformanceDimensions().withDateDimension().withPortfolioDimension();
    public static final PerformanceDimensions BY_DATE_BY_LEG = new PerformanceDimensions().withDateDimension().withInstrumentLegDimension();

    //Using security master meta data
    public static final PerformanceDimensions BY_DATE_BY_INDUSTRY_GROUP = new PerformanceDimensions().withDateDimension().withIndustryGroupDimension();
    public static final PerformanceDimensions BY_DATE_BY_SECTOR = new PerformanceDimensions().withDateDimension().withSectorDimension();
    public static final PerformanceDimensions BY_DATE_BY_RISK_GROUP = new PerformanceDimensions().withDateDimension().withRiskGroupDimension();
    public static final PerformanceDimensions BY_DATE_BY_INSTRUMENT_CLASS = new PerformanceDimensions().withDateDimension().withInstrumentClassDimension();
    public static final PerformanceDimensions BY_DATE_BY_ASSET_CLASS = new PerformanceDimensions().withDateDimension().withAssetClassDimension();
    
	public static final PerformanceDimensions CUMULATIVE_BY_LEG = new PerformanceDimensions().withCumulativeDimension().withInstrumentLegDimension();
	public static final PerformanceDimensions CUMULATIVE_BY_PORTFOLIO = new PerformanceDimensions().withCumulativeDimension().withPortfolioDimension();
	public static final PerformanceDimensions CUMULATIVE_BY_INDUSTRY_GROUP = new PerformanceDimensions().withCumulativeDimension().withIndustryGroupDimension();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (getDate() == null && getIndustryGroup() == null && getSector() == null && getRiskGroup() == null
				&& getInstrumentClass() == null && getAssetClass() == null && getPortfolio() == null
				&& getInstrumentLeg() == null) {
			int i = 0;
			for (BasePerformanceDimension performanceDimension: getSet()) {
				if(i > 0) {
					sb.append(",");
				}
				sb.append(performanceDimension.toString());
				i++;
			}
			return sb.toString();
		} else {
			if(getDate() != null) {
				sb.append("Date [" + StringUtil.formatDate(getDate()) + "] ");
			}
			if(getIndustryGroup() != null) {
				sb.append("IndustryGroup [" + getIndustryGroup().getName() + "] ");
			}
			if(getSector() != null) {
				sb.append("Sector [" + getSector().getName() + "] ");
			}
			if(getRiskGroup() != null) {
				sb.append("RiskGroup [" + getRiskGroup().getName() + "] ");
			}
			if(getInstrumentClass() != null) {
				sb.append("InstrumentClass [" + getInstrumentClass().getName() + "] ");
			}
			if(getAssetClass() != null) {
				sb.append("AssetClass [" + getAssetClass().getName() + "] ");
			}
			if(getPortfolio() != null) {
				sb.append("Portfolio [" + getPortfolio().getName() + "] ");
			}
			if(getInstrumentLeg() != null) {
				sb.append("InstrumentLeg [" + getInstrumentLeg().toString() + "] ");
			}
			return sb.toString();
		}
	}

	@Override
	public boolean equals(Object obj) {
        if(!(obj instanceof PerformanceDimensions)) {
        	return false;
        }
        return this.compareTo((PerformanceDimensions) obj) == 0;
    }
	
	@Override
	public int compareTo(PerformanceDimensions that) {
		if(allFieldValuesAreNull()) {
			Iterator<BasePerformanceDimension> it = getSet().iterator();
			while(it.hasNext()) {
				BasePerformanceDimension dimension = it.next();
				if(!that.contains(dimension)) {
					return -1;
				}
			}
			return 0;
		}
		return new CompareToBuilder()
				.append(this.date, that.date)
				.append(this.industryGroup, that.industryGroup)
				.append(this.sector, that.sector)
				.append(this.riskGroup, that.riskGroup)
				.append(this.instrumentClass, that.instrumentClass)
				.append(this.assetClass, that.assetClass)
				.append(this.portfolio, that.portfolio)
				.append(this.instrumentLeg, that.instrumentLeg)
				.toComparison();
	}
	
	public int compareToIgnoreDate(PerformanceDimensions that) {
	     return new CompareToBuilder()
	       .append(this.industryGroup, that.industryGroup)
	       .append(this.sector, that.sector)
	       .append(this.riskGroup, that.riskGroup)
	       .append(this.instrumentClass, that.instrumentClass)
	       .append(this.assetClass, that.assetClass)
	       .append(this.portfolio, that.portfolio)
	       .append(this.instrumentLeg, that.instrumentLeg)
	       .toComparison();
	   }
	
	public boolean equalsIgnoreDate(PerformanceDimensions that) {
		return compareToIgnoreDate(that) == 0;
	}

	private boolean allFieldValuesAreNull() {
		return getDate() == null && getPortfolio() == null && getInstrumentLeg() == null && getIndustryGroup() == null
				&& getSector() == null && getRiskGroup() == null && getInstrumentClass() == null
				&& getAssetClass() == null;
	}
	
	@Override
	public boolean addAll(Collection<? extends BasePerformanceDimension> arg0) {
		PerformanceDimensions augend = null;
		if(arg0 instanceof PerformanceDimensions) {
			augend = (PerformanceDimensions) arg0;
			if(augend.getDate() != null) {
				setDate(augend.getDate());
			}
			if(augend.getMaturityDate() != null) {
				setMaturityDate(augend.getMaturityDate());
			}
			if(augend.getPortfolio() != null) {
				setPortfolio(augend.getPortfolio());
			}
			if(augend.getInstrumentLeg() != null) {
				setInstrumentLeg(augend.getInstrumentLeg());
			}
			if(augend.getIndustryGroup() != null) {
				setIndustryGroup(augend.getIndustryGroup());
			}
			if(augend.getSector() != null) {
				setSector(augend.getSector());
			}
			if(augend.getRiskGroup() != null) {
				setRiskGroup(augend.getRiskGroup());
			}
			if(augend.getInstrumentClass() != null) {
				setInstrumentClass(augend.getInstrumentClass());
			}
			if(augend.getAssetClass() != null) {
				setAssetClass(augend.getAssetClass());
			}
			return getSet().addAll(augend);
		} else {
			return getSet().addAll(arg0);
		}
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public Portfolio getPortfolio() {
		return portfolio;
	}

	private void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	public InstrumentLeg getInstrumentLeg() {
		return instrumentLeg;
	}
	
	private void setInstrumentLeg(InstrumentLeg instrumentLeg) {
		this.instrumentLeg = instrumentLeg;
	}
	
	@Override
	public String getInstrumentId() {
		return getInstrumentLeg().getInstrument().getInstrumentId();
	}
	
	public IndustryGroup getIndustryGroup() {
		return industryGroup;
	}
	
	private void setIndustryGroup(IndustryGroup industryGroup) {
		this.industryGroup = industryGroup;
	}
	
	@Override
	public Date getMaturityDate() {
		return maturityDate;
	}

	private void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
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

	public PerformanceDimensions withDateDimension() {
		return withDateDimension(true);
	}
	
	public PerformanceDimensions withDateDimension(boolean b) {
		BasePerformanceDimension instance = DatePerformanceDimension.getInstance();
		if(b) {
			add(instance);
		} else {
			remove(instance);
		}
		return this;
	}
	
	public PerformanceDimensions withDateDimension(Date date) {
		return new PerformanceDimensions(date, getPortfolio(), getInstrumentLeg(), getIndustryGroup(), getSector(),
				getRiskGroup(), getInstrumentClass(), getAssetClass());
	}
	
	public PerformanceDimensions withCumulativeDimension() {
		return withCumulativeDimension(true);
	}
	
	public PerformanceDimensions withCumulativeDimension(boolean b) {
		BasePerformanceDimension instance = CumulativePerformanceDimension.getInstance();
		if(b) {
			add(instance);
		} else {
			remove(instance);
		}
		return this;
	}
	
	public PerformanceDimensions withPortfolioDimension() {
		return withPortfolioDimension(true);
	}
	
	public PerformanceDimensions withPortfolioDimension(boolean b) {
		BasePerformanceDimension instance = PortfolioPerformanceDimension.getInstance();
		if(b) {
			add(instance);
		} else {
			remove(instance);
		}
		return this;
	}
	
	public PerformanceDimensions withPortfolioDimension(Portfolio portfolio) {
		return new PerformanceDimensions(getDate(), portfolio, getInstrumentLeg(), getIndustryGroup(), getSector(),
				getRiskGroup(), getInstrumentClass(), getAssetClass());
	}
	
	public PerformanceDimensions withInstrumentLegDimension() {
		return withInstrumentLegDimension(true);
	}
	
	public PerformanceDimensions withInstrumentLegDimension(boolean b) {
		BasePerformanceDimension instance = InstrumentLegPerformanceDimension.getInstance();
		if(b) {
			add(instance);
		} else {
			remove(instance);
		}
		return this;
	}

	public PerformanceDimensions withInstrumentLegDimension(InstrumentLeg instrumentLeg) {
		return new PerformanceDimensions(getDate(), getPortfolio(), instrumentLeg, getIndustryGroup(), getSector(),
				getRiskGroup(), getInstrumentClass(), getAssetClass());
	}
	
	public PerformanceDimensions withIndustryGroupDimension() {
		return withIndustryGroupDimension(true);
	}
	
	public PerformanceDimensions withIndustryGroupDimension(boolean b) {
		BasePerformanceDimension instance = IndustryGroupPerformanceDimension.getInstance();
		if(b) {
			add(instance);
		} else {
			remove(instance);
		}
		return this;
	}

	public PerformanceDimensions withIndustryGroupDimension(IndustryGroup industryGroup) {
		return new PerformanceDimensions(getDate(), getPortfolio(), getInstrumentLeg(), industryGroup, getSector(),
				getRiskGroup(), getInstrumentClass(), getAssetClass());
	}
	
	public PerformanceDimensions withSectorDimension() {
		return withSectorDimension(true);
	}
	
	public PerformanceDimensions withSectorDimension(boolean b) {
		BasePerformanceDimension instance = SectorPerformanceDimension.getInstance();
		if(b) {
			add(instance);
		} else {
			remove(instance);
		}
		return this;
	}
	
	public PerformanceDimensions withSectorDimension(Sector sector) {
		return new PerformanceDimensions(getDate(), getPortfolio(), getInstrumentLeg(), getIndustryGroup(), sector,
				getRiskGroup(), getInstrumentClass(), getAssetClass());
	}
	
	public PerformanceDimensions withRiskGroupDimension() {
		return withRiskGroupDimension(true);
	}
	
	public PerformanceDimensions withRiskGroupDimension(boolean b) {
		BasePerformanceDimension instance = RiskGroupPerformanceDimension.getInstance();
		if(b) {
			add(instance);
		} else {
			remove(instance);
		}
		return this;
	}
	
	public PerformanceDimensions withRiskGroupDimension(RiskGroup riskGroup) {
		return new PerformanceDimensions(getDate(), getPortfolio(), getInstrumentLeg(), getIndustryGroup(), getSector(),
				riskGroup, getInstrumentClass(), getAssetClass());
	}
	
	public PerformanceDimensions withInstrumentClassDimension() {
		return withInstrumentClassDimension(true);
	}
	
	public PerformanceDimensions withInstrumentClassDimension(boolean b) {
		BasePerformanceDimension instance = InstrumentClassPerformanceDimension.getInstance();
		if(b) {
			add(instance);
		} else {
			remove(instance);
		}
		return this;
	}
	
	public PerformanceDimensions withInstrumentClassDimension(InstrumentClass instrumentClass) {
		return new PerformanceDimensions(getDate(), getPortfolio(), getInstrumentLeg(), getIndustryGroup(), getSector(),
				getRiskGroup(), instrumentClass, getAssetClass());
	}
	
	public PerformanceDimensions withAssetClassDimension() {
		return withAssetClassDimension(true);
	}
	
	public PerformanceDimensions withAssetClassDimension(boolean b) {
		BasePerformanceDimension instance = AssetClassPerformanceDimension.getInstance();
		if(b) {
			add(instance);
		} else {
			remove(instance);
		}
		return this;
	}
	
	public PerformanceDimensions withAssetClassDimension(AssetClass assetClass) {
		return new PerformanceDimensions(getDate(), getPortfolio(), getInstrumentLeg(), getIndustryGroup(), getSector(),
				getRiskGroup(), getInstrumentClass(), assetClass);
	}

	public void validate() {
		if(contains(CumulativePerformanceDimension.getInstance()) && contains(DatePerformanceDimension.getInstance())) {
			throw new IllegalStateException("The performance dimensions includes CumulativePerformanceDimension and DatePerformanceDimension. Only one or the other is supported.");
		}
	}

	public Set<PerformanceDimensions> getDimensionsForCumulativeRatesOfReturn(Set<PerformanceDimensions> set) {
		Set<PerformanceDimensions> performanceDimensionsForCumulativeRatesOfReturn = new TreeSet<>();
		for(PerformanceDimensions performanceDimensionsEntry: set) {
			PerformanceDimensions cumulativePerformanceDimensionsEntry = (PerformanceDimensions) performanceDimensionsEntry.clone();
			cumulativePerformanceDimensionsEntry.setDate(null);
			/*PerformanceDimensions cumulativePerformanceDimensionsEntry = new PerformanceDimensions(); 
			for(BasePerformanceDimension performanceDimension: performanceDimensionsEntry) {
				if (!(performanceDimension instanceof DatePerformanceDimension)
						&& !(performanceDimension instanceof CumulativePerformanceDimension)) {
					cumulativePerformanceDimensionsEntry.add(performanceDimension);
				}
			}*/
			performanceDimensionsForCumulativeRatesOfReturn.add(cumulativePerformanceDimensionsEntry);
		}
		return performanceDimensionsForCumulativeRatesOfReturn;
	}
	
	@Override
	protected PerformanceDimensions clone() {
		return new PerformanceDimensions(getDate(), getPortfolio(), getInstrumentLeg(), getIndustryGroup(), getSector(), getRiskGroup(), getInstrumentClass(), getAssetClass());
	}
}
