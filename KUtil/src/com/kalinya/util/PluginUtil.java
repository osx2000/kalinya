package com.kalinya.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kalinya.enums.FileExportFormat;
import com.kalinya.oc.util.TableUtil;
import com.olf.openjvs.ODateTime;
import com.olf.openjvs.OException;
import com.olf.openjvs.Ref;
import com.olf.openjvs.Util;
import com.olf.openjvs.enums.DEAL_COMMENTS_TYPE;
import com.olf.openjvs.enums.OLF_RETURN_CODE;
import com.olf.openjvs.enums.SHM_USR_TABLES_ENUM;
import com.olf.openrisk.application.EnumMessageSeverity;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.calendar.HolidaySchedule;
import com.olf.openrisk.calendar.HolidaySchedules;
import com.olf.openrisk.internal.OpenRiskException;
import com.olf.openrisk.io.EnumFilePermissions;
import com.olf.openrisk.io.QueryResult;
import com.olf.openrisk.market.Market;
import com.olf.openrisk.simulation.EnumResultType;
import com.olf.openrisk.simulation.ResultType;
import com.olf.openrisk.simulation.ResultTypes;
import com.olf.openrisk.simulation.RevalResults;
import com.olf.openrisk.simulation.RevalSession;
import com.olf.openrisk.simulation.Scenario;
import com.olf.openrisk.simulation.SimulationFactory;
import com.olf.openrisk.staticdata.Currency;
import com.olf.openrisk.staticdata.EnumReferenceObject;
import com.olf.openrisk.staticdata.Portfolio;
import com.olf.openrisk.staticdata.ReferenceObject;
import com.olf.openrisk.staticdata.StaticDataFactory;
import com.olf.openrisk.table.EnumColType;
import com.olf.openrisk.table.Table;
import com.olf.openrisk.trading.Comment;
import com.olf.openrisk.trading.Comments;
import com.olf.openrisk.trading.EnumCommentFieldId;
import com.olf.openrisk.trading.EnumFixedFloat;
import com.olf.openrisk.trading.EnumInsType;
import com.olf.openrisk.trading.EnumLegFieldId;
import com.olf.openrisk.trading.EnumToolset;
import com.olf.openrisk.trading.EnumTranStatus;
import com.olf.openrisk.trading.EnumTransactionFieldId;
import com.olf.openrisk.trading.Instrument;
import com.olf.openrisk.trading.InstrumentType;
import com.olf.openrisk.trading.Leg;
import com.olf.openrisk.trading.Transaction;
import com.olf.openrisk.trading.Transactions;
import com.olf.openrisk.utility.Disposable;
import com.olf.openrisk.utility.EnumRefBase;

public class PluginUtil {

