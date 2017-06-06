package com.kalinya.performance;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.csv.CSVPrinter;
import org.joda.time.Days;

import com.kalinya.enums.CurrencyBasis;
import com.kalinya.enums.DayWeighting;
import com.kalinya.enums.DebugLevel;
import com.kalinya.javafx.util.RowData;
import com.kalinya.performance.datasource.DataSource;
import com.kalinya.performance.dimensions.CumulativePerformanceDimension;
import com.kalinya.performance.dimensions.DatePerformanceDimension;
import com.kalinya.performance.dimensions.InstrumentLegPerformanceDimension;
import com.kalinya.performance.dimensions.PerformanceDimensions;
import com.kalinya.performance.dimensions.PortfolioPerformanceDimension;
import com.kalinya.performance.enums.CsvHeader;
import com.kalinya.performance.enums.InstrumentClass;
import com.kalinya.performance.enums.SecurityMasterEnum;
import com.kalinya.util.Assertions;
import com.kalinya.util.BaseSet;
import com.kalinya.util.CollectionUtil;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.PluginUtil;
import com.kalinya.util.StringUtil;
import com.kalinya.util.Timer;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.io.UserTable;
import com.olf.openrisk.table.EnumColType;
import com.olf.openrisk.table.Table;
import com.olf.openrisk.trading.EnumTranStatus;

import javafx.collections.ObservableList;

public class PerformanceResult implements Serializable {
	private static final long serialVersionUID = -3688283376622858629L;
	private static String DEBUG_INSTRUMENT_ID = "AAPL";
	private PerformanceFactory performanceFactory;
	private static int scale = 12;
	private static RoundingMode roundingMode = RoundingMode.HALF_UP;
	private DataSource dataSource;
	private Portfolios portfolios;
	private BenchmarkAssociations benchmarkAssociations;
	private SecurityMasters securityMasters;
	private Instruments instruments;
	private InstrumentLegs instrumentLegs;
	private Positions positions;
	private Cashflows cashflows;
	private PerformanceDimensions performanceDimensions;
	private Timer timer;
	private Map<PerformanceDimensions, PerformanceValue> performanceValues;
	private Map<PerformanceDimensions, PerformanceValue> cumulativePerformanceValues;

	private PerformanceResult() {
		setTimer(new Timer());
	}

	public PerformanceResult(final PerformanceFactory performanceFactory, final DataSource dataSource) {
		this(performanceFactory, dataSource.getPortfolios(), dataSource.getBenchmarkAssociations(),
				dataSource.getSecurityMasterData(), dataSource.getInstruments(), dataSource.getInstrumentLegs(),
				dataSource.getPositions(), dataSource.getCashflows());
		setDataSource(dataSource);
	}

	public PerformanceResult(PerformanceFactory performanceFactory, Portfolios portfolios,
			BenchmarkAssociations benchmarkAssociations, SecurityMasters securityMasterData, Instruments instruments, InstrumentLegs instrumentLegs,
			Positions positions, Cashflows cashflows) {
		this();
		setPerformanceFactory(performanceFactory);
		setPerformanceValues(new TreeMap<PerformanceDimensions, PerformanceValue>());
		setCumulativePerformanceValues(new TreeMap<PerformanceDimensions, PerformanceValue>());
		setPortfolios(portfolios);
		setBenchmarkAssociations(benchmarkAssociations);
		setSecurityMasterData(securityMasterData);
		setInstruments(instruments);
		setPositions(positions);
		setInstrumentLegs(instrumentLegs);
		setCashflows(cashflows);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(getPerformanceDimensions().size() > 0) {
			sb.append("PerformanceDimensions [" + getPerformanceDimensions().toString() + "]");
		}
		if(getPerformanceValues().size() > 0) {
			sb.append("\nPerformanceValues Size [" + getPerformanceValues().size() + "]");
			sb.append("\n " + getPerformanceValuesSummaryAsString());
		}
		if(getCumulativePerformanceValues().size() > 0) {
			sb.append("\nCumulativePerformanceValues Size [" + getCumulativePerformanceValues().size() + "]");
			sb.append("\n " + getCumulativePerformanceValuesSummaryAsString());
		}
		return sb.toString();
	}

	public String getCumulativePerformanceValuesSummaryAsString() {
		return BaseSet.getCollectionElementsAsString(getCumulativePerformanceValues());
	}

	public String getPerformanceValuesSummaryAsString() {
		return BaseSet.getCollectionElementsAsString(getPerformanceValues());
	}

