package com.kalinya.performance.datasource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
import com.kalinya.util.NumberUtil;
import com.kalinya.util.PluginUtil;
import com.kalinya.util.StringUtil;
import com.kalinya.util.ToStringBuilder;

final public class CSVDataSource extends DataSource {
	private final static DataSourceType DATA_SOURCE_TYPE = DataSourceType.CSV;
	private final String positionsFilePath;
	private final String securityMasterFilePath;
	private final String portfoliosFilePath;
	private final String benchmarkAssociationsFilePath;

	public CSVDataSource(Builder builder) {
		super(builder);
		positionsFilePath = builder.positionsFilePath;
		securityMasterFilePath = builder.securityMasterFilePath;
		portfoliosFilePath = builder.portfoliosFilePath;
		benchmarkAssociationsFilePath = builder.benchmarkAssociationsFilePath;
		loadData();
	}
	
	@Override
	public void loadData() {
		retrievePortfolios();
		retrieveBenchmarkAssociations();
		retrieveSecurityMasterData();
		retrieveInstruments();
		retrievePositions();
		retrieveCashflows();
		retrieveInstrumentLegs();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("DataSourceType", getDataSourceType())
				.append("RequiresFindurSession", requiresFindurSession())
				.append("StartDate", getStartDate())
				.append("EndDate", getEndDate())
				.append("PositionsFilePath", getPositionsFilePath())
				.append("SecurityMasterFilePath", getSecurityMasterFilePath())
				.append("PortfoliosFilePath", getPortfoliosFilePath())
				.append("BenchmarkAssociationsFilePath", getBenchmarkAssociationsFilePath())
				.withLineBreaks()
				.build();
	}

	public static class Builder extends DataSource.Builder<Builder> {
		private String positionsFilePath;
		private String securityMasterFilePath;
		private String portfoliosFilePath;
		private String benchmarkAssociationsFilePath;

		@Override
		public CSVDataSource build() {
			return new CSVDataSource(this);
		}

		public Builder withPositionsFilePath(String positionsFilePath) {
			this.positionsFilePath = positionsFilePath;
			return this;
		}

		public Builder withSecurityMasterFilePath(String securityMasterFilePath) {
			this.securityMasterFilePath = securityMasterFilePath;
			return this;
		}

		public Builder withPortfoliosFilePath(String portfoliosFilePath) {
			this.portfoliosFilePath = portfoliosFilePath;
			return this;
		}

		public Builder withBenchmarkAssociationsFilePath(String benchmarkAssociationsFilePath) {
			this.benchmarkAssociationsFilePath = benchmarkAssociationsFilePath;
			return this;
		}
	}

	@Override
	public DataSourceType getDataSourceType() {
		return DataSourceType.CSV;
	}

	@Override
	public void retrievePortfolios() {
		if(portfolios == null) {
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
		}
	}

	@Override
	public void retrieveBenchmarkAssociations() {
		if(benchmarkAssociations == null) {
			getTimer().start("BenchmarkAssociations");
			benchmarkAssociations = new BenchmarkAssociations();
			CSVParser csvParser = null;
			try {
				String filePath = getBenchmarkAssociationsFilePath();
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
					Portfolio portfolio = portfolios.get(portfolioName);
					Portfolio benchmark = portfolios.get(benchmarkName);
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
		}
	}

	@Override
	public void retrieveSecurityMasterData() {
		if(securityMasterData == null) {
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
		}
	}

	@Override
	public void retrievePositions() {
		if (positions == null) {
			positions = new Positions();
			getTimer().start("GetPositions");
			Assertions.notNullOrEmpty("portfolios", portfolios);
			Assertions.notNullOrEmpty("instruments", instruments);
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
					Date date = DateUtil.parseDate(dateStr);
					String legIdStr = csvRecord.get(CsvHeader.LEG_ID.getName());
					String currency = csvRecord.get(CsvHeader.CURRENCY.getName());
					String marketValueStr = csvRecord.get(CsvHeader.END_LOCAL_MARKET_VALUE.getName());
					String baseMarketValueStr = csvRecord.get(CsvHeader.END_BASE_MARKET_VALUE.getName());
					String cashFlowStr = csvRecord.get(CsvHeader.CASH_FLOW.getName());
					Portfolio portfolio = portfolios.get(portfolioName);
					if(portfolio == null || !portfolios.contains(portfolio)) {
						throw new IllegalStateException(
								String.format("Unknown portfolio [%s] in positions file [%s]", 
										portfolioName, 
										getPositionsFilePath()));
					}
					//TODO: this next bit about filtering needs to be shared
					//Do not extract the position if it's in a filtered portfolio
					if(getPortfoliosFilter().size() > 0 && !getPortfoliosFilter().contains(portfolio)) {
						System.out.println(
								String.format("Filtering position RecordId [%s] in Portfolio [%s]", 
										recordNumber,
										portfolio.getName()));
						continue;
					}
					//Do not extract the position if it's outside the start/end date period
					if(getStartDate() != null && date.before(getStartDate())) {
						System.out.println(
								String.format("Filtering position RecordId [%s] on Date [%s] before StartDate [%s]", 
										recordNumber,
										StringUtil.formatDate(date),
										StringUtil.formatDate(getStartDate())));
						continue;
					}
					if(getEndDate() != null && date.after(getEndDate())) {
						System.out.println(
								String.format("Filtering position RecordId [%s] on Date [%s] after EndDate [%s]", 
										recordNumber,
										StringUtil.formatDate(date),
										StringUtil.formatDate(getEndDate())));
						continue;
					}
					
					Instrument instrument = instruments.getInstrument(instrumentId, false);
					if(instrument == null || !instruments.contains(instrument)) {
						throw new IllegalStateException(String.format("Unknown InstrumentId [%s] in positions file [%s]", 
								instrumentId, 
								getPositionsFilePath()));
					}
					InstrumentLeg instrumentLeg = new InstrumentLeg(portfolio, instrument, Integer.valueOf(legIdStr), currency);
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
		}
	}

	@Override
	public void retrieveCashflows() {
		//Cashflows are loaded during the position bootstrap
		retrievePositions();
		Assertions.notNull("Cashflows", "Failed to retrieve position details during position bootstrap", cashflows);
	}
	
	@Override
	public void retrieveInstrumentLegs() {
		if(instrumentLegs == null) {
			Assertions.notNullOrEmpty("Positions", "No positions data available to load InstrumentLegs", positions);
			instrumentLegs = new InstrumentLegs();
			getTimer().start("GetInstrumentLegs");
			for(Position position: positions) {
				instrumentLegs.add(position.getInstrumentLeg());
			}
			getTimer().stop();
		}
	}

	public String getPositionsFilePath() {
		return positionsFilePath;
	}

	public String getPortfoliosFilePath() {
		return portfoliosFilePath;
	}

	public String getSecurityMasterFilePath() {
		return securityMasterFilePath;
	}

	public String getBenchmarkAssociationsFilePath() {
		return benchmarkAssociationsFilePath;
	}
}
