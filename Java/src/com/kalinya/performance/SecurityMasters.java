package com.kalinya.performance;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.kalinya.performance.enums.AssetClass;
import com.kalinya.performance.enums.IndustryGroup;
import com.kalinya.performance.enums.InstrumentClass;
import com.kalinya.performance.enums.RiskGroup;
import com.kalinya.performance.enums.Sector;
import com.kalinya.util.BaseSet;

final public class SecurityMasters extends BaseSet<SecurityMaster> {

	public SecurityMasters() {
		super();
	}

	public SecurityMaster getSecurityMaster(String instrumentId) {
		for(SecurityMaster securityMaster: getSet()) {
			if(securityMaster.getInstrumentId().equalsIgnoreCase(instrumentId)) {
				return securityMaster;
			}
		}
		throw new IllegalArgumentException(String.format("InstrumentId [%s] is not in the security master",instrumentId));
	}
	
	public IndustryGroup getIndustryGroup(String instrumentId) {
		return getSecurityMaster(instrumentId).getIndustryGroup();
	}
	
	public Sector getSector(String instrumentId) {
		return getSecurityMaster(instrumentId).getSector();
	}
	
	public RiskGroup getRiskGroup(String instrumentId) {
		return getSecurityMaster(instrumentId).getRiskGroup();
	}
	
	public InstrumentClass getInstrumentClass(String instrumentId) {
		return getSecurityMaster(instrumentId).getInstrumentClass();
	}
	
	public AssetClass getAssetClass(String instrumentId) {
		return getSecurityMaster(instrumentId).getAssetClass();
	}

	public Date getMaturityDate(String instrumentId) {
		return getSecurityMaster(instrumentId).getMaturityDate();
	}

	public Set<String> getInstrumentIds() {
		Set<String> instrumentIds = new HashSet<>();
		for(SecurityMaster securityMaster: getSet()) {
			instrumentIds.add(securityMaster.getInstrumentId());
		}
		return instrumentIds;
	}
}