	public void calculateReturns(PerformanceDimensions performanceDimensions) {
		setPerformanceDimensions(performanceDimensions);

		//Track execution time
		getTimer().start("CalculateReturns");
		InstrumentLegs instrumentLegs = getPositions().getInstrumentLegs();
		Date earliestDate = getPositions().getEarliestPositionDate();
		for(InstrumentLeg instrumentLeg: instrumentLegs) {
			if(instrumentLeg.getInstrumentId().equalsIgnoreCase(DEBUG_INSTRUMENT_ID)) {
				System.out.println(String.format("InstrumentId [%s]",instrumentLeg.getInstrumentId()));
			}
			TreeSet<Date> dates = getPositions().getDates(instrumentLeg);
			//Ensure that each InstrumentLeg has a record for the earliest date, to ensure the opening position is zero
			if(!dates.contains(earliestDate)) {
				dates.add(earliestDate);
			}
			BigDecimal startLocalMarketValue = null;
			BigDecimal startBaseMarketValue = null;
			BigDecimal endLocalMarketValue = BigDecimal.ZERO;
			BigDecimal endBaseMarketValue = BigDecimal.ZERO;
			Date priorDate = null;
			for(Date date: dates) {
				Position position = getPositions().getPosition(date, instrumentLeg, false);
				if(startLocalMarketValue == null) {
					priorDate = date;
					if(position == null) {
						startLocalMarketValue = BigDecimal.ZERO;
						startBaseMarketValue = BigDecimal.ZERO;
					} else {
						startLocalMarketValue = position.getMarketValue();
						startBaseMarketValue = position.getBaseMarketValue();
					}
				} else {
					endLocalMarketValue = position.getMarketValue();
					endBaseMarketValue = position.getBaseMarketValue();
					BigDecimal localCashflowsAmount = position.getLocalCashflowsAmount();
					BigDecimal baseCashflowsAmount = position.getBaseCashflowsAmount();
					BigDecimal baseGainLoss = endBaseMarketValue.subtract(startBaseMarketValue).add(localCashflowsAmount);
					BigDecimal localGainLoss = endLocalMarketValue.subtract(startLocalMarketValue).add(localCashflowsAmount);

					//Get the dimensions used for reporting disaggregation
					PerformanceDimensions key = getPerformanceFactory().createPerformanceDimensions();
					if(performanceDimensions.contains(DatePerformanceDimension.getInstance()) 
							|| performanceDimensions.contains(CumulativePerformanceDimension.getInstance())) {
						key = key.withDateDimension(date);
					}
					key.addAll(instrumentLeg.getPerformanceDimensionsKey(getPerformanceFactory(), performanceDimensions));
					PerformanceValue performanceValue = new PerformanceValue(
							date, priorDate, startLocalMarketValue, startBaseMarketValue,
							endLocalMarketValue, endBaseMarketValue, localCashflowsAmount, baseCashflowsAmount, localGainLoss, baseGainLoss);
					if(getPerformanceValues().containsKey(key)) {
						getPerformanceValues().put(key, getPerformanceValues().get(key).add(performanceValue));
					} else {
						getPerformanceValues().put(key, performanceValue);
					}

					//Reset the start MV as the current day end MV
					startLocalMarketValue = position.getMarketValue();
					startBaseMarketValue = position.getBaseMarketValue();
					priorDate = date;
				}
			}
		}

		//Rate of Return calculations.  RoR is calculated for each dimension instance (e.g by Date, by Portfolio)
		for(PerformanceDimensions key: getPerformanceValues().keySet()) {
			PerformanceValue performanceValue = getPerformanceValues().get(key);
			try {
				Assertions.notNull(String.format("PerformanceValue for key (%s)",key.toString()), performanceValue);
			} catch (Exception e) {
				System.out.println(StringUtil.getMapAsStringWithLineBreaks(getPerformanceValues()));
				throw new RuntimeException(e);
			}

			//Local Rate of Return
			BigDecimal localRateOfReturn = calculateModifiedDietzRateOfReturn(performanceValue, getDayWeighting(), CurrencyBasis.LOCAL);
			performanceValue.setLocalRateOfReturn(localRateOfReturn);

			//Base Rate of Return
			//TODO: test and delete
			BigDecimal baseRateOfReturn = calculateModifiedDietzRateOfReturn(performanceValue, getDayWeighting(), CurrencyBasis.BASE);
			performanceValue.setBaseRateOfReturn(baseRateOfReturn);
		}

		//Calculate Daily Rates of Return.  RoR is calculated for each dimension instance (e.g by Date, by Portfolio)
		if(getPerformanceDimensions().contains(CumulativePerformanceDimension.getInstance())) {
			Set<PerformanceDimensions> keysForCumulativeRatesOfReturn = performanceDimensions.getDimensionsForCumulativeRatesOfReturn(getPerformanceValues().keySet());
			for(PerformanceDimensions keyForCumulativeRatesOfReturn: keysForCumulativeRatesOfReturn) {
				final Set<PerformanceValue> performanceValuesSubSet = getPerformanceValuesByKey(keyForCumulativeRatesOfReturn);
				List<BigDecimal> localRatesOfReturn = getRatesOfReturn(performanceValuesSubSet, true);
				BigDecimal cumulativeLocalRateOfReturn = PerformanceFactory.getChainLinkedReturn(localRatesOfReturn);
				List<BigDecimal> baseRatesOfReturn = getRatesOfReturn(performanceValuesSubSet, false);
				BigDecimal cumulativeBaseRateOfReturn = PerformanceFactory.getChainLinkedReturn(baseRatesOfReturn);
				if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
					System.out.println(String.format("%s CumulativeReturns Local [%s] Base[%s]", 
							keyForCumulativeRatesOfReturn.toString(), 
							StringUtil.formatPrice(cumulativeLocalRateOfReturn), 
							keyForCumulativeRatesOfReturn.toString(), 
							StringUtil.formatPrice(cumulativeBaseRateOfReturn)));
				}
				PerformanceValue latestPerformanceValue = getLatestPerformanceValue(performanceValuesSubSet);
				latestPerformanceValue.setLocalRateOfReturn(cumulativeLocalRateOfReturn);
				latestPerformanceValue.setBaseRateOfReturn(cumulativeBaseRateOfReturn);
				getCumulativePerformanceValues().put(keyForCumulativeRatesOfReturn, latestPerformanceValue);
			}
		}
		getTimer().stop();
		getTimer().print(true);
	}

	private DayWeighting getDayWeighting() {
		return getPerformanceFactory().getDayWeighting();
	}

	private BigDecimal calculateRateOfReturn(PerformanceValue performanceValue, CurrencyBasis currencyBasis) {
		//Place holder calculates RoR using the start market value as the divisor
		BigDecimal divisor = performanceValue.getStartMarketValue(currencyBasis);
		if(divisor.compareTo(BigDecimal.ZERO) == 0) {
			divisor = performanceValue.getEndMarketValue(currencyBasis);
		}
		BigDecimal rateOfReturn = BigDecimal.ZERO;
		if(divisor.compareTo(BigDecimal.ZERO) != 0) {
			rateOfReturn = performanceValue.getGainLoss(currencyBasis).divide(divisor , getScale(), getRoundingMode());
		}
		return rateOfReturn;
	}

	protected static BigDecimal calculateModifiedDietzRateOfReturn(PerformanceValue performanceValue, DayWeighting dayWeighting, CurrencyBasis currencyBasis) {
		BigDecimal numerator = performanceValue.getGainLoss(currencyBasis);
		BigDecimal weightedCashflows = BigDecimal.ZERO;
		if(performanceValue.getPriorDate() != null) {
			Days days = Days.daysBetween(performanceValue.getPriorDateInstant(), performanceValue.getDateInstant());
			BigDecimal dayCount = NumberUtil.newBigDecimal(days.getDays());
			if(dayCount.compareTo(BigDecimal.ZERO) != 0) {
				weightedCashflows = dayWeighting.getWeight()
						.divide(dayCount, getScale(), getRoundingMode())
						.multiply(performanceValue.getCashflowsAmount(currencyBasis));
			}
		}
		BigDecimal divisor = performanceValue.getStartMarketValue(currencyBasis).add(weightedCashflows);
		if(divisor.compareTo(BigDecimal.ZERO) == 0) {
			divisor = performanceValue.getEndMarketValue(currencyBasis);
		}
		BigDecimal rateOfReturn = BigDecimal.ZERO;
		if(divisor.compareTo(BigDecimal.ZERO) != 0) {
			rateOfReturn = performanceValue.getGainLoss(currencyBasis).divide(divisor, getScale(), getRoundingMode());
		}
		return rateOfReturn;
	}

	private PerformanceValue getLatestPerformanceValue(Set<PerformanceValue> performanceValues) {
		PerformanceValue lastElement = null;
		Iterator<PerformanceValue> iterator = performanceValues.iterator();
		while (iterator.hasNext()) {
			lastElement = iterator.next();
		}
		return lastElement;
	}

	private static List<BigDecimal> getRatesOfReturn(Set<PerformanceValue> performanceValues, boolean localReturn) {
		List<BigDecimal> ratesOfReturn = new ArrayList<>();
		for(PerformanceValue performanceValue: performanceValues) {
			if(localReturn) {
				ratesOfReturn.add(performanceValue.getLocalRateOfReturn());
			} else {
				ratesOfReturn.add(performanceValue.getBaseRateOfReturn());
			}
		}
		return ratesOfReturn;
	}

	private Set<PerformanceValue> getPerformanceValuesByKey(PerformanceDimensions keyForCumulativeRatesOfReturn) {
		Set<PerformanceValue> performanceValues = new LinkedHashSet<>();
		for(PerformanceDimensions performanceValuesKey: getPerformanceValues().keySet()) {
			if(keyForCumulativeRatesOfReturn.equalsIgnoreDate(performanceValuesKey)) {
				PerformanceValue performanceValue = getPerformanceValues().get(performanceValuesKey);
				performanceValues.add(performanceValue );
			}
		}
		return performanceValues;
	}

	public Map<PerformanceDimensions, PerformanceValue> getPerformanceValues() {
		return performanceValues;
	}

	private void setPerformanceValues(Map<PerformanceDimensions, PerformanceValue> performanceValues) {
		this.performanceValues = performanceValues;
	}

	public Map<PerformanceDimensions, PerformanceValue> getCumulativePerformanceValues() {
		return cumulativePerformanceValues;
	}

	public void setCumulativePerformanceValues(Map<PerformanceDimensions, PerformanceValue> cumulativePerformanceValues) {
		this.cumulativePerformanceValues = cumulativePerformanceValues;
	}

	public PerformanceDimensions getResultsPerformanceDimensions() {
		return performanceDimensions;
	}

	public Instruments getInstruments() {
		return instruments;
	}

	private void setInstruments(Instruments instruments) {
		this.instruments = instruments;
	}

	private void setInstruments(SecurityMasters securityMasterData) {
		getTimer().start("GetInstruments");
		instruments = new Instruments(securityMasterData);
	}

	public InstrumentLegs getInstrumentLegs() {
		return instrumentLegs;
	}

	private void setInstrumentLegs(InstrumentLegs instrumentLegs) {
		this.instrumentLegs = instrumentLegs;
	}

	public Positions getPositions() {
		return positions;
	}

	private void setPositions(Positions positions) {
		this.positions = positions;
	}

	public SecurityMasters getSecurityMasterData() {
		return securityMasters;
	}

	public Portfolios getPortfolios() {
		return portfolios;
	}

	private void setPortfolios(Portfolios portfolios) {
		this.portfolios = portfolios;
	}

	public BenchmarkAssociations getBenchmarkAssociations() {
		return benchmarkAssociations;
	}

	private void setBenchmarkAssociations(BenchmarkAssociations benchmarkAssociations) {
		this.benchmarkAssociations = benchmarkAssociations;
	}

	private void setSecurityMasterData(SecurityMasters securityMasters) {
		this.securityMasters = securityMasters;
	}

	public Cashflows getCashflows() {
		return cashflows;
	}

	private void setCashflows(Cashflows cashflows) {
		this.cashflows = cashflows;
	}


	@Deprecated
	public Positions getPositions(DataSource dataSource) {
		if(positions == null || positions.size() == 0) {
			getTimer().start("GetPositions");
			switch(dataSource.getDataSourceType()) {
			case CSV:
				throw new UnsupportedOperationException("Use new data source approach to retrieve position data");
			case FINDUR_PMM:
				loadPositionsFromFindurPmm();
				break;
			case FINDUR_ACS_DESKTOP:
			case FINDUR_USER_TABLE:
			default:
				throw new UnsupportedOperationException(String.format("Unsupported DataSource[%s] with DataSourceType[%s]", dataSource, dataSource.getDataSourceType()));
			}
		}
		return positions;
	}

	private Positions loadPositionsFromFindurPmm() {
		positions = new Positions();
		Integer insNum = null;
		Table positionsTable = null;
		Table cashflowTable = null;
		try {
			cashflowTable = getCashflowTableDataFromFindurPmm();
			instrumentLegs.addAll(getInstrumentLegs(cashflowTable));
			//TODO: investigate which InstrumentLeg was added (before=179, after=180)
			Cashflows pmmCashflows = new Cashflows();
			if(cashflowTable != null) {
				int rowCount = cashflowTable.getRowCount();
				for(int rowId = 0; rowId < rowCount; rowId++) {
					Date date = cashflowTable.getDate(CsvHeader.DATE.getName(), rowId);
					String currencyName = cashflowTable.getString(CsvHeader.CURRENCY.getName(), rowId);
					String portfolioStr = cashflowTable.getString(CsvHeader.PORTFOLIO.getName(), rowId);
					String instrumentId = cashflowTable.getString(CsvHeader.INSTRUMENT_ID.getName(), rowId);
					int legId = cashflowTable.getInt(CsvHeader.LEG_ID.getName(), rowId);
					Portfolio portfolio = new Portfolio(portfolioStr);
					Instrument instrument = getInstruments().getInstrument(instrumentId, false);
					InstrumentLeg instrumentLeg = getInstrumentLegs().getInstrumentLeg(portfolio, instrument, legId);
					if(instrumentLeg == null) {
						instrumentLeg = new InstrumentLeg(portfolio, instrument, legId, currencyName);
						getInstrumentLegs().add(instrumentLeg);
					}
					if(instrumentId.equalsIgnoreCase(DEBUG_INSTRUMENT_ID)) {
						System.out.println(String.format("InstrumentId [%s] InsNum [%s]",instrumentId,insNum));
					}
					BigDecimal localCashFlowAmount = NumberUtil.newBigDecimal(cashflowTable.getDouble(CsvHeader.LOCAL_CASHFLOWS_AMOUNT.getName(), rowId));
					BigDecimal baseCashFlowAmount = NumberUtil.newBigDecimal(cashflowTable.getDouble(CsvHeader.BASE_CASHFLOWS_AMOUNT.getName(), rowId));
					pmmCashflows.add(new Cashflow(instrumentLeg, date, currencyName, localCashFlowAmount, baseCashFlowAmount));
				}
			}

			positionsTable = getPositionTableDataFromFindurPmm();
			instrumentLegs.addAll(getInstrumentLegs(positionsTable));
			if(positionsTable != null) {
				int rowCount = positionsTable.getRowCount();
				for(int rowId = 0; rowId < rowCount; rowId++) {
					String portfolioStr = positionsTable.getString(CsvHeader.PORTFOLIO.getName(), rowId);
					String instrumentId = positionsTable.getString(CsvHeader.INSTRUMENT_ID.getName(), rowId);
					if(instrumentId.equalsIgnoreCase(DEBUG_INSTRUMENT_ID)) {
						System.out.println(String.format("InstrumentId [%s] InsNum [%s]",instrumentId,insNum));
					}
					Date date = positionsTable.getDate(CsvHeader.DATE.getName(), rowId);
					int legId = positionsTable.getInt(CsvHeader.LEG_ID.getName(), rowId);
					String currencyName = positionsTable.getString(CsvHeader.CURRENCY.getName(), rowId);
					BigDecimal marketValue = NumberUtil.newBigDecimal(positionsTable.getDouble(CsvHeader.END_LOCAL_MARKET_VALUE.getName(), rowId));
					BigDecimal baseMarketValue = NumberUtil.newBigDecimal(positionsTable.getDouble(CsvHeader.END_BASE_MARKET_VALUE.getName(), rowId));

					//TODO: handle cash flows
					BigDecimal localAmount = NumberUtil.newBigDecimal("0");

					Portfolio portfolio = new Portfolio(portfolioStr);
					Instrument instrument = getInstruments().getInstrument(instrumentId, false);
					InstrumentLeg instrumentLeg = getInstrumentLegs().getInstrumentLeg(portfolio, instrument, legId, false);
					if(instrumentLeg == null) {
						instrumentLeg = new InstrumentLeg(portfolio, instrument, legId, currencyName);
					}
					/*
					 * Cashflows cashflows = new Cashflows();
					 * cashflows.add(new Cashflow(instrumentLeg, date, currencyName, localAmount, localAmount));
					 */
					Cashflows cashflows = pmmCashflows.getCashflows(date, instrumentLeg);

					Position position = new Position(instrumentLeg, date, marketValue, baseMarketValue, cashflows);
					if (getDebugLevel().atLeast(DebugLevel.HIGH)) {
						System.out.println("InsNum [" + insNum + "] Position [" + position.toString() + "]");
					}
					getPositions().add(position);
				}
			}
			getPositions().requirePositionForDate(dataSource.getStartDate());
			getPositions().requirePositionForDate(dataSource.getEndDate());
		} catch (Exception e) {
			System.out.println(String.format("Failed to parse InsNum [%s]", insNum));
			throw new RuntimeException(e);
		} finally {
			PluginUtil.dispose(positionsTable);
			PluginUtil.dispose(cashflowTable);
		}
		return getPositions();
	}

	private InstrumentLegs getInstrumentLegs(Table table) {
		InstrumentLegs instrumentLegs = new InstrumentLegs();
		if(table != null) {
			int rowCount = table.getRowCount();
			for(int rowId = 0; rowId < rowCount; rowId++) {
				String portfolioStr = table.getString(CsvHeader.PORTFOLIO.getName(), rowId);
				String instrumentId = table.getString(CsvHeader.INSTRUMENT_ID.getName(), rowId);
				if(instrumentId.equalsIgnoreCase(DEBUG_INSTRUMENT_ID)) {
					System.out.println(String.format("InstrumentId [%s]",instrumentId));
				}
				int legId = table.getInt(CsvHeader.LEG_ID.getName(), rowId);
				String currencyName = table.getString(CsvHeader.CURRENCY.getName(), rowId);
				Portfolio portfolio = new Portfolio(portfolioStr);
				Instrument instrument = getInstruments().getInstrument(instrumentId, false);
				InstrumentLeg instrumentLeg = new InstrumentLeg(portfolio, instrument, legId, currencyName);
				instrumentLegs.add(instrumentLeg);
			}
		}
		return instrumentLegs;
	}

	private Table getCashflowTableDataFromFindurPmm() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append(String.format("pstl.eod_date '%s'", CsvHeader.DATE.getName()));
		sql.append(String.format(", p.name '%s'", CsvHeader.PORTFOLIO.getName())); 
		sql.append(String.format("\n, psd.security_name '%s'", CsvHeader.INSTRUMENT_ID.getName()));
		sql.append(String.format(", pstl.param_seq_num '%s'", CsvHeader.LEG_ID.getName()));
		sql.append(String.format("\n, c.name '%s'", CsvHeader.CURRENCY.getName()));
		sql.append(String.format(", -1.0 * pstl.position '%s'", CsvHeader.LOCAL_CASHFLOWS_AMOUNT.getName()));
		//TODO: get base currency
		sql.append(String.format("\n, -1.0 * pstl.position '%s'", CsvHeader.BASE_CASHFLOWS_AMOUNT.getName()));
		sql.append("\nFROM perf_sec_tran_log pstl");
		sql.append("\nJOIN perf_sec_defn psd ON psd.security_id = pstl.src_security_id");
		sql.append("\nJOIN portfolio p ON p.id_number = pstl.portfolio_id");
		if(getDataSource().getPortfolios().size() > 0) {
			sql.append(String.format("\n AND p.name IN (%s)", dataSource.getPortfoliosFilterAsString()));
		}
		sql.append("\nJOIN currency c ON c.id_number = pstl.currency");
		Date startDate = dataSource.getStartDate();
		Date endDate = dataSource.getEndDate();
		sql.append(String.format("\nWHERE pstl.eod_date > '%s' AND pstl.eod_date <= '%s'",
				getSession().getCalendarFactory().getSQLString(startDate),
				getSession().getCalendarFactory().getSQLString(endDate)));
		sql.append("\n AND perf_entry_type IN (3 /*Income*/, 9/*Proceeds*/)");

		sql.append("\nUNION\nSELECT ");
		sql.append(String.format("pstl.eod_date '%s'", CsvHeader.DATE.getName()));
		sql.append(String.format(", p.name '%s'", CsvHeader.PORTFOLIO.getName())); 
		sql.append(String.format("\n, psd.security_name '%s'", CsvHeader.INSTRUMENT_ID.getName()));
		sql.append(String.format(", pstl.param_seq_num '%s'", CsvHeader.LEG_ID.getName()));
		sql.append(String.format("\n, c.name '%s'", CsvHeader.CURRENCY.getName()));
		sql.append(String.format(", pstl.position '%s'", CsvHeader.LOCAL_CASHFLOWS_AMOUNT.getName()));
		//TODO: get base currency
		sql.append(String.format("\n, pstl.position '%s'", CsvHeader.BASE_CASHFLOWS_AMOUNT.getName()));
		sql.append("\nFROM perf_sec_tran_log pstl");
		sql.append("\nJOIN perf_sec_defn psd ON psd.security_id = pstl.security_id");
		sql.append("\nJOIN portfolio p ON p.id_number = pstl.portfolio_id");
		if(getDataSource().getPortfolios().size() > 0) {
			sql.append(String.format("\n AND p.name IN (%s)", dataSource.getPortfoliosFilterAsString()));
		}
		sql.append("\nJOIN currency c ON c.id_number = pstl.currency");
		sql.append(String.format("\nWHERE pstl.eod_date > '%s' AND pstl.eod_date <= '%s'",
				getSession().getCalendarFactory().getSQLString(startDate),
				getSession().getCalendarFactory().getSQLString(endDate)));
		sql.append("\n AND perf_entry_type IN (3 /*Income*/, 9/*Proceeds*/)");

		sql.append("\nORDER BY pstl.eod_date, p.name, psd.security_name");
		return getSession().getIOFactory().runSQL(sql.toString());
	}

	private Table getPositionTableDataFromFindurPmm() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT ");
		sql.append(String.format("p.name '%s'", CsvHeader.PORTFOLIO.getName())); 
		sql.append(String.format(", psd.security_name '%s'", CsvHeader.INSTRUMENT_ID.getName()));
		sql.append(String.format("\n, psv.eod_date '%s'", CsvHeader.DATE.getName()));
		sql.append(String.format(", psv.param_seq_num '%s'", CsvHeader.LEG_ID.getName()));
		sql.append(String.format("\n, c.name '%s'", CsvHeader.CURRENCY.getName()));
		sql.append(String.format(", psv.market_value '%s'", CsvHeader.END_LOCAL_MARKET_VALUE.getName()));
		sql.append(String.format("\n, psv.base_market_value '%s'", CsvHeader.END_BASE_MARKET_VALUE.getName()));
		sql.append("\nFROM perf_sec_values psv");
		sql.append("\nJOIN perf_sec_defn psd ON psd.security_id = psv.security_id");
		sql.append("\nJOIN portfolio p ON p.id_number = psv.portfolio_id");
		if(dataSource.getPortfolios().size() > 0) {
			sql.append(String.format("\n AND p.name IN (%s)", dataSource.getPortfoliosFilterAsString()));
		}
		sql.append("\nJOIN currency c ON c.id_number = psv.currency");
		Date startDate = dataSource.getStartDate();
		Date endDate = dataSource.getEndDate();
		sql.append(String.format("\nWHERE psv.eod_date BETWEEN '%s' AND '%s'",
				getSession().getCalendarFactory().getSQLString(startDate),
				getSession().getCalendarFactory().getSQLString(endDate)));
		sql.append("\nORDER BY psv.eod_date, psd.security_name");
		return getSession().getIOFactory().runSQL(sql.toString());
	}

	public SecurityMasters getSecurityMasterData(DataSource dataSource) {
		getTimer().start("GetSecurityMasterData");
		if(securityMasters == null || securityMasters.size() == 0) {
			switch(dataSource.getDataSourceType()) {
			case CSV:
				throw new UnsupportedOperationException("Use new data source approach to retrieve security master data");
			case FINDUR_PMM:
				loadSecurityMasterFromFindur();
				break;
			case FINDUR_ACS_DESKTOP:
			case FINDUR_USER_TABLE:
			default:
				throw new UnsupportedOperationException(String.format("Unsupported DataSource[%s] with DataSourceType[%s]", dataSource, dataSource.getDataSourceType()));
			}
		}
		return securityMasters;
	}

	public PerformanceDimensions getPerformanceDimensions() {
		return performanceDimensions;
	}

	public void setPerformanceDimensions(PerformanceDimensions performanceDimensions) {
		this.performanceDimensions = performanceDimensions;
	}

	public static int getScale() {
		return scale ;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public static RoundingMode getRoundingMode() {
		return roundingMode;
	}

	public void setRoundingMode(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}

	public Timer getTimer() {
		return timer;
	}

	private void setTimer(Timer timer) {
		this.timer = timer;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public SecurityMasters loadSecurityMasterFromFindur() {
		securityMasters = new SecurityMasters();
		Map<Integer, String> instrumentIds = getInstrumentIdsFromFindur();
		Map<Integer, SecurityMasterEnum> instrumentClasses = getSecurityMasterDataFromFindur(InstrumentClass.class);

		for(int insNum: instrumentIds.keySet()) {
			String instrumentId = instrumentIds.get(insNum);
			InstrumentClass instrumentClass = (InstrumentClass) instrumentClasses.get(insNum);
			SecurityMaster securityMaster = new SecurityMaster(instrumentId , null, null, null,
					null, instrumentClass, null);
			if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
				System.out.println(String.format("InsNum [%s] InstrumentId [%s] SecurityMaster [%s]", insNum, instrumentId, securityMaster.toString()));
			}
			getSecurityMasterData().add(securityMaster);
		}
		return getSecurityMasterData();
	}

	private Map<Integer, String> getInstrumentIdsFromFindur() {
		Map<Integer, String> map = new HashMap<>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT ab.ins_num 'key', psd.security_name 'value'");
		sql.append("\nFROM perf_sec_defn psd");
		sql.append("\nJOIN ab_tran ab ON ab.deal_tracking_num = psd.deal_tracking_num");
		sql.append(String.format("\n AND ab.tran_status IN (%s,%s)", EnumTranStatus.Validated.getValue(), EnumTranStatus.Matured.getValue()));

		Table table = null;
		try {
			table = getSession().getIOFactory().runSQL(sql.toString());
			if(table != null) {
				int rowCount = table.getRowCount();
				for(int row = 0; row < rowCount; row++) {
					int insNum = table.getInt("key", row);
					String value = table.getString("value", row);
					map.put(insNum, value);
				}
			}
		} finally {
			PluginUtil.dispose(table);
		}
		return map;
	}

	private <E extends SecurityMasterEnum> Map<Integer, SecurityMasterEnum> getSecurityMasterDataFromFindur(Class<E> clazz) {
		Map<Integer, SecurityMasterEnum> map = new HashMap<>();
		String sql = null;
		if(clazz.equals(InstrumentClass.class)) {
			sql = getSqlStringForInstrumentClass();
		}
		Table table = null;
		try {
			table = getSession().getIOFactory().runSQL(sql);
			if(table != null) {
				int rowCount = table.getRowCount();
				for(int row = 0; row < rowCount; row++) {
					int insNum = table.getInt("key", row);
					String findurName = table.getString("value", row);
					SecurityMasterEnum value = InstrumentClass.fromName(findurName);
					map.put(insNum, value);
				}
			}
		} finally {
			PluginUtil.dispose(table);
		}
		return map;
	}

	private String getSqlStringForInstrumentClass() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT DISTINCT ab.ins_num 'key', ic.name 'value'");
		sb.append("\nFROM ab_tran ab");
		sb.append("\nJOIN ins_class ic ON ic.id_number = ab.ins_class");
		sb.append(String.format("\nWHERE ab.tran_status IN (%s,%s)", EnumTranStatus.Validated.getValue(), EnumTranStatus.Matured.getValue()));
		return sb.toString();
	}

	public Session getSession() {
		return getPerformanceFactory().getSession();
	}

	private DebugLevel getDebugLevel() {
		return performanceFactory.getDebugLevel();
	}

	public PerformanceFactory getPerformanceFactory() {
		return performanceFactory;
	}

	public void setPerformanceFactory(PerformanceFactory performanceFactory) {
		this.performanceFactory = performanceFactory;
	}

	public void extractToCsvFile(String filePath) {
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		try {
			fileWriter = new FileWriter(filePath);
			csvFilePrinter = new CSVPrinter(fileWriter, Configurator.CSV_FILE_FORMAT);

			List<List<String>> performanceResultsSummary = null;
			if(getPerformanceDimensions().contains(CumulativePerformanceDimension.getInstance())) {
				performanceResultsSummary = getCumulativePerformanceResultsSummaryForCsv();
			} else {
				performanceResultsSummary = getPerformanceResultsSummaryForCsv();
			}

			for(List<String> performanceResultsEntry: performanceResultsSummary) {
				csvFilePrinter.printRecord(performanceResultsEntry);
			}
		} catch (IOException e) {
			if(e.getMessage().contains("The process cannot access the file because it is being used by another process")) {
				//TODO: handle this
				//throw new FileLockedException(e);
			} else {
				throw new RuntimeException(e);
			}
		} finally {
			PluginUtil.close(fileWriter);
			PluginUtil.close(csvFilePrinter);
		}
	}

	public List<String> getCsvFileHeader() {
		List<String> headers = new ArrayList<>();
		List<CsvHeader> headerEnums = getSummaryHeaders();
		for(CsvHeader header: headerEnums) {
			headers.add(header.getName());
		}
		return headers;
	}

	public List<String> getUserTableFieldNames() {
		List<String> headers = new ArrayList<>();
		List<CsvHeader> headerEnums = getSummaryHeaders();
		for(CsvHeader header: headerEnums) {
			headers.add(header.getUserTableFieldName());
		}
		return headers;
	}

	public List<CsvHeader> getSummaryHeaders() {
		List<CsvHeader> header = new ArrayList<>();
		//The order is important if you want the headers to match the content!
		if(getPerformanceDimensions().contains(DatePerformanceDimension.getInstance())) {
			header.add(CsvHeader.DATE);
		}
		if(getPerformanceDimensions().contains(PortfolioPerformanceDimension.getInstance()) 
				|| getPerformanceDimensions().contains(InstrumentLegPerformanceDimension.getInstance())) {
			header.add(CsvHeader.PORTFOLIO);
		}
		if(getPerformanceDimensions().contains(InstrumentLegPerformanceDimension.getInstance())) {
			header.add(CsvHeader.INSTRUMENT_ID);
			header.add(CsvHeader.LEG_ID);
			header.add(CsvHeader.CURRENCY);
		}
		//TODO: add other industry group metadata if reqd
		header.add(CsvHeader.START_LOCAL_MARKET_VALUE);
		header.add(CsvHeader.START_BASE_MARKET_VALUE);
		header.add(CsvHeader.END_LOCAL_MARKET_VALUE);
		header.add(CsvHeader.END_BASE_MARKET_VALUE);
		header.add(CsvHeader.LOCAL_CASHFLOWS_AMOUNT);
		header.add(CsvHeader.BASE_CASHFLOWS_AMOUNT);
		header.add(CsvHeader.LOCAL_GAIN_LOSS);
		header.add(CsvHeader.BASE_GAIN_LOSS);
		header.add(CsvHeader.LOCAL_RATE_OF_RETURN);
		header.add(CsvHeader.BASE_RATE_OF_RETURN);
		return header;
	}


	private static List<List<String>> getPerformanceResultsSummary(List<String> csvFileHeader, PerformanceDimensions performanceDimensions, Map<PerformanceDimensions, PerformanceValue> performanceValues) {
		List<List<String>> performanceResultsSummary = new ArrayList<>();
		performanceResultsSummary.add(csvFileHeader);

		for(PerformanceDimensions key: performanceValues.keySet()) {
			List<String> performanceResultsEntry = new ArrayList<>();

			if(performanceDimensions.contains(DatePerformanceDimension.getInstance())) {
				//CsvHeader.DATE
				Date date = key.getDate();
				performanceResultsEntry.add(StringUtil.formatDate(date));
			}
			if(performanceDimensions.contains(PortfolioPerformanceDimension.getInstance())
					|| performanceDimensions.contains(InstrumentLegPerformanceDimension.getInstance())) {
				//CsvHeader.PORTFOLIO
				performanceResultsEntry.add(key.getPortfolio().getName());
			}
			if(performanceDimensions.contains(InstrumentLegPerformanceDimension.getInstance())) {
				InstrumentLeg instrumentLeg = key.getInstrumentLeg();
				//CsvHeader.INSTRUMENT_ID
				performanceResultsEntry.add(instrumentLeg.getInstrument().getInstrumentId());
				//CsvHeader.LEG_ID
				performanceResultsEntry.add(String.valueOf(instrumentLeg.getLegId()));
				//CsvHeader.CURRENCY
				performanceResultsEntry.add(instrumentLeg.getCurrency());
			}

			PerformanceValue performanceValue = performanceValues.get(key);

			//CsvHeader.START_LOCAL_MARKET_VALUE
			BigDecimal startLocalMarketValue = performanceValue.getStartLocalMarketValue();
			performanceResultsEntry.add(StringUtil.formatDouble(startLocalMarketValue));
			//CsvHeader.START_BASE_MARKET_VALUE
			BigDecimal startBaseMarketValue = performanceValue.getStartBaseMarketValue();
			performanceResultsEntry.add(StringUtil.formatDouble(startBaseMarketValue));
			//CsvHeader.END_LOCAL_MARKET_VALUE
			BigDecimal endLocalMarketValue = performanceValue.getEndLocalMarketValue();
			performanceResultsEntry.add(StringUtil.formatDouble(endLocalMarketValue));
			//CsvHeader.END_BASE_MARKET_VALUE
			BigDecimal endBaseMarketValue = performanceValue.getEndBaseMarketValue();
			performanceResultsEntry.add(StringUtil.formatDouble(endBaseMarketValue));
			//CsvHeader.LOCAL_CASHFLOWS_AMOUNT
			BigDecimal localCashflowsAmount = performanceValue.getLocalCashflowsAmount();
			performanceResultsEntry.add(StringUtil.formatDouble(localCashflowsAmount));
			//CsvHeader.BASE_CASHFLOWS_AMOUNT
			BigDecimal baseCashflowsAmount =performanceValue.getLocalCashflowsAmount();
			performanceResultsEntry.add(StringUtil.formatDouble(baseCashflowsAmount));
			//CsvHeader.LOCAL_GAIN_LOSS
			BigDecimal localGainLoss = performanceValue.getLocalGainLoss();
			performanceResultsEntry.add(StringUtil.formatDouble(localGainLoss));
			//CsvHeader.BASE_GAIN_LOSS
			BigDecimal baseGainLoss = performanceValue.getBaseGainLoss();
			performanceResultsEntry.add(StringUtil.formatDouble(baseGainLoss));
			//LOCAL_RATE_OF_RETURN
			BigDecimal localRateOfReturn = performanceValue.getLocalRateOfReturn();
			performanceResultsEntry.add(StringUtil.formatPrice(localRateOfReturn));
			//CsvHeader.BASE_RATE_OF_RETURN
			BigDecimal baseRateOfReturn = performanceValue.getBaseRateOfReturn();
			performanceResultsEntry.add(StringUtil.formatPrice(baseRateOfReturn));

			performanceResultsSummary.add(performanceResultsEntry);
		}
		return performanceResultsSummary;
	}

	public List<List<String>> getPerformanceResultsSummaryForCsv() {
		return getPerformanceResultsSummary(getCsvFileHeader(), getPerformanceDimensions(), getPerformanceValues());
	}

	public List<List<String>> getPerformanceResultsSummaryForUserTable() {
		return getPerformanceResultsSummary(getUserTableFieldNames(), getPerformanceDimensions(), getPerformanceValues());
	}

	public List<List<String>> getCumulativePerformanceResultsSummaryForCsv() {
		return getPerformanceResultsSummary(getCsvFileHeader(), getPerformanceDimensions(), getCumulativePerformanceValues());
	}

	public List<List<String>> getCumulativePerformanceResultsSummaryForUserTable() {
		return getPerformanceResultsSummary(getUserTableFieldNames(), getPerformanceDimensions(), getCumulativePerformanceValues());
	}

	public void extractToUserTable(String tableName) {
		UserTable userTable = getSession().getIOFactory().getUserTable(tableName);
		Table updateData = userTable.getTableStructure();

		boolean headerRecord = true;
		List<List<String>> performanceResultsSummary = null;
		if(getPerformanceDimensions().contains(CumulativePerformanceDimension.getInstance())) {
			performanceResultsSummary = getCumulativePerformanceResultsSummaryForUserTable();
		} else {
			performanceResultsSummary = getPerformanceResultsSummaryForUserTable();
		}
		if(performanceResultsSummary != null && performanceResultsSummary.size() > 0) {
			for(List<String> extractRowData: performanceResultsSummary) {
				if(extractRowData.size() != updateData.getColumnCount()) {
					throw new IllegalArgumentException(
							String.format(
									"The list data does not contain the expected number of columns. Actual[%s], Expected [%s]",
									extractRowData.size(),
									updateData.getColumnCount()));
				}
				if(headerRecord) {
					//skip header record
					headerRecord = false;
					continue;
				}
				int rowId = updateData.addRows(1);
				if(rowId >= 0) {
					int colId = 0;
					for(String extractCellData: extractRowData) {
						EnumColType colType = updateData.getColumnType(colId);
						Object value = PluginUtil.castStringToType(extractCellData, colType);
						updateData.setValue(colId, rowId, value);
						colId++;
					}
				}
			}
			//TODO: this is an awkward way to perform the update
			userTable.deleteMatchingRows(updateData);
			userTable.insertRows(updateData);
		} else {
			System.out.println("No data to extract");
		}
	}

	public ObservableList<RowData> asObservableList() {
		List<List<String>> performanceResultsSummary = null;
		if(getPerformanceDimensions().contains(CumulativePerformanceDimension.getInstance())) {
			performanceResultsSummary = getCumulativePerformanceResultsSummaryForCsv();
		} else {
			performanceResultsSummary = getPerformanceResultsSummaryForCsv();
		}
		return CollectionUtil.getListAsObservableList(performanceResultsSummary, true, 2);
	}

	public Table asTable() {
		List<List<String>> performanceResultsSummary = null;
		if(getPerformanceDimensions().contains(CumulativePerformanceDimension.getInstance())) {
			performanceResultsSummary = getCumulativePerformanceResultsSummaryForUserTable();
		} else {
			performanceResultsSummary = getPerformanceResultsSummaryForUserTable();
		}
		return CollectionUtil.getListAsTable(getSession(), performanceResultsSummary, true);
	}

	public void extractToSerializedFile(String serializedFilePath) {
		//TODO: move this to the DataSource interface
		ObjectOutputStream out = null;
		FileOutputStream fileOut = null;
		try {
			Serializable serializable = null; 
			if(getPerformanceValues().size() > 0) {
				serializable = (Serializable) getPerformanceValues();
			}
			if(getCumulativePerformanceValues().size() > 0) {
				serializable = (Serializable) getCumulativePerformanceValues();
			}

			fileOut = new FileOutputStream(serializedFilePath);
			out = new ObjectOutputStream(fileOut);
			out.writeObject(serializable);
			System.out.println(String.format("Serialized data is saved in [%s]", serializedFilePath));
		} catch(IOException e) {
			throw new RuntimeException(e);
		} finally {
			PluginUtil.close(out);
			PluginUtil.close(fileOut);
		}
	}
}
