package com.kalinya.performance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

public class Portfolios extends BaseSet<Portfolio>{
	private static final long serialVersionUID = -5525677261700574992L;

	public Portfolios() {
		super();
	}
	
	public Portfolios(Collection<Portfolio> portfolioSet) {
		this();
		for(Portfolio portfolio: portfolioSet) {
			add(portfolio);
		}
	}

	public Portfolios intersection(Portfolios that) {
		Portfolios intersection = new Portfolios();
		for(Portfolio portfolio: getSet()) {
			if(that.contains(portfolio)) {
				intersection.add(portfolio);
			}
		}
		return intersection;
	}
	@Override
	public String toMinimalString() {
		StringBuilder sb = new StringBuilder();
		String concatenator = "";
		for(Portfolio portfolio: getSet()) {
			sb.append(concatenator + portfolio.getName());
			concatenator = ", ";
		}
		return sb.toString();
	}

	public Portfolio get(String portfolioName) {
		for(Portfolio portfolio: getSet()) {
			if(portfolio.getName().equalsIgnoreCase(portfolioName)) {
				return portfolio;
			}
		}
		throw new IllegalArgumentException("[" + portfolioName + "] is not in the set of loaded portfolios");
	}

	public static Portfolios load() {
		return load(Configurator.PORTFOLIOS_DATABASE_FILE_PATH);
	}
	
	/**
	 * Loads the collection of Portfolio records
	 * 
	 * @param filePath
	 *            The path to the CSV file
	 * @return
	 */
	public static Portfolios load(String filePath) {
		Timer timer = new Timer();
		timer.start("LoadPortfolioData");
		Portfolios portfolios = Portfolios.create();
		CSVParser csvParser = null;
		try {
			Assertions.notNull(filePath, "PortfoliosDatabaseFilePath");
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
				String portfolioGroup = csvRecord.get(CsvHeader.PORTFOLIO_GROUP.getName());
				Portfolio portfolio = new Portfolio(portfolioName, portfolioGroup);
				if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
					System.out.println("Record [" + recordNumber + "] Portfolio [" + portfolio.toString() + "]");
				}
				portfolios.add(portfolio);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			PluginUtil.close(csvParser);
			timer.stop();
		}
		return portfolios;
	}

	private static DebugLevel getDebugLevel() {
		return Configurator.debugLevel;
	}

	private static Portfolios create() {
		return new Portfolios();
	}
}
