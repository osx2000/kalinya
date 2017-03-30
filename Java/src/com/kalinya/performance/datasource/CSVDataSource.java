package com.kalinya.performance.datasource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import com.kalinya.enums.DebugLevel;
import com.kalinya.performance.BenchmarkAssociation;
import com.kalinya.performance.BenchmarkAssociations;
import com.kalinya.performance.Cashflow;
import com.kalinya.performance.Cashflows;
import com.kalinya.performance.Instrument;
import com.kalinya.performance.InstrumentLeg;
import com.kalinya.performance.InstrumentLegs;
import com.kalinya.performance.Instruments;
import com.kalinya.performance.Portfolio;
import com.kalinya.performance.Portfolios;
import com.kalinya.performance.Position;
import com.kalinya.performance.Positions;
import com.kalinya.performance.SecurityMaster;
import com.kalinya.performance.SecurityMasters;
import com.kalinya.performance.enums.AssetClass;
import com.kalinya.performance.enums.CsvHeader;
import com.kalinya.performance.enums.DataSourceType;
import com.kalinya.performance.enums.IndustryGroup;
import com.kalinya.performance.enums.InstrumentClass;
import com.kalinya.performance.enums.RiskGroup;
import com.kalinya.performance.enums.Sector;
import com.kalinya.util.Assertions;
import com.kalinya.util.DateUtil;
import com.kalinya.util.Debuggable;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.PluginUtil;
import com.kalinya.util.StringUtil;
import com.kalinya.util.Timer;
import com.kalinya.util.ToStringBuilder;

final public class CSVDataSource<T extends CSVDataSource> extends DataSource<T> {
	private String positionsFilePath;
	private String securityMasterFilePath;
	private String portfoliosFilePath;
	private String benchmarkAssociationsFilePath;
	
	/*public CSVDataSource() {
		super();
	}*/
	
	public <T extends CSVDataSource> CSVDataSource() {
		super();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("DataSourceType", getDataSourceType())
				.append("RequiresFindurSession", requiresFindurSession())
				.append("StartDate", getStartDate())
				.append("EndDate", getEndDate())
				.append("PositionsFilePath", positionsFilePath)
				/*.append("SecurityMasterFilePath", securityMasterFilePath)
				.append("PortfoliosFilePath", portfoliosFilePath)
				.append("BenchmarkAssociationsFilePath", benchmarkAssociationsFilePath)
				.append("ResultsExtractFilePath", resultsExtractFilePath)*/
				.withLineBreaks()
				.build();
	}
	
	@Override
	public Portfolios getPortfolios() {
		if(portfolios != null) {
			return portfolios;
		}
		getTimer().start("GetPortfolios");
		portfolios = new Portfolios();
		CSVParser csvParser = null;
		try {
			String filePath = getPortfoliosFilePath();
			Assertions.notNull(filePath, "PortfoliosFilePath");
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
			getTimer().stop();
		}
		return portfolios;
	}
	
