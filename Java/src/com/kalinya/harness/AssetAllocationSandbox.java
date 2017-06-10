package com.kalinya.harness;

import java.math.BigDecimal;
import java.util.Map;

import com.kalinya.assetallocation.Dimensions;
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
		Dimensions dimensions = TestHarnessHelper.getAssetAllocationDimensions();
		Strategy strategy = Strategy.create("StrategicAssetAllocation");
		strategy.setDimensions(dimensions);
		strategy.setTargetAllocation(dimensions.get("Govt"), NumberUtil.newBigDecimal(0.5));
		strategy.setTargetAllocation(dimensions.get("SemiGovt"), NumberUtil.newBigDecimal(0.10));
		strategy.setTargetAllocation(dimensions.get("Corp"), NumberUtil.newBigDecimal(0.05));
		strategy.setTargetAllocation(dimensions.get("BmkBonds"), NumberUtil.newBigDecimal(0.1));
		strategy.setTargetAllocation(dimensions.get("BmkBills"), NumberUtil.newBigDecimal(0.1));
		strategy.setTargetAllocation(dimensions.get("Country"), NumberUtil.newBigDecimal(0.06));
		strategy.setTargetAllocation(dimensions.get("Duration"), NumberUtil.newBigDecimal(0.04));
		strategy.setTargetAllocation(dimensions.get("Cash"), NumberUtil.newBigDecimal(0.05));
		//System.out.println(strategy.getTargetAllocationsAsString());
		
		Map<Instrument, BigDecimal> portfolio = TestHarnessHelper.getPortfolio();
		strategy.setActualAllocation(portfolio);
		System.out.println(String.format("PortfolioSizeByDimension %s", strategy.getPortfolioSizeByDimension()));
		System.out.println(String.format("InstrumentsByDimension %s", strategy.getInstrumentsByDimension()));
		BigDecimal portfolioSize = strategy.getPortfolioSize();
		System.out.println(String.format("PortfolioSize %s", StringUtil.formatDouble(portfolioSize)));
		strategy.setMinimumOrderSize(NumberUtil.newBigDecimal(100000));
		//Map<Instrument, BigDecimal> orders = strategy.getOrders();
		System.out.println(String.format("Orders %s", strategy.getOrders()));
		System.out.println(String.format("Order Details %s", strategy.getOrderDetails()));
	}

	/**
	 * <h1 style="color:red;">To Do</h1>
	 * <ul>
	 * <li>Get post rebalance allocations</li>
	 * <li>Support strategy definition by CSV</li>
	 * <li>Use getMinimumOrderSize to ignore small trades</li>
	 * <li>Support multi-currency portfolios</li>
	 * <li>Do not target cash; sum proceeds of trades to account for cash rebalancing item</li>
	 * <li>Check cash allocation when using getMinimumOrderSize to ignore small trades</li>
	 * <li>Read Lee W. 2000 <i>Theory and Methodology of Tactical Asset Allocation</i> reference from FRAPO (2013) chapter 13, p325</li>
	 * </ul>
	 * <h1 style="color:green;">Supported</h1>
	 * <ul>
	 * <li>Strategy.getTargetsByInstrument()</li>
	 * <li>Strategy.getWeightDifferencesByInstrument()</li>
	 * <li>Strategy.getOrders()</li>
	 * <li>Strategy.getSpecifiedTargetsByDimension()</li>
	 * <li>Strategy.getWeightsInDimension()</li>
	 * <li>CalculatePortfolioSize</li>
	 * <li>SetActualAllocation</li>
	 * <li>SetMinimumOrderSize</li>
	 * <li>SetTargetAllocations</li>
	 * </ul>
	 */
	public static void todo() {
	}
	
}