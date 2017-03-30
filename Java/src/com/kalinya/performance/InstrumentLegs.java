package com.kalinya.performance;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.kalinya.util.BaseSet;

@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@XmlRootElement
public class InstrumentLegs extends BaseSet<InstrumentLeg> {
	private Set<InstrumentLeg> instrumentLegSet;
	
	public InstrumentLegs() {
		setInstrumentLegSet(getSet());
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getSimpleName() + "> Size [" + getCount() + "]";
	}
	
	@XmlElement
	public void setInstrumentLegSet(Set<InstrumentLeg> set) {
		instrumentLegSet = set;
		if(instrumentLegSet != null && instrumentLegSet.size() > 0 && getSet().size() == 0) {
			addAll(instrumentLegSet);
		}
	}
	
	@XmlElement(name="instrumentLegs")
	public Set<InstrumentLeg> getInstrumentLegSet() {
		return instrumentLegSet;
	}

	public InstrumentLeg getInstrumentLeg(Portfolio portfolio, Instrument instrument,
			int legId) {
		return getInstrumentLeg(portfolio, instrument, legId, true);
	}
	
	public InstrumentLeg getInstrumentLeg(Portfolio portfolio, Instrument instrument, int legId, boolean throwException) {
		for(InstrumentLeg instrumentLeg: getSet()) {
			if(instrumentLeg.getPortfolio().compareTo(portfolio) == 0
				&& instrumentLeg.getInstrument().compareTo(instrument) == 0
				&& instrumentLeg.getLegId().compareTo(legId) == 0) {
					return instrumentLeg;
				}
		}
		if(throwException) {
			throw new IllegalArgumentException(
					String.format(
							"Could not find InstrumentLeg Portfolio [%s], InstrumentId [%s], LegId [%s]",
							portfolio.getName(),
							instrument.getInstrumentId(),
							legId));
		} else {
			return null;
		}
	}
}
