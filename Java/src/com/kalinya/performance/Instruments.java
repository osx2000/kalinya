package com.kalinya.performance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.kalinya.optimization.MaturityBucket;
import com.kalinya.optimization.MaturityBuckets;
import com.kalinya.results.InstrumentResultEnum;
import com.kalinya.results.InstrumentResults;
import com.kalinya.util.Assertions;
import com.kalinya.util.BaseSet;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.StringUtil;

public class Instruments extends BaseSet<Instrument> {
	private static final long serialVersionUID = -1720424418344669033L;
	private Set<Instrument> instrumentSet;
	private SecurityMasters securityMasters;
	private Map<Instrument, BigDecimal> weightsByInstrument;
	private Map<MaturityBucket, Instruments> instrumentsByBucket;

	public Instruments() {
		super();
	}

	public Instruments(SecurityMasters securityMasters) {
		this();
		setSecurityMasters(securityMasters);
		if(size() == 0) {
			for(String instrumentId: securityMasters.getInstrumentIds()) {
				add(Instrument.create(instrumentId));
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

	public static Instruments create() {
		return new Instruments();
	}
	
	public Map<MaturityBucket, Instruments> setAndGetInstrumentsByBucket(MaturityBuckets maturityBuckets) {
		if(instrumentsByBucket != null) {
			return instrumentsByBucket;
		}
		instrumentsByBucket = new TreeMap<MaturityBucket, Instruments>();
		for(Instrument instrument: getSet()) {
			MaturityBucket maturityBucket = maturityBuckets.getMaturityBucketForInstrument(instrument);
			Instruments instrumentsInBucket = instrumentsByBucket.get(maturityBucket);
			if(instrumentsInBucket == null) {
				instrumentsByBucket.put(maturityBucket, Instruments.create());
			}
			instrumentsByBucket.get(maturityBucket).add(instrument);
		}
		
		if(weightsByInstrument != null) {
			for(MaturityBucket maturityBucket: instrumentsByBucket.keySet()) {
				Instruments instrumentsInBucket = instrumentsByBucket.get(maturityBucket);
				instrumentsInBucket.setInstrumentWeights(weightsByInstrument);
			}
		}
		
		return instrumentsByBucket;
	}
	
	public BigDecimal getPortfolioStatistic(List<BigDecimal> instrumentWeights, InstrumentResults instrumentsResults,
			InstrumentResultEnum instrumentResultEnum) {
		Assertions.notNullOrEmpty("InstrumentWeights", instrumentWeights);
		Assertions.isEqual("Instruments and instrument weights", size(), instrumentWeights.size());
		Instrument instrument = null;
		BigDecimal sumOfWeights = null;
		BigDecimal statisticValue = null;
		try {
			statisticValue = NumberUtil.newBigDecimal(0.0);
			sumOfWeights = NumberUtil.sum(instrumentWeights);
			//TODO: handle long-short portfolios
			if(sumOfWeights.compareTo(BigDecimal.ZERO) == 0) {
				return BigDecimal.ZERO;
			}
			Iterator<Instrument> it = getSet().iterator();
			int i = 0;
			while (it.hasNext()) {
				instrument = it.next();
				/*if(instrument.equals(Instrument.CASH)) {
					System.out.println(String.format("Handling Instrument [%s]", instrument.getInstrumentId()));
				}*/
				BigDecimal instrumentWeight = instrumentWeights.get(i);
				BigDecimal instrumentProRataWeight = instrumentWeight.divide(sumOfWeights, NumberUtil.MATH_CONTEXT);
				statisticValue = statisticValue.add(instrumentsResults.getValue(instrument, instrumentResultEnum, true).multiply(instrumentProRataWeight));
				i++;
			};
			return statisticValue;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Exception handling Instrument [%s] SumOfWeights [%s] Exception [%s]", instrument.getInstrumentId(), StringUtil.formatPrice(sumOfWeights), e.getMessage()), e);
		}
	}

	public void setInstrumentWeights(double[] instrumentWeights) {
		setInstrumentWeights(NumberUtil.getDoubleArrayAsListBigDecimal(instrumentWeights));
	}
	
	public void setInstrumentWeights(List<BigDecimal> instrumentWeightsAsList) {
		//weightsByInstrument will preserve the order of insertion
		weightsByInstrument = new LinkedHashMap<Instrument, BigDecimal>();
		int i = 0;
		for(Instrument instrument: getSet()) {
			weightsByInstrument.put(instrument, instrumentWeightsAsList.get(i));
			i++;
		}
	}
	
	/**
	 * Uses the parameter Map of Instrument weights to set this collection of
	 * Instruments weights. The parameter map may include weights for more
	 * Instruments (which is equivalent to this.Instruments collection being a
	 * subset of the Instruments in the Map)
	 * 
	 * @param weightsByInstrument
	 */
	public void setInstrumentWeights(Map<Instrument, BigDecimal> weightsByInstrument) {
		List<BigDecimal> instrumentWeightsAsList = new ArrayList<>();
		for(Instrument instrument: weightsByInstrument.keySet()) {
			if(contains(instrument)) {
				instrumentWeightsAsList.add(weightsByInstrument.get(instrument));
			}
		}
		setInstrumentWeights(instrumentWeightsAsList);
	}

	public Map<Instrument, BigDecimal> getInstrumentWeights() {
		return weightsByInstrument;
	}
	
	public double[][] getInstrumentBucketMatrix(MaturityBuckets maturityBuckets) {
		double[][] matrix = new double[maturityBuckets.size()][size()];
		int i = 0;
		for(MaturityBucket maturityBucket: maturityBuckets) {
			//TODO: compress next two lines into one
			double[] bucketMembership = getInstrumentBucketVector(maturityBucket);
			matrix[i] = bucketMembership;
			i++;
		}
		return matrix;
	}
	
	public double[] getInstrumentBucketVector(MaturityBucket maturityBucket) {
		double[] vector = new double[size()];
		Instruments instrumentsInBucket = instrumentsByBucket.get(maturityBucket);
		int i = 0;
		for(Instrument instrument: getSet()) {
			if(instrumentsInBucket.contains(instrument)) {
				vector[i] = 1.0;
			}
			i++;
		}
		return vector;
	}
}

