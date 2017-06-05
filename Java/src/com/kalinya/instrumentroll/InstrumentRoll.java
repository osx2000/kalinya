package com.kalinya.instrumentroll;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.kalinya.optimization.Instrument;
import com.kalinya.util.Assertions;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.ToStringBuilder;

public final class InstrumentRoll {
	private Map<Instrument, BigDecimal> instrumentsToDispose;
	private Map<Instrument, BigDecimal> instrumentsToAcquire;
	private InstrumentRollMethod instrumentRollMethod;
	
	private InstrumentRoll() {
	}
	
	public String toString() {
		return new ToStringBuilder(this)
				.append("InstrumentsToDispose", instrumentsToDispose.keySet())
				.append("InstrumentsToAcquire", instrumentsToAcquire.keySet())
				.build();
	}
	
	public void setInstrumentsToDispose(Map<Instrument, BigDecimal> instrumentsToDispose) {
		this.instrumentsToDispose = instrumentsToDispose;
	}
	
	public void setInstrumentsToAcquire(List<Instrument> instrumentsToAcquire) {
		this.instrumentsToAcquire = new TreeMap<Instrument, BigDecimal>();
		for(Instrument instrument: instrumentsToAcquire) {
			this.instrumentsToAcquire.put(instrument, BigDecimal.ZERO);
		}
	}
	
	public void setInstrumentRollMethod(InstrumentRollMethod instrumentRollMethod) {
		this.instrumentRollMethod = instrumentRollMethod;
	}
	
	public InstrumentRollMethod getInstrumentRollMethod() {
		return instrumentRollMethod;
	}

	public static InstrumentRoll create() {
		return new InstrumentRoll();
	}

	public void calculateRoll() {
		Assertions.notNull("InstrumentRollMethod", instrumentRollMethod);
		Assertions.notNullOrEmpty("InstrumentsToDispose", instrumentsToDispose);
		Assertions.notNullOrEmpty("InstrumentsToAcquire", instrumentsToAcquire);
		
		if(!getInstrumentRollMethod().equals(InstrumentRollMethod.DURATION_NEUTRAL)) {
			throw new UnsupportedOperationException(String.format("Unsupported InstrumentRollMethod [%s]", getInstrumentRollMethod()));
		}
		
		BigDecimal disposedInstrumentDuration = null;
		BigDecimal disposedInstrumentMarketValue = null;
		for(Instrument instrument: instrumentsToDispose.keySet()) {
			disposedInstrumentDuration = instrument.getDuration();
			disposedInstrumentMarketValue = instrumentsToDispose.get(instrument);
		}
		
		for(Instrument instrument: instrumentsToAcquire.keySet()) {
			BigDecimal acquiredInstrumentDuration = instrument.getDuration();
			BigDecimal acquiredInstrumentMarketValue = disposedInstrumentMarketValue.multiply(disposedInstrumentDuration).divide(acquiredInstrumentDuration, NumberUtil.MATH_CONTEXT);
			instrumentsToAcquire.put(instrument, acquiredInstrumentMarketValue.setScale(2, NumberUtil.ROUNDING_MODE));
		}
	}
	
	public BigDecimal getRequiredInstrumentMarketValue(String instrumentId) {
		if(instrumentsToAcquire == null || instrumentsToAcquire.size() == 0) {
			calculateRoll();
		}
		for(Instrument instrument: instrumentsToAcquire.keySet()) {
			if(instrument.getInstrumentId().equalsIgnoreCase(instrumentId)) {
				return instrumentsToAcquire.get(instrument);
			}
		}
		throw new IllegalArgumentException(String.format("InstrumentId [%s] is not one of the instruments to be acquired", instrumentId));
	}

	public String toVerboseString() {
		StringBuilder sb = new StringBuilder();
		//TODO: check nulls etc
		sb.append(String.format("InstrumentsToDispose [%s]", instrumentsToDispose.toString()));
		sb.append(String.format("\nInstrumentsToAcquire [%s]", instrumentsToAcquire.toString()));
		return sb.toString();
	}
}
