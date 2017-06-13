package com.kalinya.results;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.kalinya.performance.Instrument;
import com.kalinya.performance.Instruments;
import com.kalinya.performance.Portfolio;
import com.kalinya.performance.datasource.DataSource;
import com.kalinya.util.BaseSet;
import com.kalinya.util.StringUtil;

public class InstrumentResults extends BaseSet<InstrumentResult> implements Results {

	private static final long serialVersionUID = 18379221951726194L;
	private Date date;
	private Portfolio portfolio;
	private Set<InstrumentResultEnum> instrumentResults;
	private Map<Date, Set<InstrumentResult>> resultsByDate;
	private Map<Instrument, Set<InstrumentResult>> resultsByInstrument;
	
	public InstrumentResults() {
		super();
		instrumentResults = new HashSet<>();
	}
	
	@Override
	public String toString() {
		return toMinimalString();
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public Date getDate() {
		return date;
	}
	
	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

	public boolean setValue(Date date, Instrument instrument, InstrumentResultEnum resultEnum, BigDecimal value) {
		InstrumentResult resultValue = InstrumentResult.create(date, instrument, resultEnum, value);
		return add(resultValue);
	}

	public static InstrumentResults create() {
		return new InstrumentResults();
	}
	
	@Override
	public boolean add(InstrumentResult instrumentResult) {
		instrumentResults.add(instrumentResult.getInstrumentResultEnum());
		return super.add(instrumentResult);
	}
	
	@Override
	public boolean addAll(Collection<? extends InstrumentResult> arg0) {
		for(InstrumentResult instrumentResult: arg0) {
			instrumentResults.add(instrumentResult.getInstrumentResultEnum());
		}
		return super.addAll(arg0);
	}
	
	public BigDecimal getValue(Instrument instrument, InstrumentResultEnum instrumentResultEnum, boolean throwException) {
		setAndGetResultsByDate();
		if(resultsByDate.size() > 1) {
			if(throwException) {
				throw new IllegalArgumentException("There are results for more than one date. A date must be specified in order to retrieve the correct results");
			}
			return null;
		}
		return getValue(null, instrument, instrumentResultEnum, throwException);
	}
	
	public BigDecimal getValue(Date date, Instrument instrument, InstrumentResultEnum instrumentResultEnum, boolean throwException) {
		InstrumentResult instrumentResult = getResult(date, instrument, instrumentResultEnum, throwException);
		if(instrumentResult == null) {
			return null;
		}
		return instrumentResult.getValue();
	}
	
	public InstrumentResult getResult(Date date, Instrument instrument, InstrumentResultEnum instrumentResultEnum, boolean throwException) {
		setAndGetResultsByInstrument();
		
		//Verify there are results for this instrument
		if(!resultsByInstrument.containsKey(instrument)) {
			if(throwException) {
				throw new IllegalArgumentException(String.format("Failed to retrieve results for Instrument [%s]", 
						instrument.getInstrumentId()));
			}
			return null;
		}
		
		//Verify there are results for this date
		setAndGetResultsByDate();
		if(date != null && !resultsByDate.containsKey(date)) {
			if(throwException) {
				throw new IllegalArgumentException(String.format("Failed to retrieve results for Date [%s]", 
						StringUtil.formatDate(date)));
			}
			return null;
		}
		
		for(InstrumentResult result: getSet()) {
			if(result.getInstrument().equals(instrument)) {
				if(result.getInstrumentResultEnum().equals(instrumentResultEnum)) {
					if(date == null) {
						if(resultsByDate.size() == 1) {
							return result;
						} else {
							if(throwException) {
								throw new IllegalArgumentException("There are results for more than one date. A date must be specified in order to retrieve the correct results");
							}
							return null;
						}
					}
					if(result.getDate().compareTo(date) == 0) {
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
	
	public Map<Date, Set<InstrumentResult>> setAndGetResultsByDate() {
		if(resultsByDate != null) {
			return resultsByDate;
		}
		resultsByDate = new TreeMap<Date, Set<InstrumentResult>>();
		for(InstrumentResult result: getSet()) {
			Date date = result.getDate();
			Set<InstrumentResult> instrumentResults = resultsByDate.get(date);
			if(instrumentResults == null) {
				resultsByDate.put(date, new HashSet<>());
			}
			resultsByDate.get(date).add(result);
		}
		return resultsByDate;
	}


	/**
	 * Retrieves InstrumentResults for the parameter date, instruments and result enumeration
	 * 
	 * @param today
	 * @param instruments
	 * @param instrumentResultEnum
	 * @param throwException
	 * @return
	 */
	public InstrumentResults getResults(Date date, Instruments instruments, InstrumentResultEnum instrumentResultEnum,
			boolean addDefaultCashResults, boolean throwException) {
		InstrumentResults instrumentResults = InstrumentResults.create();
		if(addDefaultCashResults) {
			//Add the default cash value
			instrumentResults.add(InstrumentResult.create(date, Instrument.CASH, instrumentResultEnum, instrumentResultEnum.getDefaultCashValue()));
		}
		for(InstrumentResult result: getSet()) {
			if(result.getDate().compareTo(date) == 0) {
				if(result.getInstrumentResultEnum().equals(instrumentResultEnum)) {
					if(instruments.contains(result.getInstrument())) {
						instrumentResults.add(result);
					}
				}
			}
		}
		if(instrumentResults.size() != instruments.size()
				&& throwException) {
			throw new IllegalArgumentException(String.format("Failed to retrieve all results for Result [%s] for Date [%s]: [%s] instruments and [%s] results", 
					StringUtil.formatDate(date), instrumentResultEnum, instruments.size(), instrumentResults.size()));
		}
		return instrumentResults;
	}

	/**
	 * Returns the results as an array
	 * 
	 * @return
	 */
	public double[] asArray() {
		double[] array = new double[size()];
		int i = 0;
		for(InstrumentResult instrumentResult: getSet()) {
			array[i] = instrumentResult.getValue().doubleValue();
			i++;
		}
		return array;
	}

	public void addDefaultCashResults() {
		for(InstrumentResultEnum instrumentResultEnum: instrumentResults) {
			add(InstrumentResult.create(date, Instrument.CASH, instrumentResultEnum, instrumentResultEnum.getDefaultCashValue()));
		}
	}
}
