package com.kalinya.oc.util;

import java.util.Date;

import com.kalinya.util.PluginUtil;
import com.olf.openjvs.OException;
import com.olf.openjvs.Table;
import com.olf.openjvs.Tpm;
import com.olf.openrisk.calendar.CalendarFactory;
import com.olf.openrisk.internal.OpenRiskException;
import com.olf.openrisk.staticdata.EnumReferenceTable;
import com.olf.openrisk.staticdata.StaticDataFactory;
import com.olf.openrisk.table.ConstTable;

public class TpmUtil {
	/**
	 * The name of the TPM variable that has the EOD workflow run date
	 * e.g. "RunDate"
	 */
	public static final String RUN_DATE_PARAMETER_NAME = "RunDate";

	/**
	 * Helper method that can be run from a plugin in a TPM step that returns the TPM instance id
	 * @param argt
	 * @return
	 */
	public static long getTpmProcessId(ConstTable argt) {
		return argt.getLong("ProcessWorkflowId", 0);
	}

	/**
	 * Returns <code>true</code> if if any one workflow in the list of
	 * <code>workflowNamesToCheck</code> is running. Excludes
	 * <code>thisWorkflowName</code> from the test because this method can be
	 * called by a running TPM workflow
	 * @param messageLog
	 * @param thisWorkflowName
	 * @param workflowNamesToCheck
	 * @return
	 */
	public static boolean isRunning(MessageLog messageLog, String thisWorkflowName, String[] workflowNamesToCheck) {
		Table runningWorkflows = null;
		int runningWorkflowCount = 0;
		try {
			runningWorkflows = Tpm.getWorkflowStatusAll();
			runningWorkflowCount = runningWorkflows.getNumRows();
		} catch (OException e) {
			throw new OpenRiskException(e);
		}
		try {
			for(int runningWorkflowId = 1; runningWorkflowId <= runningWorkflowCount; runningWorkflowId++) {
				Table processDefinition = runningWorkflows.getTable("ProcessDefinition", runningWorkflowId);
				String workflowName = processDefinition.getString("name", 1);
				if(workflowName.equalsIgnoreCase(thisWorkflowName)) {
					//We know thisWorkflowName is running
					continue;
				}
				for(String workflowNameToCheck: workflowNamesToCheck) {
					if(workflowName.equalsIgnoreCase(workflowNameToCheck)) {
						if(messageLog != null) {
							//Print the details of the running workflow
							messageLog.info("Workflow [" + workflowName + "] is already running");
							messageLog.printTable(processDefinition.getTable("ProcessInstance", 1));
						}
						return true;
					}
				}
			}
		} catch (OException e) {
			throw new OpenRiskException(e);
		}
		return false;
	}

	/**
	 * Helper method that can be run from a plugin in a TPM step that returns the name of the running workflow
	 * the name of the running workflow
	 * @param argt
	 * @return
	 */
	public static String getThisWorkflowName(StaticDataFactory sdf, ConstTable argt) {
		int tpmDefinitionId = argt.getInt("TPMDefinitionId", 0);
		return sdf.getName(EnumReferenceTable.TpmDefinition, tpmDefinitionId);
	}

	/**
	 * Returns the Run Date from the TPM Step arguments table, and converts the date to a Julian date integer
	 * @param cf
	 * @param arguments
	 * @return
	 */
	public static Date getRunDateFromArgt(CalendarFactory cf, ConstTable arguments) {
		Date runDate = null;
		int runDateJd = -1;
		try {
			runDateJd = arguments.getInt(RUN_DATE_PARAMETER_NAME, 0);
			runDate = cf.getDate(runDateJd);
		} catch (OpenRiskException e) {
			throw new OpenRiskException("Failed to retrieve run data parameter [" + RUN_DATE_PARAMETER_NAME + "] from TPM workflow\n" + e.getMessage(), e);
		}

		return runDate;
	}
	
	/**
	 * Returns the variable from the argument table as a String 
	 * @param argt
	 * @return
	 */
	public static String getTpmVariableAsString(ConstTable argt, String variableName) {
		String value = null;
		try {
			value = argt.getString(variableName, 0);
		} catch (OpenRiskException e) {
			throw new OpenRiskException("Failed to retrieve String parameter [" + variableName + "] from TPM workflow\n" + e.getMessage(), e);
		}

		return value;
	}
	
	/**
	 * Returns the variable from the argument table as an integer
	 * @param argt
	 * @return
	 */
	public static int getTpmVariableAsInt(ConstTable argt, String variableName) {
		int value;
		try {
			value = argt.getInt(variableName, 0);
		} catch (OpenRiskException e) {
			throw new OpenRiskException("Failed to retrieve integer parameter [" + variableName + "] from TPM workflow\n" + e.getMessage(), e);
		}

		return value;
	}
	
	/**
	 * Returns the variable from the argument table as a long
	 * @param argt
	 * @param variableName
	 * @return
	 */
	public static long getTpmVariableAsLong(ConstTable argt,
			String variableName) {
		long value;
		try {
			value = argt.getLong(variableName, 0);
		} catch (OpenRiskException e) {
			throw new OpenRiskException("Failed to retrieve long parameter [" + variableName + "] from TPM workflow\n" + e.getMessage(), e);
		}

		return value;
	}
	
	/**
	 * Returns the variable from the argument table 
	 * @param argt
	 * @return
	 */
	public static String getTpmVariableFromMetaData(ConstTable argt, String variableName) {
		String value = null;
		ConstTable variablesMetaData = null;
		ConstTable variables = null;
		try {
			variablesMetaData = argt.getTable("VariablesMetaData", 0);
			variables = variablesMetaData.getTable("variable", 0);
			int rowId = variables.find(variables.getColumnId("name"), variableName, 0);
			value = argt.getString(variableName, rowId);
		} catch (OpenRiskException e) {
			throw new OpenRiskException("Failed to retrieve parameter [" + variableName + "] from variables metadata\n" + e.getMessage(), e);
		} finally {
			PluginUtil.dispose(variablesMetaData);
			PluginUtil.dispose(variables);
		}

		return value;
	}
}

