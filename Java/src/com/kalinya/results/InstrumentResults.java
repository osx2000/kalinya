package com.kalinya.results;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.kalinya.performance.Instrument;
import com.kalinya.performance.Portfolio;
import com.kalinya.performance.datasource.DataSource;
import com.kalinya.util.BaseSet;
import com.kalinya.util.StringUtil;

public class InstrumentResults extends BaseSet<InstrumentResult> implements Results {

	private static final long serialVersionUID = 18379221951726194L;
	private Date date;
	private Portfolio portfolio;
	private Map<Date, Set<InstrumentResults>> resultsByDate;
	private Map<Instrument, Set<InstrumentResult>> resultsByInstrument;
	
	@Override
	public String toString() {
		return toMinimalString();
	}
	
	@Override
	public Date getDate() {
		return date;
	}
	
	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setValue(Date date, Instrument instrument, InstrumentResultEnum resultEnum, BigDecimal value) {
		InstrumentResult resultValue = InstrumentResult.create(date, instrument, resultEnum, value);
		getSet().add(resultValue);
	}

	public static InstrumentResults create() {
		return new InstrumentResults();
	}
	
	public BigDecimal getValue(Date date, Instrument instrument, InstrumentResultEnum instrumentResultEnum, boolean throwException) {
		InstrumentResult instrumentResult = getResult(date, instrument, instrumentResultEnum, throwException);
		if(instrumentResult == null) {
			return null;
		}
		return instrumentResult.getValue();
	}
	
	public InstrumentResult getResult(Date date, Instrument instrument, InstrumentResultEnum instrumentResultEnum, boolean throwException) {
		for(InstrumentResult result: getSet()) {
			if(result.getDate().compareTo(date) == 0) {
				if(result.getInstrument().compareTo(instrument) == 0) {
					if(result.getInstrumentResultEnum().equals(instrumentResultEnum)) {
						return result;
					}
				}
			}
		}
		if(throwException) {
			throw new IllegalArgumentException(String.format("Failed to retrieve InstrumentResult Date [%s] Instrument [%s], Result [%s]", 
					StringUtil.formatDate(date), instrument.getInstrumentId(), instrumentResultEnum));
		}
		return null;
	}
	
	public InstrumentResult getResult(Date date, String instrumentId, InstrumentResultEnum instrumentResultEnum, boolean throwException) {
		return getResult(date, Instrument.create(instrumentId), instrumentResultEnum, throwException);
	}

	public static InstrumentResults retrieve(DataSource dataSource, Date dateFilter) {
		return dataSource.getInstrumentResults(dateFilter);
	}

	public InstrumentResults getResults(Date date, String instrumentId, boolean throwException) {
		Map<Instrument, Set<InstrumentResult>> resultsByInstrument = setAndGetResultsByInstrument();
		InstrumentResults instrumentResults = create();
		Set<InstrumentResult> resultsSet = resultsByInstrument.get(Instrument.create(instrumentId));
		if(resultsSet == null) {
			if(throwException) {
				throw new IllegalArgumentException(String.format("Failed to retrieve InstrumentResults Date [%s] Instrument [%s]", 
						StringUtil.formatDate(date), instrumentId));
			}
			return instrumentResults;
		}
		for(InstrumentResult instrumentResult: resultsSet) {
			instrumentResults.add(instrumentResult);
		}
		return instrumentResults;
	}
	
	public Map<Instrument, Set<InstrumentResult>> setAndGetResultsByInstrument() {
		if(resultsByInstrument != null) {
			return resultsByInstrument;
		}
		resultsByInstrument = new TreeMap<Instrument, Set<InstrumentResult>>();
		for(InstrumentResult result: getSet()) {
			Instrument instrument = result.getInstrument();
			Set<InstrumentResult> instrumentResults = resultsByInstrument.get(instrument);
			if(instrumentResults == null) {
				resultsByInstrument.put(instrument, new HashSet<>());
			}
			resultsByInstrument.get(instrument).add(result);
		}
		return resultsByInstrument;
	}
}
