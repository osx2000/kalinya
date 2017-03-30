package com.kalinya.oc.util;

import com.olf.openjvs.DBaseTable;
import com.olf.openjvs.FileUtil;
import com.olf.openjvs.OConsole;
import com.olf.openjvs.OException;
import com.olf.openjvs.Str;
import com.olf.openjvs.Table;
import com.olf.openjvs.Tpm;
import com.olf.openjvs.enums.OLF_RETURN_CODE;

public class TpmWorkflow {
	private String definitionName = null;
	private Table variables = null;
	
	public TpmWorkflow() throws OException {
		OConsole.oprint("\nInitializing TpmWorkflow class");
	}
	
	public TpmWorkflow(String workflowName) throws OException {
		setDefinitionName(workflowName);

		if (workflowExists() == true) {
			createVariableTable();
		}
		else {
			String errorMessage = "ERROR: The workflow '" + workflowName + " does not exist or is not authorized";
			OConsole.oprint("\n" + errorMessage);
			throw new OException(errorMessage);
		}
	}

	/**
	 * Checks that the workflow exists
	 * @return
	 */
	private boolean workflowExists() throws OException {
		boolean retVal = false;
		Table table = Table.tableNew();
		String sql = "SELECT * "
				+ "\nFROM bpm_definition "
				+ "\nWHERE bpm_name = '" + this.definitionName + "'"
				+ "\n  AND bpm_auth_status = 1 "
				+ "\n  AND bpm_status = 1";

		table = execISql(sql);

		if(table.getNumRows() > 0) {
			retVal = true;
		}
		else {
			retVal = false;
		}

		table.destroy();

		return retVal;
	}

	/**
	 * Creates a table of variables
	 * @throws OException
	 */
	private void createVariableTable() throws OException {
		try {
			this.variables = Tpm.createVariableTable();
		}
		catch (OException oe) {
			OConsole.oprint("\nError setting TPM message variable: " + oe.getMessage());
			for(StackTraceElement eachMessage : oe.getStackTrace()) {
				OConsole.oprint("\n" + eachMessage.toString());
			}
		}
	}

	/**
	 * Starts the TPM workflow
	 * @return
	 * @throws OException
	 */
	public void startInstance() throws OException {
		if(Str.len(this.definitionName) > 0) {
			try {
				Tpm.startWorkflow(this.definitionName, variables);
			}
			catch(Exception e) {
				OConsole.oprint("\nERROR: Failed to start TPM instance: " + this.definitionName);
				OConsole.oprint("\nERROR: There is probably no TPM service available.");
				OConsole.oprint("\nERROR: Check the Services Manager.");
				OConsole.oprint("\n" + e.getMessage());
				for(StackTraceElement eachMessage : e.getStackTrace()) {
					OConsole.oprint("\n" + eachMessage.toString());
				}
			}
		}
		else {
			String errorMessage = "ERROR: Set the definition name by running the method " + this.getClass().getName() + ".setDefinitionName(String definitionName)";
			OConsole.oprint("\n" + errorMessage);
			throw new OException("error");
		}
	}

	/**
	 * Sets a TPM double variable
	 * @param variableName The name of the variable
	 * @param value The value to which the variable will be set
	 * @throws OException
	 */
	public void setTpmVariableAsDouble(String variableName, double value) throws OException {
		int newRow = this.variables.addRow();
		this.variables.setString("name", newRow, variableName);
		this.variables.setString("value", newRow, Str.doubleToStr(value));
		this.variables.setString("type", newRow, "Double");
	}

	/**
	 * Sets a TPM String variable
	 * @param variableName The name of the variable
	 * @param value The value to which the variable will be set
	 * @throws OException
	 */
	public void setTpmVariableAsString(String variableName, String value) throws OException {
		if(this.variables == null) {
			OConsole.oprint("\nERROR: The variables table is null");
		}

		int newRow = this.variables.addRow();
		this.variables.setString("name", newRow, variableName);
		this.variables.setString("value", newRow, value);
		this.variables.setString("type", newRow, "String");
		this.variables.setString("info", newRow, "");
	}

