package com.kalinya.performance.datasource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import com.kalinya.enums.DebugLevel;
import com.kalinya.oc.util.TableUtil;
import com.kalinya.performance.BenchmarkAssociation;
import com.kalinya.performance.BenchmarkAssociations;
import com.kalinya.performance.Cashflow;
import com.kalinya.performance.Cashflows;
import com.kalinya.performance.Instrument;
import com.kalinya.performance.InstrumentLeg;
import com.kalinya.performance.InstrumentLegs;
import com.kalinya.performance.Portfolio;
import com.kalinya.performance.Portfolios;
import com.kalinya.performance.Position;
import com.kalinya.performance.Positions;
import com.kalinya.performance.SecurityMaster;
import com.kalinya.performance.SecurityMasters;
import com.kalinya.performance.enums.AssetClass;
import com.kalinya.performance.enums.CsvHeader;
import com.kalinya.performance.enums.IndustryGroup;
import com.kalinya.performance.enums.InstrumentClass;
import com.kalinya.performance.enums.RiskGroup;
import com.kalinya.performance.enums.Sector;
import com.kalinya.performance.enums.SecurityMasterEnum;
import com.kalinya.util.Assertions;
import com.kalinya.util.DateUtil;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.PluginUtil;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.table.Table;
import com.olf.openrisk.trading.EnumTranStatus;
import com.sun.browser.plugin.PluginHandler;

final public class FindurPmmDataSource extends DataSource {
	private static String DEBUG_INSTRUMENT_ID = "SMHL 13-1 A RMBS +95";
	private Session session;

	public FindurPmmDataSource(Builder builder) {
		super(builder);
		this.session = builder.session;
	}

	public static class Builder extends DataSource.Builder<Builder> {
		private Session session;

		@Override
		public FindurPmmDataSource build() {
			Assertions.notNull("Session", "This data source requires a Findur Session", session);
			return new FindurPmmDataSource(this);
		}

		public Builder withSession(Session session) {
			this.session = session;
			return this;
		}
	}

	@Override
	public Portfolios getPortfolios() {
		if(portfolios == null) {
			getTimer().start("GetPortfolios");
			portfolios = new Portfolios();
			Table table = null;
			try {
				StringBuilder sql = new StringBuilder();
				//TODO: hit the database for perf_portfolios and get portfolio_group
				sql.append("SELECT * FROM portfolio");
				table = getSession().getIOFactory().runSQL(sql.toString());
				if(table != null || table.getRowCount() == 0) {
					int rowCount = table.getRowCount();
					for(int rowId = 0; rowId < rowCount; rowId++) {
						String portfolioName = table.getString(CsvHeader.PORTFOLIO.getName(), rowId);
						String portfolioGroup = table.getString(CsvHeader.PORTFOLIO_GROUP.getName(), rowId);;
						Portfolio portfolio = new Portfolio(portfolioName, portfolioGroup);
						portfolios.add(portfolio);
					}
				} else {
					throw new RuntimeException("Failed to retrieve portfolios");
				}
			} finally {
				PluginUtil.dispose(table);
			}
			getTimer().stop();
		}
		return portfolios;
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
		if(getPortfolios().size() > 0) {
			sql.append(String.format("\n AND p.name IN (%s)", getPortfoliosAsString()));
		}
		sql.append("\nJOIN currency c ON c.id_number = pstl.currency");
		Date startDate = getStartDate();
		Date endDate = getEndDate();
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
		if(getPortfolios().size() > 0) {
			sql.append(String.format("\n AND p.name IN (%s)", getPortfoliosAsString()));
		}
		sql.append("\nJOIN currency c ON c.id_number = pstl.currency");
		sql.append(String.format("\nWHERE pstl.eod_date > '%s' AND pstl.eod_date <= '%s'",
				getSession().getCalendarFactory().getSQLString(startDate),
				getSession().getCalendarFactory().getSQLString(endDate)));
		sql.append("\n AND perf_entry_type IN (3 /*Income*/, 9/*Proceeds*/)");

		sql.append("\nORDER BY pstl.eod_date, p.name, psd.security_name");
		return getSession().getIOFactory().runSQL(sql.toString());
	}

