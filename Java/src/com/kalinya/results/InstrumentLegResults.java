package com.kalinya.results;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.kalinya.performance.InstrumentLeg;
import com.kalinya.performance.Portfolio;

public class InstrumentLegResults implements Results {
	Map<InstrumentLeg, List<InstrumentLegResult>> instrumentLegResults;
	
	private Date date;
	private Portfolio portfolio;
	
	//TODO: constructor
	
	@Override
	public Date getDate() {
		return date;
	}
	
	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}
}
