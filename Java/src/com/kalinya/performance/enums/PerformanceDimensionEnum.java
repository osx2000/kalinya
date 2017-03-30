package com.kalinya.performance.enums;

import java.util.Map;

import com.olf.openrisk.application.Session;

public interface PerformanceDimensionEnum {
	public Map<String, PerformanceDimensionEnum> getSecurityMasterDataFromFindur(Session session);
}
