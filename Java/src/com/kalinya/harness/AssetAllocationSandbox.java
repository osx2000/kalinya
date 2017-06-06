package com.kalinya.harness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.kalinya.assetallocation.Dimension;
import com.kalinya.assetallocation.Strategy;
import com.kalinya.optimization.Instrument;
import com.kalinya.util.NumberUtil;
import com.kalinya.util.StringUtil;

public final class AssetAllocationSandbox {
	public static void main(String[] args) {
		AssetAllocationSandbox sandbox = new AssetAllocationSandbox();
		sandbox.createDimensions();
	}
	
	public AssetAllocationSandbox() {
	}
	
	private void createDimensions() {
		List<Dimension> dimensions = TestHarnessHelper.getAssetAllocationDimensions();
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
		//System.out.println(strategy.getTargetAllocationsAsString());
		
		Map<Instrument, BigDecimal> portfolio = TestHarnessHelper.getPortfolio();
		System.out.println(String.format("PortfolioSize %s", StringUtil.formatDouble(TestHarnessHelper.getPortfolioSize(portfolio))));
		System.out.println(String.format("InstrumentsByDimension %s", TestHarnessHelper.getInstrumentsByDimension(portfolio)));
		System.out.println(String.format("PortfolioSizeByDimension %s", TestHarnessHelper.getPortfolioSizeByDimension(portfolio)));
		strategy.setActualAllocation(portfolio);
		
	}
	
}