package com.kalinya.performance.datasource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.kalinya.application.FindurSession;
import com.kalinya.enums.DebugLevel;
import com.kalinya.performance.BenchmarkAssociation;
import com.kalinya.performance.BenchmarkAssociations;
import com.kalinya.performance.Cashflow;
import com.kalinya.performance.Cashflows;
import com.kalinya.performance.Instrument;
import com.kalinya.performance.InstrumentLeg;
import com.kalinya.performance.Portfolio;
import com.kalinya.performance.Portfolios;
import com.kalinya.performance.Position;
import com.kalinya.performance.Positions;
import com.kalinya.performance.SecurityMaster;
import com.kalinya.performance.SecurityMasters;
import com.kalinya.performance.enums.CsvHeader;
import com.kalinya.performance.enums.DataSourceType;
import com.kalinya.performance.enums.InstrumentClass;
import com.kalinya.performance.enums.SecurityMasterEnum;
import com.kalinya.util.Assertions;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.PluginUtil;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.table.Table;
import com.olf.openrisk.trading.EnumTranStatus;

final public class FindurPmmDataSource extends DataSource {
	private static final long serialVersionUID = 6456116169344992812L;
	private static String DEBUG_INSTRUMENT_ID = "SMHL 13-1 A RMBS +95";
	private Session session;

