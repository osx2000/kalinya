package com.kalinya.harness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kalinya.assetallocation.Dimension;
import com.kalinya.optimization.Instrument;
import com.kalinya.optimization.InstrumentStatistic;
import com.kalinya.optimization.MaturityBucket;
import com.kalinya.util.Assertions;
import com.kalinya.util.DateUtil;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.StringUtil;

public class TestHarnessHelper {
	public static List<Dimension> getAssetAllocationDimensions() {
		//Level 3
		Dimension govt = Dimension.create("Govt");
		Dimension semiGovt = Dimension.create("SemiGovt");
		Dimension corp = Dimension.create("Corp");
		Dimension bmkBonds = Dimension.create("BmkBonds");
		Dimension bmkBills = Dimension.create("BmkBills");

		//Level 2
		Dimension active = Dimension.create("Active");
		Dimension passive = Dimension.create("Passive");
		Dimension country = Dimension.create("Country");
		Dimension duration = Dimension.create("Duration");
		Dimension cash = Dimension.create("Cash");

		//Level 1
		Dimension core = Dimension.create("Core");
		Dimension satellite = Dimension.create("Satellite");
		Dimension cash1 = Dimension.create("Cash1");

		//Add inheritance
		govt.setParentDimension(active);
		semiGovt.setParentDimension(active);
		corp.setParentDimension(active);
		active.setParentDimension(core);
		bmkBonds.setParentDimension(passive);
		bmkBills.setParentDimension(passive);
		passive.setParentDimension(core);
		country.setParentDimension(satellite);
		duration.setParentDimension(satellite);
		cash.setParentDimension(cash1);

		return Dimension.getDimensionsAsList(govt, semiGovt, corp, bmkBonds, bmkBills, active, passive, country, duration, cash, core, satellite, cash1);
	}
	
	public static MaturityBucket[] getMaturityBuckets() {
		return MaturityBucket.createMaturityBuckets(new String[]{"1y", "2y", "5y", "10y", "30y"});
	}
	
	public static List<Instrument> getInstruments() {
		List<Instrument> portfolio = new ArrayList<>();
		MaturityBucket[] maturityBuckets = getMaturityBuckets();
		portfolio.add(Instrument.CASH);
		portfolio.add(new Instrument.Builder("3yBondDecayed2.5y").withDuration(0.4906).withConvexity(0.2406).withYield(1.70).withMaturityDate(DateUtil.parseSymbolicDate("6m")).build());
		portfolio.add(new Instrument.Builder("3yBondDecayed2y").withDuration(0.9706).withConvexity(0.7155).withYield(2.19).withMaturityDate(DateUtil.parseSymbolicDate("1y")).build());
		portfolio.add(new Instrument.Builder("1yBill").withDuration(0.9889).withConvexity(0.7334).withYield(2.25).withMaturityDate(DateUtil.parseSymbolicDate("1y")).build());
		portfolio.add(new Instrument.Builder("3yBondDecayed1y").withDuration(1.8767).withConvexity(2.2730).withYield(3.00).withMaturityDate(DateUtil.parseSymbolicDate("2y")).build());
		portfolio.add(new Instrument.Builder("3yBond").withDuration(2.7433).withConvexity(4.5964).withYield(3.90).withMaturityDate(DateUtil.parseSymbolicDate("3y")).build());
		portfolio.add(new Instrument.Builder("5yBond").withDuration(4.2299).withConvexity(10.7844).withYield(5.00).withMaturityDate(DateUtil.parseSymbolicDate("5y")).build());
		portfolio.add(new Instrument.Builder("10yBond").withDuration(7.0298).withConvexity(31.6523).withYield(6.30).withMaturityDate(DateUtil.parseSymbolicDate("10y")).build());
		portfolio.add(new Instrument.Builder("15yBond").withDuration(8.6781).withConvexity(52.6504).withYield(7.20).withMaturityDate(DateUtil.parseSymbolicDate("15y")).build());
		portfolio.add(new Instrument.Builder("20yBond").withDuration(9.3945).withConvexity(67.5164).withYield(8.20).withMaturityDate(DateUtil.parseSymbolicDate("20y")).assignToMaturityBucket(maturityBuckets).build());
		Instrument.assignToMaturityBuckets(portfolio, maturityBuckets);
		return portfolio;
	}
	
