package com.kalinya.harness;

import java.util.List;

import com.kalinya.assetallocation.Dimension;

public final class AssetAllocationSandbox {
	public static void main(String[] args) {
		AssetAllocationSandbox sandbox = new AssetAllocationSandbox();
		sandbox.createDimensions();
	}
	
	public AssetAllocationSandbox() {
	}
	
	private void createDimensions() {
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
		
		List<Dimension> dimensions = Dimension.getDimensionsAsList(govt, semiGovt, corp, bmkBonds, bmkBills, active, passive, country, duration, cash, core, satellite, cash1);
		System.out.println(Dimension.getDimensionFamilyTree(dimensions));
		System.out.println(Dimension.getDimensionFamilyTreeAsString(dimensions));
	}
}