	/**
	 * Sets a TPM CustomList variable
	 * @param variableName The name of the variable
	 * @param value The value to which the variable will be set
	 * @throws OException
	 */
	public void setTpmVariableAsCustomList(String variableName, String value) throws OException {
		int newRow = this.variables.addRow();
		this.variables.setString("name", newRow, variableName);
		this.variables.setString("value", newRow, value);
		this.variables.setString("type", newRow, "CustomList");
	}

	/**
	 * Sets a TPM Table variable
	 * @param variableName The name of the variable
	 * @param value The table to which the variable will be set
	 * @throws OException
	 */
	public void setTpmVariableAsTable(String variableName, Table table) throws OException {
		if(Table.isTableValid(table) == 1) {
			int newRow = this.variables.addRow();
			this.variables.setString("name", newRow, variableName);
			this.variables.setTable("table", newRow, table);
			this.variables.setString("type", newRow, "ArgTable");
		}
		else {
			OConsole.oprint("\nERROR: The table passed in was not valid.  Failed to add table to the TPM workflow");
		}
	}

	/**
	 * Sets a TPM Table variable
	 * @param variableName The name of the variable
	 * @param value The table to which the variable will be set
	 * @throws OException
	 */
	public void setTpmVariableAsArgTable(String variableName, Table table) throws OException {
		if(Table.isTableValid(table) == 1) {
			Tpm.addArgTableToVariableTable(this.variables, variableName, table);
		}
		else {
			OConsole.oprint("\nERROR: The table passed in was not valid.  Failed to add table to the TPM workflow");
		}
	}
	
	/**
	 * Sets a TPM File variable
	 * @param variableName The name of the variable
	 * @param value The table to which the variable will be set
	 * @throws OException
	 */
	public void setTpmVariableAsFile(String variableName, String dirNodePath) throws OException {
		if(FileUtil.userFileExists(dirNodePath) == 1) {
			Tpm.addDirectoryFileToVariableTable(this.variables, variableName, dirNodePath);
		}
		else {
			OConsole.oprint("\nERROR: The file passed in was not valid.  Failed to add file [" + dirNodePath + "] to the TPM workflow");
		}
	}

	/**
	 * Sets a TPM String variable
	 * @param variableName The name of the variable
	 * @param value The value to which the variable will be set
	 * @throws OException
	 */
	public void setTpmVariableAsBoolean(String variableName, boolean value) throws OException {
		if(this.variables == null) {
			OConsole.oprint("\nERROR: The variables table is null");
		}
		
		String sValue = "No";
		if(value) {
			sValue = "Yes";
		}
		
		int newRow = this.variables.addRow();
		this.variables.setString("name", newRow, variableName);
		this.variables.setString("value", newRow, sValue);
		this.variables.setString("type", newRow, "Boolean");
		this.variables.setString("info", newRow, "");

	}
	/**
	 * Gets the definition name
	 * @return
	 */
	public String getDefinitionName() {
		return this.definitionName;
	}

	/**
	 * Sets the definition name
	 * @param definitionName
	 */
	public void setDefinitionName(String definitionName) {
		this.definitionName = definitionName;
	}

	/**
	 * Deallocates memory taken after loading the TPM variables table 
	 * @throws OException
	 */
	public void destroy() throws OException {
		if(Table.isTableValid(this.variables) == OLF_RETURN_CODE.OLF_RETURN_SUCCEED.toInt()) {
			this.variables.destroy();
		}
	}

	/**
	 * Safely runs a SQL command
	 * @param sql
	 * @return
	 * @throws OException
	 */
	private static Table execISql(String sql) throws OException {
		Table table = Table.tableNew();
		try {
			DBaseTable.execISql(table, sql);
		}
		catch(Exception e) {
			OConsole.oprint(e.getMessage());
			OConsole.oprint("\n" + sql + "\n");
			for(StackTraceElement eachMessage : e.getStackTrace()) {
				OConsole.oprint("\nERROR: " + eachMessage.toString());
			}
		}
		return table;
	}

	/**
	 * Returns the table of variables
	 * @return
	 * @throws OException
	 */
	public Table getVariables() throws OException {
		return this.variables;
	}
}

