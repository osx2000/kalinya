package com.kalinya.optimization.examples;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;
import com.github.rcaller.rstuff.ROutputParser;
import com.kalinya.enums.DebugLevel;
import com.kalinya.performance.Configurator;
import com.kalinya.util.Assertions;
import com.kalinya.util.ExtractUtil;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.StringUtil;
import com.kalinya.util.Timer;

/**
 *
 * @author Mehmet Hakan Satman
 * @since 2.0
 * @version 2.0
 */
public class EfficientFrontierExample {

	private DebugLevel debugLevel = DebugLevel.LOW;

	/**
	 * From Ruppert (2015) p 476
	 * <p>
	 * Example 16.6. Finding the efficient frontier, tangency portfolio, and
	 * minimum variance portfolio using quadratic programming The following R
	 * program uses the returns on three stocks, GE, IBM, and Mobil, in the
	 * CRSPday data set in the Ecdat package. The function solve.QP() in the
	 * quadprog package is used for quadratic programming.
	 */
	public EfficientFrontierExample() {
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
			code.addDouble("mufree", 1.3/253);
			code.addInt("scenarioSize", 300); //was 300
			/**
			 * Add R Code
			 */
			List<String> importedCode = RUtil.getImportedCode(Configurator.getRCode(this.getClass()));
			RUtil.addRCode(code, importedCode);
			code.addRCode("instrumentIds <- colnames(CRSPday)");
			/*
			 * Add required variables to a list so they're available to the RCaller.ROutputParser
			 * https://stackoverflow.com/questions/16962884/how-rcaller-get-results-by-runandreturnresult
			 */
			code.addRCode("returnValues <- list("
					+ "tangencyPortfolioWeights=weights[ind, ]"
					+ ", minimumVariancePortfolioWeights=weights[ind2, ]"
					+ ", instrumentIds=instrumentIds"
					+ ", weightsMatrix=weights"
					+ ", portfolioReturns=muP"
					+ ", tangencyPortfolioWeightsMatrixIndex=ind"
					+ ", minimumVariancePortfolioWeightsMatrixIndex=ind2)");
			caller.setRCode(code);
			timer.start("RCaller execute");
			caller.runAndReturnResult("returnValues");
			timer.start("Analyze results");
			ROutputParser parser = caller.getParser();
			String[] instrumentIds = parser .getAsStringArray("instrumentIds");
			instrumentIds = RUtil.removeInapplicableInstrumentsFromDataFrame(instrumentIds);
			
			double[] tangencyPortfolioWeights = parser.getAsDoubleArray("tangencyPortfolioWeights");
			Map<String, BigDecimal> tangencyPortfolioWeightsByInstrumentId = getWeightsByInstrumentId(instrumentIds, tangencyPortfolioWeights);
			print(tangencyPortfolioWeightsByInstrumentId, "Tangency Portfolio Weights By InstrumentId");
			
			double[] minimumVariancePortfolioWeights = parser.getAsDoubleArray("minimumVariancePortfolioWeights");
			Map<String, BigDecimal> minimumVariancePortfolioWeightsByInstrumentId = getWeightsByInstrumentId(instrumentIds, minimumVariancePortfolioWeights);
			print(minimumVariancePortfolioWeightsByInstrumentId, "Minimum Variance PortfolioWeights By InstrumentId");
			
			@SuppressWarnings("unused")
			List<String> tempFileNames = RUtil.getRCallerTempFileNames(caller);
			double[] portfolioReturns = parser.getAsDoubleArray("portfolioReturns");
			
			double[][] weightsMatrix = parser.getAsDoubleMatrix("weightsMatrix");
			weightsMatrix = RUtil.getAsDoubleMatrix(parser, "weightsMatrix", true);
			ExtractUtil.extractToCsv(instrumentIds, weightsMatrix, Configurator.EFFICIENT_FRONTIER_WEIGHTS_EXTRACT_FILE_PATH);
			Map<BigDecimal, Map<String, BigDecimal>> weightsPortfolioReturnByInstrumentId = getWeightsByPortfolioReturnByInstrumentId(instrumentIds, portfolioReturns, weightsMatrix);
			print(weightsPortfolioReturnByInstrumentId);
			
			String[] tangencyPortfolioWeightsMatrixIndexArray = parser.getAsStringArray("tangencyPortfolioWeightsMatrixIndex");
			int tangencyPortfolioWeightsMatrixIndex = RUtil.getIndexFromArrayFindByValue(tangencyPortfolioWeightsMatrixIndexArray, "TRUE");
			double[] tangencyPortfolioWeightsMatrixEntry = weightsMatrix[tangencyPortfolioWeightsMatrixIndex];
			System.out.println(String.format("The %s portfolio weights are available in the %sth entry of the weights matrix", "tangency", tangencyPortfolioWeightsMatrixIndex));
			String[] minimumVariancePortfolioWeightsMatrixIndexArray = parser.getAsStringArray("minimumVariancePortfolioWeightsMatrixIndex");
			int minimumVariancePortfolioWeightsMatrixIndex = RUtil.getIndexFromArrayFindByValue(minimumVariancePortfolioWeightsMatrixIndexArray, "TRUE");
			double[] minimumVariancePortfolioWeightsMatrixEntry = weightsMatrix[minimumVariancePortfolioWeightsMatrixIndex];
			System.out.println(String.format("The %s portfolio weights are available in the %sth entry of the weights matrix", "minimum variance", minimumVariancePortfolioWeightsMatrixIndex));
			caller.deleteTempFiles();
			timer.print(true);
		} catch (Exception e) {
			Logger.getLogger(EfficientFrontierExample.class.getName()).log(Level.SEVERE, e.getMessage());
		}
	}

	private Map<BigDecimal, Map<String, BigDecimal>> getWeightsByPortfolioReturnByInstrumentId(String[] instrumentIds,
			double[] portfolioReturns, double[][] weightsMatrix) {
		Map<BigDecimal, Map<String, BigDecimal>> weightsByPortfolioReturnByInstrumentId = new TreeMap<BigDecimal, Map<String, BigDecimal>>();
		for(int i = 0; i < portfolioReturns.length; i++) {
			Map<String, BigDecimal> weightsByInstrumentId = getWeightsByInstrumentId(instrumentIds, weightsMatrix[i]);
			weightsByPortfolioReturnByInstrumentId.put(NumberUtil.newBigDecimal(portfolioReturns[i]), weightsByInstrumentId);
		}
		return weightsByPortfolioReturnByInstrumentId;
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
	
	private void print(Map<String, BigDecimal> weightsByInstrumentId, String header) {
		if(header != null) {
			System.out.println(header);
		}
		for(String instrumentId: weightsByInstrumentId.keySet()) {
			System.out.println(String.format("InstrumentId [%s] Weight [%s]", instrumentId, StringUtil.formatPrice(weightsByInstrumentId.get(instrumentId))));
		}
	}
	
	private void print(Map<BigDecimal, Map<String, BigDecimal>> weightsByPortfolioReturnByInstrumentId) {
		System.out.println("Weights By Portfolio Return By InstrumentId");
		for(BigDecimal portfolioReturn: weightsByPortfolioReturnByInstrumentId.keySet()) {
			System.out.println(String.format("Portfolio Return: [%s]", StringUtil.formatPrice(portfolioReturn)));
			Map<String, BigDecimal> portfolioWeights = weightsByPortfolioReturnByInstrumentId.get(portfolioReturn);
			Assertions.sumsToOne("portfolioWeights", portfolioWeights);
			print(portfolioWeights , null);
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

	private void print(double[] vector) {
		System.out.println("vector");
		for (int i = 0; i < vector.length; i++) {
			System.out.println(String.format("Record [%s] Value [%s]", i+1, vector[i]));
		}
	}

	public static void main(String[] args) {
		new EfficientFrontierExample();
	}
	
	public final DebugLevel getDebugLevel() {
		return debugLevel;
	}
	
}
