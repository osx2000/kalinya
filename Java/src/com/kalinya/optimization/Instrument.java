package com.kalinya.optimization;

import java.math.BigDecimal;

import com.kalinya.util.NumberUtil;

public class Instrument {
	private String instrumentId;
	private BigDecimal duration;
	private BigDecimal convexity;
	private BigDecimal yield;
	
	private Instrument() {
	}
	
	public Instrument(final Builder builder) {
		this();
		instrumentId = builder.instrumentId;
		duration = builder.duration;
		convexity = builder.convexity;
		yield = builder.yield;
	}
	
	@Override
	public String toString() {
		return getInstrumentId();
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
	
	public static class Builder {
		private String instrumentId;
		private BigDecimal duration;
		private BigDecimal convexity;
		private BigDecimal yield;

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
	}

}