	public static void close(Closeable closeable) {
		try {
			if(closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Disposes a disposable object
	 * 
	 * @param disposable
	 */
	public static void dispose(Disposable disposable) {
		if(disposable != null) {
			disposable.dispose();
		}
	}

	/**
	 * Returns true if the transaction is an inflation linked bond
	 * @param transaction
	 * @return
	 */
	public static boolean isIndexedBond(Transaction transaction) {
		InstrumentType insType = null;
		InstrumentType baseInsType = null;
		try {
			insType = transaction.getInstrumentTypeObject();
			baseInsType = insType.getBaseInstrumentType();
			return isIndexedBond(baseInsType.getInstrumentTypeEnum());
		} finally {
			insType = null;
			baseInsType = null;
		}
	}

	/**
	 * Returns true if the instrument is an inflation linked bond
	 * @param instrument
	 * @return
	 */
	public static boolean isIndexedBond(Instrument instrument) {
		InstrumentType insType = null;
		InstrumentType baseInsType = null;
		try {
			insType = instrument.getInstrumentTypeObject();
			baseInsType = insType.getBaseInstrumentType();
			return isIndexedBond(baseInsType.getInstrumentTypeEnum());
		} finally {
			insType = null;
			baseInsType = null;
		}
	}

	/**
	 * Returns true if the instrument is an inflation linked bond
	 * @param insType
	 * @return
	 */
	public static boolean isIndexedBond(EnumInsType insType) {
		if(insType == EnumInsType.BondRefIndexLinked //IDX-BOND
				|| insType == EnumInsType.BondInflationLinkedAmort){ //IBOND
			return true;
		}
		return false;
	}

	/**
	 * Returns an array of toolset ids
	 * 
	 * @return
	 */
	public static Integer[] getFuturesToolsets() {
		return new Integer[] { EnumToolset.Bondfut.getValue(),
				EnumToolset.RateFut.getValue(), EnumToolset.FinFut.getValue(),
				EnumToolset.FinEto.getValue(), EnumToolset.ComOptFut.getValue()};
	}

	/**
	 * Returns true if the transaction is a conventional option (i.e. it is
	 * setup not to be margined daily)
	 * 
	 * @param transaction
	 * @return
	 */
	public static boolean isConventionalOption(Transaction transaction) {
		boolean result = false;
		if(transaction.getToolset() == EnumToolset.ComOptFut
				|| transaction.getToolset() == EnumToolset.FinEto) {
			Instrument instrument = transaction.getInstrument();
			result = isConventionalOption(instrument);
			PluginUtil.dispose(instrument);
		}
		return result;
	}

	/**
	 * Returns true if the instrument is a conventional option (i.e. it is setup
	 * not to be margined daily)
	 * 
	 * @param instrument
	 * @return
	 */
	public static boolean isConventionalOption(Instrument instrument) {
		boolean result = false;
		if(instrument.getToolset() == EnumToolset.ComOptFut
				|| instrument.getToolset() == EnumToolset.FinEto) {
			Leg leg = instrument.getLeg(0);
			if(leg.getValueAsString(EnumLegFieldId.MarginType).equalsIgnoreCase("None")) {
				result = true;
			}
			PluginUtil.dispose(leg);
		}
		return result;
	}

	/**
	 * Gets the current date from the Market object
	 * 
	 * @param session
	 * @return
	 */
	public static Date getCurrentDate(Session session) {
		Date currentDate = null;
		Market market = null;
		try {
			market = session.getMarket();
			currentDate = market.getCurrentDate();
		} finally {
			dispose(market);
		}
		return currentDate;
	}

	/**
	 * Runs a reval to calculate dependent sim results
	 * 
	 * @param transaction
	 * @param resultTypes
	 * @return
	 */
	public static RevalResults calculateResults(Session session, Transaction transaction,
			ResultType[] resultTypes) {
		Transactions transactions = null;
		Scenario childScenario = null;
		RevalSession childRevalSession = null;
		RevalResults revalResults = null;
		try {
			SimulationFactory simulationFactory = session.getSimulationFactory();

			//Create <code>Transactions</code> collection and add this <code>Transaction</code>
			transactions = session.getTradingFactory().createTransactions();
			transactions.add(transaction);

			//Setup the reval within this sim
			childRevalSession = simulationFactory.createRevalSession(transactions);
			ResultTypes childResultTypes = simulationFactory.createResultTypes();
			childResultTypes.add(resultTypes);

			childScenario = simulationFactory.createScenario("New Scenario");
			childRevalSession.setScenario(childScenario);

			//Run the reval
			revalResults = childRevalSession.calcResults(childResultTypes);
		} finally {
			dispose(transactions);
			dispose(childScenario);
			dispose(childRevalSession);
		}
		return revalResults;
	}

	/**
	 * Clears the result set from the database
	 * 
	 * @param queryResult
	 */
	public static void clearQuery(QueryResult queryResult) {
		if(queryResult != null
				&& queryResult.getId() > 0) {
			queryResult.clear();
		}
	}

	/**
	 * Deallocates the memory used by an ODateTime object
	 * 
	 * @param dt
	 * @throws OException
	 */
	public static void destroy(ODateTime dt) {
		try {
			if (dt != null) {
				dt.destroy();
			}
		} catch (OException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deallocates memory held by a JVS table
	 * 
	 * @param jvsTable
	 */
	public static void destroy(com.olf.openjvs.Table jvsTable) {
		try {
			if(TableUtil.isValidTable(jvsTable)) {
				jvsTable.destroy();
			}
		} catch (OException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deallocates memory held by a JVS transaction
	 * @param jvsTransaction
	 */
	public static void destroy(com.olf.openjvs.Transaction jvsTransaction) {
		try {
			if(jvsTransaction != null) {
				jvsTransaction.destroy();
			} 
		} catch (OException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Saves an array of Table objects and database tables into CSV/Excel
	 * format. 
	 * <li>When saving to Excel, there will be a separate worksheet for each table
	 * name/database table name. If there is a duplicate table/database table
	 * name, the first one written will be overwritten.</li> 
	 * <li>When saving to CSV, there will be a separate file for each table. 
	 * The name of the table will be appended to the base filename parameter.</li>
	 * 
	 * @param session
	 *            The Findur session
	 * @param fileExportFormat
	 *            The export format, CSV or Excel
	 * @param filename
	 *            The base filename, after which a tablename and file extension
	 *            will be added
	 * @param tables
	 *            An array of openrisk Tables
	 * @param dbTableNames
	 *            An array of database table name Strings
	 */
	public static void dumpToFile(Session session, FileExportFormat fileExportFormat,
			String filename, Table[] tables, String[] dbTableNames) {
		try {
			if(dbTableNames != null && dbTableNames.length > 0) {
				for(String dbTableName: dbTableNames) {
					Table table = null;
					try {
						table = session.getIOFactory().runSQL("SELECT * FROM " + dbTableName);
						table.setName(dbTableName);
						exportTable(session, table, filename, fileExportFormat);
					} finally {
						dispose(table);
					}
				}
			}
			if(tables != null && tables.length > 0) {
				for(Table table: tables) {
					exportTable(session, table, filename, fileExportFormat);
				}
			}
		} catch (Exception e) {
			session.getDebug().logError(e.getClass() + ": " + e.getMessage(), EnumMessageSeverity.Error);
			throw new OpenRiskException(e);
		}
	}

	/**
	 * Exports the table in to CSV/Excel
	 * @param session
	 * @param table
	 * @param filePath
	 * @param fileExportFormat
	 */
	public static void exportTable(Session session, Table table,
			String filename, FileExportFormat fileExportFormat) {
		com.olf.openjvs.Table jvsTable = null;
		try {
			String filePath = Util.reportGetDirForToday() + "\\" 
					+ filename
					+ (fileExportFormat == FileExportFormat.CSV ? "-" + table.getName() : "")
					+ fileExportFormat.getFileExtension();
			session.getDebug().logLine(
					"Exporting to format [" + fileExportFormat.toString()
					+ "], Path [" + filePath + "]");
			switch (fileExportFormat) {
			case EXCEL:
				jvsTable = session.getTableFactory().toOpenJvs(table);
				int retVal = jvsTable.excelSave(filePath, table.getName());
				if(retVal != OLF_RETURN_CODE.OLF_RETURN_SUCCEED.toInt()) {
					throw new OpenRiskException("Failed to export to format ["
							+ fileExportFormat.toString() + "], Path ["
							+ filePath + "]");
				}
				break;
			case CSV:
				table.exportCsv(filePath, true);
				break;
			default:
				throw new RuntimeException("Unsupported export format [" + fileExportFormat.toString() + "]");
			}
		} catch (Exception e) {
			session.getDebug().logError(e.getClass() + ": " + e.getMessage(), EnumMessageSeverity.Error);
			throw new OpenRiskException(e);
		} finally {
			destroy(jvsTable);
		}
	}

	/**
	 * Tests that all HolidaySchedule objects in hols1 are contained in hols2 and vice versa
	 * @param hols1
	 * @param hols2
	 * @return
	 */
	public static boolean holidaySchedulesMatch(HolidaySchedules hols1, HolidaySchedules hols2) {
		int hols1Count = hols1.getCount();
		int hols2Count = hols2.getCount();
		if(hols1Count != hols2Count) {
			return false;
		}
		for(int i = 0; i < hols1Count; i++) {
			HolidaySchedule hol1 = hols1.getSchedule(i);
			if(!hols2.containsSchedule(hol1)) {
				return false;
			}
		}
		for(int j = 0; j < hols2Count; j++) {
			HolidaySchedule hol2 = hols2.getSchedule(j);
			if(!hols1.containsSchedule(hol2)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Adds a deal comment to the transaction with the specified comment type, start date and comment string
	 * @param transaction
	 * @param comment
	 * @throws OException
	 */
	public static void addDealComment(Transaction transaction,
			String dealCommentType, Date startDate, String dealCommentMessage) {
		Comments comments = transaction.getComments();
		Comment comment = comments.addItem();
		if(dealCommentType == null)  {
			comment.setValue(EnumCommentFieldId.Type, getDefaultDealCommentType());
		} else {
			comment.setValue(EnumCommentFieldId.Type, dealCommentType);
		}
		if (startDate != null) {
			comment.setValue(EnumCommentFieldId.StartDate, startDate);
		}
		comment.setValue(EnumCommentFieldId.Comments, dealCommentMessage);
	}

	/**
	 * Returns the system-default deal comment type
	 * @return
	 */
	private static String getDefaultDealCommentType() {
		try {
			String defaultCommentType;
			defaultCommentType = Ref.getName(
					SHM_USR_TABLES_ENUM.DEAL_COMMENTS_TYPE_TABLE,
					DEAL_COMMENTS_TYPE.DEAL_COMMENTS_COMMENT.toInt());
			return defaultCommentType;
		} catch (Exception e) {
			throw new OpenRiskException(e);
		}
	}

	/**
	 * Returns the Currency reference object
	 * @param session
	 * @param applicableLegValueAsString
	 * @return
	 */
	public static Currency getCurrency(Session session, String name) {
		return (Currency) session.getStaticDataFactory().getReferenceObject(EnumReferenceObject.Currency, name) ;
	}

	/**
	 * Sets permissions to read and execute
	 */
	public static int securityReadAndExecuteAccess() {
		return EnumFilePermissions.Readable.getValue() 
				+ EnumFilePermissions.Executable.getValue();
	}

	/**
	 * Sets permissions to read, write and execute
	 */
	public static int securityReadWriteExecuteAccess() {
		return EnumFilePermissions.Readable.getValue() 
				+ EnumFilePermissions.Writable.getValue() 
				+ EnumFilePermissions.Executable.getValue();
	}

	/**
	 * Disposes a Collection of Disposables 
	 * @param disposables
	 */
	public static void disposeCollection(Collection<? extends Disposable> disposables) {
		for(Disposable disposable: disposables) {
			disposable.dispose();
		}
	}

	/**
	 * Disposes a map of Disposable values
	 * @param map
	 */
	public static <K> void disposeMapValues(Map<K, ? extends Disposable> map) {
		if(map != null) {
			for (K key : map.keySet()) {
				dispose(map.get(key));
			}
		}
	}

	/**
	 * Disposes the Disposable key Set of a Map
	 * @param map
	 */
	public static <V> void disposeMapKey(Map<? extends Disposable, V> map) {
		if(map != null) {
			disposeCollection(map.keySet());
		}
	}

	/**
	 * Returns a List of the field values for the collection of transactions
	 * @param transactions
	 * @param transactionFieldId
	 * @return
	 */
	public static List<String> getFieldList(Transactions transactions, EnumTransactionFieldId transactionFieldId) {
		List<String> list = new ArrayList<String>();
		for(Transaction transaction: transactions) {
			list.add(transaction.getValueAsString(transactionFieldId));
		}
		return list;
	}

	/**
	 * For the parameter {@code Transactions}, returns the {@code Set} of
	 * distinct {@code Portfolio} objects
	 * 
	 * @param session
	 * @param transactions
	 * @return
	 */
	public static Set<Portfolio> getPortfolios(Session session, Transactions transactions) {
		Set<Portfolio> portfolios = new HashSet<Portfolio>();
		StaticDataFactory sdf = session.getStaticDataFactory();
		for(Transaction transaction: transactions) {
			Portfolio portfolio = sdf.getReferenceObject(Portfolio.class, transaction.getValueAsInt(EnumTransactionFieldId.InternalPortfolio));
			portfolios.add(portfolio);
		}
		return portfolios;
	}

	/**
	 * Checks for the value of the TranListing result to verify that results
	 * exist for the deal. Note that it assumes that the TranListing result is
	 * part of the result set.  It is a dependent result for a lot of results
	 * <p>
	 * It is possible that the RevalResults.getResultAsDouble(..) methods should
	 * throw an API if there are missing results for a deal. This will be
	 * submitted to Support under SR#[]
	 * 
	 * @param revalResults
	 * @param dealNum
	 */
	public static void resultsExistForDealNum(RevalResults revalResults,
			int dealNum) {
		if(revalResults != null) {
			if(Math.abs(revalResults.getResultAsDouble(dealNum, EnumResultType.TranListing)) < 1.0) {
				throw new OpenRiskException("Failed to retrieve sim results for DealNum [" + dealNum + "]");
			}
		}
	}

	/**
	 * Returns true if the leg is a fixed rate leg
	 * 
	 * @param leg
	 * @return
	 */
	public static boolean isFixedLeg(Leg leg) {
		if(leg.isApplicable(EnumLegFieldId.FixFloat)) {
			return leg.getValueAsInt(EnumLegFieldId.FixFloat) == EnumFixedFloat.FixedRate.getValue();
		}
		return false;
	}

	/**
	 * Returns true if the leg is a floating rate leg
	 * 
	 * @param leg
	 * @return
	 */
	public static boolean isFloatingLeg(Leg leg) {
		if(leg.isApplicable(EnumLegFieldId.FixFloat)) {
			return leg.getValueAsInt(EnumLegFieldId.FixFloat) == EnumFixedFloat.FloatRate.getValue();
		}
		return false;
	}

	/**
	 * Returns a string to print a consistent representation across accounting rules of the deal details
	 * @param transaction
	 * @return
	 */
	public static String getTransactionDetails(Transaction transaction) {
		return "Ref [" + transaction.getValueAsString(EnumTransactionFieldId.ReferenceString) 
				+ "], DealNum [" + transaction.getDealTrackingId() 
				+ "], TranNum [" + transaction.getTransactionId() + "]";
	}

	/**
	 * Returns the integer representation of tran_status of Validated, Matured, Closeout.  The values will be comma-separated.
	 * <p>Resolves to 3,4,22
	 * @return
	 */
	public static String getTranStatusesVMC() {
		return EnumTranStatus.Validated.getValue() + "," + EnumTranStatus.Matured.getValue() + "," + EnumTranStatus.Closeout.getValue();
	}

	/**
	 * Returns a set of reference object integers
	 * 
	 * @param referenceObjects
	 * @return
	 */
	public static Set<Integer> getReferenceObjectIds(Set<? extends ReferenceObject> referenceObjects) {
		Set<Integer> referenceObjectIds = new HashSet<Integer>();
		if(referenceObjects != null) {
			for(ReferenceObject referenceObject: referenceObjects) {
				referenceObjectIds.add(Integer.valueOf(referenceObject.getId()));
			}
		}
		return referenceObjectIds;
	}

	public static Object castStringToType(String s, EnumColType colType) {
		switch(colType) {
		case Date:
		case DateTime:
			return DateUtil.parseDate(s);
		case Double:
			return NumberUtil.newBigDecimal(s).doubleValue();
		case Int:
		case UnsignedInt:
			return Integer.valueOf(s);
		case Long:
		case UnsignedLong:
			return NumberUtil.newBigDecimal(s).longValue();
		case String:
			return s;
		default:
			throw new UnsupportedOperationException(String.format(
					"Unsupported column type to cast [%s]",
					colType.toString()));
		}
	}

	/**
	 * Returns a string of the enum ID values separated by the delimiter
	 * 
	 * @param enums
	 * @param delimiter
	 * @return
	 */
	public static String joinEnumIds(EnumRefBase[] enums, String delimiter) {
		int[] ints = new int[enums.length];
		int i = 0;
		for(EnumRefBase e: enums) {
			ints[i] = e.getValue();
			i++;
		}
		return StringUtil.join(ints, delimiter);
	}

}