package com.kalinya.harness;

import java.util.List;

import com.kalinya.assetallocation.Dimension;
import com.kalinya.assetallocation.Strategy;
import com.kalinya.util.NumberUtil;

public final class AssetAllocationSandbox {
	public static void main(String[] args) {
		AssetAllocationSandbox sandbox = new AssetAllocationSandbox();
		sandbox.createDimensions();
	}
	
	public AssetAllocationSandbox() {
	}
	
	private void createDimensions() {
		List<Dimension> dimensions = getDimensions();
		//System.out.println(Dimension.getDimensionFamilyTree(dimensions));
		//System.out.println(Dimension.getDimensionFamilyTreeAsString(dimensions));
		
		Strategy strategy = Strategy.create("StrategicAssetAllocation");
		strategy.setDimensions(dimensions);
		strategy.setTargetAllocation(Dimension.get(dimensions, "Govt"), NumberUtil.newBigDecimal(0.5));
		strategy.setTargetAllocation(Dimension.get(dimensions, "SemiGovt"), NumberUtil.newBigDecimal(0.10));
		strategy.setTargetAllocation(Dimension.get(dimensions, "Corp"), NumberUtil.newBigDecimal(0.05));
		strategy.setTargetAllocation(Dimension.get(dimensions, "BmkBonds"), NumberUtil.newBigDecimal(0.1));
		strategy.setTargetAllocation(Dimension.get(dimensions, "BmkBills"), NumberUtil.newBigDecimal(0.1));
		strategy.setTargetAllocation(Dimension.get(dimensions, "Country"), NumberUtil.newBigDecimal(0.06));
		strategy.setTargetAllocation(Dimension.get(dimensions, "Duration"), NumberUtil.newBigDecimal(0.04));
		strategy.setTargetAllocation(Dimension.get(dimensions, "Cash"), NumberUtil.newBigDecimal(0.05));
		System.out.println(strategy.getTargetAllocationsAsString());
	}

	private List<Dimension> getDimensions() {
		//Level 3
		Dimension govt = Dimension.create("Govt");
		Dimension semiGovt = Dimension.create("SemiGovt");
		Dimension corp = Dimension.create("Corp");
		Dimension bmkBonds = Dimension.create("BmkBonds");
		Dimension bmkBills = Dimension.create("BmkBills");

		//Level 2
		Dimension active = Dimension.create("Active");
		Dimension passive = Dimension.create("Passive");
		Dimension country = Dimension.create("Country");
		Dimension duration = Dimension.create("Duration");
		Dimension cash = Dimension.create("Cash");

		//Level 1
		Dimension core = Dimension.create("Core");
		Dimension satellite = Dimension.create("Satellite");
		Dimension cash1 = Dimension.create("Cash1");

		//Add inheritance
		govt.setParentDimension(active);
		semiGovt.setParentDimension(active);
		corp.setParentDimension(active);
		active.setParentDimension(core);
		bmkBonds.setParentDimension(passive);
		bmkBills.setParentDimension(passive);
		passive.setParentDimension(core);
		country.setParentDimension(satellite);
		duration.setParentDimension(satellite);
		cash.setParentDimension(cash1);

		return Dimension.getDimensionsAsList(govt, semiGovt, corp, bmkBonds, bmkBills, active, passive, country, duration, cash, core, satellite, cash1);
	}
}