	public static List<Instrument> getInstruments2() {
		List<Instrument> portfolio = new ArrayList<>();
		MaturityBucket[] maturityBuckets = getMaturityBuckets();
		portfolio.add(new Instrument.Builder("Bond1").withDuration(1.863930404).withConvexity(4.455761339).withYield(2.947).withMaturityDate(DateUtil.parseSymbolicDate("2y")).build());
		portfolio.add(new Instrument.Builder("Bond2").withDuration(4.52407262).withConvexity(23.70475552).withYield(3.565).withMaturityDate(DateUtil.parseSymbolicDate("5y")).build());
		portfolio.add(new Instrument.Builder("Bond3").withDuration(8.062189241).withConvexity(77.26053665).withYield(4.180).withMaturityDate(DateUtil.parseSymbolicDate("10y")).build());
		Instrument.assignToMaturityBuckets(portfolio, maturityBuckets);
		return portfolio;
	}
	
	public static BigDecimal getYield(Instrument[] portfolio, double[] instrumentWeights) {
		return getStatistic(portfolio, instrumentWeights, InstrumentStatistic.YIELD);
	}
	
	public static BigDecimal getDuration(Instrument[] portfolio, double[] instrumentWeights) {
		return getStatistic(portfolio, instrumentWeights, InstrumentStatistic.DURATION);
	}
	
	public static BigDecimal getConvexity(Instrument[] portfolio, double[] instrumentWeights) {
		return getStatistic(portfolio, instrumentWeights, InstrumentStatistic.CONVEXITY);
	}
	
