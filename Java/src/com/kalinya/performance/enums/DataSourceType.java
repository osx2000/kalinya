package com.kalinya.performance.enums;

public enum DataSourceType {
	CSV(false), 
	FINDUR_PMM(true), 
	FINDUR_USER_TABLE(true), 
	FINDUR_ACS_DESKTOP(true);
	
	boolean requiresFindurSession;
	
	DataSourceType(boolean requiresFindurSession) {
		this.requiresFindurSession = requiresFindurSession;
	}
	
	public boolean requiresFindurSession() {
		return requiresFindurSession;
	}
}
