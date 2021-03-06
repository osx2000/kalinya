package com.kalinya.optimization;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.kalinya.assetallocation.AllocationDimension;
import com.kalinya.util.ComparableEqualsBuilder;
import com.kalinya.util.DateUtil;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.StringUtil;
import com.kalinya.util.ToStringBuilder;

public class Instrument implements Comparable<Instrument> {
	/**
	 * A cash instrument with Duration=1d, Convexity = 0, Yield = 0, MaturityDate = tomorrow
	 */
	public static final Instrument CASH = new Instrument.Builder("Cash").withDuration(1/365).withConvexity(0.0).withYield(0.0).withMaturityDate(DateUtil.parseSymbolicDate("1d")).build();
	private String instrumentId;
	private BigDecimal duration;
	private BigDecimal convexity;
	private BigDecimal yield;
	private Date maturityDate;
	private MaturityBucket maturityBucket;
	private BigDecimal termToMaturityYears;
	private boolean verboseDescription = false;
	private AllocationDimension allocationDimension;
	
	private Instrument() {
	}
	
	public Instrument(final Builder builder) {
		this();
		instrumentId = builder.instrumentId;
		duration = builder.duration;
		convexity = builder.convexity;
		yield = builder.yield;
		maturityDate = builder.maturityDate;
		termToMaturityYears = DateUtil.getDateDifferenceInYears(DateUtil.now(), getMaturityDate());
		maturityBucket = builder.maturityBucket;
		allocationDimension = builder.dimension;
	}
	
	@Override
	public String toString() {
		if(verboseDescription) {
			return toVerboseString();
		}
		return getInstrumentId();
		
	}
	
	public String toVerboseString() {
		return new ToStringBuilder(this)
				.append("InstrumentId", instrumentId)
				.append("MaturityDate", StringUtil.formatDate(maturityDate))
				.append("TermToMaturity", StringUtil.formatDouble(termToMaturityYears, 3))
				.append("MaturityBucket", maturityBucket)
				.append("Duration", duration)
				.append("Convexity", convexity)
				.append("Yield", yield)
				.build();
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
	
	public String getInstrumentId() {
		return instrumentId;
	}
	
	public BigDecimal getDuration() {
		return duration;
	}
	
	public BigDecimal getConvexity() {
		return convexity;
	}
	
	public BigDecimal getYield() {
		return yield;
	}
	
	public Date getMaturityDate() {
		return maturityDate;
	}
	
	public BigDecimal getTermToMaturityYears() {
		return termToMaturityYears;
	}
	
	public MaturityBucket getMaturityBucket() {
		return maturityBucket;
	}
	
	public void setAllocationDimension(AllocationDimension allocationDimension) {
		this.allocationDimension = allocationDimension;
	}
	
	public AllocationDimension getAllocationDimension() {
		return allocationDimension;
	}
	
	public static class Builder {
		private String instrumentId;
		private BigDecimal duration;
		private BigDecimal convexity;
		private BigDecimal yield;
		private Date maturityDate;
		private MaturityBucket maturityBucket;
		private AllocationDimension dimension;

		private Builder() {
		}
		
		public Builder(String instrumentId) {
			this();
			this.instrumentId = instrumentId;
		}

		public Instrument build() {
			return new Instrument(this);
		}

		public Builder withDuration(BigDecimal duration) {
			this.duration = duration;
			return this;
		}
		
		public Builder withDuration(double d) {
			return withDuration(NumberUtil.newBigDecimal(d));
		}
		
		public Builder withConvexity(BigDecimal convexity) {
			this.convexity = convexity;
			return this;
		}
		
		public Builder withConvexity(double d) {
			return withConvexity(NumberUtil.newBigDecimal(d));
		}
		
		public Builder withYield(BigDecimal yield) {
			this.yield = yield;
			return this;
		}
		
		public Builder withYield(double d) {
			return withYield(NumberUtil.newBigDecimal(d));
		}
		
		public Builder withMaturityDate(Date maturityDate) {
			this.maturityDate = maturityDate;
			return this;
		}
		
		public Builder withDimension(AllocationDimension dimension) {
			this.dimension = dimension;
			return this;
		}
	}
	
	public static double[] getInstrumentWeightVectorForBucket(Instrument[] portfolio, double[] instrumentWeights, MaturityBucket maturityBucket) {
		double[] vector = new double[portfolio.length];
		for(int i = 0; i < portfolio.length; i++) {
			Instrument instrument = portfolio[i];
			if(instrument.getMaturityBucket().equals(maturityBucket)) {
				vector[i] = instrumentWeights[i];
			}
		}
		return vector;
	}
}