	public static BigDecimal getStatistic(Instrument[] portfolio, double[] instrumentWeights, InstrumentStatistic instrumentStatistic) {
		Assertions.notNull("Instruments", "Portfolio of instruments");
		Assertions.isEqual("Instruments and instrument weights", portfolio.length, instrumentWeights.length);
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
			for(int i = 0; i < portfolio.length; i++) {
				instrument = portfolio[i];
				BigDecimal instrumentWeight = NumberUtil.newBigDecimal(instrumentWeights[i]);
				BigDecimal instrumentProRataWeight = instrumentWeight.divide(sumOfWeights, NumberUtil.MATH_CONTEXT);
				statisticValue = statisticValue.add(instrument.getStatistic(instrumentStatistic).multiply(instrumentProRataWeight));
			};
			return statisticValue;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Exception handling Instrument [%s] SumOfWeights [%s]", instrument.toString(), StringUtil.formatPrice(sumOfWeights)), e);
		}
	}
	
	public static double[] getInstrumentConvexity(Collection<Instrument> instruments) {
		double[] convexitys = new double[instruments.size()];
		int i = 0;
		for(Instrument instrument: instruments) {
			convexitys[i] = instrument.getConvexity().doubleValue();
			i++;
		}
		return convexitys;
	}
	
	public static double[] getInstrumentDuration(Collection<Instrument> instruments) {
		double[] durations = new double[instruments.size()];
		int i = 0;
		for(Instrument instrument: instruments) {
			durations[i] = instrument.getDuration().doubleValue();
			i++;
		}
		return durations;
	}
	
	public static double[] getInstrumentYield(Collection<Instrument> instruments) {
		double[] yields = new double[instruments.size()];
		int i = 0;
		for(Instrument instrument: instruments) {
			yields[i] = instrument.getYield().doubleValue();
			i++;
		}
		return yields;
	}

	public static Map<Instrument, BigDecimal> getPortfolio() {
		Map<Instrument, BigDecimal> portfolio = new LinkedHashMap<Instrument, BigDecimal>();
		List<Instrument> instruments = getInstruments();
		
		getInstrument(instruments, "Cash").setDimension(Dimension.CASH);
		portfolio.put(getInstrument(instruments, "Cash"), NumberUtil.newBigDecimal(600e3, 2));
		
		getInstrument(instruments, "3yBondDecayed2.5y").setDimension(Dimension.create("Govt"));
		portfolio.put(getInstrument(instruments, "3yBondDecayed2.5y"), NumberUtil.newBigDecimal(4.2e6, 2));
		
		getInstrument(instruments, "3yBondDecayed2y").setDimension(Dimension.create("Govt"));
		portfolio.put(getInstrument(instruments, "3yBondDecayed2y"), NumberUtil.newBigDecimal(4.6e6, 2));
		
		getInstrument(instruments, "1yBill").setDimension(Dimension.create("BmkBills"));
		portfolio.put(getInstrument(instruments, "1yBill"), NumberUtil.newBigDecimal(1.5e6, 2));
		
		getInstrument(instruments, "3yBondDecayed1y").setDimension(Dimension.create("SemiGovt"));
		portfolio.put(getInstrument(instruments, "3yBondDecayed1y"), NumberUtil.newBigDecimal(1.6e6, 2));
		
		getInstrument(instruments, "3yBond").setDimension(Dimension.create("Corp"));
		portfolio.put(getInstrument(instruments, "3yBond"), NumberUtil.newBigDecimal(790e3, 2));
		
		getInstrument(instruments, "5yBond").setDimension(Dimension.create("BmkBonds"));
		portfolio.put(getInstrument(instruments, "5yBond"), NumberUtil.newBigDecimal(575e3, 2));
		
		getInstrument(instruments, "10yBond").setDimension(Dimension.create("BmkBonds"));
		portfolio.put(getInstrument(instruments, "10yBond"), NumberUtil.newBigDecimal(725e3, 2));
		
		getInstrument(instruments, "15yBond").setDimension(Dimension.create("Duration"));
		portfolio.put(getInstrument(instruments, "15yBond"), NumberUtil.newBigDecimal(600e3, 2));
		
		getInstrument(instruments, "20yBond").setDimension(Dimension.create("Country"));
		portfolio.put(getInstrument(instruments, "20yBond"), NumberUtil.newBigDecimal(810e3, 2));

		return portfolio;
	}
	
	public static BigDecimal getPortfolioSize(Map<Instrument, BigDecimal> portfolio) {
		BigDecimal portfolioSize = BigDecimal.ZERO;
		for(BigDecimal instrumentValue: portfolio.values()) {
			portfolioSize = portfolioSize.add(instrumentValue);
		}
		return portfolioSize;
	}
	
	public static Map<Dimension, Set<Instrument>> getInstrumentsByDimension(Map<Instrument, BigDecimal> portfolio) {
		Map<Dimension, Set<Instrument>> instrumentsByDimension = new LinkedHashMap<Dimension, Set<Instrument>>();
		for(Instrument instrument: portfolio.keySet()) {
			Dimension dimension = instrument.getDimension();
			Set<Instrument> instruments = instrumentsByDimension.get(dimension);
			if(instruments == null) {
				instrumentsByDimension.put(dimension, new LinkedHashSet<Instrument>());
			}
			instrumentsByDimension.get(dimension).add(instrument);
		}
		return instrumentsByDimension;
	}
	
	public static Map<Dimension, BigDecimal> getPortfolioSizeByDimension(Map<Instrument, BigDecimal> portfolio) {
		Map<Dimension, BigDecimal> portfolioSizeByDimension = new LinkedHashMap<Dimension, BigDecimal>();
		for(Instrument instrument: portfolio.keySet()) {
			BigDecimal value = portfolio.get(instrument);
			Dimension dimension = instrument.getDimension();
			BigDecimal dimensionSize = portfolioSizeByDimension.get(dimension);
			if(dimensionSize != null) {
				value = dimensionSize.add(value);
			}
			portfolioSizeByDimension.put(dimension, value);
		}
		return portfolioSizeByDimension;
	}

	public static Instrument getInstrument(Collection<Instrument> instruments, String instrumentId) {
		for(Instrument instrument: instruments) {
			if(instrument.getInstrumentId().equalsIgnoreCase(instrumentId)) {
				return instrument;
			}
		}
		throw new IllegalArgumentException(String.format("InstrumentId [%s] is not one of the instruments in the collection %s", instrumentId, instruments.toString()));
	}
}
