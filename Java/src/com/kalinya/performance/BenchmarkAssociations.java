package com.kalinya.performance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import com.kalinya.enums.DebugLevel;
import com.kalinya.performance.enums.CsvHeader;
import com.kalinya.util.Assertions;
import com.kalinya.util.BaseSet;
import com.kalinya.util.PluginUtil;
import com.kalinya.util.Timer;

public class BenchmarkAssociations extends BaseSet<BenchmarkAssociation>{
	private static final long serialVersionUID = 8597352530732073478L;
	private Map<Portfolio, Portfolio> benchmarkAssociationsMap;

	public BenchmarkAssociations() {
		benchmarkAssociationsMap = new TreeMap<>();
	}
	
	@Override
	public String toMinimalString() {
		StringBuilder sb = new StringBuilder();
		String concatenator = "";
		for(BenchmarkAssociation benchmarkAssociation: getSet()) {
			sb.append(concatenator 
					+ benchmarkAssociation.getPortfolio().getName() 
					+ " = " 
					+ benchmarkAssociation.getBenchmark().getName());
			concatenator = ", ";
		}
		return sb.toString();
	}
	
	@Override
	public boolean add(BenchmarkAssociation benchmarkAssociation) {
		boolean b = super.add(benchmarkAssociation);
		setBenchmarkAssociationsMap();
		return b;
	}

	public BenchmarkAssociation get(String portfolioName) {
		for(BenchmarkAssociation benchmarkAssociation: getSet()) {
			if(benchmarkAssociation.getPortfolio().getName().equalsIgnoreCase(portfolioName)) {
				return benchmarkAssociation;
			}
		}
		throw new IllegalArgumentException("[" + portfolioName + "] is not in the set of benchmark associations");
	}
	
	public BenchmarkAssociation get(Portfolio portfolio) {
		Assertions.notNull("Portfolio", portfolio);
		for(BenchmarkAssociation benchmarkAssociation: getSet()) {
			if(benchmarkAssociation.getPortfolio().compareTo(portfolio) == 0) {
				return benchmarkAssociation;
			}
		}
		throw new IllegalArgumentException("[" + portfolio.getName() + "] is not in the set of benchmark associations");
	}

	public Portfolios getPortfolios() {
		return new Portfolios(benchmarkAssociationsMap.keySet());
	}
	
	public Portfolios getBenchmarks() {
		return new Portfolios(benchmarkAssociationsMap.values());
	}
	
	private void setBenchmarkAssociationsMap() {
		benchmarkAssociationsMap = new TreeMap<>();
		for(BenchmarkAssociation benchmarkAssociation: getSet()) {
			benchmarkAssociationsMap.put(benchmarkAssociation.getPortfolio(), benchmarkAssociation.getBenchmark());
		}
	}
	
	public Portfolio getBenchmark(Portfolio portfolio) {
		return benchmarkAssociationsMap.get(portfolio);
	}

	public static BenchmarkAssociations load() {
		return load(Configurator.BENCHMARK_ASSOCIATIONS_DATABASE_FILE_PATH);
	}
	public static BenchmarkAssociations load(String filePath) {
		Timer timer = new Timer();
		timer.start("BenchmarkAssociations");
		BenchmarkAssociations benchmarkAssociations = new BenchmarkAssociations();
		CSVParser csvParser = null;
		try {
			Assertions.notNull(filePath, "BenchmarkAssociationsFilePath");
			InputStream inputStream = new FileInputStream(filePath);
			Reader reader = new InputStreamReader(new BOMInputStream(inputStream));
			csvParser = new CSVParser(reader, CSVFormat.EXCEL.withHeader().withIgnoreHeaderCase().withIgnoreSurroundingSpaces().withTrim());

			if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
				Map<String, Integer> headerMap = csvParser.getHeaderMap();
				System.out.println("Header Map [" + (headerMap != null ? headerMap.toString() : "null") + "]");
			}

			List<CSVRecord> csvRecords = csvParser.getRecords();
			for(CSVRecord csvRecord: csvRecords) {
				long recordNumber = csvRecord.getRecordNumber();
				String portfolioName = csvRecord.get(CsvHeader.PORTFOLIO.getName());
				String benchmarkName = csvRecord.get(CsvHeader.BENCHMARK.getName());
				Portfolio portfolio = Portfolio.create(portfolioName);
				Portfolio benchmark = Portfolio.create(benchmarkName);
				BenchmarkAssociation benchmarkAssociation = new BenchmarkAssociation(portfolio, benchmark);
				if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
					System.out.println("Record [" + recordNumber + "] BenchmarkAssociation [" + benchmarkAssociation.toString() + "]");
				}
				benchmarkAssociations.add(benchmarkAssociation);
			}
			return benchmarkAssociations;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			PluginUtil.close(csvParser);
			timer.stop();
		}
	}

	private static DebugLevel getDebugLevel() {
		return Configurator.debugLevel;
	}
}
