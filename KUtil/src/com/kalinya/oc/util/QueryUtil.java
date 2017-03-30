package com.kalinya.oc.util;

import com.kalinya.enums.FileExportFormat;
import com.kalinya.util.DateUtil;
import com.kalinya.util.PluginUtil;
import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.OException;
import com.olf.openjvs.OLog;
import com.olf.openrisk.application.Debug;
import com.olf.openrisk.application.EnumMessageSeverity;
import com.olf.openrisk.application.Session;
import com.olf.openrisk.control.EnumJobStatus;
import com.olf.openrisk.internal.OpenRiskException;
import com.olf.openrisk.io.Queries;
import com.olf.openrisk.io.Query;
import com.olf.openrisk.io.QueryResult;
import com.olf.openrisk.table.EnumColType;
import com.olf.openrisk.table.Table;

public class QueryUtil {
	/**
	 * The default query results table to use.  e.g. "query_result"
	 */
	private static final String DEFAULT_QUERY_RESULTS_TABLE = "query_result";
	/**
	 * Recipients to whom the notification will be sent
	 */
	private static final String RECIPIENT_EMAIL_ADDRESSES = "findurtech@convexitycapital.com";

	/**
	 * Executes the named query and returns an array of integer object ids
	 * (depending on the query type, the object ids could be tran_nums,
	 * sim_task_ids, trade_snapshot_ids, etc)
	 * 
	 * @param session
	 * @param queryName
	 * @return
	 */
	public static int[] getObjectIds(Session session, String queryName) {
		if(queryName.isEmpty()
				|| queryName.equalsIgnoreCase("None")) {
			throw new OpenRiskException("Query name [" + queryName + "] is not valid");
		}
		Debug debug = session.getDebug();
		Queries queries = null;
		Query query = null;
		QueryResult queryResult = null;

		//Get the query first. If it doesn't exist, throw an exception
		try {
			queries = session.getIOFactory().getQueries();
			query = queries.getQuery(queryName);
		} catch (Exception e) {
			debug.logError("Failed to retrieve query [" + queryName + "] ",
					EnumMessageSeverity.Error);
			debug.logError(e.getMessage(), EnumMessageSeverity.Error);
			PluginUtil.dispose(queries);
			PluginUtil.dispose(query);
			throw new OpenRiskException(e);
		}

		//Run the query, get the query result id and array of object ids
		int[] ids;
		try {
			queryResult = query.execute(true);
			int queryId = queryResult.getId();
			debug.logLine("Query ID [" + queryId + "] ");
			ids = queryResult.getObjectIdsAsInt();
		} catch (Exception e) {
			//If there is an exception thrown when the query is run, capture information and try another method
			debug.logError("Failed to execute query [" + queryName + "]. Trying JVS...",
					EnumMessageSeverity.Warning);
			debug.logError(e.getMessage(), EnumMessageSeverity.Warning);
			//Dump database information to file on first encounter of a problem, send notification email
			emailQueryException(session, queryName);
			PluginUtil.dumpToFile(session, FileExportFormat.CSV,
					"query_details-" + DateUtil.getServerTimeString(), null,
					new String[] { "query_result", "query_info",
			"query_info_history" });
			ids = getObjectIdsUsingJvs(session, queryName);
		} finally {
			PluginUtil.dispose(queries);
			PluginUtil.dispose(query);
			PluginUtil.dispose(queryResult);
		}

		return ids;
	}

	/**
	 * Returns the ID numbers of the objects using a JVS method
	 * 
	 * @return
	 */
	private static int[] getObjectIdsUsingJvs(Session session, String queryName) {
		Table table = null;
		int[] ids;
		try {
			int queryId = com.olf.openjvs.Query.run(queryName);
			String tableName = getResultTableUsingJvs(session, queryId);
			String sql = "SELECT * " 
					+ "\nFROM " + tableName
					+ "\nWHERE unique_id = " + queryId;
			session.getDebug().logLine(
					"com.olf.openjvs.Query.getResultTableForId(" + queryId
					+ ") [" + tableName + "] ");
			table = session.getIOFactory().runSQL(sql);
			ids = table.getColumnValuesAsInt("query_result");
		} catch (OException e) {
			session.getDebug().logError(
					"Failed to execute query [" + queryName
					+ "] using com.olf.openjvs.Query",
					EnumMessageSeverity.Error);
			session.getDebug().logError(e.getMessage(),
					EnumMessageSeverity.Error);
			throw new OpenRiskException(e);
		} finally {
			PluginUtil.dispose(table);
		}
		return ids;
	}

	/**
	 * Returns the query_result table reported by
	 * <code>com.olf.openjvs.Query.getResultTableForId(queryId)</code>
	 * 
	 * @param session
	 * @param queryId
	 * @return
	 */
	private static String getResultTableUsingJvs(Session session, int queryId) {
		String tableName = "";
		try {
			tableName = com.olf.openjvs.Query.getResultTableForId(queryId);
			if(tableName.isEmpty()) {
				tableName = DEFAULT_QUERY_RESULTS_TABLE;
			}
		} catch (OException e) {
			OLog.logError(0, e.getMessage());
			tableName = DEFAULT_QUERY_RESULTS_TABLE;
		}
		return tableName;
	}

