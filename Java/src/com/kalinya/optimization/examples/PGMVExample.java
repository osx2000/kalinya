package com.kalinya.optimization.examples;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;
import com.kalinya.enums.DebugLevel;
import com.kalinya.performance.Configurator;
import com.kalinya.performance.enums.CsvHeader;
import com.kalinya.util.Assertions;
import com.kalinya.util.DateUtil;
import com.kalinya.util.PluginUtil;
import com.kalinya.util.StringUtil;
import com.kalinya.util.Timer;

/**
 *
 * @author Mehmet Hakan Satman
 * @since 2.0
 * @version 2.0
 */
public class PGMVExample {

	private DebugLevel debugLevel = DebugLevel.LOW;

	/**
	 * Standalone test application.
	 * <p>
	 * Global Minimum Variance Portfolio using R from Java.
	 * 
	 * This function returns the solution of the global minimum variance
	 * portfolio (long-only)
	 *
	 */
	public PGMVExample() {
		Timer timer = new Timer();
		try {
			/**
			 * Creating RCaller
			 */
			RCaller caller = RCaller.create();
			RCode code = RCode.create();

			/**
			 *  We are creating a random data from a normal distribution
			 * with zero mean and unit variance with size of 100
			 */
			timer.start("Create java random number array");
			/*
			 * Performance results:
			 */
			double[][] data = getRandomData();
			data = getReturnsDataFromCsvFile();
			
			/**
			 * We are transferring the double array to R
			 */
			timer.start("Inject array into R code");
			code.addDoubleMatrix("instrumentReturns", data);

			/**
			 * Adding R Code
			 */
			code.addRCode("library(corpcor)");
			code.addRCode("library(tseries)");
			/*code.addRCode("library(fPortfolio)");
			code.addRCode("library(SharpeR)");
			code.addRCode("library(PortfolioAnalytics)");
			code.addRCode("library(zoo)");
			code.addRCode("library(plotly)");*/
			code.addRCode("library(cccp)");
			code.addRCode("library(quadprog)");
			//code.addRCode("library(MASS, warn.conflicts = FALSE)");
			//code.addRCode("suppressPackageStartupMessages(library(MASS))");
			code.addRCode("library(FRAPO)");
			code.addRCode("covarianceMatrix <- cov(instrumentReturns)");
			code.addRCode("cov.shrink(covarianceMatrix)");
			//code.addRCode("cov.shrink(diff(as.matrix(na.locf(instrumentReturns))))");
			//code.addRCode("PGMV(instrumentReturns)");

			caller.setRCode(code);
			timer.start("Run R code and return result");
			//caller.runOnly();
			//caller.runAndReturnResult("instrumentReturns");
			//caller.runAndReturnResult("covarianceMatrix");
			/*
			 * Add required variables to a list so they're available to the RCaller.ROutputParser
			 * https://stackoverflow.com/questions/16962884/how-rcaller-get-results-by-runandreturnresult
			 */
			code.addRCode("result <- list(instrumentReturns=instrumentReturns, covarianceMatrix=covarianceMatrix, weights=weights)");
			caller.runAndReturnResult("result");
			//double[][] rCovarianceMatrix = caller.getParser().getAsDoubleMatrix("covarianceMatrix");
			double[][] rCovarianceMatrix = caller.getParser().getAsDoubleMatrix("covarianceMatrix");
			print(rCovarianceMatrix);
			
			timer.start("Analyze results");
			//double[][] rInstrumentReturns = caller.getParser().getAsDoubleMatrix("instrumentReturns");
			double[][] rInstrumentReturns = caller.getParser().getAsDoubleMatrix("instrumentReturns");
			print(rInstrumentReturns);
			/*double[] weights = caller.getParser().getAsDoubleArray("c");
			print(weights);*/
			
			
			timer.print(true);
		} catch (Exception e) {
			Logger.getLogger(PGMVExample.class.getName()).log(Level.SEVERE, e.getMessage());
		}
	}

