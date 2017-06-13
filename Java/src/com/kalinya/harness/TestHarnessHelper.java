package com.kalinya.harness;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kalinya.assetallocation.AllocationDimensions;
import com.kalinya.assetallocation.AllocationDimension;
import com.kalinya.optimization.Instrument;
import com.kalinya.optimization.MaturityBuckets;
import com.kalinya.performance.Instruments;
import com.kalinya.util.NumberUtil;

public class TestHarnessHelper {
	public static AllocationDimensions getAssetAllocationDimensions() {
		//Level 3
		AllocationDimension govt = AllocationDimension.create("Govt");
		AllocationDimension semiGovt = AllocationDimension.create("SemiGovt");
		AllocationDimension corp = AllocationDimension.create("Corp");
		AllocationDimension bmkBonds = AllocationDimension.create("BmkBonds");
		AllocationDimension bmkBills = AllocationDimension.create("BmkBills");

		//Level 2
		AllocationDimension active = AllocationDimension.create("Active");
		AllocationDimension passive = AllocationDimension.create("Passive");
		AllocationDimension country = AllocationDimension.create("Country");
		AllocationDimension duration = AllocationDimension.create("Duration");
		AllocationDimension cash = AllocationDimension.create("Cash");

		//Level 1
		AllocationDimension core = AllocationDimension.create("Core");
		AllocationDimension satellite = AllocationDimension.create("Satellite");
		AllocationDimension cash1 = AllocationDimension.create("Cash1");

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
		
		//Add elements to the collection
		AllocationDimensions dimensions = AllocationDimensions.create();
		dimensions.add(govt, semiGovt, corp, bmkBonds, bmkBills, active, passive, country, duration, cash, core, satellite, cash1);
		return dimensions;
	}
	
	public static MaturityBuckets getMaturityBuckets() {
		return MaturityBuckets.create(new String[]{"1y", "2y", "5y", "10y", "30y"});
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

	public static Map<com.kalinya.performance.Instrument, BigDecimal> getPortfolio() {
		Map<com.kalinya.performance.Instrument, BigDecimal> portfolio = new LinkedHashMap<com.kalinya.performance.Instrument, BigDecimal>();
		Instruments instruments = getPerformanceInstruments();
		portfolio.put(instruments.getInstrument("Cash"), NumberUtil.newBigDecimal(600e3, 2));
		portfolio.put(instruments.getInstrument("3yBondDecayed2.5y"), NumberUtil.newBigDecimal(4.2e6, 2));
		portfolio.put(instruments.getInstrument("3yBondDecayed2y"), NumberUtil.newBigDecimal(4.6e6, 2));
		portfolio.put(instruments.getInstrument("1yBill"), NumberUtil.newBigDecimal(1.5e6, 2));
		portfolio.put(instruments.getInstrument("3yBondDecayed1y"), NumberUtil.newBigDecimal(1.6e6, 2));
		portfolio.put(instruments.getInstrument("3yBond"), NumberUtil.newBigDecimal(790e3, 2));
		portfolio.put(instruments.getInstrument("5yBond"), NumberUtil.newBigDecimal(575e3, 2));
		portfolio.put(instruments.getInstrument("10yBond"), NumberUtil.newBigDecimal(725e3, 2));
		portfolio.put(instruments.getInstrument("15yBond"), NumberUtil.newBigDecimal(600e3, 2));
		portfolio.put(instruments.getInstrument("20yBond"), NumberUtil.newBigDecimal(810e3, 2));
		return portfolio;
	}
	
	public static Instrument getInstrument(Collection<Instrument> instruments, String instrumentId) {
		for(Instrument instrument: instruments) {
			if(instrument.getInstrumentId().equalsIgnoreCase(instrumentId)) {
				return instrument;
			}
		}
		throw new IllegalArgumentException(String.format("InstrumentId [%s] is not one of the instruments in the collection %s", instrumentId, instruments.toString()));
	}

	public static Instruments getPerformanceInstruments() {
		Instruments instruments = Instruments.create();
		instruments.add(com.kalinya.performance.Instrument.CASH);
		instruments.add(com.kalinya.performance.Instrument.create("3yBondDecayed2.5y"));
		instruments.add(com.kalinya.performance.Instrument.create("3yBondDecayed2y"));
		instruments.add(com.kalinya.performance.Instrument.create("1yBill"));
		instruments.add(com.kalinya.performance.Instrument.create("3yBondDecayed1y"));
		instruments.add(com.kalinya.performance.Instrument.create("3yBond"));
		instruments.add(com.kalinya.performance.Instrument.create("5yBond"));
		instruments.add(com.kalinya.performance.Instrument.create("10yBond"));
		instruments.add(com.kalinya.performance.Instrument.create("15yBond"));
		instruments.add(com.kalinya.performance.Instrument.create("20yBond"));
		return instruments;
	}
}
