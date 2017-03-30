package com.kalinya.performance;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import com.kalinya.util.BaseSet;
import com.kalinya.util.DateUtil;
import com.kalinya.util.StringUtil;

public class Positions extends BaseSet<Position> {
	private Set<Position> positionSet;
	private Portfolios portfolios;
	private Set<Date> dates;
	private Instruments instruments;
	
	public Positions() {
		super();
		setPositionSet(getSet());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<" + getClass().getSimpleName() + "> Size [" + getCount() + "]");
		return sb.toString();
	}
	
	@Override
	protected String getSetStringWithLineBreaks() {
		StringBuilder sb = new StringBuilder();
		for(Position position: getSet()) {
			sb.append("\n" + position.toVerboseString());
		}
		return sb.toString();
	}
	
	public Set<Position> getPositionSet() {
		return positionSet;
	}
	
	public void setPositionSet(Set<Position> set) {
		this.positionSet = set;
		if(positionSet != null && positionSet.size() > 0 && getSet().size() == 0) {
			addAll(set);
		}
	}
	
	public Set<Date> getDates() {
		if(dates == null) {
			dates = new TreeSet<Date>();
			for(Position position: getSet()) {
				dates.add(position.getDate());
			}
		}
		return dates;
	}
	
	public Set<Date> getDates(InstrumentLeg instrumentLeg) {
		Set<Date> dates = new TreeSet<Date>();
		for(Position position: getSet()) {
			if(position.getInstrumentLeg().compareTo(instrumentLeg) == 0) {
				dates.add(position.getDate());
			}
		}
		return dates;
	}
	
	public InstrumentLegs getInstrumentLegs() {
		InstrumentLegs instrumentLegs = new InstrumentLegs();
		for(Position position: getSet()) {
			instrumentLegs.add(position.getInstrumentLeg());
		}
		return instrumentLegs;
	}
	
	public InstrumentLegs getInstrumentLegs(Date date) {
		InstrumentLegs instrumentLegs = new InstrumentLegs();
		for(Position position: getSet()) {
			if(date.compareTo(position.getDate()) == 0) {
				instrumentLegs.add(position.getInstrumentLeg());
			}
		}
		return instrumentLegs;
	}
	
	public Portfolios getPortfolios() {
		if(portfolios == null) {
			portfolios = new Portfolios();
			for(Position position: getSet()) {
				portfolios.add(position.getPortfolio());
			}
		}
		return portfolios;
	}

	public Position getPosition(Date date, InstrumentLeg instrumentLeg) {
		return getPosition(date, instrumentLeg, true);
	}
	
	public Position getPosition(Date date, InstrumentLeg instrumentLeg, boolean throwException) {
		for(Position position: getSet()) {
			if(position.getPortfolio().compareTo(instrumentLeg.getPortfolio()) == 0) {
				if(position.getInstrumentLeg().compareTo(instrumentLeg) == 0) {
					if(position.getDate().compareTo(date) == 0) {
						return position;
					}
				}
			}
		}
		if(throwException) {
			throw new IllegalArgumentException("No such Position in collection for arguments (" + instrumentLeg.getPortfolio().getName() + "," + instrumentLeg.toString() + "," + StringUtil.formatDate(date) + ")");
		} else {
			return null;
		}
	}
	
	public Positions getPositions(String instrumentId) {
		Positions positions = new Positions();
		for (Position position : getSet()) {
			if (position.getInstrumentLeg().getInstrumentId().equalsIgnoreCase(instrumentId)) {
				positions.add(position);
			}
		}
		return positions;
	}
	
	public Positions getPositions(Date date, String instrumentId) {//TODO: should this just be returning a single position?
		Positions positions = new Positions();
		for (Position position : getSet()) {
			if (position.getDate().equals(date)) {
				if (position.getInstrumentLeg().getInstrumentId().equalsIgnoreCase(instrumentId)) {
					positions.add(position);
				}
			}
		}
		return positions;
	}
	
	public Date getFirstDate() {
		return DateUtil.getMinDate(getDates());
	}
	
	public Date getLastDate() {
		return DateUtil.getMaxDate(getDates());
	}

	public void addSecurityMasterData(SecurityMasters securityMasters) {
		getInstruments().addSecurityMasterData(securityMasters);
	}
	
	public Instruments getInstruments() {
		if(instruments == null) {
			instruments = new Instruments();
			for(Position position: getSet()) {
				instruments.add(position.getInstrument());
			}
		}
		return instruments;
	}

	public void requirePositionForDate(Date date) {
		InstrumentLegs instrumentLegs = getInstrumentLegs();
		for(InstrumentLeg instrumentLeg : instrumentLegs) {
			if(getPosition(date, instrumentLeg, false) == null) {
				add(new Position(instrumentLeg, date));
			}
		}
	}
}