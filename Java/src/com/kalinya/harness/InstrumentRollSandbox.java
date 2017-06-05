package com.kalinya.harness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.kalinya.instrumentroll.InstrumentRoll;
import com.kalinya.instrumentroll.InstrumentRollMethod;
import com.kalinya.optimization.Instrument;
import com.kalinya.util.DateUtil;
import com.kalinya.util.NumberUtil;

public class InstrumentRollSandbox {
	
	public static void main(String[] args) {
		InstrumentRollSandbox sandbox = new InstrumentRollSandbox();
		sandbox.doBondRoll();
	}
	
	public InstrumentRollSandbox() {
	}
	
	private void doBondRoll() {
		List<Instrument> portfolio = getInstruments();
		InstrumentRoll instrumentRoll = InstrumentRoll.create();
		instrumentRoll.setInstrumentRollMethod(InstrumentRollMethod.DURATION_NEUTRAL);
		
		//Set the instrument to dispose
		Map<Instrument, BigDecimal> instrumentsToDispose = new TreeMap<Instrument, BigDecimal>();
		instrumentsToDispose.put(getInstrument(portfolio, "3yBond"), NumberUtil.newBigDecimal(1e6, 2));
		instrumentRoll.setInstrumentsToDispose(instrumentsToDispose);
		
		//Set the instrument to acquire
		List<Instrument> instrumentsToAcquire = new ArrayList<>();
		instrumentsToAcquire.add(getInstrument(portfolio, "5yBond"));
		instrumentRoll.setInstrumentsToAcquire(instrumentsToAcquire);
		
		instrumentRoll.calculateRoll();
		System.out.println(instrumentRoll.toVerboseString());
	}
	
	private Instrument getInstrument(List<Instrument> portfolio, String instrumentId) {
		for(Instrument instrument: portfolio) {
			if(instrument.getInstrumentId().equalsIgnoreCase(instrumentId)) {
				return instrument;
			}
		}
		throw new IllegalArgumentException(String.format("InstrumentId [%s] was not found in the portfolio of instruments: %s", portfolio.toString()));
	}

	private List<Instrument> getInstruments() {
		List<Instrument> portfolio = new ArrayList<>();
		portfolio.add(Instrument.CASH);
		portfolio.add(new Instrument.Builder("3yBondDecayed2.5y").withDuration(0.4906).withConvexity(0.2406).withYield(1.70).withMaturityDate(DateUtil.parseSymbolicDate("6m")).build());
		portfolio.add(new Instrument.Builder("3yBondDecayed2y").withDuration(0.9706).withConvexity(0.7155).withYield(2.19).withMaturityDate(DateUtil.parseSymbolicDate("1y")).build());
		portfolio.add(new Instrument.Builder("1yBill").withDuration(0.9889).withConvexity(0.7334).withYield(2.25).withMaturityDate(DateUtil.parseSymbolicDate("1y")).build());
		portfolio.add(new Instrument.Builder("3yBondDecayed1y").withDuration(1.8767).withConvexity(2.2730).withYield(3.00).withMaturityDate(DateUtil.parseSymbolicDate("2y")).build());
		portfolio.add(new Instrument.Builder("3yBond").withDuration(2.7433).withConvexity(4.5964).withYield(3.90).withMaturityDate(DateUtil.parseSymbolicDate("3y")).build());
		portfolio.add(new Instrument.Builder("5yBond").withDuration(4.2299).withConvexity(10.7844).withYield(5.00).withMaturityDate(DateUtil.parseSymbolicDate("5y")).build());
		portfolio.add(new Instrument.Builder("10yBond").withDuration(7.0298).withConvexity(31.6523).withYield(6.30).withMaturityDate(DateUtil.parseSymbolicDate("10y")).build());
		portfolio.add(new Instrument.Builder("15yBond").withDuration(8.6781).withConvexity(52.6504).withYield(7.20).withMaturityDate(DateUtil.parseSymbolicDate("15y")).build());
		portfolio.add(new Instrument.Builder("20yBond").withDuration(9.3945).withConvexity(67.5164).withYield(8.20).withMaturityDate(DateUtil.parseSymbolicDate("20y")).build());
		return portfolio;
	}
}
