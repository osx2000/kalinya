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
import org.apache.commons.lang3.NotImplementedException;

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

public class DataSource implements Serializable, Debuggable {
	private final String notImplementedExceptionMessage = String.format("%s must implement this method", this.getClass().getSimpleName());
	private Timer timer;
	protected Portfolios portfolios;
	protected BenchmarkAssociations benchmarkAssociations;
	protected SecurityMasters securityMasterData;
	private Instruments instruments;
	private InstrumentLegs instrumentLegs;
	protected Positions positions;
	protected Cashflows cashflows;

	private Portfolios portfoliosFilter;
	private Date startDate;
	private Date endDate;
	private DebugLevel debugLevel;
	private String resultsExtractFilePath;

	public DataSource(Builder builder) {
		timer = new Timer();
		this.portfoliosFilter = builder.portfoliosFilter;
		this.startDate = builder.startDate;
		this.endDate = builder.endDate;
		this.debugLevel = builder.debugLevel;
		this.resultsExtractFilePath = builder.resultsExtractFilePath;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("DataSourceType", getDataSourceType())
				.append("RequiresFindurSession", requiresFindurSession())
				.append("StartDate", getStartDate())
				.append("EndDate", getEndDate())
				.append("Portfolios", getPortfolios())
				.append("ResultsExtractFilePath", getResultsExtractFilePath())
				.withLineBreaks()
				.build();
	}

	public static class Builder<T extends Builder<T>> {
		private Portfolios portfoliosFilter;
		private Date startDate;
		private Date endDate;
		private DebugLevel debugLevel = DebugLevel.LOW;
		private String resultsExtractFilePath;

		public Builder() {
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

		public T withResultsExtractFilePath(String resultsExtractFilePath) {
			this.resultsExtractFilePath = resultsExtractFilePath;
			return (T) this;
		}

		public DataSource build() {
			return new DataSource(this);
		}
	}

	public final Timer getTimer() {
		return timer;
	}

	public final boolean requiresFindurSession() {
		Assertions.notNull("DataSourceType", getDataSourceType());
		return getDataSourceType().requiresFindurSession();
	}

	public final Date getStartDate() {
		return startDate;
	}

	public final Date getEndDate() {
		return endDate;
	}

	public final Portfolios getPortfoliosFilter() {
		return portfoliosFilter;
	}

	public final DebugLevel getDebugLevel() {
		return debugLevel;
	}
	
	public final String getResultsExtractFilePath() {
		return resultsExtractFilePath;
	}

	public final String getPortfoliosAsString() {
		if(getPortfolios().size() > 0) {
			String[] portfoliosArray = getPortfolios().toArray(new String[getPortfolios().size()]);
			return StringUtil.join(portfoliosArray, ",", "'");
		}
		return "";
	}

	public DataSourceType getDataSourceType() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}
	
	public Portfolios getPortfolios() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}

	public Positions getPositions() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}
	public Cashflows getCashflows() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}

	public BenchmarkAssociations getBenchmarkAssociations() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}

	public SecurityMasters getSecurityMasterData() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}

	public final Instruments getInstruments() {
		if(instruments == null) {
			instruments = new Instruments();
			getSecurityMasterData();
			getTimer().start("GetInstruments");
			Assertions.notNullOrEmpty("SecurityMasterData", getSecurityMasterData());
			for(String instrumentId: getSecurityMasterData().getInstrumentIds()) {
				instruments.add(new Instrument(instrumentId));
			}
			getTimer().stop();
		}
		return instruments;
	}

	public final InstrumentLegs getInstrumentLegs() {
		if(instrumentLegs == null) {
			instrumentLegs = new InstrumentLegs();
			getPositions();
			getTimer().start("GetInstrumentLegs");
			Assertions.notNullOrEmpty("Positions", getPositions());
			for(Position position: getPositions()) {
				instrumentLegs.add(position.getInstrumentLeg());
			}
			getTimer().stop();
		}
		return instrumentLegs;
	}
}
