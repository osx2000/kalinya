package com.kalinya.enums;

public enum InstrumentReferenceType {
	REFERENCE("ab_tran", "reference"), 
	ISIN("header", "isin"), 
	TICKER("header", "ticker"), 
	CUSIP("header", "cusip"), 
	GRID_POINT_NAME("misc_ins", "market_px_gpt_name");

	private String databaseTableName;
	private String databaseFieldName;

	InstrumentReferenceType(String databaseTableName, String databaseFieldName) {
		this.databaseTableName = databaseTableName;
		this.databaseFieldName = databaseFieldName;
	}

	public String getDatabaseTableName() {
		return databaseTableName;
	}
	
	public String getDatabaseFieldName() {
		return databaseFieldName;
	}
	
	public String getFullyQualifiedDatabaseFieldName() {
		return getDatabaseTableName() + "." + getDatabaseFieldName();
	}
}