	@Override
	public BenchmarkAssociations getBenchmarkAssociations() {
		if(benchmarkAssociations != null) {
			return benchmarkAssociations;
		}
		getTimer().start("BenchmarkAssociations");
		benchmarkAssociations = new BenchmarkAssociations();
		CSVParser csvParser = null;
		try {
			String filePath = getBenchmarkAssociationsFilePath();
			Assertions.notNull(filePath, "BenchmarkAssociationsFilePath");
			InputStream inputStream = new FileInputStream(filePath);
			Reader reader = new InputStreamReader(new BOMInputStream(inputStream));
			//TODO: make this csvParser a field
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
				Portfolio portfolio = getPortfolios().get(portfolioName);
				Portfolio benchmark = getPortfolios().get(benchmarkName);
				BenchmarkAssociation benchmarkAssociation = new BenchmarkAssociation(portfolio, benchmark);
				if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
					System.out.println("Record [" + recordNumber + "] BenchmarkAssociation [" + benchmarkAssociation.toString() + "]");
				}
				benchmarkAssociations.add(benchmarkAssociation);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			PluginUtil.close(csvParser);
			getTimer().stop();
		}
		return benchmarkAssociations;
	}

	@Override
	public SecurityMasters getSecurityMasterData() {
		if(securityMasterData != null) {
			return securityMasterData;
		}
		getTimer().start("GetSecurityMasterData");
		securityMasterData = new SecurityMasters();
		CSVParser csvParser = null;
		try {
			String filePath = getSecurityMasterFilePath();
			Assertions.notNull(filePath, "SecurityMasterFilePath");
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

				String instrumentId = csvRecord.get(CsvHeader.INSTRUMENT_ID.getName());
				String maturityDateStr = csvRecord.get(CsvHeader.MATURITY_DATE.getName());
				String assetClassStr = csvRecord.get(CsvHeader.ASSET_CLASS.getName());
				String riskGroupStr = csvRecord.get(CsvHeader.RISK_GROUP.getName());
				String industryGroupStr = csvRecord.get(CsvHeader.INDUSTRY_GROUP.getName());
				String sectorStr = csvRecord.get(CsvHeader.SECTOR.getName());
				String instrumentClassStr = csvRecord.get(CsvHeader.INSTRUMENT_CLASS.getName());

				Date maturityDate = DateUtil.parseDate(maturityDateStr);
				AssetClass assetClass = AssetClass.fromName(assetClassStr);
				RiskGroup riskGroup = RiskGroup.fromName(riskGroupStr);
				IndustryGroup industryGroup = IndustryGroup.fromName(industryGroupStr);
				Sector sector = Sector.fromName(sectorStr);
				InstrumentClass instrumentClass = InstrumentClass.fromName(instrumentClassStr);

				SecurityMaster securityMaster = new SecurityMaster(instrumentId, maturityDate, industryGroup, sector,
						riskGroup, instrumentClass, assetClass);

				if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
					System.out.println("Record [" + recordNumber + "] SecurityMaster [" + securityMaster.toString() + "]");
				}
				securityMasterData.add(securityMaster);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			PluginUtil.close(csvParser);
			getTimer().stop();
		}
		return securityMasterData;
	}

	@Override
	public Positions getPositions() {
		if(positions != null) {
			return positions;
		}
		getTimer().start("GetPositions");
		positions = new Positions();
		getPortfolios();
		if(getPortfolios().size() > 0) {
			boolean getCashflows = false;
			if(cashflows == null) {
				getCashflows = true;
				cashflows = new Cashflows();
			}
			CSVParser csvParser = null;
			Long recordNumber = null;
			try {
				String filePath = getPositionsFilePath();
				Assertions.notNull(filePath, "PositionsFilePath");
				InputStream inputStream = new FileInputStream(filePath);
				Reader reader = new InputStreamReader(new BOMInputStream(inputStream));
				csvParser = new CSVParser(reader, CSVFormat.EXCEL.withHeader().withIgnoreHeaderCase().withIgnoreSurroundingSpaces().withTrim().withIgnoreEmptyLines());

				if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
					Map<String, Integer> headerMap = csvParser.getHeaderMap();
					System.out.println("Header Map [" + (headerMap != null ? headerMap.toString() : "null") + "]");
				}

				List<CSVRecord> csvRecords = csvParser.getRecords();
				for(CSVRecord csvRecord: csvRecords) {
					recordNumber = csvRecord.getRecordNumber();
					String portfolioName = csvRecord.get(CsvHeader.PORTFOLIO.getName());
					String instrumentId = csvRecord.get(CsvHeader.INSTRUMENT_ID.getName());
					String dateStr = csvRecord.get(CsvHeader.DATE.getName());
					String legIdStr = csvRecord.get(CsvHeader.LEG_ID.getName());
					String currency = csvRecord.get(CsvHeader.CURRENCY.getName());
					String marketValueStr = csvRecord.get(CsvHeader.END_LOCAL_MARKET_VALUE.getName());
					String baseMarketValueStr = csvRecord.get(CsvHeader.END_BASE_MARKET_VALUE.getName());
					String cashFlowStr = csvRecord.get(CsvHeader.CASH_FLOW.getName());
					Portfolio portfolio = portfolios.get(portfolioName);
					if(portfolio == null || !getPortfolios().contains(portfolio)) {
						throw new IllegalStateException(
								String.format("Unknown portfolio [%s] in positions file [%s]", 
								portfolioName, 
								getPositionsFilePath()));
					}
					if(!getPortfoliosFilter().contains(portfolio)) {
						System.out.println(
								String.format("Filtering position RecordId [%s] in Portfolio [%s]", 
								recordNumber,
								portfolio.getName()));
						continue;
					}
					Instrument instrument = getInstruments().getInstrument(instrumentId, false);
					if(instrument == null || !getInstruments().contains(instrument)) {
						throw new IllegalStateException(String.format("Unknown InstrumentId [%s] in positions file [%s]", 
																		instrumentId, 
																		getPositionsFilePath()));
					}
					InstrumentLeg instrumentLeg = new InstrumentLeg(portfolio, instrument, Integer.valueOf(legIdStr), currency);
					Date date = DateUtil.parseDate(dateStr);
					Cashflows instrumentLegCashflows = new Cashflows();
					Cashflow instrumentLegCashflow = new Cashflow(instrumentLeg, date, currency, NumberUtil.newBigDecimal(cashFlowStr));
					instrumentLegCashflows.add(instrumentLegCashflow );
					if(getCashflows) {
						cashflows.add(instrumentLegCashflow);
					}
					Position position = new Position(instrumentLeg, date, NumberUtil.newBigDecimal(marketValueStr),
							NumberUtil.newBigDecimal(baseMarketValueStr), instrumentLegCashflows);
					if (getDebugLevel().atLeast(DebugLevel.HIGH)) {
						System.out.println("Record [" + recordNumber + "] Position [" + position.toString() + "]");
					}
					positions.add(position);
				}
			} catch (Exception e) {
				System.out.println(String.format("Failed to parse record number [%s]", recordNumber));
				throw new RuntimeException(e);
			} finally {
				PluginUtil.close(csvParser);
			}
			if(positions.size() == 0) {
				StringBuilder message = new StringBuilder();
				message.append(String.format("No positions were extracted from [%s]", getPositionsFilePath()));
				if(getStartDate() != null && getStartDate().compareTo(DateUtil.MINIMUM_DATE) > 0) {
					message.append(String.format(" StartDate [%s]", getStartDate()));
				}
				if(getEndDate() != null && getEndDate().compareTo(DateUtil.MAXIMUM_DATE) < 0) {
					message.append(String.format(" EndDate [%s]", getEndDate()));
				}
				if(getPortfoliosFilter() != null && getPortfoliosFilter().size() > 0) {
					message.append(String.format(" PortfoliosFilter [%s]", getPortfoliosFilter().toMinimalString()));
				}
				throw new IllegalStateException(message.toString());
			}
			getTimer().stop();
			return positions;
		} else {
			throw new IllegalStateException("Failed to retrieve portfolio details");
		}
	}

	@Override
	public Cashflows getCashflows() {
		if(cashflows != null) {
			return cashflows;
		}
		getTimer().start("GetCashflows");
		cashflows = new Cashflows();
		getPositions();
		if(getPositions().size() > 0) {
			//Cashflows are loaded during the position bootstrap
			getTimer().stop();
			return cashflows;
		} else {
			throw new IllegalStateException("Failed to retrieve position details");
		}
	}
	
	public T withPositionsFilePath(String positionsFilePath) {
		this.positionsFilePath = positionsFilePath;
		return (T) this;
	}
	
	public T withSecurityMasterFilePath(String securityMasterFilePath) {
		this.securityMasterFilePath = securityMasterFilePath;
		return (T) this;
	}

	public T withPortfoliosFilePath(String portfoliosFilePath) {
		this.portfoliosFilePath = portfoliosFilePath;
		return (T) this;
	}
	
	public T withBenchmarkAssociationsFilePath(String benchmarkAssociationsFilePath) {
		this.benchmarkAssociationsFilePath = benchmarkAssociationsFilePath;
		return (T) this;
	}
	
	public String getPortfoliosFilePath() {
		return portfoliosFilePath;
	}
	
	private String getPositionsFilePath() {
		return positionsFilePath;
	}
	
	public String getSecurityMasterFilePath() {
		return securityMasterFilePath;
	}
	
	public String getBenchmarkAssociationsFilePath() {
		return benchmarkAssociationsFilePath;
	}
}