	private void print(double[][] matrix) {
		System.out.println("matrix");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				System.out.println(String.format("Record [%s,%s] Value [%s]", i+1, j+1, matrix[i][j]));
			}
		}
	}

	@SuppressWarnings("unused")
	private void print(double[] vector) {
		System.out.println("vector");
		for (int i = 0; i < vector.length; i++) {
			System.out.println(String.format("Record [%s] Value [%s]", i+1, vector[i]));
		}
	}

	private double[][] getRandomData() {
		/**
		 * Creating Java's random number generator
		 */
		Random random = new Random();
		int periodCount = 240;
		int instrumentCount = 16;
		double[][] data = new double[instrumentCount][periodCount];
		for (int i = 0; i < instrumentCount; i++) {
			for (int j = 0; j < periodCount; j++) {
				data[i][j] = Math.abs(random.nextGaussian());
			}
		}
		return data;
	}

	private double[][] getReturnsDataFromCsvFile() {
		CSVParser csvParser = null;
		try {
			String filePath = Configurator.OPTIMIZATION_ASSET_RETURNS_FILE_PATH;
			Assertions.notNull(filePath, "OptimizationAssetReturnsFilePath");
			InputStream inputStream = new FileInputStream(filePath);
			Reader reader = new InputStreamReader(new BOMInputStream(inputStream));
			csvParser = new CSVParser(reader, CSVFormat.EXCEL.withHeader().withIgnoreHeaderCase().withIgnoreSurroundingSpaces().withTrim());

			if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
				Map<String, Integer> headerMap = csvParser.getHeaderMap();
				System.out.println("Header Map [" + (headerMap != null ? headerMap.toString() : "null") + "]");
			}

			List<CSVRecord> csvRecords = csvParser.getRecords();
			List<Date> dates = new ArrayList<>();
			List<Double> returnsAssetA = new ArrayList<>();
			List<Double> returnsAssetB = new ArrayList<>();
			List<Double> returnsAssetC = new ArrayList<>();
			List<Double> returnsAssetD = new ArrayList<>();
			for(CSVRecord csvRecord: csvRecords) {
				long recordNumber = csvRecord.getRecordNumber();
				String dateStr = csvRecord.get(CsvHeader.DATE.getName());
				String returnStrAssetA = csvRecord.get("Asset A");
				String returnStrAssetB = csvRecord.get("Asset B");
				String returnStrAssetC = csvRecord.get("Asset C");
				String returnStrAssetD = csvRecord.get("Asset D");
				Date date = DateUtil.parseDate(dateStr);
				dates.add(date);
				if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
					System.out.println(String.format("Record [%s] Date [%s]", recordNumber, StringUtil.formatDate(date)));
				}
				returnsAssetA.add(Double.parseDouble(returnStrAssetA));
				returnsAssetB.add(Double.parseDouble(returnStrAssetB));
				returnsAssetC.add(Double.parseDouble(returnStrAssetC));
				returnsAssetD.add(Double.parseDouble(returnStrAssetD));
			}
			
			//Prepare multidimensional array of doubles
			double[][] data = new double[4][returnsAssetA.size()];

			//Inject values from the Lists into the multidimensional array
			for (int i = 0; i < data.length; i++) {
				List<Double> assetReturns = null;
				if(i == 0) {
					assetReturns = returnsAssetA;
				} else if(i == 1) {
					assetReturns = returnsAssetB;
				} else if(i == 2) {
					assetReturns = returnsAssetC;
				} else if(i == 3) {
					assetReturns = returnsAssetD;
				} else {
					throw new RuntimeException("Unexpected asset count");
				}
				int j = 0;
				for(Double assetReturn: assetReturns) {
					data[i][j] = assetReturn;
					j++;
				}
			}
			return data;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			PluginUtil.close(csvParser);
		}
	}
	
	public static void main(String[] args) {
		new PGMVExample();
	}
	
	public final DebugLevel getDebugLevel() {
		return debugLevel;
	}
	
}