	/**
	 * @param table
	 * @return
	 * @deprecated get InstrumentLegs from the Positions
	 */
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
		if(getPortfolios().size() > 0) {
			sql.append(String.format("\n AND p.name IN (%s)", getPortfoliosAsString()));
		}
		sql.append("\nJOIN currency c ON c.id_number = psv.currency");
		Date startDate = getStartDate();
		Date endDate = getEndDate();
		sql.append(String.format("\nWHERE psv.eod_date BETWEEN '%s' AND '%s'",
				getSession().getCalendarFactory().getSQLString(startDate),
				getSession().getCalendarFactory().getSQLString(endDate)));
		sql.append("\nORDER BY psv.eod_date, psd.security_name");
		return getSession().getIOFactory().runSQL(sql.toString());
	}

	@Override
	public BenchmarkAssociations getBenchmarkAssociations() {
		if(benchmarkAssociations == null) {
			getPortfolios();
			getTimer().start("GetBenchmarkAssociations");
			benchmarkAssociations = new BenchmarkAssociations();
			Table table = null;
			try {
				StringBuilder sql = new StringBuilder();
				//TODO: hit the database to get the benchmark associations
				sql.append("SELECT * FROM portfolio");
				table = getSession().getIOFactory().runSQL(sql.toString());
				if(table != null || table.getRowCount() == 0) {
					int rowCount = table.getRowCount();
					for(int rowId = 0; rowId < rowCount; rowId++) {
						String portfolioName = table.getString(CsvHeader.PORTFOLIO.getName(), rowId);
						String benchmarkName = table.getString(CsvHeader.BENCHMARK.getName(), rowId);
						Portfolio portfolio = getPortfolios().get(portfolioName);
						Portfolio benchmark = getPortfolios().get(benchmarkName);
						BenchmarkAssociation benchmarkAssociation = new BenchmarkAssociation(portfolio, benchmark);
						if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
							System.out.println(String.format("BenchmarkAssociation [%s]", benchmarkAssociation.toString()));
						}
						benchmarkAssociations.add(benchmarkAssociation);
					}
				} else {
					throw new RuntimeException("Failed to retrieve benchmark associations");
				}
			} finally {
				PluginUtil.dispose(table);
			}
			getTimer().stop();
		}
		return benchmarkAssociations;
	}

	@Override
	public SecurityMasters getSecurityMasterData() {
		if(securityMasterData == null) {
			getTimer().start("GetSecurityMasterData");
			securityMasterData = new SecurityMasters();
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
			getTimer().stop();
		}
		return securityMasterData;
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

	@Override
	public Positions getPositions() {
		if(positions == null) {
			getCashflows();
			getTimer().start("GetPositions");
			positions = new Positions();
			Integer insNum = null;
			Table positionsTable = null;
			try {
				positionsTable = getPositionTableDataFromFindurPmm();
				if(positionsTable != null) {
					int rowCount = positionsTable.getRowCount();
					for(int rowId = 0; rowId < rowCount; rowId++) {
						String portfolioName = positionsTable.getString(CsvHeader.PORTFOLIO.getName(), rowId);
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

						Portfolio portfolio = getPortfolios().get(portfolioName);
						Instrument instrument = getInstruments().getInstrument(instrumentId, false);
						//TODO: there is circularity here
						InstrumentLeg instrumentLeg = getInstrumentLegs().getInstrumentLeg(portfolio, instrument, legId, false);
						if(instrumentLeg == null) {
							instrumentLeg = new InstrumentLeg(portfolio, instrument, legId, currencyName);
							getInstrumentLegs().add(instrumentLeg);
						}
						Cashflows cashflows = getCashflows().getCashflows(date, instrumentLeg);
						//TODO: might need a method to inject Cashflows into the Positions
						Position position = new Position(instrumentLeg, date, marketValue, baseMarketValue, cashflows);
						if (getDebugLevel().atLeast(DebugLevel.HIGH)) {
							System.out.println("InsNum [" + insNum + "] Position [" + position.toString() + "]");
						}
						positions.add(position);
					}
				}
				positions.requirePositionForDate(getStartDate());
				positions.requirePositionForDate(getEndDate());
			} catch (Exception e) {
				System.out.println(String.format("Failed to parse InsNum [%s]", insNum));
				throw new RuntimeException(e);
			} finally {
				PluginUtil.dispose(positionsTable);
			}
			getTimer().stop();
		}
		return positions;
	}

	@Override
	public Cashflows getCashflows() {
		if(cashflows == null) {
			getTimer().start("GetCashflows");
			Table cashflowTable = null;
			try {
				cashflowTable = getCashflowTableDataFromFindurPmm();
				Cashflows cashflows = new Cashflows();
				if(cashflowTable != null) {
					int rowCount = cashflowTable.getRowCount();
					for(int rowId = 0; rowId < rowCount; rowId++) {
						Date date = cashflowTable.getDate(CsvHeader.DATE.getName(), rowId);
						String currencyName = cashflowTable.getString(CsvHeader.CURRENCY.getName(), rowId);
						String portfolioName = cashflowTable.getString(CsvHeader.PORTFOLIO.getName(), rowId);
						String instrumentId = cashflowTable.getString(CsvHeader.INSTRUMENT_ID.getName(), rowId);
						int legId = cashflowTable.getInt(CsvHeader.LEG_ID.getName(), rowId);
						Portfolio portfolio = getPortfolios().get(portfolioName);
						Instrument instrument = getInstruments().getInstrument(instrumentId, false);
						InstrumentLeg instrumentLeg = new InstrumentLeg(portfolio, instrument, legId, currencyName);
						if(instrumentId.equalsIgnoreCase(DEBUG_INSTRUMENT_ID)) {
							System.out.println(String.format("InstrumentId [%s]",instrumentId));
						}
						BigDecimal localCashFlowAmount = NumberUtil.newBigDecimal(cashflowTable.getDouble(CsvHeader.LOCAL_CASHFLOWS_AMOUNT.getName(), rowId));
						BigDecimal baseCashFlowAmount = NumberUtil.newBigDecimal(cashflowTable.getDouble(CsvHeader.BASE_CASHFLOWS_AMOUNT.getName(), rowId));
						cashflows.add(new Cashflow(instrumentLeg, date, currencyName, localCashFlowAmount, baseCashFlowAmount));
					}
				}
				getTimer().stop();
			} finally {
				PluginUtil.dispose(cashflowTable);
			}
		}
		return cashflows;
	}

	public Session getSession(){
		return session;
	}

	public final void extractToUserTable(String tableName) {
		throw new UnsupportedOperationException(String.format("User table extract is not supported for [%s]", getClass().getSimpleName()));
	}

}
