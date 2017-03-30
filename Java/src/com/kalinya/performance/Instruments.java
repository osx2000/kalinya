package com.kalinya.performance;

import java.util.HashSet;
import java.util.Set;

import com.kalinya.util.BaseSet;

public class Instruments extends BaseSet<Instrument> {
	private Set<Instrument> instrumentSet;
	private SecurityMasters securityMasters;

	public Instruments() {
		super();
	}

	public Instruments(SecurityMasters securityMasters) {
		this();
		setSecurityMasters(securityMasters);
		if(size() == 0) {
			for(String instrumentId: securityMasters.getInstrumentIds()) {
				add(new Instrument(instrumentId));
			}
		}
		addSecurityMasterData(getSecurityMasters());
	}

	@Override
	protected String getSetStringWithLineBreaks() {
		StringBuilder sb = new StringBuilder();
		for(Instrument instrument: getSet()) {
			sb.append("\n" + instrument.toVerboseString());
		}
		return sb.toString();
	}

	public SecurityMasters getSecurityMasters() {
		return securityMasters;
	}

	private void setSecurityMasters(SecurityMasters securityMasters) {
		this.securityMasters = securityMasters;
	}

	public void addSecurityMasterData(SecurityMasters securityMasters) {
		setSecurityMasters(securityMasters);
		for(Instrument instrument: getSet()) {
			instrument.addSecurityMasterData(securityMasters);
		}
	}

	@Override
	public String toString() {
		return super.toMinimalString();
	}

	public Set<String> getTickers() {
		Set<String> tickers = new HashSet<>();
		for(Instrument instrument: getSet()) {
			tickers.add(instrument.getPricingTicker());
		}
		return tickers;
	}

	public Instrument getInstrument(String instrumentId) {
		return getInstrument(instrumentId, true);
	}

	public Instrument getInstrument(String instrumentId, boolean b) {
		for(Instrument instrument: getSet()) {
			if(instrument.getInstrumentId().equalsIgnoreCase(instrumentId)) {
				return instrument;
			}
		}
		if(b) {
			throw new IllegalArgumentException("Instrument [" + instrumentId + "] not found");
		}
		return new Instrument(instrumentId);
	}

	public Set<Instrument> getInstrumentSet() {
		return instrumentSet;
	}

	public void setInstrumentSet(Set<Instrument> set) {
		instrumentSet = set;
		if(instrumentSet != null && instrumentSet.size() > 0 && getSet().size() == 0) {
			addAll(instrumentSet);
		}
	}
}

