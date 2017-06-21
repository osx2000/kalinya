package com.kalinya.optimization.examples;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;
import com.kalinya.enums.DebugLevel;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.StringUtil;
import com.kalinya.util.Timer;

/**
 *
 * @author Mehmet Hakan Satman
 * @since 2.0
 * @version 2.0
 */
public class PGMVExample2 {

	private DebugLevel debugLevel = DebugLevel.LOW;

	/**
	 * Standalone test application.
	 * <p>
	 * Global Minimum Variance Portfolio using R from Java.
	 * 
	 * This function returns the solution of the global minimum variance
	 * portfolio (long-only) from FRAPO PDF p38
	 *
	 */
	public PGMVExample2() {
		Timer timer = new Timer();
		try {
			timer.start("RCaller constructor");
			/**
			 * Creating RCaller
			 */
			RCaller caller = RCaller.create();
			RCode code = RCode.create();
			/**
			 * Adding R Code
			 */
			code.addRCode("library(FRAPO)");
			code.addRCode("data(StockIndexAdj)");
			code.addRCode("R <- returnseries(StockIndexAdj, method = \"discrete\", trim = TRUE)");
			code.addRCode("P <- PGMV(R)");
			code.addRCode("instrumentIds <- colnames(StockIndexAdj)");

			/*
			 * Add required variables to a list so they're available to the RCaller.ROutputParser
			 * https://stackoverflow.com/questions/16962884/how-rcaller-get-results-by-runandreturnresult
			 */
			code.addRCode("returnValues <- list(weights=Weights(P), optimizationType=P@type, instrumentIds=instrumentIds)");
			caller.setRCode(code);
			timer.start("RCaller execute");
			caller.runAndReturnResult("returnValues");
			timer.start("Analyze results");
			String[] optimizationTypes = caller.getParser().getAsStringArray("optimizationType");
			String optimizationType = null;
			if(optimizationTypes != null && optimizationTypes.length == 1) {
				optimizationType = optimizationTypes[0];
			}
			System.out.println(String.format("OptimizationType [%s]", optimizationType));
			String[] instrumentIds = caller.getParser().getAsStringArray("instrumentIds");
			double[] weights = caller.getParser().getAsDoubleArray("weights");
			Map<String, BigDecimal> weightsByInstrumentId = getWeightsByInstrumentId(instrumentIds , weights);
			print(weightsByInstrumentId);
			
			//timer.print(true);
		} catch (Exception e) {
			Logger.getLogger(PGMVExample2.class.getName()).log(Level.SEVERE, e.getMessage());
		}
	}

	private Map<String, BigDecimal> getWeightsByInstrumentId(String[] instrumentIds, double[] weights) {
		Map<String, BigDecimal> weightsByInstrumentId = new HashMap<>();
		int i = 0;
		for(String instrumentId: instrumentIds) {
			weightsByInstrumentId.put(instrumentId, NumberUtil.newBigDecimal(weights[i]));
			i++;
		}
		return weightsByInstrumentId;
	}
	
	private void print(Map<String, BigDecimal> weightsByInstrumentId) {
		System.out.println("Weights By InstrumentId");
		for(String instrumentId: weightsByInstrumentId.keySet()) {
			System.out.println(String.format("InstrumentId [%s] Weight [%s]", instrumentId, StringUtil.formatDouble(weightsByInstrumentId.get(instrumentId))));
		}
	}

	@SuppressWarnings("unused")
	private void print(double[][] matrix) {
		System.out.println("matrix");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				System.out.println(String.format("Record [%s,%s] Value [%s]", i+1, j+1, matrix[i][j]));
			}
		}
	}

	public static void main(String[] args) {
		new PGMVExample2();
	}
	
	public final DebugLevel getDebugLevel() {
		return debugLevel;
	}
	
}
