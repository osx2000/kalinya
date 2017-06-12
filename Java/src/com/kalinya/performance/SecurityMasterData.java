package com.kalinya.performance;

import java.util.Date;

import com.kalinya.assetallocation.Dimension;
import com.kalinya.performance.enums.AssetClass;
import com.kalinya.performance.enums.IndustryGroup;
import com.kalinya.performance.enums.InstrumentClass;
import com.kalinya.performance.enums.RiskGroup;
import com.kalinya.performance.enums.Sector;

public interface SecurityMasterData {
	public String getInstrumentId();
	public Date getMaturityDate();
	public IndustryGroup getIndustryGroup();
	public Sector getSector();
	public InstrumentClass getInstrumentClass();
	public RiskGroup getRiskGroup();
	public AssetClass getAssetClass();
	public Dimension getDimension();
}
