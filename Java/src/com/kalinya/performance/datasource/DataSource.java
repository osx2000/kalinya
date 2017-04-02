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
	protected Instruments instruments;
	protected InstrumentLegs instrumentLegs;
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

		/**
		 * CSV order of data load -
		 * <ul>
		 * <li>getPortfolios();</li>
		 * <li>getBenchmarkAssociations();</li>
		 * <li>getSecurityMasterData();</li>
		 * <li>getInstruments();</li>
		 * <li>getPositions();</li>
		 * <li>getInstrumentLegs();</li>
		 * <li>getCashflows();</li>
		 * </ul>
		 * 
		 * @return
		 */
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

	public final Instruments retrieveInstruments() {
		if(instruments == null) {
			Assertions.notNullOrEmpty("SecurityMasters", "No security master data available", securityMasterData);
			instruments = new Instruments();
			getTimer().start("GetInstruments");
			Assertions.notNullOrEmpty("SecurityMasterData", getSecurityMasterData());
			for(String instrumentId: securityMasterData.getInstrumentIds()) {
				instruments.add(new Instrument(instrumentId));
			}
			getTimer().stop();
		}
		return instruments;
	}

	public void retrieveInstrumentLegs() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}
	
	public DataSourceType getDataSourceType() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}
	
	public void retrievePortfolios() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}

	public void retrievePositions() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}

	public void retrieveCashflows() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}
	
	public void retrieveBenchmarkAssociations() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}

	public void retrieveSecurityMasterData() {
		throw new NotImplementedException(notImplementedExceptionMessage);
	}
	
	public void loadData() {
		retrievePortfolios();
		retrieveBenchmarkAssociations();
		retrieveSecurityMasterData();
		retrieveInstruments();
		retrievePositions();
		retrieveCashflows();
		retrieveInstrumentLegs();
	}

	public void injectCashflowsToPositions() {
		positions.injectCashflows(cashflows);
	}

	public Portfolios getPortfolios() {
		if(portfolios == null) {
			retrievePortfolios();
		}
		return portfolios;
	}

	public BenchmarkAssociations getBenchmarkAssociations() {
		if(benchmarkAssociations == null) {
			retrieveBenchmarkAssociations();
		}
		return benchmarkAssociations;
	}

	public SecurityMasters getSecurityMasterData() {
		if(securityMasterData == null) {
			retrieveSecurityMasterData();
		}
		return securityMasterData;
	}

	public Instruments getInstruments() {
		if(instruments == null) {
			retrieveInstruments();
		}
		return instruments;
	}

	public InstrumentLegs getInstrumentLegs() {
		if(instrumentLegs == null) {
				retrieveInstrumentLegs();
		}
		return instrumentLegs;
	}

	public Positions getPositions() {
		if(positions == null) {
			retrievePositions();
		}
		return positions;
	}

	public Cashflows getCashflows() {
		if(cashflows == null) {
			retrieveCashflows();
		}
		return cashflows;
	}
}
