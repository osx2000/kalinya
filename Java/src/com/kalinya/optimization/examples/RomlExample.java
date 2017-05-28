package com.kalinya.optimization.examples;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;
import com.github.rcaller.rstuff.ROutputParser;
import com.kalinya.performance.Configurator;
import com.kalinya.util.Assertions;
import com.kalinya.util.ExtractUtil;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.StringUtil;
import com.kalinya.util.Timer;

public class RomlExample {

	public RomlExample() {
		Timer timer = new Timer();
		try {
			timer.start("RCaller constructor");
			/**
			 * Creating RCaller
			 */
			RCaller caller = RCaller.create();
			RCode code = RCode.create();
			/*
			 * Add input variables
			 */
			//code.addDouble("mufree", 1.3/253);
			//code.addInt("scenarioSize", 300); //was 300
			/**
			 * Add R Code
			 */
			List<String> importedCode = RUtil.getImportedCode(Configurator.getRCode(this.getClass()));
			RUtil.addRCode(code, importedCode);
			RUtil.addRDetailsToRCode(code);
			/*
			 * Add required variables to a list so they're available to the RCaller.ROutputParser
			 * https://stackoverflow.com/questions/16962884/how-rcaller-get-results-by-runandreturnresult
			 */
			code.addRCode("returnValues <- list("
					+ "maximumReturnPortfolioWeights=maximumReturnPortfolioWeights"
					+ ", minimumVariancePortfolioWeights=minimumVariancePortfolioWeights"
					+ ", minimumMaximumLossPortfolioWeights=minimumMaximumLossPortfolioWeights"
					+ ", minimumMeanAbsoluteDeviationPortfolioWeights=minimumMeanAbsoluteDeviationPortfolioWeights"
					+ ", minimumLowerSemiVariancePortfolioWeights=minimumLowerSemiVariancePortfolioWeights"
					+ ", minimumLowerSemiAbsoluteDeviationPortfolioWeights=minimumLowerSemiAbsoluteDeviationPortfolioWeights"
					+ ", instrumentIds=instrumentIds"
					+ ", " + RUtil.getRDetailsVariableNamesCsv() + ")");
			caller.setRCode(code);
			timer.start("RCaller execute");
			caller.runAndReturnResult("returnValues");
			timer.start("Analyze results");
			ROutputParser parser = caller.getParser();
			RDetails rDetails = new RDetails(parser);
			System.out.println("R Details: " + rDetails.toString());
			
			String[] instrumentIds = parser.getAsStringArray("instrumentIds");
			instrumentIds = RUtil.removeInapplicableInstrumentsFromDataFrame(instrumentIds);
			
			// Maximize Expected Return Portfolio
			double[] maximumReturnPortfolioWeights = parser.getAsDoubleArray("maximumReturnPortfolioWeights");
			Map<String, BigDecimal> maximumReturnPortfolioWeightsByInstrumentId = getWeightsByInstrumentId(instrumentIds, maximumReturnPortfolioWeights);
			StringUtil.printMap(maximumReturnPortfolioWeightsByInstrumentId, "Maximum Returne Portfolio Weights By InstrumentId");
			Assertions.sumsToOne("maximumReturnPortfolioWeightsByInstrumentId", maximumReturnPortfolioWeightsByInstrumentId);
			
			// Minimize Variance Portfolio
			double[] minimumVariancePortfolioWeights = parser.getAsDoubleArray("minimumVariancePortfolioWeights");
			Map<String, BigDecimal> minimumVariancePortfolioWeightsByInstrumentId = getWeightsByInstrumentId(instrumentIds, minimumVariancePortfolioWeights);
			StringUtil.printMap(minimumVariancePortfolioWeightsByInstrumentId, "Minimum Variance Portfolio Weights By InstrumentId");
			Assertions.sumsToOne("minimumVariancePortfolioWeightsByInstrumentId", minimumVariancePortfolioWeightsByInstrumentId);
			
			//Minimize Mean Absolute Deviation
			double[] minimumMeanAbsoluteDeviationPortfolioWeights = parser.getAsDoubleArray("minimumMeanAbsoluteDeviationPortfolioWeights");
			Map<String, BigDecimal> minimumMeanAbsoluteDeviationPortfolioWeightsByInstrumentId = getWeightsByInstrumentId(instrumentIds, minimumMeanAbsoluteDeviationPortfolioWeights);
			StringUtil.printMap(minimumMeanAbsoluteDeviationPortfolioWeightsByInstrumentId, "Minimize Mean Absolute Deviation Portfolio Weights By InstrumentId");
			Assertions.sumsToOne("minimumMeanAbsoluteDeviationPortfolioWeightsByInstrumentId", minimumMeanAbsoluteDeviationPortfolioWeightsByInstrumentId);
			
			//Minimize Lower Semi-Variance
			double[] minimumLowerSemiVariancePortfolioWeights = parser.getAsDoubleArray("minimumLowerSemiVariancePortfolioWeights");
			Map<String, BigDecimal> minimumLowerSemiVariancePortfolioWeightsByInstrumentId = getWeightsByInstrumentId(instrumentIds, minimumLowerSemiVariancePortfolioWeights);
			StringUtil.printMap(minimumLowerSemiVariancePortfolioWeightsByInstrumentId, "Minimize Lower Semi-Variance Portfolio Weights By InstrumentId");
			Assertions.sumsToOne("minimumLowerSemiVariancePortfolioWeightsByInstrumentId", minimumLowerSemiVariancePortfolioWeightsByInstrumentId);
			
			//Minimize Lower Semi-Absolute Deviation
			double[] minimumLowerSemiAbsoluteDeviationPortfolioWeights = parser.getAsDoubleArray("minimumLowerSemiAbsoluteDeviationPortfolioWeights");
			if(RUtil.hasFeasibleSolution(minimumLowerSemiAbsoluteDeviationPortfolioWeights)) {
				Map<String, BigDecimal> minimumLowerSemiAbsoluteDeviationPortfolioWeightsByInstrumentId = getWeightsByInstrumentId(instrumentIds, minimumLowerSemiAbsoluteDeviationPortfolioWeights);
				StringUtil.printMap(minimumLowerSemiAbsoluteDeviationPortfolioWeightsByInstrumentId, "Minimize Lower Semi-Absolute Deviation Portfolio Weights By InstrumentId");
				Assertions.sumsToOne("minimumLowerSemiAbsoluteDeviationPortfolioWeightsByInstrumentId", minimumLowerSemiAbsoluteDeviationPortfolioWeightsByInstrumentId);
			} else {
				System.err.println(String.format("WARNING: There was no feasible solution to the [%s] optimization problem", "Minimize Lower Semi-Absolute Deviation"));
			}
			
			
			//Minimize the Maximum Loss
			double[] minimumMaximumLossPortfolioWeights = parser.getAsDoubleArray("minimumMaximumLossPortfolioWeights");
			if(RUtil.hasFeasibleSolution(minimumMaximumLossPortfolioWeights)) {
				Map<String, BigDecimal> minimumMaximumLossPortfolioWeightsByInstrumentId = getWeightsByInstrumentId(instrumentIds, minimumMaximumLossPortfolioWeights);
				StringUtil.printMap(minimumMaximumLossPortfolioWeightsByInstrumentId, "Minimize the Maximum Loss Portfolio Weights By InstrumentId");
				Assertions.sumsToOne("minimumMaximumLossPortfolioWeightsByInstrumentId", minimumMaximumLossPortfolioWeightsByInstrumentId);
			} else {
				System.err.println(String.format("WARNING: There was no feasible solution to the [%s] optimization problem", "Minimize the Maximum Loss"));
			}
			
			List<String> tempFileNames = RUtil.getRCallerTempFileNames(caller);
			System.out.println(String.format("RCaller temp files path [%s]", tempFileNames));
			ExtractUtil.extractToCsv(instrumentIds, minimumVariancePortfolioWeights, Configurator.EFFICIENT_FRONTIER_WEIGHTS_EXTRACT_FILE_PATH);
			
			caller.deleteTempFiles();
			timer.print(true);
		} catch (Exception e) {
			Logger.getLogger(RomlExample.class.getName()).log(Level.SEVERE, e.getMessage());
		}
	}

	private Map<String, BigDecimal> getWeightsByInstrumentId(String[] instrumentIds, double[] weights) {
		Map<String, BigDecimal> weightsByInstrumentId = new LinkedHashMap<>();
		int i = 0;
		for(String instrumentId: instrumentIds) {
			weightsByInstrumentId.put(instrumentId, NumberUtil.newBigDecimal(weights[i]));
			i++;
		}
		return weightsByInstrumentId;
	}
	
	public static void main(String[] args) {
		new RomlExample();
	}
}
