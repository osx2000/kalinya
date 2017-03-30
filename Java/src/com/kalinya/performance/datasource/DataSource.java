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

public abstract class DataSource<T extends DataSource> implements Serializable, Debuggable {
	private static DataSource instance;
	private Timer timer;
	protected Portfolios portfolios;
	protected BenchmarkAssociations benchmarkAssociations;
	protected SecurityMasters securityMasterData;
	protected Instruments instruments;
	protected InstrumentLegs instrumentLegs;
	protected Positions positions;
	protected Cashflows cashflows;
	protected Portfolios portfoliosFilter;
	private String resultsExtractFilePath;
	
	private Date startDate;
	private Date endDate;
	private DebugLevel debugLevel;
	
	public DataSource() {
		setTimer(new Timer());
		withDebugLevel(DebugLevel.LOW);
		withStartDate(DateUtil.MINIMUM_DATE);
		withEndDate(DateUtil.MAXIMUM_DATE);
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("DataSourceType", getDataSourceType())
				.append("RequiresFindurSession", requiresFindurSession())
				.append("StartDate", getStartDate())
				.append("EndDate", getEndDate())
				.append("ResultsExtractFilePath", resultsExtractFilePath)
				.withLineBreaks()
				.build();
	}
	
	public static DataSource getInstance() {
		return instance;
	}
	
	public Timer getTimer() {
		return timer;
	}

	private void setTimer(Timer timer) {
		this.timer = timer;
	}
	
	public DataSourceType getDataSourceType() {
		return DataSourceType.CSV;
	}

	public abstract Portfolios getPortfolios();
	public abstract BenchmarkAssociations getBenchmarkAssociations();
	public abstract SecurityMasters getSecurityMasterData();
	
	public Instruments getInstruments() {
		if(instruments != null) {
			return instruments;
		}
		getTimer().start("GetInstruments");
		instruments = new Instruments();
		getSecurityMasterData();
		if(getSecurityMasterData().size() > 0) {
			for(String instrumentId: getSecurityMasterData().getInstrumentIds()) {
				instruments.add(new Instrument(instrumentId));
			}
		} else {
			throw new IllegalStateException("Failed to retrieve security master data");
		}
		getTimer().stop();
		return instruments;
	}

	public InstrumentLegs getInstrumentLegs() {
		if(instrumentLegs != null) {
			return instrumentLegs;
		}
		getTimer().start("GetInstrumentLegs");
		instrumentLegs = new InstrumentLegs();
		getPositions();
		if(getPositions().size() > 0) {
			for(Position position: getPositions()) {
				instrumentLegs.add(position.getInstrumentLeg());
			}
		} else {
			throw new IllegalStateException("Failed to retrieve position details");
		}
		getTimer().stop();
		return instrumentLegs;
	}

	public abstract Positions getPositions();
	public abstract Cashflows getCashflows();

	public boolean requiresFindurSession() {
		return getDataSourceType().requiresFindurSession();
	}

	public String getResultsExtractFilePath() {
		return resultsExtractFilePath;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	
	protected Portfolios getPortfoliosFilter() {
		return portfoliosFilter;
	}

	public T build() {
		getPortfolios();
		getBenchmarkAssociations();
		getSecurityMasterData();
		getInstruments();
		getPositions();
		getInstrumentLegs();
		getCashflows();
		getTimer().print(true);
		return (T) this;
	}
	
	public DebugLevel getDebugLevel() {
		return debugLevel;
	}
	
	public T withResultsExtractFilePath(String resultsExtractFilePath) {
		this.resultsExtractFilePath = resultsExtractFilePath;
		return (T) this;
	}
	
	public T withPortfoliosFilter(Portfolios portfoliosFilter) {
		this.portfoliosFilter = portfoliosFilter;
		return (T) this;
	}

	public T withStartDate(Date startDate) {
		this.startDate = startDate;
		return (T) this;
	}

	public T withEndDate(Date endDate) {
		this.endDate = endDate;
		return (T) this;
	}

	public T withDebugLevel(DebugLevel debugLevel) {
		this.debugLevel = debugLevel;
		return (T) this;
	}

	public String getPortfoliosAsString() {
		if(getPortfolios().size() > 0) {
			String[] portfoliosArray = getPortfolios().toArray(new String[getPortfolios().size()]);
			return StringUtil.join(portfoliosArray, ",", "'");
		}
		return "";
	}

	public void extractToUserTable(String tableName) {
		throw new UnsupportedOperationException(String.format("User table extract is not supported for [%s]", getClass().getSimpleName()));
	}
	
	public final static CSVDataSource<CSVDataSource> CSV = new CSVDataSource<CSVDataSource>();
	
	public enum Predefined {
		CSV(DataSource.CSV),
		//TODO: link FINDUR_PMM to FindurPMM
		FINDUR_PMM(DataSource.CSV);

		private DataSource dataSource;

		Predefined(DataSource dataSource) {
			this.dataSource = dataSource;
		}
		
		public DataSource getDataSource() {
			return dataSource;
		}
	}

}