	/**
	 * Clears the query id from the query_result% table
	 * @param queryId
	 */
	public static void clearQueryId(int queryId) {
		try {
			if(queryId > 0) {
				com.olf.openjvs.Query.clear(queryId);
			}
		} catch (OException e) {
			OLog.logError(0, "Failed to clear query ID [" + queryId + "]. Skipping.");
			OLog.logError(0, e.getMessage());
		}
	}

	/**
	 * Sends a notification that there was a problem executing a saved query
	 * @param session
	 * @param queryName
	 */
	private static void emailQueryException(Session session, String queryName) {
		EodEmail eodEmail = new EodEmail(
				session,
				"QueryHelper", 
				EnumJobStatus.Failed, 
				"<p>There was an exception encountered running a saved query<p>"
						+ "<li>Query Name [" + queryName + "]</li>"
						+ "<li>User Name [" + session.getUser().getAliasName() + "]<li></p>",
						RECIPIENT_EMAIL_ADDRESSES);
		eodEmail.send();
	}

	/**
	 * Returns the query_result table reported by
	 * <code>com.olf.openjvs.Query.getResultTableForId(queryId)</code>
	 * 
	 * @param session
	 * @param queryId
	 * @return
	 */
	public static String getResultTable(Session session, int queryId) {
		String tableName = "";
		try {
			tableName = com.olf.openjvs.Query.getResultTableForId(queryId);
			if(tableName.isEmpty()) {
				tableName = DEFAULT_QUERY_RESULTS_TABLE;
				session.getDebug().logError("Trying query_result table", EnumMessageSeverity.Warning);
			}
		} catch (Exception e) {
			session.getDebug().logError(e.getClass() + ": " + e.getMessage(), EnumMessageSeverity.Error);
			session.getDebug().logError("Trying query_result table", EnumMessageSeverity.Warning);
			tableName = DEFAULT_QUERY_RESULTS_TABLE;
		}
		return tableName;
	}

	/**
	 * From a given array of object ids, inserts the array of object ids into a
	 * query_result* table and returns the unique_id
	 * <p>
	 * The queryId must cleared using {@link #clearQueryId(int)}
	 * 
	 * @param session
	 *            The Findur session
	 * @param objectIds
	 *            An array of ids (e.g. tran_num, document_num,
	 *            acs_journal_entry_id)
	 * @return
	 */
	public static int createQueryIdFromObjectIds(Session session, int[] objectIds) {
		Table ocTable = null;
		com.olf.openjvs.Table jvsTable = null;
		try {
			ocTable = session.getTableFactory().createTable();
			ocTable.addColumn("id_number", EnumColType.Int);
			ocTable.addRows(objectIds.length);
			ocTable.setColumnValues("id_number", objectIds);
			jvsTable = session.getTableFactory().toOpenJvs(ocTable);
			int queryId = com.olf.openjvs.Query.tableQueryInsert(jvsTable, "id_number");
			return queryId;
		} catch (Exception e) {
			session.getDebug().logError(e.getClass() + ": " + e.getMessage(), EnumMessageSeverity.Error);
			throw new OpenRiskException(e);
		} finally {
			PluginUtil.dispose(ocTable);
			PluginUtil.destroy(jvsTable);
		}
	}

	/**
	 * Returns an array of object ids for the parameter queryId
	 * @param session
	 * @param queryId
	 * @return
	 */
	public static int[] getObjectIdsFromQueryId(Session session, int queryId) {
		if(queryId > 0) {
			String sql = "SELECT * " 
					+ "\nFROM " + getResultTable(session, queryId)
					+ "\nWHERE unique_id = " + queryId;
			Table table = null;
			try {
				table = session.getIOFactory().runSQL(sql);
				if(table != null && table.getRowCount() > 0) {
					return table.getColumnValuesAsInt("query_result");
				}
			} finally {
				PluginUtil.dispose(table);
			}
		}
		return new int[0];
	}

	/**
	 * Returns the number of records in the query result
	 * 
	 * @param queryId
	 * @return
	 * @throws OException
	 */
	public static int getQueryResultCount(int queryId) throws OException {
		String sql = "SELECT COUNT(*) FROM " + getResultTable(queryId) + " WHERE unique_id = " + queryId;
		com.olf.openjvs.Table table = com.olf.openjvs.Table.tableNew("QueryResultCount");
		try {
			DBaseTable.execISql(table, sql);
			if(TableUtil.isValidTable(table) && table.getNumRows() == 1) {
				return table.getInt(1,1);
			}
			return 0;
		} finally {
			PluginUtil.destroy(table);
		}
	}

	private static String getResultTable(int queryId) throws OException {
		return com.olf.openjvs.Query.getResultTableForId(queryId);
	}
}