	public FindurPmmDataSource(Builder builder) {
		super(builder);
		this.session = builder.session;
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

	public static class Builder extends DataSource.Builder<Builder> {
		private Session session;

		private Builder() {
			//disable default ctor
		}
		
		public Builder(FindurSession findurSession) {
			this();
			this.session = findurSession.getSession();
		}

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
	public DataSourceType getDataSourceType() {
		return DataSourceType.FINDUR_PMM;
	}
	
	@Override
	public void retrievePortfolios() {
		if(portfolios == null) {
			getTimer().start("GetPortfolios");
			portfolios = Portfolios.create();
			Table table = null;
			try {
				StringBuilder sql = new StringBuilder();
				sql.append(String.format("SELECT p.id_number, p.name '%s', pt.name '%s'",
						CsvHeader.PORTFOLIO.getName(), CsvHeader.PORTFOLIO_GROUP.getName()));
				sql.append("\nFROM perf_portfolio pp");
				sql.append("\nJOIN portfolio p ON p.id_number = pp.portfolio_id"); 
				sql.append("\nLEFT JOIN portfolio_type pt ON pt.id_number = p.portfolio_type");
				table = getSession().getIOFactory().runSQL(sql.toString());
				if(table != null) {
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
	}

	private Table getCashflowTableDataFromFindurPmm(Portfolios portfolios) {
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
		if(portfolios.size() > 0) {
			sql.append(String.format("\n AND p.name IN (%s)", getPortfoliosFilterAsString()));
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
		if(portfolios.size() > 0) {
			sql.append(String.format("\n AND p.name IN (%s)", getPortfoliosFilterAsString()));
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
		if(getPortfoliosFilter().size() > 0) {
			sql.append(String.format("\n AND p.name IN (%s)", getPortfoliosFilterAsString()));
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
	public void retrieveBenchmarkAssociations() {
		if(benchmarkAssociations == null) {
			getTimer().start("GetBenchmarkAssociations");
			benchmarkAssociations = new BenchmarkAssociations();
			Table table = null;
			try {
				StringBuilder sql = new StringBuilder();
				//TODO: hit the database to get the benchmark associations
				sql.append(String.format("SELECT p.name '%s', bmk.name '%s'", 
						CsvHeader.PORTFOLIO.getName(), CsvHeader.BENCHMARK.getName()));
				sql.append("\nFROM perf_portfolio_benchmark ppb");
				sql.append("\nJOIN portfolio p ON p.id_number = ppb.portfolio_id");
				sql.append("\nJOIN perf_sec_defn psd ON psd.security_id = ppb.benchmark_id");
				sql.append("\nJOIN portfolio bmk ON bmk.id_number = psd.model_pfolio_id");
				sql.append("\nWHERE ppb.primary_bench = 1");
				table = getSession().getIOFactory().runSQL(sql.toString());
				if(table != null) {
					int rowCount = table.getRowCount();
					for(int rowId = 0; rowId < rowCount; rowId++) {
						String portfolioName = table.getString(CsvHeader.PORTFOLIO.getName(), rowId);
						String benchmarkName = table.getString(CsvHeader.BENCHMARK.getName(), rowId);
						Portfolio portfolio = portfolios.get(portfolioName);
						Portfolio benchmark = portfolios.get(benchmarkName);
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
	}

	@Override
	public void retrieveSecurityMasterData() {
		if(securityMasterData == null) {
			getTimer().start("GetSecurityMasterData");
			securityMasterData = new SecurityMasters();
			Map<Integer, String> instrumentIds = getInstrumentIdsFromFindur();
			Map<Integer, SecurityMasterEnum> instrumentClasses = getSecurityMasterDataFromFindur(InstrumentClass.class);

			for(int insNum: instrumentIds.keySet()) {
				String instrumentId = instrumentIds.get(insNum);
				InstrumentClass instrumentClass = (InstrumentClass) instrumentClasses.get(insNum);
				SecurityMaster securityMaster = new SecurityMaster(instrumentId, null, null, null,
						null, instrumentClass, null, null, null);
				if(getDebugLevel().atLeast(DebugLevel.HIGH)) {
					System.out.println(String.format("InsNum [%s] InstrumentId [%s] SecurityMaster [%s]", 
							insNum, instrumentId, securityMaster.toString()));
				}
				securityMasterData.add(securityMaster);
			}
			getTimer().stop();
		}
	}

	private Map<Integer, String> getInstrumentIdsFromFindur() {
		Map<Integer, String> map = new HashMap<>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT ab.ins_num 'key', psd.security_name 'value'");
		sql.append("\nFROM perf_sec_defn psd");
		sql.append("\nJOIN ab_tran ab ON ab.deal_tracking_num = psd.deal_tracking_num");
		sql.append(String.format("\n AND ab.tran_status IN (%s,%s)", 
				EnumTranStatus.Validated.getValue(), EnumTranStatus.Matured.getValue()));

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
		sb.append(String.format("\nWHERE ab.tran_status IN (%s,%s)", 
				EnumTranStatus.Validated.getValue(), EnumTranStatus.Matured.getValue()));
		return sb.toString();
	}

	@Override
	public void retrievePositions() {
		if(positions == null) {
			getTimer().start("RetrievePositions");
			positions = new Positions();
			int insNum = -1;
			Table positionsTable = null;
			try {
				positionsTable = getPositionTableDataFromFindurPmm();
				if(positionsTable != null) {
					int rowCount = 4159;//positionsTable.getRowCount();
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
						//BigDecimal localAmount = NumberUtil.newBigDecimal("0");

						Portfolio portfolio = portfolios.get(portfolioName);
						Instrument instrument = instruments.getInstrument(instrumentId, false);
						//TODO: there is circularity here
						InstrumentLeg instrumentLeg = instrumentLegs.getInstrumentLeg(portfolio, instrument, legId, false);
						if(instrumentLeg == null) {
							instrumentLeg = new InstrumentLeg(portfolio, instrument, legId, currencyName);
							instrumentLegs.add(instrumentLeg);
						}
						//Cashflows cashflows = retrieveCashflows().getCashflows(date, instrumentLeg);
						//TODO: might need a method to inject Cashflows into the Positions
						//Position position = new Position(instrumentLeg, date, marketValue, baseMarketValue, cashflows);
						Position position = new Position(instrumentLeg, date, marketValue, baseMarketValue);
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
	}

	@Override
	public void retrieveCashflows() {
		if(cashflows == null) {
			getTimer().start("GetCashflows");
			Table cashflowTable = null;
			try {
				cashflowTable = getCashflowTableDataFromFindurPmm(portfolios);
				Cashflows cashflows = new Cashflows();
				if(cashflowTable != null) {
					int rowCount = cashflowTable.getRowCount();
					for(int rowId = 0; rowId < rowCount; rowId++) {
						Date date = cashflowTable.getDate(CsvHeader.DATE.getName(), rowId);
						String currencyName = cashflowTable.getString(CsvHeader.CURRENCY.getName(), rowId);
						String portfolioName = cashflowTable.getString(CsvHeader.PORTFOLIO.getName(), rowId);
						String instrumentId = cashflowTable.getString(CsvHeader.INSTRUMENT_ID.getName(), rowId);
						int legId = cashflowTable.getInt(CsvHeader.LEG_ID.getName(), rowId);
						Portfolio portfolio = portfolios.get(portfolioName);
						Instrument instrument = instruments.getInstrument(instrumentId, false);
						InstrumentLeg instrumentLeg = instrumentLegs.getInstrumentLeg(portfolio, instrument, legId);
						//InstrumentLeg instrumentLeg = new InstrumentLeg(portfolio, instrument, legId, currencyName);
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
	}

	public Session getSession(){
		return session;
	}

	public final void extractToUserTable(String tableName) {
		throw new UnsupportedOperationException(String.format("User table extract is not supported for [%s]", getClass().getSimpleName()));
	}

}
